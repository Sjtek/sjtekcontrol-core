package nl.sjtek.control.data.response

data class Lights(val state: Map<Int, Boolean>) : Response("lights") {
    override val type: String = javaClass.canonicalName
}