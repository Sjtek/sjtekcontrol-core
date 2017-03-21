package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 22-2-17.
 */
public class AudioResponse extends Response {

    private final State state;
    private final int activeDevices;

    public AudioResponse(State state, int activeDevices) {
        type = getClass().getCanonicalName();
        this.state = state;
        this.activeDevices = activeDevices;
    }

    public State getState() {
        return state;
    }

    public int getActiveDevices() {
        return activeDevices;
    }

    public enum State {
        ENABLED,
        DISABLED,
        SCHEDULED_DISABLED
    }
}
