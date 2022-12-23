package nl.pvanassen.led.animation

import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal class CreateMaskTest {
    @Test
    fun create4x4() {
        val pixels = listOf(4, 4, 4, 4)
        val width = 10 + (pixels.size * 10)
        val height = 10 + (pixels.max() * 10)
        val canvas = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        var pos = 0
        for (strip in pixels.indices) {
            for (pixel in 0 until pixels[strip]) {
                // Red -> strip
                // Blue -> pixel
                // Green 255

                canvas.setRGB(10 + (strip * 10), 10 + (pixel * 10), Color(strip, 255, pixel).rgb)
                pos++
            }
        }
        ImageIO.write(canvas, "png", File("test4x4.png"))
    }

    @Test
    fun create15x30() {
        val pixels = (0 until 30).map {
            if (it % 2 == 0) {
                return@map 29
            }
            30
        }

        val width = 5 + (pixels.size * 5)
        val height = 10 + (pixels.max() * 10)
        val canvas = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        var pos = 0
        for (strip in pixels.indices) {
            for (pixel in 0 until pixels[strip]) {
                // Red -> strip
                // Blue -> pixel
                // Green 255
                val offset = if (pixels[strip] == 29) {
                    5
                } else {
                    0
                }

                canvas.setRGB(5 + (strip * 5), offset + 10 + (pixel * 10), Color(strip, 255, pixel).rgb)
                pos++
            }
        }
        ImageIO.write(canvas, "png", File("test31x30.png"))
    }
}