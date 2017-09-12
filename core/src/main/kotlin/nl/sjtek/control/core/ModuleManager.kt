package nl.sjtek.control.core

import nl.sjtek.control.core.events.AMQP
import nl.sjtek.control.core.modules.*
import nl.sjtek.control.core.net.SjtekWebSocket
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.staticdata.User
import org.slf4j.LoggerFactory
import spark.Spark.path
import spark.Spark.webSocket
import spark.kotlin.after
import spark.Request as SparkRequest
import spark.Response as SparkResponse

object ModuleManager {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val modules: Map<String, Module> = mapOf(
            "base" to Base("base"),
            "lights" to Lights("lights"),
            "nightmode" to NightMode("nightmode"),
            "tv" to TV("tv"),
            "audio" to Audio("audio"),
            "coffee" to Coffee("coffee"),
            "music" to Music("music"),
            "temperature" to Temperature("temperature"),
            "assistant" to Assistant("assistant"),
            "color" to ColorSwitcher("color")
    )

    fun init() {
        AMQP.init()
        modules.values.forEach {
            ResponseCache.post(it)
        }

        if (SettingsManager.settings.spark) {
            logger.info("Spark enabled")
            webSocket("/api/ws", SjtekWebSocket::class.java)
            path("/api") {
                modules.forEach {
                    logger.info("Spark init ${it.key}")
                    it.value.initSpark()
                }
            }
            after {
                if (response.body() == null) {
                    logger.warn("Empty body")
                }
            }
        }
    }

    fun isEnabled(user: User? = null): Boolean {
        modules.values.forEach {
            if (it.isEnabled(user)) return true
        }
        return false
    }
}