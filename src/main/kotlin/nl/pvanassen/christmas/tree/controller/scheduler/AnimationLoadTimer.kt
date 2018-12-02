package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import io.micronaut.scheduling.annotation.Scheduled
import nl.pvanassen.christmas.tree.controller.service.ByteArrayStoreService
import nl.pvanassen.christmas.tree.controller.service.RemoteAnimationService
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

@Context
class AnimationLoadTimer(private val byteArrayStoreService: ByteArrayStoreService,
                         private val remoteAnimationService: RemoteAnimationService,
                         @Property(name = "christmas.tree.fps") private val fps:Int) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val loading = AtomicBoolean(false)

    @Scheduled(fixedRate = "5s")
    fun checkBufferStatus() {
        if (!byteArrayStoreService.needsFrames()) {
            logger.info("Byte buffer full")
            return
        }
        if (loading.get()) {
            logger.info("Byte buffer still loading")
            return
        }
        logger.info("Byte buffer needs animation!")
        loading.set(true)
        remoteAnimationService.getFramesFromRandomAnimation(15, fps) {
            processByteArray(it)
        }
    }

    private fun processByteArray(byteArray: ByteArray) {
        loading.set(false)
        byteArrayStoreService.addAnimation(byteArray)
    }
}