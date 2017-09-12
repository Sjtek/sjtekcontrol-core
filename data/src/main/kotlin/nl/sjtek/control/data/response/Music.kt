package nl.sjtek.control.data.response

import java.io.Serializable

data class Music(
        override val key: String,
        val connected: Boolean,
        val state: State,
        val name: String,
        val artist: String,
        val album: String,
        val uri: String,
        val albumArt: String,
        val artistArt: String,
        val volume: Int,
        val red: Int, val green: Int, val blue: Int
) : Response(), Serializable {
    override val type: String = javaClass.canonicalName

    enum class State : Serializable {PLAYING, PAUSED, STOPPED }
}