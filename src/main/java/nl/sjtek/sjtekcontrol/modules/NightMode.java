package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.ApiHandler;
import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.json.JSONObject;

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
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enabled", enabled);
        jsonObject.put("nextDisable", nextDisable.toString());
        return jsonObject;
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
