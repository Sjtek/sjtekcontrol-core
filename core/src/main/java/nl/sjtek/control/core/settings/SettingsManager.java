package nl.sjtek.control.core.settings;

import com.google.gson.Gson;
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
    private static SettingsManager instance = DummyData.getSettingsManager();

    private final Music music;
    private final TV tv;
    private final Quotes quotes;
    private final LastFM lastFM;
    private final Map<String, User> users;

    public SettingsManager(Music music, TV tv, Quotes quotes, LastFM lastFM, Map<String, User> users) {
        this.music = music;
        this.tv = tv;
        this.quotes = quotes;
        this.lastFM = lastFM;
        this.users = users;
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
        System.out.println();
        System.out.println("Reloading settings...");
        SettingsManager newSettingsManager;
        try {
            String jsonString = FileUtils.readFile(path);
            if (!jsonString.isEmpty()) {
                newSettingsManager = new Gson().fromJson(jsonString, this.getClass());
                System.out.println("Reload completed.");
            } else {
                System.out.println("Reload error. Data is empty.");
                throw new IOException("Data empty");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Reload error. File not found");
            try {
                FileUtils.writeFile(path, dump());
            } catch (IOException ignored) {
            }
            newSettingsManager = DummyData.getSettingsManager();
        } catch (IOException e) {
            System.out.println("Reload error. IOException");
            e.printStackTrace();
            newSettingsManager = DummyData.getSettingsManager();
        }

        instance = newSettingsManager;
    }

    public Music getMusic() {
        return music;
    }

    public TV getTv() {
        return tv;
    }

    public Quotes getQuotes() {
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

    public LastFM getLastFM() {
        return lastFM;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
