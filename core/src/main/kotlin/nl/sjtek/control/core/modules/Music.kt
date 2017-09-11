package nl.sjtek.control.core.modules

import io.habets.artparser.ArtParser
import io.habets.mopidy.base.models.PlaybackState
import io.habets.mopidy.base.net.client.ConnectionChangedListener
import io.habets.mopidy.base.net.client.ErrorListener
import io.habets.mopidy.base.net.client.Mopidy
import io.habets.mopidy.base.net.events.EventListener
import io.habets.mopidy.base.net.events.PlaybackStateEvent
import io.habets.mopidy.base.net.events.TrackPlaybackStateEvent
import io.habets.mopidy.base.net.events.VolumeChangedEvent
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.AudioEvent
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.NightModeEvent
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.core.getDefaultPlaylist
import nl.sjtek.control.core.net.HttpClient
import nl.sjtek.control.core.net.MopidyWebSocket
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.response.Music
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.staticdata.User
import org.slf4j.LoggerFactory
import spark.Spark.path
import java.lang.Exception

class Music(key: String) : Module(key), ConnectionChangedListener, ErrorListener, EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val defaultVolume = SettingsManager.settings.music.volume
    private val mopidy: Mopidy = Mopidy(MopidyWebSocket(SettingsManager.settings.music.url))
    private val artParser = ArtParser(
            clientId = SettingsManager.settings.artParser.clientId,
            clientSecret = SettingsManager.settings.artParser.clientSecret,
            redirectUri = "",
            httpClient = HttpClient.client,
            path = SettingsManager.settings.artParser.path,
            baseUrl = SettingsManager.settings.artParser.baseUrl)

    override val response: Response
        get() = mopidy.toResponse()

    init {
        mopidy.setOnConnectionChangedListener(this)
        mopidy.addErrorListener(this)
        mopidy.addEventListener(this)
        mopidy.connect()
        Bus.subscribe(this)
    }

    override fun isEnabled(user: User?): Boolean = mopidy.playbackState == PlaybackState.PLAYING

    override fun initSpark() {
        path("/music") {
            get("/noSatisfaction") { _, _ -> }
            get("/play") { _, _ -> mopidy.play() }
            get("/pause") { _, _ -> mopidy.pause() }
            get("/toggle") { _, _ -> mopidy.toggle() }
            get("/next") { _, _ -> mopidy.next() }
            get("/previous") { _, _ -> mopidy.previous() }
            get("/clear") { _, _ -> mopidy.clear() }
            get("/shuffle") { _, _ -> mopidy.shuffle() }
            get("/start", this::startMusic)
            get("/volume", this::volume)
            spark.Spark.get("/status", this::status)
        }
    }

    @Handler
    fun onNightMode(event: NightModeEvent) = if (event.enabled) mopidy.pause() else Unit

    @Handler
    fun onToggle(event: ToggleEvent) = if (!event.enabled) mopidy.pause() else Unit


    private fun startMusic(req: spark.Request, res: spark.Response) {
        val args = req.queryMap()
        val shuffle = args.hasKey("shuffle")
        val clear = args.hasKey("clear")
        val givenUri = args["uri"]?.value()
        val uri = if (givenUri.isNullOrBlank()) SettingsManager.getUser(req).getDefaultPlaylist() else givenUri
        val resetVolume = args.hasKey("reset")

        if (clear) mopidy.clear()
        mopidy.addUri(uri)
        if (shuffle) mopidy.shuffle()
        if (resetVolume) mopidy.volume = defaultVolume
        mopidy.play()
    }

    private fun volume(req: spark.Request, res: spark.Response) {
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

    private fun status(req: spark.Request, res: spark.Response): String = when (mopidy.playbackState) {
        PlaybackState.PAUSED -> "0"
        PlaybackState.PLAYING -> "1"
        PlaybackState.STOPPED -> "1"
        else -> "0"
    }

    override fun onVolumeChanged(event: VolumeChangedEvent) {
        ResponseCache.post(this, true)
    }

    override fun onTrackPlaybackState(event: TrackPlaybackStateEvent) {
        ResponseCache.post(this, true)
    }

    override fun onPlaybackState(event: PlaybackStateEvent) {
        ResponseCache.post(this, true)
        sendAudioEvent()
    }

    override fun onError(e: Exception) {
        logger.error("Mopidy error", e)
        ResponseCache.post(this, true)
    }

    override fun onConnected() {
        logger.info("Mopidy connected")
        ResponseCache.post(this, true)
        sendAudioEvent()
    }

    override fun onDisconnected() {
        logger.info("Mopidy disconnected")
        ResponseCache.post(this, true)
        sendAudioEvent()
    }

    private fun sendAudioEvent() {
        when (mopidy.playbackState) {
            PlaybackState.PLAYING -> Bus.post(AudioEvent(key, true))
            PlaybackState.PAUSED, PlaybackState.STOPPED -> Bus.post(AudioEvent(key, false))
            else -> Bus.post(AudioEvent(key, false))
        }
    }

    private fun Mopidy.toResponse(): Music {
        val uri = this.currentTrack?.track?.uri ?: ""
        val album = this.currentTrack?.track?.album?.name ?: ""
        val artist = this.currentTrack?.track?.artistNames ?: ""

        val result = artParser.get(uri)
        val albumArt = result?.image ?: ""
        val artistArt = result?.secondImage ?: ""
        val r = result?.color?.red ?: -1
        val g = result?.color?.green ?: -1
        val b = result?.color?.blue ?: -1

        return Music(
                key,
                this.isConnected,
                this.playbackState.convert(),
                this.currentTrack?.track?.name ?: "",
                artist, album,
                uri,
                albumArt, artistArt,
                this.volume,
                r, g, b)
    }

    private fun PlaybackState?.convert(): Music.State = when (this) {
        PlaybackState.PAUSED -> Music.State.PAUSED
        PlaybackState.PLAYING -> Music.State.PLAYING
        PlaybackState.STOPPED -> Music.State.STOPPED
        else -> Music.State.STOPPED
    }
}