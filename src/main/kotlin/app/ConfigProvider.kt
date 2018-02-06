package app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object ConfigProvider {

    private const val CONFIG_FILE = "config.json";

    fun readConfig(): Config {
        val json = GitBranchManager::class.java.getResource("/" + CONFIG_FILE).readText()
        return jacksonObjectMapper().readValue(json)
    }

}