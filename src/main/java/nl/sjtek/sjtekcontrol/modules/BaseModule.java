package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.json.JSONObject;

/**
 * Created by wouter on 11-12-15.
 */
public abstract class BaseModule {

    public void info(Arguments arguments) {
        if (arguments.useVoice()) Speech.speakAsync(getSummaryText());
    }

    public abstract JSONObject toJson();

    public abstract String getSummaryText();

    @Override
    public final String toString() {
        return super.toString();
    }
}
