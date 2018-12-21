package nl.pvanassen.christmas.tree.controller.service

import nl.pvanassen.christmas.tree.animation.common.model.TreeModel
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import javax.inject.Singleton

@Singleton
class FrameStreamService(private val treeModel: TreeModel,
                         private val pixelStrips: StripsModel) {

    fun pushFrame(byteArray: ByteArray) {
        if (pixelStrips.isEmpty()) {
            return
        }
        (0 until byteArray.size step 3).forEach {
            val red = byteArray[it].toInt() and 0xFF
            val green = byteArray[it + 1].toInt() and 0xFF
            val blue = byteArray[it + 2].toInt() and 0xFF
            val color = (red and 0xFF shl 16) or
                    (green and 0xFF shl 8) or
                    (blue and 0xFF shl 0)
            setPixelColor(it / 3, color)
        }
        pixelStrips.push()
    }

    private fun setPixelColor(arrayPos:Int, color:Int) {
        val pixelStrip = arrayPos / treeModel.ledsPerStrip
        val offset = treeModel.ledsPerStrip * pixelStrip
        // Reverse pixels
        val pixel = treeModel.ledsPerStrip - (arrayPos - offset) - 1
        pixelStrips.setPixelColor(pixelStrip, pixel, color)
    }

}