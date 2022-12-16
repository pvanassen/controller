package nl.pvanassen.led.brightness

import nl.pvanassen.christmas.tree.controller.model.BrightnessState
import nl.pvanassen.christmas.tree.controller.model.StripsModel

class BrightnessService(private val brightnessClient: BrightnessClient,
                        private val stripsModel: StripsModel) {

    suspend fun updateBrightnessState(state: BrightnessState.State) {
        BrightnessState.state = state
        when(state) {
            BrightnessState.State.AUTO -> stripsModel.setBrightness(brightnessClient.getBrightness())
            BrightnessState.State.MAX -> stripsModel.setBrightness(0.8f)
            BrightnessState.State.MIN -> stripsModel.setBrightness(0.1f)
        }
    }

    fun getBrightnessState() = BrightnessState.state

}