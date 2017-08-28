package nl.sjtek.control.core.events;

/**
 * Created by wouter on 22-3-17.
 */
public class NightModeEvent {

    private final boolean enabled;

    public NightModeEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
