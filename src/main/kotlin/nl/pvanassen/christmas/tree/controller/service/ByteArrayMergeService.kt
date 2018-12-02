package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.context.annotation.Property
import java.util.*
import javax.inject.Singleton

@Singleton
class ByteArrayMergeService(private val frameService:ByteArrayFrameService,
                            @Property(name="christmas.tree.fps") private val fps:Int) {

    fun mergeByteArrays(first: List<ByteArray>, second: List<ByteArray>, transitionMs:Int): List<ByteArray> {
        if (first.isEmpty()) {
            return second
        }

        val framesToMerge = framesToMerge(transitionMs)

        val resultArray = LinkedList<ByteArray>()
        resultArray.addAll(first.subList(0, first.size - (framesToMerge / 2)))

        val reversedFirst = first.reversed()

        (0 until framesToMerge).forEach { frameToMerge ->
            val firstFrame = reversedFirst[frameToMerge]
            val secondFrame = second[frameToMerge]
            val factor = frameToMerge / (framesToMerge - 1).toDouble()
            val mergedFrame = (0 until frameService.getPixelsPerFrame()).flatMap { pixelToMerge ->
                val firstPixel = frameService.getPixel(firstFrame, pixelToMerge)
                val secondPixel = frameService.getPixel(secondFrame, pixelToMerge)
                transition(firstPixel, secondPixel, factor)
            }.toByteArray()
            resultArray.add(mergedFrame)
        }
        resultArray.addAll(second.subList(framesToMerge / 2, second.size))

        return resultArray
    }

    private fun framesToMerge(milliseconds:Int): Int {
        val seconds = milliseconds.toDouble() / 1000f
        // 3 bytes per frame, 60 frames per second times second
        return Math.ceil(seconds * fps).toInt()
    }

    private fun transition(first:List<Byte>, second:List<Byte>, factor: Double): Iterable<Byte> {
        val red = transition(first[0], second[0], factor)
        val green = transition(first[1], second[1], factor)
        val blue = transition(first[2], second[2], factor)
        return arrayOf(red, green, blue).asIterable()
    }

    private fun transition(first:Byte, second:Byte, factor: Double): Byte {
        val firstResult = Math.floor((first.toInt() and 0xFF) * factor)
        val secondResult = Math.floor((second.toInt() and 0xFF) * (1-factor))
        return Math.min(255.toDouble(), firstResult + secondResult).toByte()
    }
}