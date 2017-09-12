package nl.sjtek.control.data.response

import java.io.Serializable

data class Lights(override val key: String, val state: Map<Int, Boolean>) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}