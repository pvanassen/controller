package nl.pvanassen.christmas.tree.controller.factory

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import nl.pvanassen.christmas.tree.animation.common.model.TreeModel
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import nl.pvanassen.opc.Opc
import javax.inject.Singleton

@Factory
class PixelStripFactory {

    @Bean
    @Singleton
    fun getOpcClient(@Property(name = "christmas.tree.opc.hostname")hostname:String,
                     @Property(name = "christmas.tree.opc.port")port:Int,
                     treeModel: TreeModel):Opc {
        val opcBuilder = Opc.builder(hostname, port)
        val devices = Math.ceil(treeModel.strips / 8.0).toInt()
        (0 until devices).forEach {
            val opcDevice = opcBuilder.addDevice()
            (0 until 8).forEach {
                opcDevice.addPixelStrip(treeModel.ledsPerStrip)
            }
        }
        return opcBuilder.build()
    }

    @Bean
    @Singleton
    fun getPixelStrips(treeModel: TreeModel, opc: Opc): StripsModel {
        return StripsModel(treeModel.strips * treeModel.ledsPerStrip == 0, opc)
    }
}