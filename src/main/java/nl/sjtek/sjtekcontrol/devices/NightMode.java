package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.handlers.ApiHandler;
import nl.sjtek.sjtekcontrol.utils.Speech;

/**
 * Created by wouter on 28-11-15.
 */
public class NightMode {

    private boolean enabled = false;

    public void enable(Arguments arguments) {
        ApiHandler.getInstance().getMusic().pause(new Arguments());
        if (arguments.isUseVoice()) Speech.speak("Entering night mode");
        enabled = true;
    }

    public void disable(Arguments arguments) {
        enabled = false;
        if (arguments.isUseVoice()) Speech.speak("Disabling night mode. Good morning");
    }

    public boolean isEnabled() {
        return enabled;
    }
}
