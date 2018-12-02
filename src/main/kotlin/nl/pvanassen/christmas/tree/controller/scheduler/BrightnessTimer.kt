package nl.pvanassen.christmas.tree.controller.scheduler

import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.schedulers.Schedulers
import nl.pvanassen.christmas.tree.controller.client.BrightnessClient
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import nl.pvanassen.christmas.tree.controller.service.ConsulChristmasTreeService
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class BrightnessTimer(private val stripsModel: StripsModel,
                      private val consulChristmasTreeService: ConsulChristmasTreeService,
                      private val brightnessClient: BrightnessClient) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedRate = "5m")
    fun tuneBrightness() {
        val brightnessServer = consulChristmasTreeService.getChristmasTreeServices()
                .filter { it.first.contains("brightness") }
                .map { it.second }
                .firstOrNull()
        if (brightnessServer == null) {
            logger.error("Brightness server not found")
        }
        brightnessClient.getBrightness(brightnessServer!!)
                .map { stripsModel.setBrightness(it) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}