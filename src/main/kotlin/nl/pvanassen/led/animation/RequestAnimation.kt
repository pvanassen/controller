package nl.pvanassen.led.animation

import kotlinx.serialization.Serializable

@Serializable
data class RequestAnimation(val seconds: Int, val fps: Int)
