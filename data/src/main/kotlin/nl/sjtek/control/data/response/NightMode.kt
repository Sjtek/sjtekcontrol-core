package nl.sjtek.control.data.response

data class NightMode(override val key: String, val nightMode: Boolean) : Response() {
    override val type: String = javaClass.canonicalName
}