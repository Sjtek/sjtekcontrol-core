package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 28-1-16.
 */
public class TimeResponse extends Response {

    private final String serverTime;

    public TimeResponse(String serverTime) {
        type = this.getClass().getCanonicalName();
        this.serverTime = serverTime;
    }

    public String getServerTime() {
        return serverTime;
    }
}
