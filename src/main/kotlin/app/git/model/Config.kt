package app.git.model

import java.io.File

data class Config(val projectsRootPath: String, val cmd: Map<String, String>) {

    fun listProjects(): List<String> {
        return File(projectsRootPath).listFiles()
                .filter { it.isDirectory }
                .map { it.name }
                .filterNot { it.equals("external") }
                .filterNot { it.equals("kotlindrom") }
                .sorted()
    }

}