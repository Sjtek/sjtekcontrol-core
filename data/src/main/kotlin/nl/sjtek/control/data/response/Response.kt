package nl.sjtek.control.data.response

import java.io.Serializable

abstract class Response : Serializable {
    abstract val key: String
    abstract val type: String
}