package nl.pvanassen.christmas.tree.controller.service

import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import io.micronaut.management.endpoint.health.HealthLevelOfDetail
import io.micronaut.management.health.aggregator.RxJavaHealthAggregator
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import io.micronaut.management.health.indicator.discovery.DiscoveryClientHealthIndicator
import io.micronaut.runtime.ApplicationConfiguration
import org.reactivestreams.Publisher

@Replaces
@Primary
class CustomJavaHealthAggregator(applicationConfiguration: ApplicationConfiguration?) : RxJavaHealthAggregator(applicationConfiguration) {
    override fun aggregate(indicators: Array<out HealthIndicator>?, healthLevelOfDetail: HealthLevelOfDetail?): Publisher<HealthResult> {
        return super.aggregate(indicators?.filter { it !is DiscoveryClientHealthIndicator }?.toTypedArray(), healthLevelOfDetail)
    }
}