package nl.pvanassen.christmas.tree.controller.client

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.reactivex.Single

class BrightnessClient(@Client("http://localhost") private val httpClient: RxHttpClient) {

    fun getBrightness(server:String): Single<Float> {
        val uri = "$server/brightness"
        val req = HttpRequest.GET<Any>(uri)
        val flowable = httpClient.retrieve(req, Argument.of(String::class.java))
        return flowable.singleOrError()
                .map { it.toFloat() }
    }

}