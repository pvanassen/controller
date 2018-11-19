package nl.pvanassen.christmas.tree.controller.factory

import fr.bmartel.opc.OpcClient
import fr.bmartel.opc.PixelStrip
import io.micronaut.context.annotation.Factory
import javax.inject.Singleton

@Factory
class PixelStripFactory(private val strips:Int, private val ledsPerStrip:Int, private val opcClient: OpcClient) {

    @Singleton
    fun getPixelStrip():Array<PixelStrip> {
        val devices = Math.ceil(strips.toDouble() / 8).toInt()
        return (0 until devices).flatMap {
            val opcDevice = opcClient.addDevice()
            (0 until 8).map {
                opcDevice.addPixelStrip(it, ledsPerStrip)
            }
        }.toTypedArray()
    }

}