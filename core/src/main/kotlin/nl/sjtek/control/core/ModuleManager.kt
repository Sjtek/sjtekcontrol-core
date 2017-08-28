package nl.sjtek.control.core

import nl.sjtek.control.core.events.AMQP
import nl.sjtek.control.core.modules.*
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.core.settings.User
import org.slf4j.LoggerFactory
import spark.Spark.path
import spark.kotlin.after
import spark.Request as SparkRequest
import spark.Response as SparkResponse

object ModuleManager {

    private val settings = Settings()
    private val logger = LoggerFactory.getLogger(javaClass)
    private val modules: Map<String, Module> = mapOf(
            "lights" to Lights("lights", settings),
            "nightmode" to NightMode("nightmode", settings),
            "tv" to TV("tv", settings),
            "audio" to Audio("audio", settings),
            "coffee" to Coffee("coffee", settings),
            "music" to Music("music", settings)
    )

    fun init() {
        AMQP.init()
        val settings = Settings()
        modules.values.forEach {
            ResponseCache.post(it)
        }

        if (settings.spark) {
            logger.info("Spark enabled")
            path("/api") {
                modules.forEach {
                    logger.info("Spark init ${it.key}")
                    it.value.initSpark()
                }
            }
            after("/api/*") { ->
                response.header("Content-Type", "application/json")
                response.body(ResponseCache.json)
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