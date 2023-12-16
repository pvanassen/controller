package nl.pvanassen.led.brightness

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlin.math.max
import kotlin.math.min

class BrightnessClient(config: ApplicationConfig) {

    private val brightnessHost = config.property("app.brightness.host").getString()

    private val brightnessPort = config.property("app.brightness.port").getString().toInt()

    suspend fun getBrightness(): Float =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }.use {
            val response = it.get("http://$brightnessHost:$brightnessPort/")
            max(0.1f, min(1f, response.body<BrightnessResponse>().lux / 1600f))
        }

}