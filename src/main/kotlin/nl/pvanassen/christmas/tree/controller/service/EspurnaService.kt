package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.context.annotation.Property
import io.reactivex.Single
import nl.pvanassen.christmas.tree.controller.client.EspurnaClient
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class EspurnaService(@Property(name = "espurna.apikey") private val espurnaApiKey:String,
                     private val espurnaClient: EspurnaClient) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun switchOff(): Single<Boolean> =
        espurnaClient.toggleSwitch(espurnaApiKey, 0)
                .map { it["relay/0"]!!.toString() }
                .map { it.toBoolean() }
                .onErrorReturn {
                    logger.info("Failed to switch off power")
                    false }

    fun switchOn(): Single<Boolean> =
        espurnaClient.toggleSwitch(espurnaApiKey, 1)
                .map { it["relay/0"]!!.toString() }
                .map { it.toBoolean() }
                .onErrorReturn {
                    logger.info("Failed to switch on power")
                    false }

}