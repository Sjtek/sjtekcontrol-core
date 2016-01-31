package nl.sjtek.control.data.settings;

/**
 * Created by wouter on 10-1-16.
 */
public class TVSettings extends Setting {
    private final String host;
    private final int port;
    private final String key;

    public TVSettings(String host, int port, String key) {
        this.host = host;
        this.port = port;
        this.key = key;
    }

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
