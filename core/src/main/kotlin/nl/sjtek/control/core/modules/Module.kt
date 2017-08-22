package nl.sjtek.control.core.modules

import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.core.settings.User
import nl.sjtek.control.data.response.Response
import org.slf4j.LoggerFactory


abstract class Module(val key: String, settings: Settings) {

    abstract val response: Response
    open fun isEnabled(user: User? = null): Boolean = false
    abstract fun initSpark()

    init {
        LoggerFactory.getLogger(javaClass).info("Init $key")
    }
}
