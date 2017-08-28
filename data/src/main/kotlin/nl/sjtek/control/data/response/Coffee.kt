package nl.sjtek.control.data.response

class Coffee(override val key: String, val enabled: Boolean, val lastTimeEnabled: Long) : Response() {
    override val type: String = javaClass.canonicalName
}