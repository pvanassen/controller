package nl.pvanassen.led.model

import nl.pvanassen.opc.Opc
import org.slf4j.LoggerFactory

class OpcStripsModel(private val empty: Boolean, private val opc: Opc) : StripsModel {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        opc.clear()
    }

    override suspend fun push() {
        opc.flush()
    }

    override fun isEmpty() = empty

    override fun setPixelColor(pixel: Int, color: Int) {
        opc.setPixelColor(pixel, color)
    }

    override suspend fun setBrightness(brightness: Float) {
        logger.info("Setting brightness to $brightness")

        if (brightness >= 0.8) {
            opc.setDithering(true)
        } else {
            opc.setDithering(false)
        }
        opc.setColorCorrection(2.4f, brightness, brightness, brightness)
    }
}