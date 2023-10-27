package nl.pvanassen.led.brightness

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.config.*

class BrightnessClient(config: ApplicationConfig) {

    private val brightnessHost = config.property("app.brightness.host").getString()

    suspend fun getBrightness() =
            HttpClient(CIO).use {
                val response = it.get("http://$brightnessHost/brightness")
                response.body<String>().toFloat()
            }
}