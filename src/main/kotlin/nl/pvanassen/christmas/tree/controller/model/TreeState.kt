package nl.pvanassen.christmas.tree.controller.model

object TreeState {

    var state:State = State.STARTING_UP

    enum class State {
        ON, OFF, STARTING_UP, SHUTTING_DOWN, FIREWORK
    }
}