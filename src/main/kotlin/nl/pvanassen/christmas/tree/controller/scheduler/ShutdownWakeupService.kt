package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.annotation.Scheduled
import nl.pvanassen.christmas.tree.controller.client.EspurnaClient
import nl.pvanassen.christmas.tree.controller.model.TreeState

@Context
class ShutdownWakeupService(private val espurnaClient: EspurnaClient,
                            private val animationLoader: AnimationLoader) {

    init {
        espurnaClient.switchOn()
    }

    @Scheduled(cron = "55 6 * * * *")
    fun wakePower() {
        TreeState.state = TreeState.State.STARTING_UP
        espurnaClient.switchOn()
    }

    @Scheduled(cron = "0 7 * * * *")
    fun startup() {
        animationLoader.loadSunrise()
    }

    @Scheduled(cron = "0 23 * * * *")
    fun shuttingDown() {
        animationLoader.loadSunset()
        TreeState.state = TreeState.State.SHUTTING_DOWN
    }

    @Scheduled(cron = "5 23 * * * *")
    fun shutdown() {
        TreeState.state = TreeState.State.OFF
        espurnaClient.switchOff()
    }

}