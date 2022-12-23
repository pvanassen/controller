package nl.pvanassen.led.scheduler

import kotlinx.coroutines.*
import nl.pvanassen.led.brightness.BrightnessClient
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.StripsModel
import java.util.concurrent.Executors
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AutoBrightnessService(private val stripsModel: StripsModel,
                            private val brightnessClient: BrightnessClient) {

    fun start(coroutineExceptionHandler: CoroutineExceptionHandler) {
        CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
                .launch(coroutineExceptionHandler) {
                    autoAdjustBrightness()
                    delay(1.toDuration(DurationUnit.MINUTES))
                }

    }

    private suspend fun autoAdjustBrightness() {
        if (BrightnessState.state != BrightnessState.State.AUTO) {
            return
        }
        stripsModel.setBrightness(brightnessClient.getBrightness())
    }
}