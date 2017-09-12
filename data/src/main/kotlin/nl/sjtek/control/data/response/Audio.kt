package nl.sjtek.control.data.response

import java.io.Serializable

data class Audio(override val key: String, val enabled: Boolean, val modules: Map<String, Boolean>) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}