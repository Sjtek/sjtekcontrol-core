package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.responses.CoffeeResponse;
import nl.sjtek.control.data.responses.Response;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 14-11-16.
 */
public class Coffee extends BaseModule {

    private static final long HEAT_DELAY = 600000;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private long lastTriggered;

    public Coffee(String key) {
        super(key);
    }

    public synchronized void start(Arguments arguments) {
        if (!isHeated()) {
            new EnableThread().start();
        }
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

    @Override
    public boolean isEnabled(String user) {
        return false;
    }

    private class EnableThread extends Thread {
        @Override
        public void run() {
            super.run();
            Bus.post(new LightEvent(10, false));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            Bus.post(new LightEvent(10, true));
            lastTriggered = System.currentTimeMillis();
            dataChanged(true);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    dataChanged(true);
                }
            }, HEAT_DELAY + 100, TimeUnit.MILLISECONDS);
        }
    }
}
