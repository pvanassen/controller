package nl.pvanassen.led.scheduler

import kotlinx.coroutines.*
import nl.pvanassen.led.animation.ByteArrayStoreService
import nl.pvanassen.led.animation.FrameStreamService
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

class AnimationPlayerRunnable(fps: Int,
                              private val byteArrayStoreService: ByteArrayStoreService,
                              private val frameStreamService: FrameStreamService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val nsPerFrame = floor((1 / fps.toDouble()) * 1_000_000_000).toInt()

    fun start(coroutineExceptionHandler: CoroutineExceptionHandler) {
        CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
                .launch(coroutineExceptionHandler) {
                    while (true) {
                        logger.info("$nsPerFrame ns per frame")
                        while (true) {
                            val start = System.nanoTime()
                            if (byteArrayStoreService.hasFrames()) {
                                frameStreamService.pushFrame(byteArrayStoreService.tick())
                            }
                            val timeTaken = System.nanoTime() - start
                            delay(getWaitTimes(nsPerFrame, timeTaken))
                        }
                    }
                }
    }

    private fun getWaitTimes(nsPerFrame: Int, timeTakenNs: Long): Duration {
        val nsToWait = nsPerFrame - timeTakenNs
        if (nsToWait <= 0) {
            return Duration.ZERO
        }
        return nsToWait.nanoseconds
    }
}