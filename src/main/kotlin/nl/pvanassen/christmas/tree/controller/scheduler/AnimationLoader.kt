package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import io.micronaut.scheduling.annotation.Scheduled
import nl.pvanassen.christmas.tree.controller.model.TreeState
import nl.pvanassen.christmas.tree.controller.service.ByteArrayStoreService
import nl.pvanassen.christmas.tree.controller.service.RemoteAnimationService
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

@Context
class AnimationLoader(private val byteArrayStoreService: ByteArrayStoreService,
                      private val remoteAnimationService: RemoteAnimationService,
                      @Property(name = "christmas.tree.fps") private val fps:Int,
                      @Property(name = "christmas.tree.seconds") private val seconds:Int) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val loading = AtomicBoolean(false)

    init {
        loadSunrise()
    }

    fun loadSunrise() {
        loading.set(true)
        remoteAnimationService.getSunriseAnimation(fps) {
            processByteArray(it)
            TreeState.state = TreeState.State.ON
        }
    }

    fun loadSunset() {
        loading.set(true)
        remoteAnimationService.getSunsetAnimation(fps) {
            processByteArray(it)
            TreeState.state = TreeState.State.ON
        }
    }

    @Scheduled(fixedRate = "1s")
    fun checkBufferStatus() {
        if (!byteArrayStoreService.needsFrames()) {
            return
        }
        if (!(TreeState.state == TreeState.State.ON || TreeState.state == TreeState.State.FIREWORK)) {
            return
        }
        if (loading.get()) {
            logger.info("Byte buffer still loading")
            return
        }
        loading.set(true)
        if (TreeState.state == TreeState.State.ON) {
            remoteAnimationService.getFramesFromRandomAnimation(seconds, fps) {
                processByteArray(it)
            }
        }
        else {
            remoteAnimationService.getFramesFromFireworks(fps) {
                processByteArray(it)
            }
        }

    }

    private fun processByteArray(byteArray: ByteArray) {
        loading.set(false)
        byteArrayStoreService.addAnimation(byteArray)
    }
}