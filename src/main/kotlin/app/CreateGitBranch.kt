package app

fun main(args: Array<String>) {
    GitBranchManager.start()
}

object GitBranchManager {

    fun start() {
        println("Available projects")

        val config = ConfigProvider.readConfig()
        val gitClient = GitClient(config)

        config.projects.forEachIndexed { index, project -> println("[$index]$project (" + gitClient.findCurrentBranch(project) +")") }

        print("\nChoose project indexes(csv) or leave it blank to choose all: ")
        val selectedProjects = chooseProjects(config)
        if (selectedProjects.isEmpty()) {
            println("There are no selected projects")
            return
        }

        print("\nChange branch to(new branch will be created): ")
        val branch = readLine().orEmpty()
        if (branch.isEmpty()) {
            println("Branch is empty")
            return
        }

        selectedProjects.forEach{ gitClient.checkoutBranch(config.projects.get(it), branch) }
    }

    private fun chooseProjects(config: Config): List<Int> {
        val selectedProjectsInput = readLine()!!
        if (selectedProjectsInput.isEmpty()) {
            return (0 until config.projects.size).toList()
        } else if (selectedProjectsInput.isEmpty()) {
            return listOf()
        }

        return selectedProjectsInput.split(",").map { it.trim().toInt() }
    }
}

data class Config(val projectsRootPath: String, val projects: List<String>, val cmd: Map<String, String>)