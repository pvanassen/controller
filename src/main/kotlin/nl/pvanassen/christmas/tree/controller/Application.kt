package nl.pvanassen.christmas.tree.controller

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("nl.pvanassen.christmas.tree.controller.service",
                        "nl.pvanassen.christmas.tree.controller.scheduler",
                        "nl.pvanassen.christmas.tree.controller.endpoint",
                        "nl.pvanassen.christmas.tree.controller.client",
                        "nl.pvanassen.christmas.tree.controller.model",
                        "nl.pvanassen.christmas.tree.controller.factory")
                .mainClass(Application.javaClass)
                .start()
    }
}