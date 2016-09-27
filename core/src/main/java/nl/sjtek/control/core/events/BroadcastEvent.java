package nl.sjtek.control.core.events;

/**
 * Created by wouter on 25-8-16.
 */
public class BroadcastEvent {
    private final String data;

    public BroadcastEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
