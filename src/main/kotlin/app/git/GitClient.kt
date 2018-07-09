package app.git

import app.git.model.BranchCommitter
import app.git.model.Config
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GitClient(val config: Config) {

    private val listBranchesCmd = config.cmd["listBranches"]!!
    private val coBranchCmd = config.cmd["coBranch"]!!
    private val coNewBranchCmd = config.cmd["coNewBranch"]!!
    private val removeBranch = config.cmd["removeBranch"]!!
    private val removeBranchForce = config.cmd["removeBranchForce"]!!
    private val branchLastCommitterCmd = config.cmd["branchLastCommitter"]!!

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
            try {
                removeBranch.format(branch).runCommand(getProjectDir(project))
            } catch (e: Exception) {
                println("Failed to remove $branch: " + e.message)
                println("Force remove $branch?[y/n]")

                val confirmRemove = readLine()!!
                if (confirmRemove == "y") {
                    removeBranchForce.format(branch).runCommand(getProjectDir(project))
                    println("Branch $branch removed")
                }
            }
        }
    }

    fun fetch(project: String) {
        println("Fetching $project")

        "git fetch origin --prune".runCommand(getProjectDir(project));
    }

    fun findLastCommitter(project: String): List<BranchCommitter> {
        println("Looking for $project authors")

        fetch(project);

        return branchLastCommitterCmd.runCommand(getProjectDir(project))
                .split("\n")
                .filter { it.isNotBlank() }
                .filter { it.contains("remotes") }
                .filterNot { it.contains("HEAD") }
                .filterNot { it.contains("develop") }
                .filterNot { it.contains("master") }
                .filterNot { it.contains("release") }
                .map { BranchCommitter(project, it) }
    }

    private fun getProjectDir(project: String) = File(config.projectsRootPath + File.separator + project)
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
        if (!proc.waitFor(10, TimeUnit.SECONDS) || proc.exitValue() != 0) {
            throw RuntimeException("Error while executing $this \n ${proc.errorStream.bufferedReader().readText()}")
        }

        return procText
    } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
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
