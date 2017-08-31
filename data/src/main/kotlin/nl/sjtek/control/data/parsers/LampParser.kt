package nl.sjtek.control.data.parsers

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import nl.sjtek.control.data.static.Lamp

object LampParser {

    fun parse(input: String?): Map<Int, Lamp>? {
        val type = object : TypeToken<Map<Int, Lamp>>() {}.type
        return try {
            Gson().fromJson(input, type)
        } catch (e: JsonParseException) {
            null
        }
    }
}