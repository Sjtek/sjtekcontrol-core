package nl.sjtek.sjtekcontrol.data;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wouter on 22-11-15.
 */
public class Settings {

    private static final String DEFAULT_PATH = "/etc/sjtekcontrol.json";
    private static Settings settings = new Settings();

    private String mpdHost = "mopidy";
    private int mpdPort = 6600;

    private String tvHost = "192.168.0.66";
    private int tvPort = 8080;
    private int tvKey = 861540;

    private String musicDefaultPlaylist = "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW";
    private String musicDefaultRave = "spotify:track:3QKv87XsylJWvTCzssDvnr";
    private String musicTaylorSwiftPath = "Local media/Taylor Swift";
    private boolean musicInjectTaylorSwift = true;
    private int musicVolumeNeutral = 10;
    private int musicVolumeUpStep = 3;
    private int musicVolumeDownStep = 3;

    private int speechVolume = 50;

    private String[] quotes = {};

    private Settings() {

    }

    public static Settings getInstance() {
        return settings;
    }

    public void reload() {
        reload(DEFAULT_PATH);
    }

    public void reload(String path) {
        Settings newSettings;
        try {
            String jsonString = readFile(path);
            if (!jsonString.isEmpty()) {
                newSettings = new Gson().fromJson(jsonString, this.getClass());
            } else {
                throw new IOException("Data empty");
            }
        } catch (IOException e) {
            e.printStackTrace();
            newSettings = new Settings();
        }

        this.mpdHost = newSettings.getMpdHost();
        this.mpdPort = newSettings.getMpdPort();
        this.tvHost = newSettings.getTvHost();
        this.tvPort = newSettings.getTvPort();
        this.tvKey = newSettings.getTvKey();
        this.musicDefaultPlaylist = newSettings.getMusicDefaultPlaylist();
        this.musicDefaultRave = newSettings.getMusicDefaultRave();
        this.musicTaylorSwiftPath = newSettings.getMusicTaylorSwiftPath();
        this.musicInjectTaylorSwift = newSettings.getMusicInjectTaylorSwift();
        this.musicVolumeNeutral = newSettings.getMusicVolumeNeutral();
        this.musicVolumeUpStep = newSettings.getMusicVolumeUpStep();
        this.musicVolumeDownStep = newSettings.getMusicVolumeDownStep();
        this.speechVolume = newSettings.getSpeechVolume();
    }

    private String readFile(String path) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        }
    }

    public String getMpdHost() {
        return mpdHost;
    }

    public int getMpdPort() {
        return mpdPort;
    }

    public String getTvHost() {
        return tvHost;
    }

    public int getTvPort() {
        return tvPort;
    }

    public int getTvKey() {
        return tvKey;
    }

    public String getMusicDefaultPlaylist() {
        return musicDefaultPlaylist;
    }

    public String getMusicDefaultRave() {
        return musicDefaultRave;
    }

    public String getMusicTaylorSwiftPath() {
        return musicTaylorSwiftPath;
    }

    public boolean getMusicInjectTaylorSwift() {
        return musicInjectTaylorSwift;
    }

    public int getMusicVolumeNeutral() {
        return musicVolumeNeutral;
    }

    public int getMusicVolumeUpStep() {
        return musicVolumeUpStep;
    }

    public int getMusicVolumeDownStep() {
        return musicVolumeDownStep;
    }

    public int getSpeechVolume() {
        return speechVolume;
    }

    public String[] getQuotes() {
        return quotes;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
