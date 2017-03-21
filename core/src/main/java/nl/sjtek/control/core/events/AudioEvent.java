package nl.sjtek.control.core.events;

/**
 * Created by wouter on 22-2-17.
 */
public class AudioEvent {
    private final String key;
    private final boolean active;

    public AudioEvent(String key, boolean active) {
        this.key = key;
        this.active = active;
    }

    public String getKey() {
        return key;
    }

    public boolean isActive() {
        return active;
    }
}
