package nl.pvanassen.christmas.tree.controller.model

import fr.bmartel.opc.OpcClient
import fr.bmartel.opc.PixelStrip
import org.slf4j.LoggerFactory

class StripsModel(private val strips:Array<PixelStrip>, private val opcClient: OpcClient) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        opcClient.clear()
    }

    fun push() {
        opcClient.show()
    }

    fun setPixelColor(strip:Int, pixel:Int, color:Int) {
        strips[strip].setPixelColor(pixel, color)
    }

    fun isEmpty() = strips.isEmpty()

    fun setBrightness(brightness:Float) {
        logger.info("Setting brightness to $brightness")

        if (brightness >= 0.8) {
            opcClient.setDithering(true)
        } else {
            opcClient.setDithering(false)
        }
        opcClient.setColorCorrection(2.4f, brightness, brightness, brightness)
    }
}