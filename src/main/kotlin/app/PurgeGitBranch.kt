package app

fun main(args: Array<String>) {
    PurgeGitBranch.start()
}

object PurgeGitBranch {

    fun start() {
        println("Purge project branches")

        val config = ConfigProvider.readConfig()
        val gitClient = GitClient(config)

        config.projects.forEachIndexed { index, project -> println("[$index]$project") }

        print("\nChoose project: ")
        val selectedProject = readLine()!!
        if (selectedProject.isBlank()) {
            println("No project selected")
            return
        }

        val project = config.projects[selectedProject.toInt()]
        val branches = gitClient.findAllBranches(project)

        println("\nList of local branches('+' exists on remote, '-' not exists on remote)")
        val localBranches = branches.filter { !it.isRemote }.toMutableList()
        localBranches.forEachIndexed { index, branch -> println("[$index]$branch") }

        print("\nSync branches with remote except(empty mean full sync):")
        val branchesToSkip = readLine()!!
        if (branchesToSkip.isNotBlank()) {
            branchesToSkip.split(",")
                    .map { it.trim().toInt() }
                    .sortedDescending()
                    .map { localBranches.removeAt(it) }
        }

        val localBranchesToRemove = localBranches.map { it.name }
                .subtract(branches.filter { it.isRemote }.map { it.name })

        println("\nBranches to remove:")
        localBranchesToRemove.forEach{ branch -> println(branch) }

        print("\nAre you sure you want to remove them?[y/n]")
        val confirmRemove = readLine()!!
        if (confirmRemove == "y") {
            gitClient.removeLocalBranch(project, localBranchesToRemove)
        }
    }

}

