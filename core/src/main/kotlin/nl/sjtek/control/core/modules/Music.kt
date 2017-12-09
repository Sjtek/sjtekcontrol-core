package nl.sjtek.control.core.modules

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.AudioEvent
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.NightModeEvent
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.core.getDefaultPlaylist
import nl.sjtek.control.core.media.MediaPlayer
import nl.sjtek.control.core.media.PlayerHandler
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.staticdata.User
import org.slf4j.LoggerFactory
import spark.Spark.path

class Music(key: String) : Module(key) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val defaultVolume = SettingsManager.settings.music.volume

    private val player = PlayerHandler(key, SettingsManager.settings.music.mopidy, SettingsManager.settings.music.chromecast, this::onUpdate)

    override val response: Response
        get() = player.response

    init {
        Bus.subscribe(this)
    }

    override fun isEnabled(user: User?): Boolean = player.activePlayer.active

    override fun initSpark() {
        path("/music") {
            get("/noSatisfaction") { _, _ -> }
            get("/play") { _, _ -> player.activePlayer.play() }
            get("/pause") { _, _ -> player.activePlayer.pause() }
            get("/toggle") { _, _ -> player.activePlayer.toggle() }
            get("/next") { _, _ -> player.activePlayer.next() }
            get("/previous") { _, _ -> player.activePlayer.previous() }
            get("/clear") { _, _ -> player.activePlayer.clear() }
            get("/shuffle") { _, _ -> player.activePlayer.shuffle() }
            get("/start", this::startMusic)
            get("/volume", this::volume)
            spark.Spark.get("/status", this::status)
        }
    }

    @Handler
    fun onNightMode(event: NightModeEvent) = if (event.enabled) player.activePlayer.pause() else Unit

    @Handler
    fun onToggle(event: ToggleEvent) = if (!event.enabled) player.activePlayer.pause() else Unit

    private fun startMusic(req: spark.Request, res: spark.Response) {
        val args = req.queryMap()
        val shuffle = args.hasKey("shuffle")
        val clear = args.hasKey("clear")
        val givenUri = args["uri"]?.value()
        val uri = if (givenUri.isNullOrBlank()) SettingsManager.getUser(req).getDefaultPlaylist() else givenUri
        val resetVolume = args.hasKey("reset")

        if (clear) player.activePlayer.clear()
        if (uri != null) player.activePlayer.addUri(uri)
        if (shuffle) player.activePlayer.shuffle()
        if (resetVolume) player.activePlayer.volume = defaultVolume
        player.activePlayer.play()
    }

    private fun volume(req: spark.Request, res: spark.Response) {
        val args = req.queryMap()
        val increase = args.hasKey("increase")
        val lower = args.hasKey("lower")
        val custom = args.hasKey("value")
        val value = args["value"].integerValue() ?: -1
        val player = player.activePlayer
        when {
            custom -> player.volume = value
            increase -> player.volume = player.volume + 2
            lower -> player.volume = player.volume - 2
            else -> player.volume = defaultVolume
        }
    }

    private fun status(req: spark.Request, res: spark.Response): String = when (player.activePlayer.track.state) {
        MediaPlayer.State.PAUSED -> "0"
        MediaPlayer.State.PLAYING -> "1"
        MediaPlayer.State.STOPPED -> "1"
        else -> "0"
    }

    private fun onUpdate(name: String) {
        ResponseCache.post(this, true)
        sendAudioEvent()
    }

    private fun sendAudioEvent() {
        when (player.activePlayer.track.state) {
            MediaPlayer.State.PLAYING -> Bus.post(AudioEvent(key, true))
            MediaPlayer.State.PAUSED, MediaPlayer.State.STOPPED -> Bus.post(AudioEvent(key, false))
            else -> Bus.post(AudioEvent(key, false))
        }
    }
}