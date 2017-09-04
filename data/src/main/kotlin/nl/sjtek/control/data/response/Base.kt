package nl.sjtek.control.data.response

import java.io.Serializable

data class Base(override val key: String) : Response(), Serializable {
    override val type: String = javaClass.canonicalName
}