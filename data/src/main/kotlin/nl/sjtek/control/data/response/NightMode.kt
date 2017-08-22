package nl.sjtek.control.data.response

data class NightMode(val nightMode: Boolean) : Response("nightmode") {
    override val type: String = javaClass.canonicalName
}