package nl.pvanassen.christmas.tree.controller.service

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import fr.bmartel.opc.PixelStrip
import nl.pvanassen.christmas.tree.canvas.Canvas
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

internal class ByteStreamServiceTest {

    @Test
    fun when_mask_loaded_expect_pixels_to_be_of_color_red() {
        val pixelStrips = mockPixelStripArray(16)
        val sut = ByteStreamService(60, pixelStrips)
        val canvas = Canvas(16, 60)
        val mask: BufferedImage = ImageIO.read(Canvas::class.java.getResourceAsStream("/test-mask2-16-60.png"))
        canvas.canvas.graphics.drawImage(mask, 0, 0, null)
        val byteArray = canvas.getValues()
        sut.pushByteStream(byteArray)
    }

    private fun mockPixelStripArray(strips:Int):Array<PixelStrip> {
        return (0 until strips).map {TextPixelStrip() }.toTypedArray()
    }

    class TextPixelStrip : PixelStrip(mock {}, 0, 60, 0, "") {

        override fun setPixelColor(pixelNumber: Int, color: Int) {
            assert(Color(color).red).isEqualTo(255)
            assert(Color(color).green).isEqualTo(0)
            assert(Color(color).blue).isEqualTo(0)
        }
    }
}