package nl.pvanassen.led.model

import java.awt.BorderLayout.CENTER
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
import javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED


class DebugStripsModel : StripsModel, JFrame() {

    private val buffer = ImageIO.read(javaClass.getResourceAsStream("/static/mask.png"))

    private val icon = BufferedImage(buffer.width * 2, buffer.height * 2, BufferedImage.TYPE_INT_ARGB)

    private val positionsList: List<Position>

    init {
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        setTitle("LED controller debug")
        isResizable = false

        val g2 = icon.graphics as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(buffer, 0, 0, buffer.width * 2, buffer.height * 2, null)
        g2.dispose()

        val image = ImageIcon(icon)

        val label = JLabel(image)
        val scrollPane = JScrollPane(label)
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        add(scrollPane, CENTER)
        pack()
        isVisible = true

        positionsList = (0 until buffer.width)
            .flatMap { x ->
                (0 until buffer.height)
                    .map { y -> Triple(x, y, buffer.getRGB(x, y) and 0xFFFFFF) }
            }
            .filter { it.third != 0 }
            .map { Position(it.third shr 16 and 0xFF, it.third and 0xFF, it.first, it.second) }
    }

    override suspend fun push() {
        val g2 = icon.graphics as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(buffer, 0, 0, buffer.width * 2, buffer.height * 2, null)
        g2.dispose()

        repaint()
    }

    override fun setPixelColor(pixel: Int, color: Int) {
        val position = positionsList[pixel]
        buffer.setRGB(position.x, position.y, color)
    }

    override suspend fun setBrightness(brightness: Float) {

    }

    private data class Position(val strip: Int, val pixel: Int, val x: Int, val y: Int)
}