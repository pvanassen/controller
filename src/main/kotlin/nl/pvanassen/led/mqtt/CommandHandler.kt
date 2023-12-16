package nl.pvanassen.led.mqtt

import kotlinx.serialization.json.Json
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState

class CommandHandler {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(content: String) {
        val command = json.decodeFromString<Command>(content)
        when (command.command) {
            "brightness" -> brightness(content)
            "state" -> state(content)
        }
    }

    private fun brightness(content: String) {
        BrightnessState.state = json.decodeFromString<Brightness>(content).payload
    }

    private fun state(content: String) {
        TreeState.state = json.decodeFromString<ChangeState>(content).payload
    }

}