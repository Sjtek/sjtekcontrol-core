package nl.sjtek.control.data.response

data class Color(override val key: String, val lamps: Set<Int>) : Response() {
    override val type: String = javaClass.canonicalName
}