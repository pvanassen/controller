package nl.pvanassen.led.brightness

object BrightnessState {

    var state: State = State.AUTO

    enum class State {
        AUTO, MIN, MAX
    }
}