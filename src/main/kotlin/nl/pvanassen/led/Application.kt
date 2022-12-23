package nl.pvanassen.led

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import nl.pvanassen.led.brightness.BrightnessState
import java.time.Duration
import io.ktor.serialization.kotlinx.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import kotlinx.serialization.json.*
import nl.pvanassen.led.model.TreeState
import java.io.File
import java.io.OutputStream

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSockets()
}

fun Application.configureRouting() {
    val brightnessService = Context.brightnessService
    val stateEndpoint = Context.stateEndpoint

    routing {
        get("/api/brightness") {
            brightnessService.getBrightnessState()
        }
        post("/api/brightness/{state}") {
            call.parameters["state"]?.let {
                brightnessService.updateBrightnessState(BrightnessState.State.valueOf(it.uppercase()))
            }
        }
        post("/api/state/shutdown") {
            stateEndpoint.shutdown()
        }
        post("/api/state/shutdown-now") {
            stateEndpoint.shutdownNow()
        }
        post("/api/state/startup") {
            stateEndpoint.startup()
        }
        post("/api/state/fireworks") {
            stateEndpoint.fireworks()
        }
        post("/api/state/force-on") {
            stateEndpoint.forceOn()
        }
        get("/api/state") {
            TreeState.state
        }
        get("/resource/mask.png") {
            call.respondOutputStream(contentType = ContentType.Image.PNG,
                    status = HttpStatusCode.OK,
                    producer = producer())
        }
    }
}

fun producer(): suspend OutputStream.() -> Unit = {
    javaClass.getResourceAsStream("/static/mask.png")!!.copyTo(this)
}

fun Application.configureSockets() {
    val animationWebsocketEndpoint = Context.animationWebsocketEndpoint

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
