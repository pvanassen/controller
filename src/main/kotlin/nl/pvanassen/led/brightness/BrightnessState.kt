package nl.pvanassen.led.brightness

import kotlinx.serialization.Serializable
import java.util.*
import java.util.function.BiConsumer

object BrightnessState {

    var state: State = State.AUTO
        set(value) {
            val oldValue = field
            field = value
            callbackHandlers.forEach { it.accept(oldValue, value) }
        }

    private val callbackHandlers: MutableList<BiConsumer<State, State>> = LinkedList()

    @Serializable
    enum class State {
        AUTO, MIN, MAX
    }

    fun registerCallback(callback: BiConsumer<State, State>) {
        callbackHandlers.add(callback)
    }

}