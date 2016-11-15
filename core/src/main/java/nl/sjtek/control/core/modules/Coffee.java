package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.data.responses.CoffeeResponse;
import nl.sjtek.control.data.responses.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wouter on 14-11-16.
 */
public class Coffee extends BaseModule {

    private static final long HEAT_DELAY = 600000;
    private static final String URL = "http://10.10.0.2/cgi-bin/%s";

    private final OkHttpClient client = new OkHttpClient();

    private final Request requestOn;
    private final Request requestOff;
    private final TimerTask resetTask = new TimerTask() {
        @Override
        public void run() {
            dataChanged(true);
        }
    };

    private long lastTriggered;

    private Timer resetTimer = new Timer();

    public Coffee() {
        super("coffee");
        requestOn = new Request.Builder()
                .url(String.format(URL, "switch-4-on"))
                .build();
        requestOff = new Request.Builder()
                .url(String.format(URL, "switch-4-off"))
                .build();
    }

    public synchronized void start(Arguments arguments) {
        if (isHeated()) return;
        try {
            client.newCall(requestOff).execute().body().close();
            Thread.sleep(3000);
            client.newCall(requestOn).execute().body().close();
            lastTriggered = System.currentTimeMillis();
            resetTimer.schedule(resetTask, HEAT_DELAY + 2000);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        dataChanged(true);
    }

    @Override
    public Response getResponse() {
        return new CoffeeResponse(lastTriggered, isHeated());
    }

    private boolean isHeated() {
        return (System.currentTimeMillis() - lastTriggered) < HEAT_DELAY;
    }

    @Override
    public String getSummaryText() {
        return "The coffee is " + (isHeated() ? "heated." : "not heated.");
    }
}
