package nl.sjtek.sjtekcontrol.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 11-12-15.
 */
public class Music {
    private String mpdHost = "mopidy";
    private int mpdPort = 6600;

    private boolean taylorSwiftInject = true;
    private String taylorSwiftPath = "Local media/Taylor Swift";
    private int volumeNeutral = 10;
    private int volumeStepUp = 3;
    private int volumeStepDown = 3;
    private PlaylistSet playlistSet;

    public Music() {
        Map<String, String> playlists = new HashMap<>();
        playlists.put("SjtekSjpeellijst", "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW");
        playlistSet = new PlaylistSet("SjtekSjpeellijst", playlists);
    }

    public String getMpdHost() {
        return mpdHost;
    }

    public int getMpdPort() {
        return mpdPort;
    }

    public String getDefaultPlaylist() {
        return playlistSet.getDefaultPlaylist();
    }

    public boolean isTaylorSwiftInject() {
        return taylorSwiftInject;
    }

    public String getTaylorSwiftPath() {
        return taylorSwiftPath;
    }

    public int getVolumeNeutral() {
        return volumeNeutral;
    }

    public int getVolumeStepUp() {
        return volumeStepUp;
    }

    public int getVolumeStepDown() {
        return volumeStepDown;
    }

    public Map<String, String> getPlaylists() {
        return playlistSet.getPlaylists();
    }

    public PlaylistSet getPlaylistSet() {
        return playlistSet;
    }
}
