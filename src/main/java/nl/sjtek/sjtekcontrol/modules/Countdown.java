package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.ApiHandler;
import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wouter on 31-12-15.
 */
public class Countdown extends BaseModule {


    public Countdown() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.JANUARY, 1, 0, 0, 0);

        Timer countdownTimer = new Timer();
        countdownTimer.schedule(new CountdownTask(), calendar.getTime());
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject();
    }

    @Override
    public String getSummaryText() {
        return "";
    }

    private class CountdownTask extends TimerTask {

        Arguments dummyArguments = new Arguments();

        @Override
        public void run() {
            Arguments arguments = new Arguments();
            arguments.setUrl("spotify:user:1133212423:playlist:2dVyM9GfwN00h8grBVRXFj");
            Music music = ApiHandler.getInstance().getMusic();
            music.clear(dummyArguments);
            music.next(arguments);
            music.play(dummyArguments);
            Speech.speakAsync("Watson wishes you all a happy new year!");

            toggleOn();
            for (int i = 0; i < 5; i++) {
                toggleOff();
                toggleOn();
            }
        }

        private void toggleOn() {
            Lights lights = ApiHandler.getInstance().getLights();
            lights.toggle1on(dummyArguments);
            lights.toggle2on(dummyArguments);
        }

        private void toggleOff() {
            Lights lights = ApiHandler.getInstance().getLights();
            lights.toggle1off(dummyArguments);
            lights.toggle2off(dummyArguments);
        }
    }
}
