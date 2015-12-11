package nl.sjtek.sjtekcontrol.utils;

import java.util.Random;

/**
 * Created by wouter on 28-11-15.
 */
public enum User {
    WOUTER,
    TIJN,
    KEVIN;

    static Random random = new Random();

    private SettingsManager.UserSettings getUserSettings() {
        return SettingsManager.getInstance().getUserSettings().get(super.toString());
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

    @Override
    public String toString() {
        return getNickNames()[random.nextInt(getNickNames().length)];
    }
}
