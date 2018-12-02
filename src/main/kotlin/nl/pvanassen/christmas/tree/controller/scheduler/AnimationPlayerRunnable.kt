package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import nl.pvanassen.christmas.tree.controller.service.ByteArrayStoreService
import nl.pvanassen.christmas.tree.controller.service.FrameStreamService
import org.slf4j.LoggerFactory

@Context
class AnimationPlayerRunnable(@Property(name ="christmas.tree.fps") private val fps:Int,
                              private val byteArrayStoreService: ByteArrayStoreService,
                              private val frameStreamService: FrameStreamService) :Runnable {

    private val nsPerFrame = Math.floor((1 / fps.toDouble()) * 1_000_000_000).toInt()

    init {
        val thread = Thread(this, this.javaClass.name)
        thread.isDaemon = true
        thread.start()
    }

    override fun run() {
        logger.info("$nsPerFrame ns per frame")
        while (true) {
            val start = System.nanoTime()
            if (byteArrayStoreService.hasFrames()) {
                frameStreamService.pushFrame(byteArrayStoreService.tick())
            }
            val timeTaken = System.nanoTime() - start
            val waitTimes = getWaitTimes(nsPerFrame, timeTaken)
            Thread.sleep(waitTimes.first, waitTimes.second)
        }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(this::class.java)

        fun getWaitTimes(nsPerFrame:Int, timeTakenNs:Long):Pair<Long, Int> {
            val nsToWait = nsPerFrame - timeTakenNs
            if (nsToWait <= 0) {
                return Pair(0, 0)
            }
            val msToWait = Math.floor(nsToWait / 1_000_000.0).toLong()
            val nsToWaitWithoutNs = nsToWait - (msToWait * 1_000_000)
            return Pair(msToWait, nsToWaitWithoutNs.toInt())
        }
    }
}