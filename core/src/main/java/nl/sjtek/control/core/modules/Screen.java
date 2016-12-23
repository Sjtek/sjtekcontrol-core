package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.ScreenResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wouter on 21-11-16.
 */
public class Screen extends BaseModule {

    private final String header = "Sjtek Enterprises presents:";
    private final String title = "Sjtek 2016 fissa";
    private final String subtitle = "";
    private final String playlist = "spotify:user:1133212423:playlist:42MtUpC8Zbs3LN14KXQrZK";
    private final Timer timer = new Timer();
    private final Date triggerDate;
    private ScreenResponse.State state = ScreenResponse.State.FULLSCREEN;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            triggered(null);
        }
    };

    public Screen(String key) {
        super(key);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 31, 23, 0, 0);
        triggerDate = calendar.getTime();
        timer.schedule(timerTask, triggerDate);
        refresh(null);
    }

    public void fullscreen(Arguments arguments) {
        this.state = ScreenResponse.State.FULLSCREEN;
        dataChanged(true);
    }

    public void music(Arguments arguments) {
        this.state = ScreenResponse.State.MUSIC;
        dataChanged(true);
    }

    public void tv(Arguments arguments) {
        this.state = ScreenResponse.State.TV;
        dataChanged(true);
    }

    public void countdown(Arguments arguments) {
        this.state = ScreenResponse.State.COUNTDOWN;
        dataChanged(true);
    }

    public void newyear(Arguments arguments) {
        this.state = ScreenResponse.State.NEWYEAR;
        dataChanged(true);
    }

    public void refresh(Arguments arguments) {
        dataChanged(true);
    }

    public void triggered(Arguments arguments) {
        this.state = ScreenResponse.State.NEWYEAR;
        dataChanged(true);
        Speech.speakAsync("Triggered");
        Music music = ApiHandler.getInstance().getMusic();
        ApiHandler.getInstance().getMusic().start(new Arguments("?noshuffle&url=" + playlist), false);
    }

    @Override
    public Response getResponse() {
        return new ScreenResponse(state, header, title, subtitle, Calendar.getInstance().getTime().toString(), triggerDate.toString());
    }

    @Override
    public String getSummaryText() {
        return "Current state: " + state.toString();
    }
}
