package nl.pvanassen.christmas.tree.controller.service

import nl.pvanassen.christmas.tree.animation.common.model.TreeModel
import javax.inject.Singleton

@Singleton
class ByteArrayFrameService(private val treeModel: TreeModel) {

    fun validateArraySize(byteArray: ByteArray) {
        if (byteArray.size % 3 != 0) {
            throw IllegalArgumentException("Byte array does not have a number of pixels dividable by 3")
        }
        val pixels = byteArray.size / 3
        if (pixels % treeModel.strips != 0) {
            throw IllegalArgumentException("Byte array does not divide by the number of pixels")
        }
        if (pixels % treeModel.ledsPerStrip != 0) {
            throw IllegalArgumentException("Number of strips do not match")
        }
    }

    fun getPixelsPerFrame() = treeModel.ledsPerStrip * treeModel.strips

    fun getPixel(frame:ByteArray, pos:Int) = frame.slice(pos * 3 until 3 + pos * 3)

    fun getFrame(colors:ByteArray, pos:Int) = colors.slice( getPixelsPerFrame() * pos * 3 until getPixelsPerFrame() * (pos + 1) * 3)
}