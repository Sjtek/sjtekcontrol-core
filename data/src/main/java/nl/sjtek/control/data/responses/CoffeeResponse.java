package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 14-11-16.
 */
public class CoffeeResponse extends Response {

    private final long lastTriggered;
    private final boolean heated;

    public CoffeeResponse(long lastTriggered, boolean heated) {
        type = getClass().getCanonicalName();
        this.lastTriggered = lastTriggered;
        this.heated = heated;
    }

    public long getLastTriggered() {
        return lastTriggered;
    }

    public boolean isHeated() {
        return heated;
    }
}
