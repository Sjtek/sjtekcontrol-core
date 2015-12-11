package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.ApiHandler;
import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.json.JSONObject;

/**
 * Created by wouter on 28-11-15.
 */
public class NightMode extends BaseModule {

    private boolean enabled = false;

    public void enable(Arguments arguments) {
        ApiHandler.getInstance().getMusic().pause(new Arguments());
        if (arguments.useVoice()) Speech.speak("Entering night mode");
        enabled = true;
    }

    public void disable(Arguments arguments) {
        enabled = false;
        if (arguments.useVoice()) Speech.speak("Disabling night mode. Good morning");
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enabled", enabled);
        return jsonObject;
    }

    @Override
    public String getSummaryText() {
        return "Night mode is " + (enabled ? "enabled" : "disabled") + ".";
    }
}
