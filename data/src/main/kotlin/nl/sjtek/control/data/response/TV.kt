package nl.sjtek.control.data.response

data class TV(override val key: String, val enabled: Boolean) : Response() {
    override val type: String = javaClass.canonicalName
}