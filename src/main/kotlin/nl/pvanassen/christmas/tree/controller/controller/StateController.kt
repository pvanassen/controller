package nl.pvanassen.christmas.tree.controller.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.reactivex.Single
import nl.pvanassen.christmas.tree.controller.model.TreeState
import nl.pvanassen.christmas.tree.controller.scheduler.ShutdownWakeupService
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Controller("/api/state", consumes = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN], produces = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN])
class StateController(private val shutdownWakeupService: ShutdownWakeupService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Post("/shutdown")
    fun shutdown(): Single<HttpStatus> {
        return Single.just(0)
                .map { shutdownWakeupService.shuttingDown() }
                .delay(3, TimeUnit.MINUTES)
                .map { shutdownWakeupService.shutdown() }
                .map { "ok" }
                .doOnError {
                    logger.error("Error in shutdown", it)
                }
                .map { HttpStatus.OK; }
    }

    @Post("/shutdown-now")
    fun shutdownNow():Single<String> {
        shutdownWakeupService.shutdown()
        return Single.just("ok")
    }

    @Post("/startup")
    fun startup(): Single<HttpStatus> {
        return Single.just(0)
                .map { shutdownWakeupService.wakePower() }
                .delay(1, TimeUnit.MINUTES)
                .map { shutdownWakeupService.startup() }
                .map { HttpStatus.OK }
                .doOnError {
                    logger.error("Error in startup", it)
                }
    }

    @Post("/fireworks")
    fun fireworks(): Single<HttpStatus> {
        return Single.just(0)
                .map { shutdownWakeupService.fireworks() }
                .map { HttpStatus.OK }
    }

    @Get
    fun getState(): Single<TreeState.State> = Single.just(TreeState.state)
}