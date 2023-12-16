package nl.pvanassen.led.mqtt

import kotlinx.serialization.Serializable
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState

@Serializable
data class Command(val command: String)

@Serializable
data class Brightness(val payload: BrightnessState.State)

@Serializable
data class ChangeState(val payload: TreeState.State)