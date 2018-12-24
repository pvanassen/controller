package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.aop.Interceptor
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Primary
import io.micronaut.discovery.consul.client.v1.HealthEntry
import io.micronaut.discovery.consul.client.v1.`AbstractConsulClient$Intercepted`
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory

@Primary
abstract class CustomConsulClient(p0: BeanContext?, p1: Array<out Interceptor<Any, Any>>?) : `AbstractConsulClient$Intercepted`(p0, p1) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        logger.info("Init!")
    }

    override fun getHealthyServices(p0: String?, p1: Boolean?, p2: String?, p3: String?): Publisher<*> {
        return Flowable.fromPublisher(super.getHealthyServices(p0, p1, p2, p3))
                .onErrorReturn {
                    if (!p0!!.contains("christmas-tree")) {
                        logger.info("Got an error on $p0")
                    }
                    emptyList<HealthEntry>()
                }
    }

}
