package nl.sjtek.sjtekcontrol.settings;

import com.google.gson.Gson;
import nl.sjtek.sjtekcontrol.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by wouter on 22-11-15.
 */
public class SettingsManager {

    private static final String DEFAULT_PATH = "/var/sjtekcontrol/config.json";
    private static SettingsManager instance = new SettingsManager();

    private Music music = new Music();
    private TV tv = new TV();
    private Quotes quotes = new Quotes();
    private LastFM lastFM = new LastFM();

    private Map<String, User> users = User.getDefaults();

    private SettingsManager() {

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
            newSettingsManager = new SettingsManager();
        } catch (IOException e) {
            System.out.println("Reload error. IOException");
            e.printStackTrace();
            newSettingsManager = new SettingsManager();
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

    public LastFM getLastFM() {
        return lastFM;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public class TV {
        private String host = "192.168.0.66";
        private int port = 8080;
        private String key = "861540";

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getKey() {
            return key;
        }
    }

}
