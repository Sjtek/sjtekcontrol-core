package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.ampq.events.LightEvent;
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
//            triggered(null);
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
        ApiHandler.getInstance().getMusic().pause(new Arguments());
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
        ApiHandler.getInstance().getMusic().start(new Arguments("noshuffle&url=" + playlist), false);
        new DiscoThread().start();
    }

    @Override
    public Response getResponse() {
        return new ScreenResponse(state, header, title, subtitle, Calendar.getInstance().getTime().toString(), triggerDate.toString());
    }

    @Override
    public String getSummaryText() {
        return "Current state: " + state.toString();
    }

    private class DiscoThread extends Thread {
        @Override
        public void run() {
            super.run();
            int r = 255;
            int g = 0;
            int b = 0;

            try {
                for (int i = 0; i < 480; i++) {
                    int step = i % 6;
                    switch (step) {
                        case 0:
                            r = 255;
                            g = 0;
                            b = 0;
                            break;
                        case 1:
                            r = 255;
                            g = 255;
                            b = 0;
                            break;
                        case 2:
                            r = 0;
                            g = 255;
                            b = 0;
                            break;
                        case 3:
                            r = 0;
                            g = 255;
                            b = 255;
                            break;
                        case 4:
                            r = 0;
                            g = 0;
                            b = 255;
                            break;
                        case 5:
                            r = 255;
                            g = 0;
                            b = 255;
                            break;
                    }
                    Bus.post(new LightEvent(3, r, g, b));
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bus.post(new LightEvent(3, 255, 150, 0));
        }
    }
}
