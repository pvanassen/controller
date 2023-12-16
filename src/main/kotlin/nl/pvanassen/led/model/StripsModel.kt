package nl.pvanassen.led.model

interface StripsModel {
    suspend fun push()
    
    fun setPixelColor(pixel: Int, color: Int)

    suspend fun setBrightness(brightness: Float)
}

