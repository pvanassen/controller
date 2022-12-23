package nl.pvanassen.led

import io.ktor.server.config.yaml.*
import kotlinx.coroutines.CoroutineExceptionHandler
import nl.pvanassen.led.animation.*
import nl.pvanassen.led.brightness.BrightnessClient
import nl.pvanassen.led.brightness.BrightnessService
import nl.pvanassen.led.model.StripsModel
import nl.pvanassen.led.power.TasmotaClient
import nl.pvanassen.led.scheduler.AnimationPlayerRunnable
import nl.pvanassen.led.scheduler.AutoBrightnessService
import nl.pvanassen.led.scheduler.ShutdownWakeupService
import nl.pvanassen.led.state.StateEndpoint
import nl.pvanassen.opc.Opc
import org.slf4j.LoggerFactory

object Context {
    private val log = LoggerFactory.getLogger(Context::class.java)
    private val config = YamlConfig(null)!!
    private val fps = config.property("app.leds.fps").getString().toInt()
    private val strips = buildMatrix()
    private val opc = buildOpc(strips)
    private val stripsModel = StripsModel(false, opc)
    private val animationClients = AnimationClients()
    val animationWebsocketEndpoint = AnimationWebsocketEndpoint(animationClients, strips)
    private val brightnessClient = BrightnessClient(config)
    val brightnessService = BrightnessService(brightnessClient, stripsModel)
    private val tasmotaClient = TasmotaClient(config)
    private val byteArrayFrameService = ByteArrayFrameService(opc.ledModel)
    private val byteArrayMergeService = ByteArrayMergeService(fps, byteArrayFrameService)
    private val byteArrayStoreService = ByteArrayStoreService(opc.ledModel, fps, byteArrayMergeService)
    private val animationLoader = AnimationLoader(config, byteArrayStoreService, animationClients)
    private val shutdownWakeupService = ShutdownWakeupService(tasmotaClient, animationLoader)
    val stateEndpoint = StateEndpoint(shutdownWakeupService, byteArrayStoreService)

    private val frameStreamService = FrameStreamService(stripsModel)
    private val animationPlayerRunnable = AnimationPlayerRunnable(fps, byteArrayStoreService, frameStreamService)
    private val autoBrightnessService = AutoBrightnessService(stripsModel, brightnessClient)

    init {
        autoBrightnessService.start(CoroutineExceptionHandler { _, exception ->
            log.error("Uncaught exception in auto-brightness", exception)
        })
        shutdownWakeupService.start()
        animationPlayerRunnable.start(CoroutineExceptionHandler { _, exception ->
            log.error("Uncaught exception in animation-player", exception)
        })
        animationLoader.start(CoroutineExceptionHandler { _, exception ->
            log.error("Uncaught exception in animation-loader", exception)
        })
        stateEndpoint.forceOn()
    }

    private fun buildMatrix(): List<Int> = (0 until 30).map {
        if (it % 2 == 0) {
            return@map 29
        }
        30
    }

    private fun buildOpc(strips: List<Int>): Opc {
        val builder = Opc.builder(config.property("app.leds.opc.hostname").getString(), config.property("app.leds.opc.port").getString().toInt())
        strips.forEach {
            builder.addPixelStrip(it)
        }
        return builder.build()
    }
}