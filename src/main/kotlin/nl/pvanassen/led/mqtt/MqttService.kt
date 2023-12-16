package nl.pvanassen.led.mqtt

import MQTTClient
import io.ktor.server.config.*
import mqtt.MQTTVersion
import mqtt.Subscription
import mqtt.packets.Qos
import mqtt.packets.mqttv5.SubscriptionOptions
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState
import org.slf4j.LoggerFactory
import java.net.ConnectException
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.concurrent.thread


@OptIn(ExperimentalUnsignedTypes::class)
class MqttService(config: ApplicationConfig, commandHandler: CommandHandler) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    private val client = MQTTClient(
        MQTTVersion.MQTT5,
        config.property("app.mqtt.host").getString(),
        config.property("app.mqtt.port").getString().toInt(),
        null
    ) {
        try {
            when (it.topicName) {
                "cmnd/led-controller/${InetAddress.getLocalHost().hostName}" -> commandHandler.parse(String(it.payload!!.toByteArray()))
            }
        } catch (e: Exception) {
            log.error("Error handling message", e)
        }
    }

    init {
        try {
            client.subscribe(
                listOf(
                    Subscription(
                        "cmnd/led-controller/${InetAddress.getLocalHost().hostName}",
                        SubscriptionOptions(Qos.AT_LEAST_ONCE)
                    )
                )
            )
            client.publish(
                false,
                Qos.AT_LEAST_ONCE,
                "led-controller/discovery/${InetAddress.getLocalHost().hostName}",
                """{"hostname": "${InetAddress.getLocalHost().hostName}", 
                    |"ip": "${getIpAddress()}",
                    |"version": "1"
                    |} """.trimMargin().encodeToByteArray().toUByteArray()
            )
            TreeState.registerCallback {
                publishState()
            }
            thread(start = true) {
                client.run()
            }
        } catch (e: ConnectException) {
            log.error("Error connecting to MQTT. Continuing", e)
        }
    }

    fun sendAnimationRunning(animation: String) {
        client.publish(
            false,
            Qos.EXACTLY_ONCE,
            "stat/led-controller/animation",
            """{ "animation": "$animation" }""".encodeToByteArray().toUByteArray()
        )
    }

    fun sendBrightness(brightness: Float) {
        client.publish(
            false,
            Qos.EXACTLY_ONCE,
            "tele/led-controller/brightness",
            """{ "brightness": $brightness }""".encodeToByteArray().toUByteArray()
        )
    }

    private fun publishState() {
        client.publish(
            false,
            Qos.AT_LEAST_ONCE,
            "stat/led-controller/${InetAddress.getLocalHost().hostName}",
            """{
                        |"tree-state": "${TreeState.state}",
                        |"brightness-state": "${BrightnessState.state}"
                        |} """.trimMargin().encodeToByteArray().toUByteArray()
        )
    }

    private fun getIpAddress(): String {
        val e: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
        while (e.hasMoreElements()) {
            val n = e.nextElement() as NetworkInterface
            val ee: Enumeration<*> = n.inetAddresses
            while (ee.hasMoreElements()) {
                val i = ee.nextElement() as InetAddress
                if (i is Inet6Address || i.hostAddress.startsWith("127")) {
                    continue
                }
                return i.hostAddress
            }
        }
        return "127.0.0.1"
    }

}