package nl.pvanassen.led.brightness

import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import nl.pvanassen.led.model.StripsModel

class BrightnessService(
    config: ApplicationConfig,
    private val brightnessClient: BrightnessClient,
    private val stripsModel: StripsModel
) {

    init {
        BrightnessState.registerCallback { _, state ->
            runBlocking {
                updateBrightnessState(state)
            }
        }
    }

    private val minBrightness = config.tryGetString("app.brightness.min")!!.toFloat()

    private suspend fun updateBrightnessState(state: BrightnessState.State) {
        when (state) {
            BrightnessState.State.AUTO -> stripsModel.setBrightness(brightnessClient.getBrightness())
            BrightnessState.State.MAX -> stripsModel.setBrightness(0.9f)
            BrightnessState.State.MIN -> stripsModel.setBrightness(minBrightness)
        }
    }

}