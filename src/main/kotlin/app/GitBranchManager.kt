package app

import com.fasterxml.jackson.module.kotlin.*

fun main(args: Array<String>) {
    GitBranchManager.start()
}

object GitBranchManager {

    var configFile = "config.json";

    fun start() {
        println("Available projects")

        val config = readConfig()
        val gitClient = GitClient(config)

        config.projects.forEachIndexed { index, project -> println("[$index]$project (" + gitClient.findCurrentBranch(project) +")") }

        print("Choose project indexes(csv) or leave it blank to choose all: ")
        val selectedProjects = chooseProjects(config)
        if (selectedProjects.isEmpty()) {
            println("There are no selected projects")
            return
        }

        print("Change branch to(new branch will be created): ")
        val branch = readLine().orEmpty()
        if (branch.isEmpty()) {
            println("Branch is empty")
            return
        }

        selectedProjects.forEach{ gitClient.checkoutBranch(config.projects.get(it), branch) }
    }

    private fun readConfig(): Config {
        val json = GitBranchManager::class.java.getResource("/" + configFile).readText()
        return jacksonObjectMapper().readValue(json)
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