package nl.sjtek.control.core.media

import io.habets.mopidy.base.models.PlaybackState
import io.habets.mopidy.base.net.client.ConnectionChangedListener
import io.habets.mopidy.base.net.client.ErrorListener
import io.habets.mopidy.base.net.client.Mopidy
import io.habets.mopidy.base.net.events.EventListener
import io.habets.mopidy.base.net.events.PlaybackStateEvent
import io.habets.mopidy.base.net.events.TrackPlaybackStateEvent
import io.habets.mopidy.base.net.events.VolumeChangedEvent
import nl.sjtek.control.core.net.MopidyWebSocket
import org.slf4j.LoggerFactory
import java.lang.Exception

class MopidyPlayer(name: String, url: String, update: (name: String) -> Unit) : MediaPlayer(name, update), ConnectionChangedListener, ErrorListener, EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val mopidy = Mopidy(MopidyWebSocket(url))

    init {
        mopidy.setOnConnectionChangedListener(this)
        mopidy.addErrorListener(this)
        mopidy.addEventListener(this)
    }

    override fun open(): MopidyPlayer {
        logger.info("Connecting Mopidy")
        mopidy.connect()
        return this
    }

    override fun toggle() = mopidy.toggle()
    override fun play() = mopidy.play()
    override fun pause() = mopidy.pause()
    override fun next() = mopidy.next()
    override fun previous() = mopidy.previous()
    override fun clear() = mopidy.clear()
    override fun shuffle() = mopidy.shuffle()
    override fun addUri(uri: String) = mopidy.addUri(uri)

    override var volume: Int
        get() = mopidy.volume
        set(value) {
            mopidy.volume = value
        }

    override val track: Track
        get() = Track(
                getState(),
                mopidy.currentTrack?.track?.uri ?: "",
                mopidy.volume,
                mopidy.currentTrack?.track?.name ?: "",
                mopidy.currentTrack?.track?.artistNames ?: "",
                mopidy.currentTrack?.track?.album?.name ?: ""
        )

    override fun close() {
        mopidy.disconnect()
    }

    override fun onVolumeChanged(p0: VolumeChangedEvent?) = update(name)
    override fun onTrackPlaybackState(p0: TrackPlaybackStateEvent?) = update(name)
    override fun onPlaybackState(p0: PlaybackStateEvent?) = update(name)
    override fun onError(p0: Exception?) = update(name)
    override fun onConnected() {
        logger.info("Mopidy connected")
        update(name)
    }

    override fun onDisconnected() {
        logger.info("Mopidy disconnected")
        update(name)
    }

    private fun getState(): State {
        return when {
            !mopidy.isConnected -> State.DISCONNECTED
            else -> {
                when (mopidy.playbackState) {
                    PlaybackState.PAUSED -> State.PAUSED
                    PlaybackState.PLAYING -> State.PLAYING
                    PlaybackState.STOPPED -> State.STOPPED
                    else -> State.DISCONNECTED
                }
            }
        }
    }
}