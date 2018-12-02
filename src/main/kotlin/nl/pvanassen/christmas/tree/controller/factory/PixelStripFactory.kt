package nl.pvanassen.christmas.tree.controller.factory

import fr.bmartel.opc.OpcClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import nl.pvanassen.christmas.tree.animation.common.model.TreeModel
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import javax.inject.Singleton

@Factory
class PixelStripFactory {

    @Bean
    @Singleton
    fun getOpcClient(@Property(name = "christmas.tree.opc.hostname")hostname:String,
                     @Property(name = "christmas.tree.opc.port")port:Int):OpcClient = OpcClient(hostname, port)

    @Bean
    @Singleton
    fun getPixelStrips(treeModel: TreeModel, opcClient: OpcClient): StripsModel {
        val devices = Math.ceil(treeModel.strips / 8.0).toInt()
        val array = (0 until devices).flatMap {
            val opcDevice = opcClient.addDevice()
            (0 until 8).map {strip ->
                opcDevice.addPixelStrip(strip, treeModel.ledsPerStrip)
            }
        }.toTypedArray()
        return StripsModel(array, opcClient)
    }
}