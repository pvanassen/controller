package nl.pvanassen.led.model

import com.oracle.svm.core.annotate.Substitute
import com.oracle.svm.core.annotate.TargetClass
import nl.pvanassen.opc.Opc


object StripModelFactory {
    fun getStripModel(opc: Opc): StripsModel {
        return if (System.getenv()["app.debug"] == "true") {
            DebugStripsModel()
        } else {
            OpcStripsModel(false, opc)
        }
    }
}


@TargetClass(value = StripModelFactory::class)
internal class StripModelFactoryIsSubstitutions {
    @Substitute
    fun getStripModel(opc: Opc): StripsModel {
        return OpcStripsModel(false, opc)
    }
}