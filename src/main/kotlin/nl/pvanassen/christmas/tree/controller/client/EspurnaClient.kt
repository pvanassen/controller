package nl.pvanassen.christmas.tree.controller.client

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client

class EspurnaClient(@Client("http://localhost") private val httpClient: HttpClient,
                    @Property(name = "espurna.host") private val espurnaHostname: String,
                    @Property(name = "espurna.apikey") private val espurnaApiKey:String) {

    fun switchOff():String {
        val req = HttpRequest.PUT<Request>("http://$espurnaHostname/api/relay/0", Request(espurnaApiKey, 0))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        httpClient.toBlocking().exchange<Request, Any>(req)
        return "ok"
    }

    fun switchOn():String {
        val req = HttpRequest.PUT<Request>("http://$espurnaHostname/api/relay/0", Request(espurnaApiKey, 1))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        httpClient.toBlocking().exchange<Request, Any>(req)
        return "ok"
    }

    private class Request(val apikey:String, val value:Int)
}