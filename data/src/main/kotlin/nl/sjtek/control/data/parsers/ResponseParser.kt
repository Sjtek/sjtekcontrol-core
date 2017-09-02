package nl.sjtek.control.data.parsers

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import nl.sjtek.control.data.response.Response
import java.lang.reflect.Type

object ResponseParser {
    fun parse(input: String?): ResponseHolder {
        val type = object : TypeToken<Map<String, Response>>() {}.type
        val gson = GsonBuilder().registerTypeAdapter(Response::class.java, ResponseAdapter()).create()
        return try {
            val holder = ResponseHolder(gson.fromJson<Map<String, Response>>(input, type))
            holder.test()
            holder
        } catch (e: Exception) {
            ResponseHolder(exception = e)
        }
    }

    private class ResponseAdapter : JsonDeserializer<Response> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Response? {
            val jsonObject = json.asJsonObject ?: throw JsonParseException("Invalid json")
            val type = jsonObject.get("type")?.asString ?: throw JsonParseException("Response is missing a string")
            try {
                return context.deserialize(json, Class.forName(type))
            } catch (e: ClassNotFoundException) {
                throw JsonParseException("Unknown element type $type", e)
            }
        }
    }
}