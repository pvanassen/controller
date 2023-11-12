package nl.pvanassen.led.model

interface StripsModel {
    suspend fun push()
    fun isEmpty(): Boolean
    fun setPixelColor(pixel: Int, color: Int)

    suspend fun setBrightness(brightness: Float)
}

