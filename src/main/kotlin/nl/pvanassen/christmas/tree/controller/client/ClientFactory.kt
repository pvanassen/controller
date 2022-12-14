package nl.pvanassen.christmas.tree.controller.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*

object ClientFactory {
    fun createClient() =
        HttpClient(CIO) {
        }

}