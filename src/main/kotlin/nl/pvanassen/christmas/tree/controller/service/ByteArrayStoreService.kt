package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.context.annotation.Property
import nl.pvanassen.christmas.tree.animation.common.model.TreeModel
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Singleton
import kotlin.concurrent.withLock

@Singleton
class ByteArrayStoreService(treeModel: TreeModel,
                            private val byteArrayMergeService: ByteArrayMergeService,
                            @Property(name="christmas.tree.fps") private val fps:Int) {

    private val lock = ReentrantLock()

    private val frameSize = treeModel.ledsPerStrip * treeModel.strips * 3

    private val frameList: MutableList<ByteArray> = LinkedList()

    fun addAnimation(frames:ByteArray) {
        if (frames.size % frameSize != 0) {
            throw IllegalArgumentException("Size of ${frames.size} not equal to framesize")
        }
        val nextAnimation = (0 until frames.size / frameSize).map {
            frames.sliceArray(IntRange(it * frameSize, ((it + 1) * frameSize) - 1))
        }
        lock.withLock {
            val mergedResult = byteArrayMergeService.mergeByteArrays(frameList, nextAnimation, 2000)
            frameList.clear()
            frameList.addAll(mergedResult)
        }
    }

    fun tick(): ByteArray {
        return lock.withLock {
            frameList.removeAt(0)
        }
    }

    fun needsFrames() = frameList.size < (fps * 30)

    fun hasFrames() = !frameList.isEmpty()
}