package nl.pvanassen.led.animation

import nl.pvanassen.opc.LedModel

class ByteArrayFrameService(private val ledModel: LedModel) {

    fun getPixelsPerFrame() = ledModel.totalPixels

    fun getPixel(frame: ByteArray, pos: Int) = frame.slice(pos * 3 until 3 + pos * 3)
}