package nl.pvanassen.christmas.tree.controller.client

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable
import io.reactivex.Single

@Client(
        value = "http://\${espurna.host}",
        path = "/api/relay/0")
@Retryable(
    attempts = "3",
    delay = "500ms")
interface EspurnaClient {

    @Put(produces = [MediaType.APPLICATION_FORM_URLENCODED])
    fun toggleSwitch(apikey:String, value:Int): Single<Map<String, Any>>
}