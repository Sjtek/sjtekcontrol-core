package nl.sjtek.control.core.modules

import io.habets.mopidy.base.models.PlaybackState
import io.habets.mopidy.base.net.client.ConnectionChangedListener
import io.habets.mopidy.base.net.client.ErrorListener
import io.habets.mopidy.base.net.client.Mopidy
import io.habets.mopidy.base.net.events.EventListener
import io.habets.mopidy.base.net.events.PlaybackStateEvent
import io.habets.mopidy.base.net.events.TrackPlaybackStateEvent
import io.habets.mopidy.base.net.events.VolumeChangedEvent
import nl.sjtek.control.core.net.MopidyWebSocket
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.core.settings.User
import nl.sjtek.control.core.settings.UserManager
import nl.sjtek.control.core.settings.getDefaultPlaylist
import nl.sjtek.control.data.response.Music
import nl.sjtek.control.data.response.Response
import org.slf4j.LoggerFactory
import spark.Spark.get
import spark.Spark.path
import spark.kotlin.get
import java.lang.Exception

class Music(key: String, settings: Settings) : Module(key, settings), ConnectionChangedListener, ErrorListener, EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val defaultVolume = settings.music.volume
    private val mopidy: Mopidy = Mopidy(MopidyWebSocket(settings.music.url))
    override val response: Response
        get() = mopidy.toResponse()

    init {
        mopidy.setOnConnectionChangedListener(this)
        mopidy.addErrorListener(this)
        mopidy.addEventListener(this)
        mopidy.connect()
    }

    override fun isEnabled(user: User?): Boolean = mopidy.playbackState == PlaybackState.PLAYING

    override fun initSpark() {
        path("/music") {
            get("/noSatisfaction") {}
            get("/play") { mopidy.play() }
            get("/pause") { mopidy.pause() }
            get("/toggle") { mopidy.toggle() }
            get("/next") { mopidy.next() }
            get("/previous") { mopidy.previous() }
            get("/clear") { mopidy.clear() }
            get("/shuffle") { mopidy.shuffle() }
            get("/start", this::startMusic)
            get("/volume", this::volume)
        }
    }

    fun startMusic(req: spark.Request, res: spark.Response) {
        val args = req.queryMap()
        val shuffle = args.hasKey("shuffle")
        val clear = args.hasKey("clear")
        val uri = args["uri"]?.value() ?: UserManager.get(req).getDefaultPlaylist()
        val resetVolume = args.hasKey("reset")

        if (clear) mopidy.clear()
        mopidy.addUri(uri)
        if (shuffle) mopidy.shuffle()
        if (resetVolume) mopidy.volume = defaultVolume
        mopidy.play()
    }

    fun volume(req: spark.Request, res: spark.Response) {
        val args = req.queryMap()
        val increase = args.hasKey("increase")
        val lower = args.hasKey("lower")
        val custom = args.hasKey("value")
        val value = args["value"].integerValue() ?: -1
        when {
            custom -> mopidy.volume = value
            increase -> mopidy.volume = mopidy.volume + 2
            lower -> mopidy.volume = mopidy.volume - 2
            else -> mopidy.volume = defaultVolume
        }
    }

    override fun onVolumeChanged(event: VolumeChangedEvent) {
        ResponseCache.post(this, true)
    }

    override fun onTrackPlaybackState(event: TrackPlaybackStateEvent) {
        ResponseCache.post(this, true)
    }

    override fun onPlaybackState(event: PlaybackStateEvent) {
        ResponseCache.post(this, true)
    }

    override fun onError(e: Exception) {
        logger.error("Mopidy error", e)
        ResponseCache.post(this, true)
    }

    override fun onConnected() {
        logger.info("Mopidy connected")
        ResponseCache.post(this, true)
    }

    override fun onDisconnected() {
        logger.info("Mopidy disconnected")
        ResponseCache.post(this, true)
    }

    private fun Mopidy.toResponse(): Music = Music(
            this.isConnected,
            this.playbackState.convert(),
            this.currentTrack?.track?.name ?: "",
            this.currentTrack?.track?.artistNames ?: "",
            this.currentTrack?.track?.album?.name ?: "",
            this.currentTrack?.track?.uri ?: "",
            "", "",
            this.volume)

    private fun PlaybackState?.convert(): Music.State = when (this) {
        PlaybackState.PAUSED -> Music.State.PAUSED
        PlaybackState.PLAYING -> Music.State.PLAYING
        PlaybackState.STOPPED -> Music.State.STOPPED
        else -> Music.State.STOPPED
    }
}