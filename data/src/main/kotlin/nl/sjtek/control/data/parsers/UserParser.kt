package nl.sjtek.control.data.parsers

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import nl.sjtek.control.data.static.User

object UserParser {

    fun parse(input: String?): UserHolder {
        val type = object : TypeToken<Map<String, User>>() {}.type
        return try {
            UserHolder(Gson().fromJson(input, type))
        } catch (e: JsonParseException) {
            UserHolder(exception = e)
        }
    }
}