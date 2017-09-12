package nl.sjtek.control.core.modules

import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.staticdata.User
import org.slf4j.LoggerFactory


abstract class Module(val key: String) {

    abstract val response: Response
    open fun isEnabled(user: User? = null): Boolean = false
    abstract fun initSpark()

    init {
        LoggerFactory.getLogger(javaClass).info("Init $key")
    }
}
