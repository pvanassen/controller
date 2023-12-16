package nl.pvanassen.led.brightness

import kotlinx.serialization.Serializable

object BrightnessState {

    var state: State = State.AUTO

    @Serializable
    enum class State {
        AUTO, MIN, MAX
    }
}