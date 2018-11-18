package nl.pvanassen.christmas.tree.controller.service

import fr.bmartel.opc.OpcClient
import fr.bmartel.opc.PixelStrip

class ByteStreamService(strips:Int, private val ledsPerStrip:Int, private val opcClient:OpcClient) {
    private val pixelStrips:Array<PixelStrip>

    init {
        val devices = Math.ceil(strips.toDouble() / 8).toInt()
        pixelStrips = (0 until devices).flatMap {
            val opcDevice = opcClient.addDevice()
            (0 until 8).map {
                opcDevice.addPixelStrip(it, ledsPerStrip)
            }
        }.toTypedArray()
    }

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
        val pixelStrip = pixelStrips[arrayPos / ledsPerStrip]
        val pixel = arrayPos % ledsPerStrip
        pixelStrip.setPixelColor(pixel, color)
    }


}