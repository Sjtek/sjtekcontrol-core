package nl.sjtek.control.core.modules

import io.habets.mopidy.base.net.client.ConnectionChangedListener
import io.habets.mopidy.base.net.client.ErrorListener
import io.habets.mopidy.base.net.client.Mopidy
import io.habets.mopidy.base.net.events.EventListener
import io.habets.mopidy.base.net.events.PlaybackStateEvent
import io.habets.mopidy.base.net.events.TrackPlaybackStateEvent
import io.habets.mopidy.base.net.events.VolumeChangedEvent
import nl.sjtek.control.core.net.MopidyWebSocket
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.core.settings.User
import nl.sjtek.control.data.response.Response
import java.lang.Exception

class Music(key: String, settings: Settings) : Module(key, settings), ConnectionChangedListener, ErrorListener, EventListener {

    private val mopidy: Mopidy
    override val response: Response
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    init {
        mopidy = Mopidy(MopidyWebSocket(settings.music.url))
        mopidy.setOnConnectionChangedListener(this)
        mopidy.addErrorListener(this)
        mopidy.addEventListener(this)
        mopidy.connect()
    }

    override fun isEnabled(user: User?): Boolean {
        return super.isEnabled(user)
    }

    override fun initSpark() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onVolumeChanged(event: VolumeChangedEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrackPlaybackState(event: TrackPlaybackStateEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaybackState(event: PlaybackStateEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(e: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisconnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}