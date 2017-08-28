package nl.sjtek.control.core.settings;

import com.google.gson.Gson;
import io.habets.javautils.Log;
import nl.sjtek.control.core.utils.DummyData;
import nl.sjtek.control.core.utils.FileUtils;
import nl.sjtek.control.data.settings.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by wouter on 22-11-15.
 */
public class SettingsManager {

    private static final String DEFAULT_PATH = "/var/sjtekcontrol/config.json";
    private static final String DEBUG = SettingsManager.class.getSimpleName();
    private static SettingsManager instance = DummyData.getSettingsManager();

    private final LoggingSettings logging;
    private final MusicSettings music;
    private final TVSettings tv;
    private final QuotesSettings quotes;
    private final LastFMSettings lastFM;
    private final Map<String, User> users;
    private final ScreenSettings screen;
    private final WeatherSettings weather;

    public SettingsManager(MusicSettings music,
                           TVSettings tv,
                           QuotesSettings quotes,
                           LastFMSettings lastFM,
                           Map<String, User> users,
                           ScreenSettings screen,
                           WeatherSettings weatherSettings,
                           LoggingSettings logging) {
        this.music = music;
        this.tv = tv;
        this.quotes = quotes;
        this.lastFM = lastFM;
        this.users = users;
        this.screen = screen;
        this.weather = weatherSettings;
        this.logging = logging;
    }

    public static SettingsManager getInstance() {
        return instance;
    }

    public String dump() {
        return new Gson().toJson(this, this.getClass());
    }

    public void reload() {
        reload(DEFAULT_PATH);
    }

    public void reload(String path) {
        Log.i(SettingsManager.class.getSimpleName(), "");
        SettingsManager newSettingsManager;
        try {
            String jsonString = FileUtils.readFile(path);
            if (!jsonString.isEmpty()) {
                newSettingsManager = new Gson().fromJson(jsonString, this.getClass());
                Log.i(DEBUG, "Settings reloaded (" + path + ")");
            } else {
                throw new IOException("Data empty");
            }
        } catch (FileNotFoundException e) {
            Log.e(DEBUG, "Could not load settings", e);
            try {
                FileUtils.writeFile(path, dump());
            } catch (IOException ignored) {
            }
            newSettingsManager = DummyData.getSettingsManager();
        } catch (IOException e) {
            Log.e(DEBUG, "Reload error", e);
            newSettingsManager = DummyData.getSettingsManager();
        }

        instance = newSettingsManager;
        Log.setListener(new Log.PrintListener(instance.getLoggingSettings().isPrintStackTrace()));
        Log.setLevel(Log.Level.valueOf(instance.getLoggingSettings().getLevel()));
        Log.i(DEBUG, "Log level " + instance.getLoggingSettings().getLevel() + " PrintStackTrace " + instance.getLoggingSettings().isPrintStackTrace());
    }

    public MusicSettings getMusic() {
        return music;
    }

    public TVSettings getTv() {
        return tv;
    }

    public QuotesSettings getQuotes() {
        return quotes;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUser(String name) {
        return users.get(name.toLowerCase());
    }

    public User getDefaultUser() {
        return getUser("default");
    }

    public LastFMSettings getLastFM() {
        return lastFM;
    }

    public ScreenSettings getScreen() {
        return screen;
    }

    public WeatherSettings getWeather() {
        return weather;
    }

    public LoggingSettings getLoggingSettings() {
        return logging;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
