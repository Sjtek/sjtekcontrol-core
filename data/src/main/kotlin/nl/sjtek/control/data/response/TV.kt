package nl.sjtek.control.data.response

data class TV(val enabled: Boolean) : Response("tv") {
    override val type: String = javaClass.canonicalName
}