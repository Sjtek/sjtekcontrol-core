package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 21-11-16.
 */
public class ScreenResponse extends Response {

    private final State state;
    private final String header;
    private final String title;
    private final String subtitle;
    private final String currentTime;
    private final String nextTrigger;

    public ScreenResponse(State state, String header, String title, String subtitle, String currentTime, String nextTrigger) {
        this.currentTime = currentTime;
        this.nextTrigger = nextTrigger;
        type = ScreenResponse.class.getCanonicalName();
        this.state = state;
        this.header = header;
        this.title = title;
        this.subtitle = subtitle;
    }

    public State getState() {
        return state;
    }

    public String getHeader() {
        return header;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getNextTrigger() {
        return nextTrigger;
    }

    public enum State {
        FULLSCREEN,
        MUSIC,
        TV,
        COUNTDOWN,
        NEWYEAR
    }
}
