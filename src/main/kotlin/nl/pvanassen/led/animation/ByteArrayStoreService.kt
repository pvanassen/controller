package nl.pvanassen.led.animation

import nl.pvanassen.led.model.TreeState
import nl.pvanassen.opc.LedModel
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ByteArrayStoreService(ledModel: LedModel,
                            private val fps: Int,
                            private val byteArrayMergeService: ByteArrayMergeService) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val lock = ReentrantLock()
    private val frameSize = ledModel.totalPixels * 3
    private val frameList: MutableList<ByteArray> = LinkedList()

    fun addAnimation(frames: ByteArray) {
        if (frames.size % frameSize != 0) {
            log.error("Size of ${frames.size} not equal to framesize")
            return
        }

        val nextAnimation = (0 until frames.size / frameSize).map {
            frames.sliceArray(IntRange(it * frameSize, ((it + 1) * frameSize) - 1))
        }
        val transition = if (TreeState.state == TreeState.State.FIREWORK) {
            1000
        } else {
            2000
        }
        lock.withLock {
            val mergedResult = byteArrayMergeService.mergeByteArrays(frameList, nextAnimation, transition)
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

    fun hasFrames() = frameList.isNotEmpty()

    fun reset() {
        lock.withLock {
            frameList.clear()
        }
    }
}