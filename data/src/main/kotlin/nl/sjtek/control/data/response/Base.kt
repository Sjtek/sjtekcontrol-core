package nl.sjtek.control.data.response

data class Base(override val key: String) : Response() {
    override val type: String = javaClass.canonicalName
}