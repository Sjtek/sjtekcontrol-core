package nl.sjtek.control.data.response

data class Screen(override val key: String, val video: Boolean) : Response() {
    override val type: String = javaClass.canonicalName
}
