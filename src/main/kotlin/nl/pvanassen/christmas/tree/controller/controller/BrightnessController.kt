package nl.pvanassen.christmas.tree.controller.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.reactivex.Single
import nl.pvanassen.christmas.tree.controller.model.BrightnessState
import nl.pvanassen.christmas.tree.controller.model.StripsModel
import nl.pvanassen.christmas.tree.controller.scheduler.BrightnessService

@Controller("/api/brightness")
class BrightnessController(private val stripsModel: StripsModel,
                           private val brightnessService: BrightnessService) {

    @Post("max")
    fun maxBrightness(): HttpStatus {
        BrightnessState.state = BrightnessState.State.MAX
        stripsModel.setBrightness(.8f)
        return HttpStatus.OK
    }

    @Post("min")
    fun minBrightness(): HttpStatus {
        BrightnessState.state = BrightnessState.State.MIN
        stripsModel.setBrightness(0.1f)
        return HttpStatus.OK
    }

    @Post("auto")
    fun autoBrightness(): HttpStatus {
        BrightnessState.state = BrightnessState.State.AUTO
        brightnessService.autoAdjustBrightness()
        return HttpStatus.OK
    }

    @Get("/")
    fun getBrightness(): Single<Float> {
        return brightnessService.getBrightness()
    }
}