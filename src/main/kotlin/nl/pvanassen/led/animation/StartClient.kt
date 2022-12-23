package nl.pvanassen.led.animation

import kotlinx.serialization.Serializable

@Serializable
data class StartClient(val maskPath: String, val pixels: List<Int>)
