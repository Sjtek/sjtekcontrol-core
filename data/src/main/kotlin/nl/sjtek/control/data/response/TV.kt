package nl.sjtek.control.data.response

import java.io.Serializable

data class TV(override val key: String, val enabled: Boolean) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}