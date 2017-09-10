package nl.sjtek.control.core.artcache

import io.habets.lastfmfetcher.Album
import io.habets.lastfmfetcher.Artist
import io.habets.lastfmfetcher.Cache
import nl.sjtek.control.core.settings.SettingsManager
import org.slf4j.LoggerFactory
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.concurrent.thread

class LastFMCache(private val test: Boolean) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val pathArtists = SettingsManager.settings.artFetcher.cachePathArtists
    private val pathAlbums = SettingsManager.settings.artFetcher.cachePathAlbums
    private val test2 = !test
    val cache = InnerCache()

    init {
        logger.info("Last.FM artist cache $pathArtists")
        logger.info("Last.FM album cache $pathAlbums")
    }

    inner class InnerCache : Cache() {
        override fun saveArtists(p0: MutableMap<String, Artist>?) {
            if (p0 != null) {
                storeObject(pathArtists, p0)
            }
        }

        override fun saveAlbums(p0: MutableMap<String, Album>?) {
            if (p0 != null) {
                storeObject(pathAlbums, p0)
            }
        }

        override fun loadAlbums(): MutableMap<String, Album> {
            return loadObject(pathAlbums) as? MutableMap<String, Album> ?: mutableMapOf()
        }

        override fun loadArtists(): MutableMap<String, Artist> {
            return loadObject(pathArtists) as? MutableMap<String, Artist> ?: mutableMapOf()
        }
    }

    private fun storeObject(path: String, obj: Any) {
        thread(start = true) {
            try {
                ObjectOutputStream(File(path).outputStream()).use { stream ->
                    stream.writeObject(obj)
                }
            } catch (e: Exception) {
                logger.error("Last.FM cache store error (path $path)", e)
            }
        }
    }

    private fun loadObject(path: String): Any? {
        try {
            ObjectInputStream(File(path).inputStream()).use { stream ->
                return stream.readObject()
            }
        } catch (e: Exception) {
            logger.error("Last.FM cache load error (path $path)", e)
            return null
        }
    }
}