package nl.sjtek.control.core.media

import io.habets.artparser.models.TrackResult
import nl.sjtek.control.data.response.Music

class PlayerHandler(private val moduleKey: String, mopidyUrl: String, chromeCastIp: String, update: (name: String) -> Unit) {

    private val chromeCast = ChromeCastPlayer("chromecast", chromeCastIp, update).open()
    private val mopidy = MopidyPlayer("mopidy", mopidyUrl, update).open()

    val activePlayer: MediaPlayer
        get() = when {
            mopidy.active -> mopidy
            chromeCast.active -> chromeCast
            !mopidy.connected -> chromeCast
            else -> mopidy
        }
    val response: Music
        get() {
            val player = activePlayer
            val track = player.track
            val name: String
            val artists: String
            val album: String

            val result = ArtParserHolder.get(track.uri) as? TrackResult
            if (track.needsFullResolve) {
                name = result?.name ?: track.name
                artists = result?.artists ?: track.artist
                album = result?.album ?: track.album
            } else {
                name = track.name
                artists = track.artist
                album = track.album
            }

            return Music(moduleKey,
                    true,
                    activePlayer.track.state.convert(),
                    name, artists, album, track.uri,
                    result?.image ?: "",
                    result?.secondImage ?: "",
                    track.volume,
                    result?.color1?.red ?: -1,
                    result?.color1?.green ?: -1,
                    result?.color1?.blue ?: -1)
        }

    private fun MediaPlayer.State.convert(): Music.State = when (this) {
        MediaPlayer.State.PAUSED -> Music.State.PAUSED
        MediaPlayer.State.PLAYING -> Music.State.PLAYING
        MediaPlayer.State.STOPPED -> Music.State.STOPPED
        MediaPlayer.State.DISCONNECTED -> Music.State.STOPPED
    }
}