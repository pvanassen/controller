package nl.pvanassen.led.model

import kotlinx.serialization.Serializable
import java.util.*
import java.util.function.BiConsumer

object TreeState {

    var state: State = State.STARTING_UP
        set(value) {
            val oldValue = field
            field = value
            callbackHandlers.forEach { it.accept(oldValue, value) }
        }

    private val callbackHandlers: MutableList<BiConsumer<State, State>> = LinkedList()

    @Serializable
    enum class State {
        ON, OFF, STARTING_UP, SHUTTING_DOWN, FIREWORK
    }

    fun registerCallback(callback: BiConsumer<State, State>) {
        callbackHandlers.add(callback)
    }
}