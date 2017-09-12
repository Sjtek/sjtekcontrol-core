package nl.sjtek.control.core.modules

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.TemperatureEvent
import nl.sjtek.control.core.net.HttpClient
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.response.Temperature
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Temperature(key: String) : Module(key) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor = ScheduledThreadPoolExecutor(1)
    override val response: Response
        get() = Temperature(key, insideTemp, insideHumidity, outsideTemp, outsideHumidity)

    private var insideTemp: Float = -100f
    private var insideHumidity: Float = -100f
    private var insideUpdate: Long = System.currentTimeMillis()
    private var outsideTemp: Float = -100f
    private var outsideHumidity: Float = -100f
    private var outsideUpdate: Long = System.currentTimeMillis()

    init {
        executor.scheduleAtFixedRate(this::updateInside, 0, 15, TimeUnit.MINUTES)
        executor.scheduleAtFixedRate(this::updateOutside, 0, 15, TimeUnit.MINUTES)
        Bus.subscribe(this)
    }

    override fun initSpark() {

    }

    @Handler
    fun onTemperatureUpdate(event: TemperatureEvent) {
        if (event.id == SENSOR_ID) {
            insideTemp = event.temperature
            insideHumidity = event.humidity
            insideUpdate = System.currentTimeMillis()
        }
        ResponseCache.post(this, true)
    }

    private fun updateInside() {
        if (System.currentTimeMillis() - insideUpdate > UPDATE_TIMEOUT) {
            insideTemp = -100f
            insideHumidity = -100f
        }
        ResponseCache.post(this, true)
    }

    private fun updateOutside() {
        logger.info("Updating temperature outside")
        val request = Request.Builder()
                .url(URL_OUTSIDE)
                .build()
        HttpClient.client.newCall(request).execute().use { response ->
            val jsonString = response.body()?.string() ?: ""
            try {
                val json = JsonParser().parse(jsonString).asJsonObject
                val currently = json.getAsJsonObject("currently")
                val temp = currently.get("temperature").asFloat
                val humidity = currently.get("humidity").asFloat

                outsideTemp = ((temp - 32) * 5 / 9)
                outsideHumidity = humidity
                outsideUpdate = System.currentTimeMillis()
            } catch (e: JsonParseException) {
                logger.error("Temperatue parsing error", e)
            }
        }

        if (System.currentTimeMillis() - outsideUpdate > UPDATE_TIMEOUT) {
            outsideTemp = -100f
            outsideHumidity = -100f
        }

        ResponseCache.post(this, true)
    }

    companion object {
        val SENSOR_ID = SettingsManager.settings.temperature.insideSensorId
        val URL_OUTSIDE = SettingsManager.settings.temperature.urlOutside.format(SettingsManager.settings.temperature.apiKey)
        const val UPDATE_TIMEOUT = 3000000 // 50 minutes
    }
}