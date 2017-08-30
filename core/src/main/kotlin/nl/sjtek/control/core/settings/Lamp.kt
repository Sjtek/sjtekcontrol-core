package nl.sjtek.control.core.settings

import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.SwitchEvent
import spark.QueryParamsMap

data class Lamp(
        val name: String,
        val visibleName: String,
        val internalId: Int,
        val rgb: Boolean,
        val room: String,
        var state: Boolean = false,
        val sensorId: Int = -1,
        val owner: String? = null,
        val onlyOff: Boolean = false) {

    val hasSensor: Boolean = sensorId != -1

    fun turnOn(color: Color) = turnOn(color.r, color.g, color.b)
    fun turnOn(red: Int = -1, green: Int = -1, blue: Int = -1) {
        Bus.post(SwitchEvent(internalId, true, red, green, blue))
    }

    fun turnOff() {
        Bus.post(SwitchEvent(internalId, false))
    }

    fun toggle(color: Color = Color()) = if (state) turnOff() else turnOn(color)
    data class Color(val r: Int = -1, val g: Int = -1, val b: Int = -1) {
        companion object {
            fun parseQuery(query: QueryParamsMap): Color {
                if (query.hasKey("hex")) {
                    val hexValue = query["hex"].value()
                    return try {
                        val r = hexValue.substring(0, 2).toInt(radix = 16)
                        val g = hexValue.substring(2, 4).toInt(radix = 16)
                        val b = hexValue.substring(4, 6).toInt(radix = 16)
                        Color(r, g, b)
                    } catch (e: Exception) {
                        Color()
                    }

                } else {
                    val sR = query["r"].integerValue() ?: return Color()
                    val sG = query["g"].integerValue() ?: return Color()
                    val sB = query["b"].integerValue() ?: return Color()
                    return Color(sR, sG, sB)
                }
            }
        }
    }
}

data class LampHolder(val lamps: Map<Int, Lamp>)
