package nl.pvanassen.led.brightness

import kotlinx.serialization.Serializable

@Serializable
internal data class BrightnessResponse(val lux: Float)