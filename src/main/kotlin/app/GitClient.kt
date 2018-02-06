package app

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GitClient(val config: Config) {

    private val listBranchesCmd = config.cmd.get("listBranches")!!
    private val coBranchCmd = config.cmd.get("coBranch")!!
    private val coNewBranchCmd = config.cmd.get("coNewBranch")!!
    private val removeBranch = config.cmd.get("removeBranch")!!

    fun checkoutBranch(project: String, branch: String) {
        println("Switch $project to branch $branch")

        val workingDir = getProjectDir(project)
        val branches = findAllBranches(workingDir)

        val isAbsentLocally = branches.filter { it.name == branch }.all { it.isRemote }
        if (isAbsentLocally) {
            coNewBranchCmd.format(branch).runCommand(workingDir)
        } else {
            coBranchCmd.format(branch).runCommand(workingDir)
        }
    }

    fun findCurrentBranch(project: String): String {
        return findAllBranches(project).filter { it.isCurrent }.map { it.name }.first()
    }

    fun findAllBranches(project: String): List<Branch> {
        return findAllBranches(getProjectDir(project))
    }

    private fun findAllBranches(projectDir: File): List<Branch> {
        val branches = listBranchesCmd
                .runCommand(projectDir)
                .split("\n")
                .filter { it.isNotBlank() }
                .filterNot { it.contains("HEAD") }
                .map { Branch(it) }
                .toList()

        branches.filter { it.isRemote }
                .forEach{ remoteBranch ->
                    branches.filterNot { it.isRemote }
                            .filter { remoteBranch.name == it.name }
                            .forEach { it.isExistsLocally = true }
                }

        return branches
    }

    fun removeLocalBranch(project: String, localBranches: Set<String>) {
        localBranches.forEach { branch ->
            println("Remove branch $branch")
            removeBranch.format(branch).runCommand(getProjectDir(project))
        }
    }
    
    private fun getProjectDir(project: String) = File(config.projectsRootPath + File.separator + project)
}


class Branch(private var fullName: String) {

    private val remotesPrefix: String = "remotes/origin"
    private val currentBranchPrefix: String = "* "

    val isRemote: Boolean = fullName.contains(remotesPrefix)
    val isCurrent: Boolean

    var isExistsLocally: Boolean = false

    init {
        isCurrent = fullName.startsWith(currentBranchPrefix)
        fullName = fullName.trimStart().removePrefix(currentBranchPrefix)
    }

    val name: String
        get() {
            if (isRemote) {
                return fullName.removePrefix(remotesPrefix + "/")
            } else {
                return fullName
            }
        }

    override fun toString(): String {
        val str = StringBuilder(fullName).append("[")
        if (isExistsLocally) {
            str.append("+")
        } else {
            str.append("-")
        }

        return str.append("]").toString()
    }
}

fun String.runCommand(workingDir: File): String {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        val procText = proc.inputStream.bufferedReader().readText()
        if (proc.exitValue() != 0) {
            throw RuntimeException("Error while executing $this \n ${proc.errorStream.bufferedReader().readText()}")
        }

        return procText
    } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
}
