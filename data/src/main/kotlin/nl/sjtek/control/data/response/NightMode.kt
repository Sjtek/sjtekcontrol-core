package nl.sjtek.control.data.response

data class NightMode(override val key: String, val enabled: Boolean) : Response() {
    override val type: String = javaClass.canonicalName
}