package nl.sjtek.control.core.events;

import nl.sjtek.control.data.settings.User;

/**
 * Created by wouter on 22-3-17.
 */
public class StateEvent {

    private final boolean enabled;
    private final User user;

    public StateEvent(boolean enabled, User user) {
        this.enabled = enabled;
        this.user = user;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User getUser() {
        return user;
    }
}
