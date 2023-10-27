package nl.pvanassen.led.animation

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.slf4j.LoggerFactory

class AnimationWebsocketEndpoint(
        private val animationClients: AnimationClients,
        private val strips: List<Int>
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val json = kotlinx.serialization.json.Json

    suspend fun endpoint(webSocketServerSession: DefaultWebSocketServerSession) {
        log.info("New connection!")
        webSocketServerSession.sendSerialized(Message("welcome", StartClient("/resource/mask.png", strips)))
        for (frame in webSocketServerSession.incoming) {
            when (frame.frameType) {
                FrameType.BINARY -> handleLedData(frame as Frame.Binary, webSocketServerSession)
                FrameType.TEXT -> handleCommand(frame as Frame.Text, webSocketServerSession)
                FrameType.CLOSE -> animationClients.removeClient(webSocketServerSession)
                FrameType.PING, FrameType.PONG -> log.info("Ping? Pong? ${frame.frameType}")
            }
        }
    }

    private fun handleCommand(text: Frame.Text, session: DefaultWebSocketServerSession) {
        val registration = json.decodeFromString<Message<Registration>>(text.readText())
        if (registration.type == "registration") {
            val name = registration.payload.name
            log.info("Hello $name")
            animationClients.addClient(registration.payload, session)
        }
    }

    private fun handleLedData(frame: Frame.Binary, session: DefaultWebSocketServerSession) {
        animationClients.receivedAnimation(frame.data, session)
    }

}