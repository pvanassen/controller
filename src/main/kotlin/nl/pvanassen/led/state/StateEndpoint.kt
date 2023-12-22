package nl.pvanassen.led.state

import nl.pvanassen.led.animation.ByteArrayStoreService
import nl.pvanassen.led.model.TreeState
import nl.pvanassen.led.scheduler.TimedActionsService

class StateEndpoint(
    private val timedActionsService: TimedActionsService,
    private val byteArrayStoreService: ByteArrayStoreService
) {

    fun shutdown(): TreeState.State {
        timedActionsService.shuttingDown()
        return TreeState.state
    }

    fun shutdownNow(): TreeState.State {
        timedActionsService.shutdown()
        return TreeState.state
    }

    fun startup(): TreeState.State {
        timedActionsService.wakePower()
        return TreeState.state
    }

    fun fireworks(): TreeState.State {
        timedActionsService.fireworks()
        return TreeState.state
    }

    fun forceOn(): TreeState.State {
        TreeState.state = TreeState.State.ON
        timedActionsService.forceOn()
        byteArrayStoreService.reset()
        return TreeState.state
    }
}