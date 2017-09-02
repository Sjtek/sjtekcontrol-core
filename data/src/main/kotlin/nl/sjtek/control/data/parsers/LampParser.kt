package nl.sjtek.control.data.parsers

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import nl.sjtek.control.data.static.Lamp

object LampParser {

    fun parse(input: String?): LampHolder {
        val type = object : TypeToken<Map<Int, Lamp>>() {}.type
        try {
            val lamps: Map<Int, Lamp> = Gson().fromJson(input, type)
            return LampHolder(lamps)
        } catch (e: JsonParseException) {
            return LampHolder(exception = e)
        }
    }
}