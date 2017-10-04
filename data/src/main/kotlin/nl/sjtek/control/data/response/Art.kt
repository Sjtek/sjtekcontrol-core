package nl.sjtek.control.data.response

data class Art(override val key: String, val tracks: Int, val artists: Int, val albums: Int) : Response() {
    override val type: String = javaClass.canonicalName
}