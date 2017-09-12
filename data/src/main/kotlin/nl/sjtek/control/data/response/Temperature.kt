package nl.sjtek.control.data.response

import java.io.Serializable

data class Temperature(
        override val key: String,
        val insideTemperature: Float,
        val insideHumidity: Float,
        val outsideTemperature: Float,
        val outsideHumidity: Float) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}