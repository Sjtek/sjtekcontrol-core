package nl.sjtek.control.core.artcache

import io.habets.lastfmfetcher.LastFM
import nl.sjtek.control.core.getRequest
import nl.sjtek.control.core.net.HttpClient
import nl.sjtek.control.core.settings.SettingsManager
import okio.Okio
import okio.Source
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

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

            if (!File(basePath.format(FOLDER_ARTIST, artistKey)).exists()) {
                try {
                    logger.info("Downloading $artist")
                    fetch(artistUrl, basePath.format(FOLDER_ARTIST, artistKey))
                } catch (e: Exception) {
                    logger.error("Failed downloading artist art for $artist", e)
                    return
                }
            } else {
                logger.info("Artist $artist already exists")
            }

            if (!File(basePath.format(FOLDER_ALBUM, albumKey)).exists()) {
                try {
                    logger.info("Downloading $album")
                    fetch(albumUrl, basePath.format(FOLDER_ALBUM, albumKey))
                } catch (e: Exception) {
                    logger.error("Failed downloading artist art for $artist", e)
                    return
                }
            } else {
                logger.info("Album $album already exists")
            }

            Thread.sleep(1000)
            val result = Result(uri, baseUrl.format(FOLDER_ALBUM, albumKey), baseUrl.format(FOLDER_ARTIST, artistKey))
            logger.info("Completed $result")
            cache.put(uri, result)
            callback(result)
        }

        private fun fetch(url: String, path: String) {
            val response = HttpClient.client.newCall(url.getRequest()).execute()
            response.use {
                if (response.isSuccessful) {
                    val stream = response.body()!!.source()
                    writeImage(path, stream)
                } else {
                    throw IOException("Download failed (result ${response.code()})")
                }
            }
        }

        private fun writeImage(path: String, source: Source) {
            Okio.buffer(Okio.sink(File(path))).use { sink ->
                sink.writeAll(source)
            }
        }
    }

    data class Result(val uri: String = "", val albumArt: String = "", val artistArt: String = "")

    companion object {
        private const val FOLDER_ALBUM = "album"
        private const val FOLDER_ARTIST = "artist"
        private const val PATH = "/%s/%s.png"
        private const val BUFFER_SIZE = 2097152 // 2MB
    }
}