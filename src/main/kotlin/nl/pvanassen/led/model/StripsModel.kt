package nl.pvanassen.led.model

import nl.pvanassen.opc.Opc
import org.slf4j.LoggerFactory

class StripsModel(private val empty: Boolean, private val opc: Opc) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        opc.clear()
    }

    suspend fun push() {
        opc.flush()
    }

    fun isEmpty() = empty

    fun setPixelColor(pixel: Int, color: Int) {
        opc.setPixelColor(pixel, color)
    }

    suspend fun setBrightness(brightness: Float) {
        logger.info("Setting brightness to $brightness")

        if (brightness >= 0.8) {
            opc.setDithering(true)
        } else {
            opc.setDithering(false)
        }
        opc.setColorCorrection(2.4f, brightness, brightness, brightness)
    }
}