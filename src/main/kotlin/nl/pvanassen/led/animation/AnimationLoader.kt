package nl.pvanassen.led.animation

import io.ktor.server.config.*
import kotlinx.coroutines.*
import nl.pvanassen.led.model.TreeState
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class AnimationLoader(config: ApplicationConfig,
                      private val byteArrayStoreService: ByteArrayStoreService,
                      private val animationClients: AnimationClients) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val loading = AtomicBoolean(false)

    private val fps = config.property("app.leds.fps").getString().toInt()

    private val seconds = config.property("app.leds.seconds").getString().toInt()

    private var waitCycles = 0

    fun start(coroutineExceptionHandler: CoroutineExceptionHandler) {
        CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
                .launch(coroutineExceptionHandler) {
                    while (true) {
                        delay(1000L)
                        checkBufferStatus()
                    }
                }
    }

    private suspend fun checkBufferStatus() {
        if (!byteArrayStoreService.needsFrames()) {
            return
        }
        if (!(TreeState.state == TreeState.State.ON || TreeState.state == TreeState.State.FIREWORK)) {
            return
        }
        if (loading.get()) {
            log.info("Byte buffer still loading")
            if (waitCycles-- == 0) {
                log.info("Byte buffer loading reset")
                loading.set(false)
            }
            return
        }
        loading.set(true)
        waitCycles = 15
        if (TreeState.state == TreeState.State.ON) {
            log.info("Known animations: ${animationClients.getAnimations()}")
            val name = animationClients.getAnimations()
                    .asSequence()
                    .shuffled()
                    .find { true }
            log.info("Picking: $name")
            if (name == null) {
                loading.set(false)
            }
            name?.let {
                try {
                    animationClients.requestAnimation(it, seconds, fps) { animation ->
                        loading.set(false)
                        byteArrayStoreService.addAnimation(animation, seconds, fps)
                    }
                }
                catch (e: Exception) {
                    log.info("Error fetching from $name", e)
                    animationClients.removeClient(name)
                }
            }
        }
    }

    fun loadSunrise() {
        TODO("Not yet implemented")
    }

    fun loadSunset() {
        TODO("Not yet implemented")
    }
}