package nl.sjtek.control.data.response

abstract class Response(val key: String) {
    abstract val type: String
}