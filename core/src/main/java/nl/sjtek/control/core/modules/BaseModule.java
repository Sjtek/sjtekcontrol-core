package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.events.StateEvent;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;


public abstract class BaseModule {

    private final String key;

    public BaseModule(String key) {
        this.key = key;
        Bus.regsiter(this);
    }

    final void dataChanged() {
        dataChanged(true);
    }

    final void dataChanged(boolean send) {
        Bus.post(new DataChangedEvent(key, getResponse(), send));
    }

    public void info(Arguments arguments) {
        dataChanged();
        if (arguments.useVoice()) Speech.speakAsync(getSummaryText());
    }

    public abstract Response getResponse();

    public abstract String getSummaryText();

    public final boolean isEnabled() {
        return isEnabled("default");
    }

    public boolean isEnabled(String user) {
        return false;
    }

    @Subscribe
    public final void onStateChangeEvent(StateEvent event) {
        onStateChanged(event.isEnabled(), event.getUser());
    }

    public void onStateChanged(boolean enabled, User user) {

    }

    public final BaseModule init() {
        dataChanged(false);
        return this;
    }

    public String getKey() {
        return key;
    }

    @Override
    public final String toString() {
        return super.toString();
    }
}
