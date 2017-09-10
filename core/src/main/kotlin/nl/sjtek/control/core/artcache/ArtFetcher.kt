package nl.sjtek.control.core.artcache

import io.habets.lastfmfetcher.LastFM
import nl.sjtek.control.core.getRequest
import nl.sjtek.control.core.net.HttpClient
import nl.sjtek.control.core.settings.SettingsManager
import org.slf4j.LoggerFactory
import org.soualid.colorthief.MMCQ
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class ArtFetcher(private val callback: (result: Result) -> Unit) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val basePath = SettingsManager.settings.artFetcher.basePath + PATH
    private val baseUrl = SettingsManager.settings.artFetcher.baseUrl + PATH
    private val workQueue = LinkedBlockingQueue<Runnable>()
    private val pool = ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, workQueue)
    private val lastFMCache = LastFMCache(true)
    private val lastFM: LastFM = LastFM(SettingsManager.settings.artFetcher.apiKey, lastFMCache.cache)
    private val cache = ConcurrentHashMap<String, Result>()

    fun get(uri: String, artist: String, album: String): Result {
        if (artist.isBlank() || album.isBlank()) return Result()
        val result = cache[uri]
        if (result != null) {
            return result
        } else {
            pool.execute(Fetcher(uri, artist, album))
            return Result()
        }
    }

    //TODO Store color separate
    //TODO Use Spotify api instead of Last.FM
    inner class Fetcher(private val uri: String, private val artist: String, private val album: String) : Runnable {
        override fun run() {
            if (cache[uri] != null) return
            val artistKey = URLEncoder.encode(artist, "UTF-8")
            logger.info("Last.FM fetching $artist")
            val artistUrl = lastFM.getArtist(artist).image.mega
            val albumKey = URLEncoder.encode(album, "UTF-8") + artistKey
            logger.info("Last.FM fetching $album")
            val albumUrl = lastFM.getAlbum(artist, album).image.mega
            if (albumUrl.isNullOrBlank() || artistUrl.isNullOrBlank()) {
                logger.error("LastFM data is blank for \"$artist\" \"$album\"")
                return
            }

            var colorArtist: Color? = null
            if (!File(basePath.format(FOLDER_ARTIST, artistKey)).exists()) {
                try {
                    logger.info("Downloading $artist")
                    colorArtist = fetch(artistUrl, basePath.format(FOLDER_ARTIST, artistKey))
                } catch (e: Exception) {
                    logger.error("Failed downloading artist art for $artist", e)
                    return
                }
            } else {
                logger.info("Artist $artist already exists")
            }

            var colorAlbum: Color? = null
            if (!File(basePath.format(FOLDER_ALBUM, albumKey)).exists()) {
                try {
                    logger.info("Downloading $album")
                    colorAlbum = fetch(albumUrl, basePath.format(FOLDER_ALBUM, albumKey))
                } catch (e: Exception) {
                    logger.error("Failed downloading artist art for $artist", e)
                    return
                }
            } else {
                logger.info("Album $album already exists")
            }

            val color = when {
                colorAlbum != null -> colorAlbum
                colorArtist != null -> colorArtist
                else -> null
            }

            val result = Result(uri, baseUrl.format(FOLDER_ALBUM, albumKey), baseUrl.format(FOLDER_ARTIST, artistKey), color)
            logger.info("Completed $result")
            cache.put(uri, result)
            callback(result)
        }

        private fun fetch(url: String, path: String): Color {
            val response = HttpClient.client.newCall(url.getRequest()).execute()
            response.use {
                if (response.isSuccessful) {
                    response.body()!!.use { b ->
                        val image = ImageIO.read(b.byteStream())
                        ImageIO.write(image, "png", File(path))
                        val colors = MMCQ.compute(image, 2)
                        return Color(colors.first())
                    }
                } else {
                    throw IOException("Download failed (result ${response.code()})")
                }
            }
        }
    }

    data class Result(val uri: String = "", val albumArt: String = "", val artistArt: String = "", val r: Int = 0, val g: Int = 0, val b: Int = 0) {
        constructor(uri: String, albumArt: String, artistArt: String, color: Color?) : this(uri, albumArt, artistArt, color?.r ?: 0, color?.g ?: 0, color?.b ?: 0)
    }

    data class Color(val r: Int, val g: Int, val b: Int) {
        constructor(color: IntArray) : this(color[0], color[1], color[2])
    }

    companion object {
        private const val FOLDER_ALBUM = "album"
        private const val FOLDER_ARTIST = "artist"
        private const val PATH = "/%s/%s.png"
        private const val BUFFER_SIZE = 2097152 // 2MB
    }
}