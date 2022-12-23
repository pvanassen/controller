package nl.pvanassen.led.animation

import io.ktor.server.websocket.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class AnimationClients {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val connectionsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()
    private val connectionsByConnection = ConcurrentHashMap<DefaultWebSocketServerSession, String>()
    private val callbackByName = ConcurrentHashMap<String, (ByteArray) -> Unit>()

    fun addClient(name: String, session: DefaultWebSocketServerSession) {
        connectionsByName[name] = session
        connectionsByConnection[session] = name
    }

    fun removeClient(session: DefaultWebSocketServerSession) {
        connectionsByConnection.remove(session)?.let { connectionsByName.remove(it) }
    }

    fun receivedAnimation(frames: ByteArray, session: DefaultWebSocketServerSession) {
        val name = connectionsByConnection[session]
        callbackByName.remove(name)?.invoke(frames)
    }

    suspend fun requestAnimation(name: String, seconds:Int, fps:Int, callback: (ByteArray) -> Unit) {
        log.info("Requesting animation from $name")
        callbackByName[name] = callback
        connectionsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(seconds, fps)))
    }

    fun getAnimations() = connectionsByName.keys


}