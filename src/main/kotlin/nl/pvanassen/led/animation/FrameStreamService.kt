package nl.pvanassen.led.animation

import nl.pvanassen.led.model.StripsModel

class FrameStreamService(private val pixelStrips: StripsModel) {

    suspend fun pushFrame(byteArray: ByteArray) {
        (byteArray.indices step 3).forEach {
            val red = byteArray[it].toInt() and 0xFF
            val green = byteArray[it + 1].toInt() and 0xFF
            val blue = byteArray[it + 2].toInt() and 0xFF
            val color = (red and 0xFF shl 16) or
                    (green and 0xFF shl 8) or
                    (blue and 0xFF shl 0)
            pixelStrips.setPixelColor(it / 3, color)
        }
        pixelStrips.push()
    }

}