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
class MqttService(private val config: ApplicationConfig, commandHandler: CommandHandler) {

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
                "cmnd/led-controller/${getHostname()}/state" -> commandHandler.state(String(it.payload!!.toByteArray()))
                "cmnd/led-controller/${getHostname()}/brightness" -> commandHandler.brightness(String(it.payload!!.toByteArray()))
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
                        "cmnd/led-controller/${getHostname()}/state",
                        SubscriptionOptions(Qos.AT_LEAST_ONCE)
                    ),
                    Subscription(
                        "cmnd/led-controller/${getHostname()}/brightness",
                        SubscriptionOptions(Qos.AT_LEAST_ONCE)
                    )
                )
            )
            client.publish(
                false,
                Qos.AT_LEAST_ONCE,
                "led-controller/discovery/${getHostname()}",
                """{"hostname": "${getHostname()}", 
                    |"ip": "${getIpAddress()}",
                    |"version": "1"
                    |} """.trimMargin().encodeToByteArray().toUByteArray()
            )
            TreeState.registerCallback { _, state ->
                publishState(state, BrightnessState.state)
            }
            BrightnessState.registerCallback { _, state ->
                publishState(TreeState.state, state)
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
            "stat/led-controller/${getHostname()}/animation", animation.encodeToByteArray().toUByteArray()
        )
    }

    fun sendBrightness(brightness: Float) {
        client.publish(
            false,
            Qos.EXACTLY_ONCE,
            "tele/led-controller/${getHostname()}/brightness", brightness.toString().encodeToByteArray().toUByteArray()
        )
    }

    private fun publishState(treeState: TreeState.State, brightnessState: BrightnessState.State) {
        client.publish(
            false,
            Qos.AT_LEAST_ONCE,
            "stat/led-controller/${getHostname()}/state", treeState.name.encodeToByteArray().toUByteArray()
        )
        client.publish(
            false,
            Qos.AT_LEAST_ONCE,
            "stat/led-controller/${getHostname()}/brightness",
            brightnessState.name.encodeToByteArray().toUByteArray()
        )
    }

    private fun getIpAddress(): String {
        if (config.propertyOrNull("app.ip") != null && config.propertyOrNull("app.ip")!!.getString().isNotBlank()) {
            return config.propertyOrNull("app.ip")!!.getString()
        }
        val e: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
        while (e.hasMoreElements()) {
            val n = e.nextElement() as NetworkInterface
            val ee: Enumeration<*> = n.inetAddresses
            while (ee.hasMoreElements()) {
                val i = ee.nextElement() as InetAddress
                if (i is Inet6Address || i.hostAddress.startsWith("127") || i.hostAddress.startsWith("172")) {
                    continue
                }
                return i.hostAddress
            }
        }
        return "127.0.0.1"
    }

    private fun getHostname(): String {
        if (config.propertyOrNull("app.hostname") != null && config.propertyOrNull("app.hostname")!!.getString()
                .isNotBlank()
        ) {
            return config.propertyOrNull("app.hostname")!!.getString()
        }
        return InetAddress.getLocalHost().hostName
    }

}