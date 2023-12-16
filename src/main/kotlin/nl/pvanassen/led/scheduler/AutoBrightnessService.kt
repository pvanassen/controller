package nl.pvanassen.led.scheduler

import kotlinx.coroutines.*
import nl.pvanassen.led.brightness.BrightnessClient
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.StripsModel
import nl.pvanassen.led.model.TreeState
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AutoBrightnessService(
    private val stripsModel: StripsModel,
    private val brightnessClient: BrightnessClient
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun start(coroutineExceptionHandler: CoroutineExceptionHandler) {
        CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
            .launch(coroutineExceptionHandler) {
                while (true) {
                    autoAdjustBrightness()
                    delay(1.toDuration(DurationUnit.MINUTES))
                }
            }

    }

    private suspend fun autoAdjustBrightness() {
        if (TreeState.state == TreeState.State.FIREWORK) {
            stripsModel.setBrightness(1f)
            return
        }
        if (BrightnessState.state != BrightnessState.State.AUTO) {
            return
        }
        try {
            stripsModel.setBrightness(brightnessClient.getBrightness())
        } catch (e: Exception) {
            log.warn("Error setting brightness", e)
            stripsModel.setBrightness(0.3f)
        }
    }
}