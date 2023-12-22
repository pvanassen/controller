package nl.pvanassen.led

import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState
import java.io.OutputStream
import java.time.Duration

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
    configureSockets()
}

fun Application.configureRouting() {
    val stateEndpoint = Context.stateEndpoint

    routing {
        get("/api/brightness") {
            call.respond(BrightnessState.state)
        }
        post("/api/brightness/{state}") {
            call.parameters["state"]?.let {
                BrightnessState.state = BrightnessState.State.valueOf(it.uppercase())
            }
            call.respond(BrightnessState.state)
        }
        post("/api/state/shutdown") {
            call.respond(stateEndpoint.shutdown())
        }
        post("/api/state/shutdown-now") {
            call.respond(stateEndpoint.shutdownNow())
        }
        post("/api/state/startup") {
            call.respond(stateEndpoint.startup())
        }
        post("/api/state/fireworks") {
            call.respond(stateEndpoint.fireworks())
        }
        post("/api/state/force-on") {
            call.respond(stateEndpoint.forceOn())
        }
        get("/api/state") {
            call.respond(TreeState.state)
        }
        get("/resource/mask.png") {
            call.respondOutputStream(
                contentType = ContentType.Image.PNG,
                status = HttpStatusCode.OK,
                producer = producer()
            )
        }
    }
}

fun producer(): suspend OutputStream.() -> Unit = {
    javaClass.getResourceAsStream("/static/mask.png")!!.copyTo(this)
}

fun Application.configureSockets() {
    val animationWebsocketEndpoint = Context.animationWebsocketEndpoint

    install(WebSockets) {
        pingPeriod = Duration.ofMillis(1000)
        timeout = Duration.ofSeconds(5)
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
