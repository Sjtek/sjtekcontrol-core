package nl.sjtek.control.core

import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.SwitchEvent
import nl.sjtek.control.core.response.Transformer
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.static.Lamp
import nl.sjtek.control.data.static.User
import okhttp3.Request
import spark.QueryParamsMap
import spark.Route

fun get(path: String, function: (req: spark.Request, res: spark.Response) -> Any) {
    spark.Spark.get(path, Transformer.contentType, Route { req, res ->
        res.header("Content-Type", Transformer.contentType)
        function(req, res)
    }, Transformer)
}

fun String.getRequest() = Request.Builder().url(this).build()

fun User?.getDefaultPlaylist(): String {
    val values = this?.playlists?.values ?: listOf()
    return if (values.isEmpty()) {
        SettingsManager.settings.music.defaultPlaylist
    } else {
        values.first()
    }
}

fun Lamp.turnOn(color: Color) = turnOn(color.r, color.g, color.b)
fun Lamp.turnOn(red: Int = -1, green: Int = -1, blue: Int = -1) {
    Bus.post(SwitchEvent(internalId, true, red, green, blue))
}

fun Lamp.turnOff() {
    Bus.post(SwitchEvent(internalId, false))
}

fun Lamp.hasSensor(): Boolean = this.sensorId != -1

fun Lamp.toggle(color: Color = Color()) = if (state) turnOff() else turnOn(color)
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