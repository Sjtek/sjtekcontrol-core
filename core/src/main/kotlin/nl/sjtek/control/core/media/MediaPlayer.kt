package nl.sjtek.control.core.media

import java.io.Closeable

abstract class MediaPlayer(val name: String, protected val update: (name: String) -> Unit) : Closeable {
    abstract fun open(): MediaPlayer
    abstract fun toggle()
    abstract fun play()
    abstract fun pause()
    abstract fun next()
    abstract fun previous()
    abstract fun clear()
    abstract fun shuffle()
    abstract fun addUri(uri: String)
    abstract var volume: Int
    abstract val track: Track

    val active: Boolean
        get() = track.state == State.PLAYING
    val connected: Boolean
        get() = track.state != State.DISCONNECTED

    enum class State {
        PAUSED, PLAYING, STOPPED, DISCONNECTED
    }

    data class Track(
            val state: State,
            val uri: String,
            val volume: Int = -1,
            val name: String = "",
            val artist: String = "",
            val album: String = "") {

        val needsFullResolve = name.isBlank() || artist.isBlank() || album.isBlank()
    }
}