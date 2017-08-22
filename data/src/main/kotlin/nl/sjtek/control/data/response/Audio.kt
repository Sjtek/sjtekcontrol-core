package nl.sjtek.control.data.response

data class Audio(val enabled: Boolean, val modules: Map<String, Boolean>) : Response("audio") {
    override val type: String = javaClass.canonicalName
}