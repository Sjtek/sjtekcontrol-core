package nl.sjtek.control.data.response

data class Audio(override val key: String, val enabled: Boolean, val modules: Map<String, Boolean>) : Response() {
    override val type: String = javaClass.canonicalName
}