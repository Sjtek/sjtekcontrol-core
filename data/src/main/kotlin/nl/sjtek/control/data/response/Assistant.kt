package nl.sjtek.control.data.response

data class Assistant(override val key: String) : Response() {
    override val type: String = javaClass.canonicalName
}