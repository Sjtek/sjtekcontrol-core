package nl.sjtek.sjtekcontrol.settings;

import com.google.gson.Gson;

import java.io.*;
import java.util.Map;

/**
 * Created by wouter on 22-11-15.
 */
public class SettingsManager {

    private static final String DEFAULT_PATH = "/etc/sjtekcontrol/config.json";
    private static SettingsManager instance = new SettingsManager();

    private Music music = new Music();
    private TV tv = new TV();
    private Quotes quotes = new Quotes();

    private Map<String, UserSettings> userSettings = UserSettings.getDefaults();

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
            String jsonString = readFile(path);
            if (!jsonString.isEmpty()) {
                newSettingsManager = new Gson().fromJson(jsonString, this.getClass());
                System.out.println("Reload completed.");
            } else {
                System.out.println("Reload error. Data is empty.");
                throw new IOException("Data empty");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Reload error. File not found");
            writeFile(path);
            newSettingsManager = new SettingsManager();
        } catch (IOException e) {
            System.out.println("Reload error. IOException");
            e.printStackTrace();
            newSettingsManager = new SettingsManager();
        }

        instance = newSettingsManager;
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

    private void writeFile(String path) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new PrintWriter(new FileWriter(path)))) {
            bufferedWriter.write(dump());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Map<String, UserSettings> getUserSettings() {
        return userSettings;
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
