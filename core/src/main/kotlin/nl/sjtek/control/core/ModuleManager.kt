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
            "base" to Base("base"),
            "lights" to Lights("lights"),
            "nightmode" to NightMode("nightmode"),
            "tv" to TV("tv"),
            "audio" to Audio("audio"),
            "coffee" to Coffee("coffee"),
            "music" to Music("music")
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