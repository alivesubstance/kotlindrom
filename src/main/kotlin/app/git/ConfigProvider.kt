package app.git

import app.git.model.Config
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object ConfigProvider {

    private const val CONFIG_FILE = "git/config.json";

    fun readConfig(): Config {
        val json = CheckoutBranch::class.java.getResource("/$CONFIG_FILE").readText()
        return jacksonObjectMapper().readValue(json)
    }

}
