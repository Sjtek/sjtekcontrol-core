package nl.sjtek.sjtekcontrol.settings;

/**
 * Created by wouter on 21-12-15.
 */
public class LastFM {

    private String apiKey = "";
    private String cachePathArtists = "/var/sjtekcontrol/lastfm/artists.json";
    private String cachePathAlbum = "/var/sjtekcontrol/lastfm/albums.json";

    public String getApiKey() {
        return apiKey;
    }

    public String getCachePathArtists() {
        return cachePathArtists;
    }

    public String getCachePathAlbum() {
        return cachePathAlbum;
    }
}
