package nl.pvanassen.led.animation

import io.ktor.server.websocket.*

class AnimationWebsocketEndpoint {
    suspend fun endpoint(webSocketServerSession: DefaultWebSocketServerSession) {
        webSocketServerSession.send(Message("welcome"))
    }

}