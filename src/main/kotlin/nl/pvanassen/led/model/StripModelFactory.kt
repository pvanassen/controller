package nl.pvanassen.led.model

import com.oracle.svm.core.annotate.Substitute
import com.oracle.svm.core.annotate.TargetClass
import io.ktor.server.config.*
import nl.pvanassen.led.mqtt.MqttService
import nl.pvanassen.opc.Opc


object StripModelFactory {
    fun getStripModel(opc: Opc, mqttService: MqttService, config: ApplicationConfig): StripsModel {
        return if (config.property("app.debug").getString() == "true") {
            DebugStripsModel()
        } else {
            OpcStripsModel(opc, mqttService)
        }
    }
}


@TargetClass(value = StripModelFactory::class)
internal class StripModelFactoryIsSubstitutions {
    @Substitute
    fun getStripModel(opc: Opc, mqttService: MqttService, config: ApplicationConfig): StripsModel {
        return OpcStripsModel(opc, mqttService)
    }
}