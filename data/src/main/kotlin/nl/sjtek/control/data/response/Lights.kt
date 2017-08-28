package nl.sjtek.control.data.response

data class Lights(override val key: String, val state: Map<Int, Boolean>) : Response() {
    override val type: String = javaClass.canonicalName
}