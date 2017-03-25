package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import io.habets.javautils.Log;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.ampq.events.SensorEvent;
import nl.sjtek.control.data.responses.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 23-3-17.
 */
public class Motion extends BaseModule {

    private static final int HOUR_DISABLE = 1;
    private static final int HOUR_ENABLE = 9;
    private final String DEBUG = MotionHandler.class.getSimpleName();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private final Map<Integer, MotionHandler> handlerMap = new HashMap<>();

    public Motion(String key) {
        super(key);
//        handlerMap.put(1, new MotionHandler(5, 2));
//        handlerMap.put(2, new MotionHandler(7, 2));
        Bus.regsiter(this);
    }

    @Subscribe
    public void onSensorEvent(SensorEvent event) {
        if (event.getType() == SensorEvent.Type.MOTION) {
            MotionHandler handler = handlerMap.get(event.getId());
            if (handler != null) handler.onMotion();
        }
    }

    private boolean shouldTurnOn() {
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour();
        return hour < HOUR_DISABLE && hour > HOUR_ENABLE;
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
                if (future != null) future.cancel(false);
                future = executor.schedule(this::turnOff, delay, TimeUnit.MINUTES);
                if (!state) turnOn();
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
