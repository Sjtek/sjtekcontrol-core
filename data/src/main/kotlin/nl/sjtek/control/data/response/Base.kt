package nl.sjtek.control.data.response

data class Base(val laaien: Boolean) : Response("base") {
    override val type: String = javaClass.canonicalName
}