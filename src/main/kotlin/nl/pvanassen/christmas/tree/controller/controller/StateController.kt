package nl.pvanassen.christmas.tree.controller.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.reactivex.Single
import nl.pvanassen.christmas.tree.controller.scheduler.ShutdownWakeupService
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Controller("/state")
class StateController(private val shutdownWakeupService: ShutdownWakeupService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Post("/shutdown")
    fun shutdown(): HttpStatus {
        return Single.just(0)
                .map { shutdownWakeupService.shuttingDown() }
                .delay(3, TimeUnit.MINUTES)
                .map { shutdownWakeupService.shutdown() }
                .map { "ok" }
                .doOnError {
                    logger.error("Error in shutdown", it)
                }
                .map { HttpStatus.OK; }
                .blockingGet()
    }

    @Post("/shutdown-now")
    fun shutdownNow():Single<String> {
        shutdownWakeupService.shutdown()
        return Single.just("ok")
    }

    @Post("/startup")
    fun startup(): HttpStatus {
        return Single.just(0)
                .map { shutdownWakeupService.wakePower() }
                .delay(1, TimeUnit.MINUTES)
                .map { shutdownWakeupService.startup() }
                .map { HttpStatus.OK; }
                .blockingGet()
    }

}