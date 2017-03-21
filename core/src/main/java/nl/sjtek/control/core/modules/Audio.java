package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.AudioEvent;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.responses.AudioResponse;
import nl.sjtek.control.data.responses.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 22-2-17.
 */
public class Audio extends BaseModule {

    private static final int THRESHOLD = 1;
    private static final long DEACTIVATION_DELAY = 30;
    private static final int SWITCH_ID = 11;
    private final ScheduledThreadPoolExecutor executor;
    private boolean deactivationRunning = false;
    private boolean activated = false;
    private ScheduledFuture future;
    private Map<String, Boolean> devicesMap = new HashMap<>();

    public Audio(String audio) {
        super(audio);
        Bus.regsiter(this);
        executor = new ScheduledThreadPoolExecutor(5);
        executor.setRemoveOnCancelPolicy(true);
    }

    @Subscribe
    public void onEvent(AudioEvent event) {
        synchronized (this) {
            devicesMap.put(event.getKey(), event.isActive());
            int activeDevices = countDevices();
            if (activeDevices >= THRESHOLD && (deactivationRunning || !activated)) {
                activateAudio();
            } else if (activeDevices < THRESHOLD && activated) {
                deactivateAudio();
            }
            dataChanged();
        }
    }

    private void activateAudio() {
        System.out.println("Activating audio");
        if (future != null) future.cancel(false);
        deactivationRunning = false;
        send(true);
    }

    private void deactivateAudio() {
        if (deactivationRunning) return;
        System.out.println("Audio deactivation scheduled");
        deactivationRunning = true;
        future = executor.schedule(new Runnable() {
            @Override
            public void run() {
                send(false);
                deactivationRunning = false;
                dataChanged();
            }
        }, DEACTIVATION_DELAY, TimeUnit.SECONDS);
    }

    private synchronized void send(boolean state) {
        Bus.post(new LightEvent(SWITCH_ID, state));
        activated = state;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response getResponse() {
        AudioResponse.State state;
        if (deactivationRunning) {
            state = AudioResponse.State.SCHEDULED_DISABLED;
        } else if (activated) {
            state = AudioResponse.State.ENABLED;
        } else {
            state = AudioResponse.State.DISABLED;
        }
        return new AudioResponse(state, countDevices());
    }

    private int countDevices() {
        int counter = 0;
        for (Map.Entry<String, Boolean> entry : devicesMap.entrySet()) {
            if (entry.getValue()) counter++;
        }

        return counter;
    }

    @Override
    public String getSummaryText() {
        return "Audio is " + (activated ? "enabled" : "disabled") + ".";
    }

    @Override
    public boolean isEnabled(String user) {
        return false;
    }
}
