package nl.sjtek.control.core.settings

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import nl.sjtek.control.data.parsers.LampHolder
import nl.sjtek.control.data.parsers.QuotesHolder
import nl.sjtek.control.data.parsers.UserHolder
import nl.sjtek.control.data.static.Lamp
import nl.sjtek.control.data.static.User
import org.slf4j.LoggerFactory
import java.io.File

object SettingsManager {
    private const val PATH_CONFIG = "/var/sjtekcontrol/config.json"
    private const val PATH_USERS = "/var/sjtekcontrol/users.json"
    private const val PATH_QUOTES = "/var/sjtekcontrol/quotes.json"
    private const val PATH_LAMPS = "/var/sjtekcontrol/lamps.json"
    private val logger = LoggerFactory.getLogger(javaClass)

    val settings: Settings
    val users: List<User>
    val quotes: List<String>
    val lamps: Map<Int, Lamp>

    val jsonUsers: String
        get() = GsonBuilder().setPrettyPrinting().create().toJson(users)
    val jsonQuotes: String
        get() = GsonBuilder().setPrettyPrinting().create().toJson(quotes)
    val jsonLamps: String
        get() = GsonBuilder().setPrettyPrinting().create().toJson(lamps)

    init {
        settings = load(PATH_CONFIG, Settings::class.java) ?: Settings()
        users = load(PATH_USERS, UserHolder::class.java)?.users ?: listOf()
        logger.info("Loaded ${users.size} users")
        quotes = load(PATH_QUOTES, QuotesHolder::class.java)?.quotes ?: listOf()
        logger.info("Loaded ${quotes.size} quotes")
        lamps = load(PATH_LAMPS, LampHolder::class.java)?.lamps ?: mapOf()
        logger.info("Loaded ${lamps.size} lamps")
    }

    fun getUser(name: String?): User? {
        if (name == null) return null
        return users.find { it.username == name }
    }

    fun getUser(request: spark.Request): User? {
        val userName = request.queryParams("user")
        return getUser(userName)
    }

    private fun <T> load(path: String, clazz: Class<T>): T? {
        val jsonString = try {
            File(path).readText()
        } catch (e: Exception) {
            logger.error("Failed loading $path", e)
            return null
        }

        return try {
            Gson().fromJson<T>(jsonString, clazz)
        } catch (e: Exception) {
            logger.error("Failed parsing $path", e)
            null
        }
    }
}