package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.responses.Response;


public abstract class BaseModule {

    private final String key;

    public BaseModule(String key) {
        this.key = key;
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

    public abstract boolean isEnabled(String user);

    public final BaseModule init() {
        dataChanged(false);
        return this;
    }

    @Override
    public final String toString() {
        return super.toString();
    }
}
