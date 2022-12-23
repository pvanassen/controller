package nl.pvanassen.led.state

import nl.pvanassen.led.animation.ByteArrayStoreService
import nl.pvanassen.led.model.TreeState
import nl.pvanassen.led.scheduler.ShutdownWakeupService

class StateEndpoint(private val shutdownWakeupService: ShutdownWakeupService,
                    private val byteArrayStoreService: ByteArrayStoreService) {

    fun shutdown(): TreeState.State {
        shutdownWakeupService.shuttingDown()
        return TreeState.state
    }

    suspend fun shutdownNow(): TreeState.State {
        shutdownWakeupService.shutdown()
        return TreeState.state
    }

    suspend fun startup(): TreeState.State {
        shutdownWakeupService.wakePower()
        return TreeState.state
    }

    fun fireworks(): TreeState.State {
        shutdownWakeupService.fireworks()
        return TreeState.state
    }

    fun forceOn(): TreeState.State {
        shutdownWakeupService.forceOn()
        byteArrayStoreService.reset()
        return TreeState.state
    }
}