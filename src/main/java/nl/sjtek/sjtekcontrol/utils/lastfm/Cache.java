package nl.sjtek.sjtekcontrol.utils.lastfm;

import com.google.gson.Gson;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 21-12-15.
 */
public class Cache {

    private static final String TAG = Cache.class.getCanonicalName();

    private ArtistHolder artistHolder = new ArtistHolder();
    private AlbumHolder albumHolder = new AlbumHolder();

    public Cache() {
        reload();
    }

    public void reload() {
        synchronized (TAG) {
            try {
                String artistString = FileUtils.readFile(SettingsManager.getInstance().getLastFM().getCachePathArtists());
                String albumString = FileUtils.readFile(SettingsManager.getInstance().getLastFM().getCachePathAlbum());

                Gson gson = new Gson();

                artistHolder = gson.fromJson(artistString, ArtistHolder.class);
                albumHolder = gson.fromJson(albumString, AlbumHolder.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (artistHolder == null) {
                    artistHolder = new ArtistHolder();
                }

                if (artistHolder.artistMap == null) {
                    artistHolder.artistMap = new HashMap<>();
                }

                if (albumHolder == null) {
                    albumHolder = new AlbumHolder();
                }

                if (albumHolder.albumMap == null) {
                    albumHolder.albumMap = new HashMap<>();
                }
            }
        }
    }

    private synchronized void saveArtists() {
        try {
            Gson gson = new Gson();
            FileUtils.writeFile(SettingsManager.getInstance().getLastFM().getCachePathArtists(), gson.toJson(artistHolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void saveAlbums() {
        try {
            Gson gson = new Gson();
            FileUtils.writeFile(SettingsManager.getInstance().getLastFM().getCachePathAlbum(), gson.toJson(albumHolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Album getAlbum(String queryArtist, String queryAlbum) {
        String key = Album.getKey(queryArtist, queryAlbum);
        synchronized (key) {
            return albumHolder.albumMap.get(key);
        }
    }

    public Artist getArtist(String query) {
        String key = Artist.getKey(query);
        synchronized (key) {
            return artistHolder.artistMap.get(key);
        }
    }

    public void update(Artist artist) {
        synchronized (artist.getKey()) {
            artistHolder.artistMap.put(artist.getKey(), artist);
            saveArtists();
        }
    }

    public void update(Album album) {
        synchronized (album.getKey()) {
            albumHolder.albumMap.put(album.getKey(), album);
            saveAlbums();
        }
    }

    private class ArtistHolder {
        Map<String, Artist> artistMap = new HashMap<>();
    }

    private class AlbumHolder {
        Map<String, Album> albumMap = new HashMap<>();
    }
}
