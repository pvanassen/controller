package nl.pvanassen.led.power

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.config.*
import org.slf4j.LoggerFactory

class TasmotaClient(config: ApplicationConfig) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val tasmotaHost = config.property("app.tasmota.host").getString()

    suspend fun switchOff() {
        try {
            HttpClient(CIO).use { it.get("http://$tasmotaHost/cm?cmnd=Power%20Off") }
        } catch (e: Exception) {
            log.error("Error communicating with power switch", e)
        }
    }

    suspend fun switchOn() {
        try {
            HttpClient(CIO).use { it.get("http://$tasmotaHost/cm?cmnd=Power%20On") }
        } catch (e: Exception) {
            log.error("Error communicating with power switch", e)
        }
    }
}