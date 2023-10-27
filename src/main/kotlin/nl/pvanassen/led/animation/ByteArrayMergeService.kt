package nl.pvanassen.led.animation

import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class ByteArrayMergeService(private val fps: Int,
                            private val frameService: ByteArrayFrameService) {

    fun mergeByteArrays(first: List<ByteArray>, second: List<ByteArray>, transitionMs: Int): List<ByteArray> {
        if (first.isEmpty()) {
            return second
        }
        if (second.isEmpty()) {
            return first
        }

        val framesToMerge = framesToMerge(transitionMs)

        val resultArray = LinkedList<ByteArray>()
        resultArray.addAll(first.subList(0, first.size - framesToMerge))

        (0 until framesToMerge).forEach { frameToMerge ->
            val firstFrame = first[first.size - framesToMerge + frameToMerge]
            val secondFrame = second[frameToMerge]
            val factor = frameToMerge / (framesToMerge - 1).toDouble()
            val mergedFrame = (0 until frameService.getPixelsPerFrame()).flatMap { pixelToMerge ->
                val firstPixel = frameService.getPixel(firstFrame, pixelToMerge)
                val secondPixel = frameService.getPixel(secondFrame, pixelToMerge)
                transition(firstPixel, secondPixel, factor)
            }.toByteArray()
            resultArray.add(mergedFrame)
        }
        resultArray.addAll(second.subList(framesToMerge, second.size))

        return resultArray
    }

    private fun framesToMerge(milliseconds: Int): Int {
        val seconds = milliseconds.toDouble() / 1000f
        return ceil(seconds * fps).toInt()
    }

    private fun transition(first: List<Byte>, second: List<Byte>, factor: Double): Iterable<Byte> {
        val red = transition(first[0], second[0], factor)
        val green = transition(first[1], second[1], factor)
        val blue = transition(first[2], second[2], factor)
        return arrayOf(red, green, blue).asIterable()
    }

    private fun transition(first: Byte, second: Byte, factor: Double): Byte {
        val firstResult = floor((first.toInt() and 0xFF) * (1 - factor))
        val secondResult = floor((second.toInt() and 0xFF) * factor)
        return min(255.toDouble(), firstResult + secondResult).toInt().toByte()
    }
}