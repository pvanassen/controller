package nl.pvanassen.led

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.config.yaml.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import nl.pvanassen.christmas.tree.controller.model.BrightnessState
import nl.pvanassen.opc.Opc
import java.time.Duration
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.*
import nl.pvanassen.led.animation.AnimationWebsocketEndpoint
import nl.pvanassen.led.brightness.BrightnessEndpoint

private val config = YamlConfig(null)!!
private val opc = buildOpc(config)

fun main(args: Array<String>) {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}


fun Application.module() {
    configureRouting()
    configureSockets()
}

fun Application.configureRouting() {
    val brightnessEndpoint = BrightnessEndpoint(config, opc)

    routing {
        get("/api/brightness") {
            brightnessEndpoint.getBrightness()
        }
        post("/api/brightness/{state}") {
            call.parameters["state"]?.let {
                brightnessEndpoint.setBrightness(BrightnessState.State.valueOf(it.uppercase()))
            }
        }
    }
}

fun Application.configureSockets() {
    val animationWebsocketEndpoint = AnimationWebsocketEndpoint()

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(30)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        webSocket("/animation") {
            animationWebsocketEndpoint.endpoint(this)
        }
    }
}



private fun buildOpc(config: YamlConfig) =
        Opc.builder(config.property("app.leds.opc.hostname").getString(), config.property("app.leds.opc.port").getString().toInt())
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .addPixelStrip(29).addPixelStrip(30)
                .build()