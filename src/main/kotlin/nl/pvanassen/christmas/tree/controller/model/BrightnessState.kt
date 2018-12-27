package nl.pvanassen.christmas.tree.controller.model

object BrightnessState {

    var state:State = State.AUTO

    enum class State {
        AUTO, MIN, MAX
    }
}