package nl.sjtek.sjtekcontrol.settings;

import java.util.Random;

/**
 * Created by wouter on 28-11-15.
 */
public enum User {
    WOUTER,
    TIJN,
    KEVIN;

    static Random random = new Random();

    private UserSettings getUserSettings() {
        return SettingsManager.getInstance().getUserSettings().get(super.toString().toLowerCase());
    }

    public String[] getPlaylists() {
        return getUserSettings().getPlaylists();
    }

    public String[] getNickNames() {
        return getUserSettings().getNickNames();
    }

    public String getMusic() {
        if (getPlaylists().length == 0) return "";
        return getPlaylists()[0];
    }

    public String[] getNFCTags() {
        return getUserSettings().getNfcTags();
    }

    public String[][] getGreetings() {
        return getUserSettings().getGreetings();
    }

    public String[][] getFarewells() {
        return getUserSettings().getFarewells();
    }

    @Override
    public String toString() {
        return getNickNames()[random.nextInt(getNickNames().length)];
    }
}
