package nl.sjtek.control.core.modules

import com.google.gson.Gson
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.assistant.AssistantRequest
import nl.sjtek.control.core.assistant.AssistantResponse
import nl.sjtek.control.core.assistant.SensorRequest
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.ModuleUpdate
import nl.sjtek.control.data.response.Assistant
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.response.Temperature
import spark.Request
import spark.Spark

class Assistant(key: String) : Module(key) {
    override val response: Response
        get() = Assistant(key)

    private val gson = Gson()
    private var outsideHumidity = 0
    private var outsideTemp = 0
    private var insideHumidity = 0
    private var insideTemp = 0

    init {
        Bus.subscribe(this)
    }

    override fun initSpark() {
        Spark.post("/assistant", "application/json", this::onAction, gson::toJson)
    }

    @Handler
    fun onUpdate(event: ModuleUpdate) {
        val response = event.response
        if (response is Temperature) {
            outsideHumidity = response.outsideHumidity.toInt()
            outsideTemp = response.outsideTemperature.toInt()
            insideHumidity = response.insideHumidity.toInt()
            insideTemp = response.insideTemperature.toInt()
        }
    }

    private fun onAction(req: Request, res: spark.Response): AssistantResponse {
        res.header("Content-Type", "application/json")
        val body = req.body()
        val request = AssistantRequest.fromJson(body)
        return when (request) {
            is SensorRequest -> getTemperature(request)
            else -> getError()
        }
    }

    private fun getTemperature(request: SensorRequest): AssistantResponse {
        val (location, type) = request
        val value: String = when (location) {
            SensorRequest.Location.OUTSIDE -> when (type) {
                SensorRequest.Type.TEMPERATURE -> "$outsideTemp degrees"
                SensorRequest.Type.HUMIDITY -> "$outsideHumidity%"
            }
            SensorRequest.Location.INSIDE -> when (type) {
                SensorRequest.Type.TEMPERATURE -> "$insideTemp degrees"
                SensorRequest.Type.HUMIDITY -> "$insideHumidity%"
            }
        }
        return AssistantResponse("The $location $type is $value.")
    }

    private fun getError(): AssistantResponse = AssistantResponse("I don't know what you mean.")
}