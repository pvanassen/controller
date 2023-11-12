package nl.pvanassen.led.animation

import io.ktor.server.websocket.*
import nl.pvanassen.led.animation.AnimationType.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class AnimationClients {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val connectionNameMap = ConcurrentHashMap<DefaultWebSocketServerSession, String>()

    private val connectionsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    private val startupClientsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    private val shutdownClientsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    private val timedClientsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    private val timedClientNameCronMap = ConcurrentHashMap<String, String>()

    private val callbackByName = ConcurrentHashMap<String, (ByteArray) -> Unit>()

    private val fireworksClientsByName = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    fun addClient(registration: Registration, session: DefaultWebSocketServerSession) {
        val name = registration.name
        connectionNameMap[session] = name
        when (registration.type) {
            NORMAL -> connectionsByName[name] = session
            ON_SHUTDOWN -> shutdownClientsByName[name] = session
            ON_STARTUP -> startupClientsByName[name] = session
            TIMED -> {
                timedClientsByName[name] = session
                timedClientNameCronMap[name] = registration.cron
            }

            FIREWORKS -> fireworksClientsByName[name] = session
        }
    }

    fun removeClient(session: DefaultWebSocketServerSession) {
        connectionNameMap.remove(session)?.let { connectionsByName.remove(it) }
    }

    fun removeClient(name: String) {
        connectionsByName.remove(name)?.let { connectionNameMap.remove(it) }
        shutdownClientsByName.remove(name)?.let { connectionNameMap.remove(it) }
        startupClientsByName.remove(name)?.let { connectionNameMap.remove(it) }
        timedClientsByName.remove(name)?.let { connectionNameMap.remove(it) }
        fireworksClientsByName.remove(name)?.let { connectionNameMap.remove(it) }
    }

    fun receivedAnimation(frames: ByteArray, session: DefaultWebSocketServerSession) {
        connectionNameMap[session]?.let {
            callbackByName.remove(it)?.invoke(frames)
        }
    }

    suspend fun requestAnimation(name: String, seconds: Int, fps: Int, callback: (ByteArray) -> Unit) {
        log.info("Requesting animation from $name")
        callbackByName[name] = callback
        connectionsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(seconds, fps)))
    }

    suspend fun requestStartupAnimation(fps: Int, callback: (ByteArray) -> Unit) {
        val name = startupClientsByName.keys().asSequence().shuffled().find { true }
        log.info("Requesting startup animation $name")
        name?.let {
            callbackByName[name] = callback
            startupClientsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(-1, fps)))
        }
    }

    suspend fun requestShutdownAnimation(fps: Int, callback: (ByteArray) -> Unit) {
        val name = shutdownClientsByName.keys().asSequence().shuffled().find { true }
        log.info("Requesting shutdown animation $name")
        name?.let {
            callbackByName[name] = callback
            shutdownClientsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(-1, fps)))
        }
    }

    fun getAnimations() = connectionsByName.keys

    fun removeNameCronEntries(): Map<String, String> {
        val copy = HashMap(timedClientNameCronMap)
        timedClientNameCronMap.clear()
        return copy
    }

    suspend fun requestCronAnimation(name: String, fps: Int, callback: (ByteArray) -> Unit) {
        log.info("Requesting cron animation $name")
        callbackByName[name] = callback
        shutdownClientsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(-1, fps)))
    }

    suspend fun requestFireworks(fps: Int, callback: (ByteArray) -> Unit) {
        val name = fireworksClientsByName.keys().asSequence().shuffled().find { true }
        log.info("Requesting fireworks animation $name")
        name?.let {
            callbackByName[name] = callback
            fireworksClientsByName[name]?.sendSerialized(Message("request-animation", RequestAnimation(-1, fps)))
        }
    }
}