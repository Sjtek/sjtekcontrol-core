package nl.sjtek.control.data.settings;

import java.util.Map;
import java.util.Random;

/**
 * Created by wouter on 11-12-15.
 */
public class User {
    private final String[] nickNames;
    private final PlaylistSet playlistSet;
    private final String[] nfcTags;
    private final String[][] greetings;
    private final String[][] farewells;
    private final boolean checkExtraLight;
    private final boolean injectTaylorSwift;
    private final boolean autoStartMusic;

    public User(
            String[] nickNames, PlaylistSet playlistSet, String[] nfcTags,
            String[][] greetings, String[][] farewells, boolean checkExtraLight, boolean injectTaylorSwift, boolean autoStartMusic) {
        this.nickNames = nickNames;
        this.playlistSet = playlistSet;
        this.nfcTags = nfcTags;
        this.greetings = greetings;
        this.farewells = farewells;
        this.checkExtraLight = checkExtraLight;
        this.injectTaylorSwift = injectTaylorSwift;
        this.autoStartMusic = autoStartMusic;
    }

    public String[] getNickNames() {
        return nickNames;
    }

    public String getRandomNickname() {
        return getNickNames()[new Random().nextInt(getNickNames().length)];
    }

    public Map<String, String> getPlaylists() {
        return playlistSet.getPlaylists();
    }

    public String getDefaultPlaylist() {
        return playlistSet.getDefaultPlaylist();
    }

    public PlaylistSet getPlaylistSet() {
        return playlistSet;
    }

    public String[] getNfcTags() {
        return nfcTags;
    }

    public String[][] getGreetings() {
        return greetings;
    }

    public String[][] getFarewells() {
        return farewells;
    }

    public boolean isCheckExtraLight() {
        return checkExtraLight;
    }

    public boolean isInjectTaylorSwift() {
        return injectTaylorSwift;
    }

    public boolean isAutoStartMusic() {
        return autoStartMusic;
    }
}
