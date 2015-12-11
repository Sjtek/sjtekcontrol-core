package nl.sjtek.sjtekcontrol.data;

import com.google.gson.Gson;

import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by wouter on 22-11-15.
 */
public class SettingsManager {

    private static final String DEFAULT_PATH = "/etc/sjtekcontrol/config.json";
    private static SettingsManager instance = new SettingsManager();

    private Music music = new Music();
    private TV tv = new TV();
    private Quotes quotes = new Quotes();

    private Map<String, UserSettings> userSettings;

    private SettingsManager() {

        userSettings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        userSettings.put("Wouter", new UserSettings(
                new String[]{
                        "sir wouter",
                        "lord habets"
                },
                new String[]{
                        "spotify:user:1133212423:playlist:6GoCxjOJ5pXgr74Za0z9bt"
                }
        ));
        userSettings.put("Tijn", new UserSettings(
                new String[]{
                        "3D",
                        "master renders"
                },
                new String[]{
                        "spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW"
                }
        ));
        userSettings.put("Kevin", new UserSettings(
                new String[]{
                        "kevin"
                },
                new String[]{
                        "spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz"
                }
        ));
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

    public class Music {
        private String mpdHost = "mopidy";
        private int mpdPort = 6600;

        private String defaultPlaylist = "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW";
        private boolean taylorSwiftInject = true;
        private String taylorSwiftPath = "Local media/Taylor Swift";
        private int volumeNeutral = 10;
        private int volumeStepUp = 3;
        private int volumeStepDown = 3;

        public String getMpdHost() {
            return mpdHost;
        }

        public int getMpdPort() {
            return mpdPort;
        }

        public String getDefaultPlaylist() {
            return defaultPlaylist;
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

    public class Quotes {
        private String[] quotes = {
                "Alleen massaproductie",
                "Dien mam",
                "Mwoah, Gertje",
                "Analysamson",
                "Een frietkraam dat geen frieten verkoopt",
                "Moet hebben, afblijven",
                "Sjtek masterrace",
                "Ja joa",
                "10/10 would yolo again",
        };

        public String[] getQuotes() {
            return quotes;
        }

        public String getQuote() {
            return getQuotes()[new Random().nextInt(getQuotes().length)];
        }
    }

    public class UserSettings {
        private final String[] nickNames;
        private final String[] playlists;

        public UserSettings(String[] nickNames, String[] playlists) {
            this.nickNames = nickNames;
            this.playlists = playlists;
        }

        public String[] getNickNames() {
            return nickNames;
        }

        public String[] getPlaylists() {
            return playlists;
        }
    }
}
