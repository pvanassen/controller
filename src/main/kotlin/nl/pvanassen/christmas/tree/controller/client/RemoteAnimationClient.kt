package nl.pvanassen.christmas.tree.controller.client

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriTemplate
import io.reactivex.Single
import java.util.*
import javax.inject.Singleton

@Singleton
class RemoteAnimationClient(@Client("http://localhost") private val httpClient: RxHttpClient) {

    private val path = "/animation/{seconds}/{fps}"

    fun getAnimation(server:String, seconds:Int, fps:Int): Single<ByteArray> {
        val uri = UriTemplate.of(server + path).expand(mapOf(Pair("seconds", seconds), Pair("fps", fps)))
        val req = HttpRequest.GET<Any>(uri)
        val flowable = httpClient.retrieve(req, Argument.of(ByteArray::class.java))
        return flowable.collectInto(LinkedList<Byte>()) { t1, t2 -> t1.addAll(t2.toList()) }
                .map { it.toByteArray() }
    }
}
