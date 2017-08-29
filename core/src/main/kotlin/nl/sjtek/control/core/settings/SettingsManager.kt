package nl.sjtek.control.core.settings

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File

object SettingsManager {
    private const val PATH_CONFIG = "/var/sjtekcontrol/config.json"
    private const val PATH_USERS = "/var/sjtekcontrol/users.json"
    private const val PATH_QUOTES = "/var/sjtekcontrol/quotes.json"
    private val logger = LoggerFactory.getLogger(javaClass)

    val settings: Settings
    val users: List<User>
    val quotes: List<String>

    val jsonUsers: String
        get() = GsonBuilder().setPrettyPrinting().create().toJson(users)
    val jsonQuotes: String
        get() = GsonBuilder().setPrettyPrinting().create().toJson(quotes)

    init {
        settings = load(PATH_CONFIG, Settings::class.java) ?: Settings()
        users = load(PATH_USERS, UserHolder::class.java)?.users ?: listOf(User("wouter", "Wouter", "Habets", mapOf("test" to "asdf")))
        quotes = load(PATH_QUOTES, QuotesHolder::class.java)?.quotes ?: listOf("asdf")
    }

    fun getUser(name: String?): User? {
        if (name == null) return null
        return users.find { it.firstName == name }
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