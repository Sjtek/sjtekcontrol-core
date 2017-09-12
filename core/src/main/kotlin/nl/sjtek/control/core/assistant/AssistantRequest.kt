package nl.sjtek.control.core.assistant

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.slf4j.LoggerFactory

abstract class AssistantRequest {

    companion object {
        private val logger = LoggerFactory.getLogger(AssistantRequest::class.java)
        fun fromJson(jsonString: String): AssistantRequest? {
            try {
                val json = JsonParser().parse(jsonString).asJsonObject
                val intent = json["result"].asJsonObject["metadata"].asJsonObject["intentName"].asString
                return when (intent) {
                    "Sensors" -> toSensor(json)
                    "Coffee" -> toCoffee(json)
                    "NightMode" -> toNightMode(json)
                    "TV" -> toTV(json)
                    else -> null
                }
            } catch (e: JsonParseException) {
                logger.error("Parse error", e)
                return null
            }
        }

        private fun toSensor(json: JsonObject): SensorRequest? {
            val params = json["result"].asJsonObject["parameters"].asJsonObject
            val location = when (params["sensor-location"].asString) {
                "inside" -> SensorRequest.Location.INSIDE
                "outside" -> SensorRequest.Location.OUTSIDE
                else -> return null
            }
            val type = when (params["sensor-type"].asString) {
                "temperature" -> SensorRequest.Type.TEMPERATURE
                "humidity" -> SensorRequest.Type.HUMIDITY
                else -> return null
            }
            return SensorRequest(location, type)
        }

        private fun toCoffee(json: JsonObject): CoffeeRequest? = CoffeeRequest()

        private fun toNightMode(json: JsonObject): NightModeRequest? {
            val params = json["result"].asJsonObject["parameters"].asJsonObject
            return when (params["nightmode-state"].asString) {
                "enable" -> NightModeRequest(true)
                "disable" -> NightModeRequest(false)
                else -> null
            }
        }

        private fun toTV(json: JsonObject): TVRequest? = TVRequest()
    }
}