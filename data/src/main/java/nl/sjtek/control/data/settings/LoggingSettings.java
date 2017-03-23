package nl.sjtek.control.data.settings;

/**
 * Created by wouter on 23-3-17.
 */
public class LoggingSettings extends Setting {

    private final String level;
    private final boolean printStackTrace;

    public LoggingSettings(String level, boolean printStackTrace) {
        this.level = level;
        this.printStackTrace = printStackTrace;
    }

    public String getLevel() {
        return level;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }
}
