package nl.sjtek.control.data.response

data class Music(
        val connected: Boolean,
        val state: State,
        val name: String,
        val artist: String,
        val album: String,
        val uri: String,
        val albumArt: String,
        val artistArt: String,
        val volume: Int
) : Response("music") {
    override val type: String = javaClass.canonicalName

    enum class State {PLAYING, PAUSED, STOPPED }
}