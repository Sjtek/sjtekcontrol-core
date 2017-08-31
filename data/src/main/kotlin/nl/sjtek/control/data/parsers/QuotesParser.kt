package nl.sjtek.control.data.parsers

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken

object QuotesParser {

    fun parse(input: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(input, type)
        } catch (e: JsonParseException) {
            null
        }
    }
}