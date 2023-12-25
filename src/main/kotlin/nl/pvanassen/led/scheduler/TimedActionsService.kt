package nl.pvanassen.led.scheduler

import com.ucasoft.kcron.KCron
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import nl.pvanassen.led.animation.AnimationClients
import nl.pvanassen.led.animation.AnimationLoader
import nl.pvanassen.led.brightness.BrightnessState
import nl.pvanassen.led.model.TreeState
import nl.pvanassen.led.power.TasmotaClient
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

class TimedActionsService(
    config: ApplicationConfig,
    tasmotaClient: TasmotaClient,
    private val animationLoader: AnimationLoader,
    private val animationClients: AnimationClients
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val executor = Executors.newScheduledThreadPool(1)

    private val wakePowerCron = config.tryGetString("app.cron.power-on")!!

    private val fullOn = config.tryGetString("app.cron.full-on")!!

    private val shuttingDownCron = config.tryGetString("app.cron.shutting-down")!!

    private val shutdownCron = config.tryGetString("app.cron.shutdown")!!

    private val fireworkCron = config.tryGetString("app.cron.firework")!!

    init {
        TreeState.registerCallback { oldState, newState ->
            runBlocking {
                animationLoader.reset()
                if (newState == TreeState.State.OFF) {
                    tasmotaClient.switchOff()
                } else {
                    tasmotaClient.switchOn()
                }
                if (newState == TreeState.State.STARTING_UP) {
                    animationLoader.loadSunrise()
                }
                if (newState == TreeState.State.SHUTTING_DOWN) {
                    animationLoader.loadSunset()
                }
                if (newState == TreeState.State.FIREWORK) {
                    BrightnessState.state = BrightnessState.State.MAX
                }
                if (oldState == TreeState.State.FIREWORK && newState != TreeState.State.FIREWORK) {
                    BrightnessState.state = BrightnessState.State.AUTO
                }
            }
        }
    }

    fun start() {
        executor.scheduleWithFixedDelay(
            { runBlocking { wakePower() } },
            nextRunInMinutes(wakePowerCron),
            24.hours.inWholeMinutes,
            TimeUnit.MINUTES
        )
        executor.scheduleWithFixedDelay(
            { forceOn() },
            nextRunInMinutes(fullOn),
            24.hours.inWholeMinutes,
            TimeUnit.MINUTES
        )
        executor.scheduleWithFixedDelay(
            { shuttingDown() },
            nextRunInMinutes(shuttingDownCron),
            24.hours.inWholeMinutes,
            TimeUnit.MINUTES
        )
        executor.scheduleWithFixedDelay(
            { shutdown() },
            nextRunInMinutes(shutdownCron),
            24.hours.inWholeMinutes,
            TimeUnit.MINUTES
        )
        executor.scheduleAtFixedRate({ loadTimedTasks() }, 0, 1, TimeUnit.MINUTES)
        executor.schedule({ fireworks() }, nextRunInMinutes(fireworkCron), TimeUnit.MINUTES)
    }

    private fun loadTimedTasks() {
        val nameCronMap = animationClients.removeNameCronEntries()
        nameCronMap.forEach { (name, cron) ->
            try {
                val nextRuns = KCron.parseAndBuild(cron).nextRunList(5000)
                    .filter { it.toJavaLocalDateTime().isBefore(java.time.LocalDateTime.now().plusYears(1)) }
                    .map { (it.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()).inWholeMinutes }
                nextRuns.forEach {
                    executor.schedule({ runBlocking { animationLoader.loadCron(name) } }, it, TimeUnit.MINUTES)
                }
            } catch (e: Exception) {
                logger.error("Error scheduling $name with cron '$cron'")
            }
        }
    }

    private fun nextRunInMinutes(cron: String) =
        (KCron.parseAndBuild(cron).nextRun!!.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()).inWholeMinutes

    fun wakePower() {
        logger.info("Waking up!")
        TreeState.state = TreeState.State.STARTING_UP
    }

    fun forceOn() {
        if (TreeState.state != TreeState.State.ON) {
            logger.info("Current state: ${TreeState.state}, forcing on")
            TreeState.state = TreeState.State.ON
        }
    }

    fun shuttingDown() {
        logger.info("Shutting down!")
        TreeState.state = TreeState.State.SHUTTING_DOWN
    }

    fun shutdown() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (now.month == Month.DECEMBER && now.dayOfMonth == 31) {
            logger.info("No shutdown, fireworks!")
            return
        }
        logger.info("Shutdown. ")
        TreeState.state = TreeState.State.OFF
    }

    fun fireworks() {
        TreeState.state = TreeState.State.FIREWORK
        logger.info("Fireworks!")
    }
}