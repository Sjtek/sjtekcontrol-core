package nl.sjtek.control.core.media

import io.habets.chromecast.listener.CastListener
import org.slf4j.LoggerFactory

class ChromeCastPlayer(name: String, ip: String, update: (name: String) -> Unit) : MediaPlayer(name, update) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val chromeCast = CastListener(ip, this::onEvent)

    private var lastEvent = CastListener.Event("", "", CastListener.Event.State.DISCONNECTED)

    private fun onEvent(event: CastListener.Event) {
        if (event != lastEvent) {
            lastEvent = event
        }
        update(name)
    }

    override fun open(): ChromeCastPlayer {
        logger.info("Connecting ChromeCast")
        chromeCast.start()
        return this
    }

    override fun toggle() = if (getState() == State.PLAYING) chromeCast.pause() else chromeCast.play()
    override fun play() = chromeCast.play()
    override fun pause() = chromeCast.pause()
    override fun next() {}
    override fun previous() {}
    override fun clear() = chromeCast.pause()
    override fun shuffle() {}
    override fun addUri(uri: String) {}

    override var volume: Int
        get() = 0
        set(value) {
            chromeCast.volume = volume.toFloat()
        }
    override val track: Track
        get() = Track(getState(), lastEvent.uri)

    override fun close() {
        chromeCast.close()
    }

    private fun getState(): State = when (lastEvent.state) {
        CastListener.Event.State.PLAYING -> State.PLAYING
        CastListener.Event.State.PAUSED -> State.PAUSED
        CastListener.Event.State.STOPPED -> State.STOPPED
        CastListener.Event.State.DISCONNECTED -> State.DISCONNECTED
    }
}
