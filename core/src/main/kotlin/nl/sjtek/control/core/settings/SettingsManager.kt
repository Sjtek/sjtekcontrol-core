package nl.sjtek.control.core.settings

import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File

object SettingsManager {
    val settings: Settings

    init {
        val logger = LoggerFactory.getLogger(javaClass)
        logger.info("Loading settings")
        val json: String = try {
            File("/etc/sjtekcontrol-core/config.json").readText()
        } catch (e: Exception) {
            ""
        }
        val gson = GsonBuilder().setPrettyPrinting().create()
        if (!json.isBlank()) {
            var parsed: Settings? = null
            try {
                parsed = gson.fromJson<Settings>(json, Settings::class.java)
            } catch (e: Exception) {
                logger.error("Error loading settings", e)

            }

            if (parsed == null) {
                parsed = Settings()
                println("Missing settings, printing default ones")
                println(gson.toJson(parsed))
            }
            settings = parsed
        } else {
            settings = Settings()
            println("Missing settings, printing default ones")
            println(gson.toJson(settings))
        }
    }
}