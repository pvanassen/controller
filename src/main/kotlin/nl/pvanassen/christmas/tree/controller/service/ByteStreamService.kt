package nl.pvanassen.christmas.tree.controller.service

import fr.bmartel.opc.PixelStrip
import javax.inject.Singleton

@Singleton
class ByteStreamService(private val ledsPerStrip:Int, private val pixelStrips:Array<PixelStrip>) {

    fun pushByteStream(byteArray: ByteArray) {
        (0 until byteArray.size step 3).forEach {
            val red = byteArray[it].toInt() and 0xFF
            val green = byteArray[it + 1].toInt() and 0xFF
            val blue = byteArray[it + 2].toInt() and 0xFF
            val color = (red and 0xFF shl 16) or
                    (green and 0xFF shl 8) or
                    (blue and 0xFF shl 0)
            setPixelColor(it, color)
        }
    }

    private fun setPixelColor(arrayPos:Int, color:Int) {
        val pixelStrip = pixelStrips[(arrayPos / 3) / ledsPerStrip]
        val pixel = (arrayPos / 3) % ledsPerStrip
        pixelStrip.setPixelColor(pixel, color)
    }


}