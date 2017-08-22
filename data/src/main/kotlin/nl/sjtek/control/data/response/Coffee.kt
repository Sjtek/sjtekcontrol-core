package nl.sjtek.control.data.response

class Coffee(val enabled: Boolean, val lastTimeEnabled: Long) : Response("coffee") {
    override val type: String = javaClass.canonicalName
}