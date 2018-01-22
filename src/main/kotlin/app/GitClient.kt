package app

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GitClient(val config: Config) {

    private val listBranchesCmd = config.cmd.get("listBranches")!!
    private val coBranchCmd = config.cmd.get("coBranch")!!
    private val coNewBranchCmd = config.cmd.get("coNewBranch")!!

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

    fun findCurrentBranche(project: String): String {
        return findAllBranches(getProjectDir(project)).filter { it.isCurrent }.map { it.name }.first()
    }

    private fun findAllBranches(projectDir: File): List<Branch> {
        return listBranchesCmd.
                runCommand(projectDir).
                split("\n").
                map { Branch(it) }.
                toList()
    }

    private fun getProjectDir(project: String) = File(config.projectsRootPath + File.separator + project)
}


class Branch(var fullName: String) {

    private val remotesPrefix: String = "remotes/origin"
    private val currentBranchPrefix: String = "* "

    val isRemote: Boolean = fullName.contains(remotesPrefix)
    val isCurrent: Boolean

    init {
        isCurrent = fullName.startsWith(currentBranchPrefix)
        fullName = fullName.trimStart().removePrefix(currentBranchPrefix)
    }

    val name: String
        get() {
            if (isRemote ) {
                return fullName.removePrefix(remotesPrefix + "/")
            } else{
                return fullName
            }
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

        proc.waitFor(10, TimeUnit.SECONDS)

        if (proc.exitValue() != 0) {

        }

        return proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
}

fun main(args: Array<String>) {
    "git checkout -b CORE-3889_resteasy_dep".runCommand(File("c:\\tmp\\pleeco-core-parent"))
}