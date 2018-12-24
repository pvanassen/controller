package nl.pvanassen.christmas.tree.controller.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import nl.pvanassen.christmas.tree.controller.scheduler.ShutdownWakeupService
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Controller("/state")
class StateController(private val shutdownWakeupService: ShutdownWakeupService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Get("/shutdown")
    fun shutdown():String {
        Single.just(0)
                .map { shutdownWakeupService.shuttingDown() }
                .delay(3, TimeUnit.MINUTES)
                .map { shutdownWakeupService.shutdown() }
                .doOnError {
                    logger.error("Error in shutdown", it)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
        return "ok"
    }

    @Get("/shutdown-now")
    fun shutdownNow():String {
        shutdownWakeupService.shutdown()
        return "ok"
    }

    @Get("/startup")
    fun startup():String {
        Single.just(0)
                .map { shutdownWakeupService.wakePower() }
                .delay(1, TimeUnit.MINUTES)
                .map { shutdownWakeupService.startup() }
                .subscribeOn(Schedulers.io())
                .subscribe()
        return "ok"
    }

}