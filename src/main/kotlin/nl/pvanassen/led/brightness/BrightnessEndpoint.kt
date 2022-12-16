package nl.pvanassen.led.brightness

import io.ktor.server.config.*
import nl.pvanassen.christmas.tree.controller.model.BrightnessState
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import nl.pvanassen.opc.Opc

class BrightnessEndpoint(config: ApplicationConfig,
                         opc: Opc) {
    private val stripsModel = StripsModel(false, opc)
    private val brightnessClient = BrightnessClient(config)
    private val brightnessService = BrightnessService(brightnessClient, stripsModel)

    fun getBrightness() = brightnessService.getBrightnessState()


    suspend fun setBrightness(state: BrightnessState.State) {
        brightnessService.updateBrightnessState(state)
    }
}