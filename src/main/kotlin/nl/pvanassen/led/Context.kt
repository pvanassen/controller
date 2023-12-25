package nl.pvanassen.led

import io.ktor.server.config.yaml.*
import kotlinx.coroutines.CoroutineExceptionHandler
import nl.pvanassen.led.animation.*
import nl.pvanassen.led.brightness.BrightnessClient
import nl.pvanassen.led.brightness.BrightnessService
import nl.pvanassen.led.model.StripModelFactory
import nl.pvanassen.led.mqtt.CommandHandler
import nl.pvanassen.led.mqtt.MqttService
import nl.pvanassen.led.power.TasmotaClient
import nl.pvanassen.led.scheduler.AnimationPlayerRunnable
import nl.pvanassen.led.scheduler.AutoBrightnessService
import nl.pvanassen.led.scheduler.TimedActionsService
import nl.pvanassen.led.state.StateEndpoint
import nl.pvanassen.opc.Opc
import org.slf4j.LoggerFactory

object Context {
    private val log = LoggerFactory.getLogger(Context::class.java)
    private val config = YamlConfig(null)!!
    private val fps = config.property("app.leds.fps").getString().toInt()
    private val strips = buildMatrix()
    private val opc = buildOpc()
    private val mqttService = MqttService(config, CommandHandler())
    private val stripsModel = StripModelFactory.getStripModel(opc, mqttService, config)
    private val animationClients = AnimationClients()
    val animationWebsocketEndpoint = AnimationWebsocketEndpoint(animationClients, strips)
    private val brightnessClient = BrightnessClient(config)
    private val tasmotaClient = TasmotaClient(config)
    private val byteArrayFrameService = ByteArrayFrameService(opc.ledModel)
    private val byteArrayMergeService = ByteArrayMergeService(fps, byteArrayFrameService)
    private val byteArrayStoreService = ByteArrayStoreService(opc.ledModel, fps, byteArrayMergeService)
    private val animationLoader = AnimationLoader(config, byteArrayStoreService, animationClients, mqttService)
    private val timedActionsService = TimedActionsService(config, tasmotaClient, animationLoader, animationClients)
    val stateEndpoint = StateEndpoint(timedActionsService, byteArrayStoreService)

    private val frameStreamService = FrameStreamService(stripsModel)
    private val animationPlayerRunnable = AnimationPlayerRunnable(fps, byteArrayStoreService, frameStreamService)
    private val autoBrightnessService = AutoBrightnessService(stripsModel, brightnessClient)

    init {
        BrightnessService(config, brightnessClient, stripsModel)
        autoBrightnessService.start(CoroutineExceptionHandler { _, exception ->
            log.error("Uncaught exception in auto-brightness", exception)
        })
        timedActionsService.start()
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

    private fun buildOpc(): Opc {
        val builder = Opc.builder(
            config.property("app.leds.opc.host").getString(),
            config.property("app.leds.opc.port").getString().toInt()
        )
        strips.forEach {
            builder.addPixelStrip(it)
        }
        return builder.build()
    }
}