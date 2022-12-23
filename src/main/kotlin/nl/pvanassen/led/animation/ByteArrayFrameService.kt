package nl.pvanassen.led.animation

import nl.pvanassen.opc.LedModel

class ByteArrayFrameService(private val ledModel: LedModel) {

    fun validateArraySize(byteArray: ByteArray) {
        if (byteArray.size % 3 != 0) {
            throw IllegalArgumentException("Byte array does not have a number of pixels dividable by 3")
        }
        val pixels = byteArray.size / 3

        if (pixels % ledModel.totalPixels != 0) {
            throw IllegalArgumentException("Byte array does not divide by the number of pixels")
        }
    }

    fun getPixelsPerFrame() = ledModel.totalPixels

    fun getPixel(frame:ByteArray, pos:Int) = frame.slice(pos * 3 until 3 + pos * 3)

    fun getFrame(colors:ByteArray, pos:Int) = colors.slice( ledModel.totalPixels * pos * 3 until ledModel.totalPixels * (pos + 1) * 3)
}