package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import io.habets.javautils.Log;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.ampq.events.SensorEvent;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 23-3-17.
 */
public class Motion extends BaseModule {

    private final String DEBUG = MotionHandler.class.getSimpleName();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private final Map<Integer, MotionHandler> handlerMap = new HashMap<>();
    private long timeDisabled = 0;

    public Motion(String key) {
        super(key);
        handlerMap.put(1, new MotionHandler(5, 2));
        handlerMap.put(2, new MotionHandler(7, 2));
        Bus.regsiter(this);
    }

    @Override
    public void onStateChanged(boolean enabled, User user) {
        if (enabled) {
            handlerMap.forEach((integer, motionHandler) -> motionHandler.onMotion());
        } else {
            timeDisabled = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onSensorEvent(SensorEvent event) {
        if (System.currentTimeMillis() - timeDisabled < 10000) {
            Log.d(DEBUG, "Motion disabled");
            return;
        }

        if (event.getType() == SensorEvent.Type.MOTION) {
            MotionHandler handler = handlerMap.get(event.getId());
            if (handler != null) handler.onMotion();
        }
    }

    private boolean shouldTurnOn() {
        Lights lights = ApiHandler.getInstance().getLights();
        if (lights.getToggle1()) return false;

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Europe/Amsterdam");
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(now, zoneId);
        int hour = dateTime.getHour();
        return hour >= 20 || hour <= 1 || hour == 6 || hour == 7;
    }

    @Override
    public Response getResponse() {
        return null;
    }

    @Override
    public String getSummaryText() {
        return null;
    }

    private class MotionHandler {

        private final int id;
        private final int delay;
        private ScheduledFuture future;
        private boolean state = false;

        private MotionHandler(int id, int delay) {
            this.id = id;
            this.delay = delay;
        }

        public void onMotion() {
            if (shouldTurnOn()) {
                Log.d(DEBUG, "Motion for light 1, light allowed");
                if (future != null) future.cancel(false);
                future = executor.schedule(this::turnOff, delay, TimeUnit.MINUTES);
                if (!state) turnOn();
            } else {
                Log.d(DEBUG, "Motion for light 1, light not allowed");
            }
        }

        private void turnOn() {
            state = true;
            Bus.post(new LightEvent(id, true));
            Log.d(DEBUG, "Light " + id + " on");
        }

        private void turnOff() {
            state = false;
            Bus.post(new LightEvent(id, false));
            Log.d(DEBUG, "Light " + id + " off");
        }
    }
}
