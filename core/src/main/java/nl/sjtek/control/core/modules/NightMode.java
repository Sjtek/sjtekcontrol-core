package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.responses.NightModeResponse;
import nl.sjtek.control.data.responses.Response;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wouter on 28-11-15.
 */
public class NightMode extends BaseModule {

    private boolean enabled = false;
    private Timer timer;
    private Date nextDisable = Calendar.getInstance().getTime();

    public NightMode() {
        this.timer = new Timer();
        schedule();
    }

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
    public Response getResponse() {
        return new NightModeResponse(enabled, nextDisable.toString());
    }

    @Override
    public String getSummaryText() {
        return "Night mode is " + (enabled ? "enabled" : "disabled") + ".";
    }

    private void schedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        nextDisable = calendar.getTime();
        timer.schedule(new DisableTask(), nextDisable);
    }

    private class DisableTask extends TimerTask {

        @Override
        public void run() {
            enabled = false;
            schedule();
        }
    }
}
