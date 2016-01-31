package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.responses.Response;

/**
 * Created by wouter on 11-12-15.
 */
public abstract class BaseModule {

    public void info(Arguments arguments) {
        if (arguments.useVoice()) Speech.speakAsync(getSummaryText());
    }

    public abstract Response getResponse();

    public abstract String getSummaryText();

    @Override
    public final String toString() {
        return super.toString();
    }
}