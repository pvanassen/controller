package nl.pvanassen.led.power

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.config.*

class TasmotaClient(config: ApplicationConfig) {
    private val tasmotaHost = config.property("app.tasmota.host").getString()

    suspend fun switchOff() {
        HttpClient(CIO).use { it.get("http://$tasmotaHost/cm?cmnd=Power%20Off") }
    }

    suspend fun switchOn() {
        HttpClient(CIO).use { it.get("http://$tasmotaHost/cm?cmnd=Power%20On") }
    }
}