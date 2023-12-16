package nl.pvanassen.led.model

import kotlinx.serialization.Serializable
import java.util.*
import java.util.function.Consumer

object TreeState {

    var state: State = State.STARTING_UP
        set(value) {
            field = value
            callbackHandlers.forEach { it.accept(value) }
        }

    private val callbackHandlers: MutableList<Consumer<State>> = LinkedList()

    @Serializable
    enum class State {
        ON, OFF, STARTING_UP, SHUTTING_DOWN, FIREWORK
    }

    fun registerCallback(callback: Consumer<State>) {
        callbackHandlers.add(callback)
    }


}