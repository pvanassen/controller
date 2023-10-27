package nl.pvanassen.led.animation

import nl.pvanassen.opc.LedModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Color

internal class ByteArrayMergeServiceTest {

    private val fps = 60

    private val treeModel = LedModel(mapOf(Pair(2, 2)))

    private val frameService = ByteArrayFrameService(treeModel)

    private val sut = ByteArrayMergeService(fps, frameService)

    @Test
    fun testMergeArray() {
        val strips = 2
        val ledsPerStrip = 2
        val ms = 2000

        // 10 seconds red
        val first = buildArray(Color.RED, strips, ledsPerStrip, 600)
        // 10 seconds blue
        val second = buildArray(Color.BLUE, strips, ledsPerStrip, 600)

        val result = sut.mergeByteArrays(first, second, ms)

        assertEquals(result.size, first.size + second.size - 120)

        val r2 = sut.mergeByteArrays(result, first, ms)
        assertEquals(r2.size, result.size + second.size - 120)
    }

    private fun buildArray(color: Color, strips: Int, ledsPerStrip: Int, length: Int): List<ByteArray> {
        return (0 until length).map {
            (0 until strips * ledsPerStrip).flatMap {
                arrayOf(color.red.toByte(), color.green.toByte(), color.blue.toByte()).asIterable()
            }.toByteArray()
        }
    }
}