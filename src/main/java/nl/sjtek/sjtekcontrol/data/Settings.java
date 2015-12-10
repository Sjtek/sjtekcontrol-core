package nl.sjtek.sjtekcontrol.data;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Created by wouter on 22-11-15.
 */
public class Settings {

    private static final String DEFAULT_PATH = "/etc/sjtekcontrol.json";
    private static Settings instance = new Settings();

    private Music music = new Music();
    private TV tv = new TV();
    private Quotes quotes = new Quotes();

    private Settings() {
        dump();
    }

    public static Settings getInstance() {
        return instance;
    }

    public void dump() {
        System.out.println("Dump: " + new Gson().toJson(this));
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
        } catch (FileNotFoundException e) {

            newSettings = new Settings();
        } catch (IOException e) {
            e.printStackTrace();
            newSettings = new Settings();
        }

        instance = newSettings;
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

    public Music getMusic() {
        return music;
    }

    public TV getTv() {
        return tv;
    }

    public Quotes getQuotes() {
        return quotes;
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
}
