package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.schedulers.Schedulers
import nl.pvanassen.christmas.tree.controller.model.TreeState
import nl.pvanassen.christmas.tree.controller.service.EspurnaService
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.Month

@Context
class ShutdownWakeupService(private val espurnaService: EspurnaService,
                            private val animationLoader: AnimationLoader) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        espurnaService.switchOn()
    }

    @Scheduled(cron = "0 55 6 * * *")
    fun wakePower() {
        logger.info("Waking up!")
        TreeState.state = TreeState.State.STARTING_UP
        espurnaService.switchOn()
                .subscribeOn(Schedulers.io())
                .subscribe()
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
        if (LocalDate.now().month == Month.DECEMBER && LocalDate.now().dayOfMonth == 31) {
            logger.info("No shutdown, fireworks!")
            return
        }
        logger.info("Shutdown. ")
        TreeState.state = TreeState.State.OFF
        espurnaService.switchOff()
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    @Scheduled(cron = "0 59 23 31 12 *")
    fun fireworks() {
        TreeState.state = TreeState.State.FIREWORK
        logger.info("Fireworks!")
    }
}