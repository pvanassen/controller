package nl.pvanassen.christmas.tree.controller.service

import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.catalog.CatalogServicesRequest
import com.ecwid.consul.v1.health.HealthServicesRequest
import javax.inject.Singleton

@Singleton
class ConsulChristmasTreeService(private val consulClient: ConsulClient) {

    fun getChristmasTreeServices() = consulClient.getCatalogServices(CatalogServicesRequest.newBuilder().build())
                .value
                .filter { it.value.contains("christmas-tree") }
                .map {it -> Pair(it.value, consulClient.getHealthServices(it.key, HealthServicesRequest.newBuilder().build()).value)}
                .flatMap { it -> it.second.map { service -> Pair(it.first, service) }.asIterable() }
                .filter { it -> it.second.service.port > 32000 }
                .map { it -> Pair(it.first, it.second.service.port) }
                .map { it -> Pair(it.first, "http://192.168.0.2:${it.second}") }
}