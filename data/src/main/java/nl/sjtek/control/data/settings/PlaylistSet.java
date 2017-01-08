package nl.sjtek.control.data.settings;

import java.util.Map;

/**
 * Created by wouter on 10-1-16.
 */
public class PlaylistSet extends Setting {

    private final String defaultPlaylist;
    private final Map<String, String> playlists;

    public PlaylistSet(String defaultPlaylist, Map<String, String> playlists) {
        this.defaultPlaylist = defaultPlaylist;
        this.playlists = playlists;
    }

    public Map<String, String> getPlaylists() {
        return playlists;
    }

    public String getDefaultPlaylist() {
        return playlists.get(defaultPlaylist);
    }
}
