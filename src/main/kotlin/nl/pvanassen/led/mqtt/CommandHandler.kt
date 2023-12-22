package nl.pvanassen.led.mqtt

import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState

class CommandHandler {

    fun brightness(content: String) {
        BrightnessState.state = BrightnessState.State.valueOf(content)
    }

    fun state(content: String) {
        TreeState.state = TreeState.State.valueOf(content)
    }

}