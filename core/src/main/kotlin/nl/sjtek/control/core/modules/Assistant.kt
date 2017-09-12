package nl.sjtek.control.core.modules

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.assistant.*
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.CoffeeEvent
import nl.sjtek.control.core.events.ModuleUpdate
import nl.sjtek.control.core.executeCommand
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.actions.Actions
import nl.sjtek.control.data.response.Assistant
import nl.sjtek.control.data.response.NightMode
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.response.Temperature
import org.slf4j.LoggerFactory
import spark.Request
import spark.Spark
import spark.kotlin.halt

class Assistant(key: String) : Module(key) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val code = SettingsManager.settings.assistant.code

    override val response: Response
        get() = Assistant(key)

    private val gson = Gson()
    private var outsideHumidity = 0
    private var outsideTemp = 0
    private var insideHumidity = 0
    private var insideTemp = 0
    private var nightMode = false

    init {
        Bus.subscribe(this)
    }

    override fun initSpark() {
        Spark.post("/assistant", "application/json", this::onAction, gson::toJson)
        Spark.post("/webhook", "application/json", this::onWebHook, gson::toJson)
    }

    @Handler
    fun onUpdate(event: ModuleUpdate) {
        val response = event.response
        if (response is Temperature) {
            outsideHumidity = response.outsideHumidity.toInt()
            outsideTemp = response.outsideTemperature.toInt()
            insideHumidity = response.insideHumidity.toInt()
            insideTemp = response.insideTemperature.toInt()
        } else if (response is NightMode) {
            nightMode = response.enabled
        }
    }

    private fun onWebHook(req: Request, res: spark.Response): String {
        try {
            val json = JsonParser().parse(req.body()).asJsonObject
            val givenCode = json["code"].asString
            if (givenCode != code) {
                logger.warn("Inavlid IFTTT code used")
                halt(401, "Invalid code")
            }
            json["actions"].asJsonArray.forEach {
                val action = it.asString
                logger.info("IFTTT: $action")
                action.executeCommand()
            }
        } catch (e: JsonParseException) {
            logger.warn("Invalid IFTTT request")
            halt(400, "Bad request")
        }
        return "OK"
    }

    private fun onAction(req: Request, res: spark.Response): AssistantResponse {
        res.header("Content-Type", "application/json")
        val body = req.body()
        val request = AssistantRequest.fromJson(body)
        return when (request) {
            is SensorRequest -> getTemperature(request)
            is CoffeeRequest -> getCoffee(request)
            is NightModeRequest -> setNightMode(request)
            is TVRequest -> setTV(request)
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

    private fun getCoffee(request: CoffeeRequest): AssistantResponse {
        Bus.post(CoffeeEvent())
        return AssistantResponse("Sure, the machine is warming up.")
    }

    private fun setNightMode(request: NightModeRequest): AssistantResponse {
        if (request.enabled) {
            Actions.nightMode.enable().executeCommand()
            return AssistantResponse("Good night sir")
        } else {
            Actions.nightMode.disable().executeCommand()
            return AssistantResponse("Good morning sir")
        }
    }

    private fun setTV(request: TVRequest): AssistantResponse {
        Actions.tv.turnOff().executeCommand()
        return AssistantResponse("Turning the tv off")
    }

    private fun getError(): AssistantResponse = AssistantResponse("I don't know what you mean.")
}