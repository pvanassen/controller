package nl.pvanassen.christmas.tree.controller.factory

import com.ecwid.consul.v1.ConsulClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import javax.inject.Singleton

@Factory
class ConsulClientFactory {

    @Bean
    @Singleton
    fun getConsulClient(@Property(name = "consul.client.default-zone") consulUrl:String) = ConsulClient(consulUrl)
}