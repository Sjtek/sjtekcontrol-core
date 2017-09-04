package nl.sjtek.control.data.response

import java.io.Serializable

class Coffee(override val key: String, val enabled: Boolean, val lastTimeEnabled: Long) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}