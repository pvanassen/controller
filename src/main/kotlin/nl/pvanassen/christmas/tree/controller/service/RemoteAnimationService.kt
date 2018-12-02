package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.schedulers.Schedulers
import nl.pvanassen.christmas.tree.controller.client.RemoteAnimationClient
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.PostConstruct
import kotlin.concurrent.withLock

@Context
class RemoteAnimationService(private val remoteAnimationClient: RemoteAnimationClient,
                             private val consulChristmasTreeService: ConsulChristmasTreeService) {

    private val lock = ReentrantLock()

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val normalAnimationNameUrl: MutableMap<String, String> = HashMap()

    private val random = Random()

    @Scheduled(fixedRate = "1m")
    @PostConstruct
    fun discoverAnimations() {
        val services = consulChristmasTreeService.getChristmasTreeServices()
                .filter { it.first.contains("animation") }
                .map { it -> Pair(it.first.filter { it != "christmas-tree" }, it.second) }
                .map { it -> Pair(it.first.filter { it != "animation" }, it.second) }

        val normalAnimations = services
                .filter { it.first.size == 1 }
                .map { Pair(it.first[0] ?: "unknown", it.second) }
                .toMap()

        lock.withLock {
            normalAnimationNameUrl.clear()
            normalAnimationNameUrl.putAll(normalAnimations)
        }
    }

    fun getFramesFromRandomAnimation(seconds:Int, fps:Int, callback: (ByteArray) -> Unit) {
        // [random.nextInt(normalAnimationClients.size)]
        val animationName = normalAnimationNameUrl.keys.toList()[random.nextInt(normalAnimationNameUrl.size)]
        logger.info("Using $animationName")
        remoteAnimationClient.getAnimation(normalAnimationNameUrl[animationName]!!, seconds, fps)
                .map { callback(it) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}