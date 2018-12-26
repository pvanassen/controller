package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.annotation.Scheduled
import nl.pvanassen.christmas.tree.controller.client.EspurnaClient
import nl.pvanassen.christmas.tree.controller.model.TreeState
import org.slf4j.LoggerFactory

@Context
class ShutdownWakeupService(private val espurnaClient: EspurnaClient,
                            private val animationLoader: AnimationLoader) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        espurnaClient.switchOn()
    }

    @Scheduled(cron = "0 55 6 * * *")
    fun wakePower() {
        logger.info("Waking up!")
        TreeState.state = TreeState.State.STARTING_UP
        espurnaClient.switchOn()
    }

    @Scheduled(cron = "0 0 7 * * *")
    fun startup() {
        logger.info("Starting up")
        TreeState.state = TreeState.State.STARTING_UP
        animationLoader.loadSunrise()
    }

    @Scheduled(cron = "0 5 7 * * *")
    fun forceOn() {
        if (TreeState.state != TreeState.State.ON) {
            logger.info("Current state: ${TreeState.state}, forcing on")
            TreeState.state = TreeState.State.ON
        }
    }

    @Scheduled(cron = "0 0 23 * * *")
    fun shuttingDown() {
        logger.info("Shutting down!")
        animationLoader.loadSunset()
        TreeState.state = TreeState.State.SHUTTING_DOWN
    }

    @Scheduled(cron = "0 5 23 * * *")
    fun shutdown() {
        logger.info("Shutdown. ")
        TreeState.state = TreeState.State.OFF
        espurnaClient.switchOff()
    }

}