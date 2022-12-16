package nl.pvanassen.led.brightness

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.config.*

class BrightnessClient(config: ApplicationConfig) {

    private val brightnessUri = config.property("app.brightnes.uri").getString()

    suspend fun getBrightness() =
        HttpClient(CIO).use {
            val response = it.get(brightnessUri)
            response.body<String>().toFloat()
    }
}