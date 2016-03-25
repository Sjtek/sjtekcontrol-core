package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 28-1-16.
 */
public class NightModeResponse extends Response {

    private final boolean enabled;
    private final String nextDisable;

    public NightModeResponse(boolean enabled, String nextDisable) {
        type = this.getClass().getCanonicalName();
        this.enabled = enabled;
        this.nextDisable = nextDisable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getNextDisable() {
        return nextDisable;
    }
}
