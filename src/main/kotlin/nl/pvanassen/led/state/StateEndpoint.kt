package nl.pvanassen.led.state

import nl.pvanassen.led.animation.ByteArrayStoreService
import nl.pvanassen.led.model.StripsModel
import nl.pvanassen.led.model.TreeState
import nl.pvanassen.led.scheduler.TimedActionsService

class StateEndpoint(private val timedActionsService: TimedActionsService,
                    private val byteArrayStoreService: ByteArrayStoreService,
                    private val stripsModel: StripsModel) {

    fun shutdown(): TreeState.State {
        timedActionsService.shuttingDown()
        return TreeState.state
    }

    suspend fun shutdownNow(): TreeState.State {
        timedActionsService.shutdown()
        return TreeState.state
    }

    suspend fun startup(): TreeState.State {
        timedActionsService.wakePower()
        return TreeState.state
    }

    suspend fun fireworks(): TreeState.State {
        timedActionsService.fireworks()
        stripsModel.setBrightness(1f)
        return TreeState.state
    }

    fun forceOn(): TreeState.State {
        timedActionsService.forceOn()
        byteArrayStoreService.reset()
        return TreeState.state
    }
}