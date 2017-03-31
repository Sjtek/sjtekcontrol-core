package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.ampq.events.LightStateEvent;
import nl.sjtek.control.data.responses.LightsResponse;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"UnusedParameters", "unused"})
public class Lights extends BaseModule {

    private static final String SWITCH1ON = "switch-1-on";
    private static final String SWITCH1OFF = "switch-1-off";
    private static final String SWITCH2ON = "switch-2-on";
    private static final String SWITCH2OFF = "switch-2-off";
    private static final String SWITCH3ON = "switch-3-on";
    private static final String SWITCH3OFF = "switch-3-off";
    private static final String SWITCH4ON = "switch-4-on";
    private static final String SWITCH4OFF = "switch-4-off";

    private static final String ROOT_URL_NORMAL = "http://10.10.0.2/cgi-bin/";
    private static final String ROOT_URL_RGB = "http://10.10.0.4:8000/";

    private static final long UPDATE_DELAY = 100;
    private final ScheduledThreadPoolExecutor executor;
    private ScheduledFuture future;

    private boolean states[] = new boolean[10];

    public Lights(String key) {
        super(key);
        executor = new ScheduledThreadPoolExecutor(2);
    }

    @Subscribe
    public void onLightStateUpdate(LightStateEvent event) {
        states[event.getId()] = event.isEnabled();
        delayedUpdate();
    }

    @Override
    public void onStateChanged(boolean enabled, User user) {
        Arguments arguments = new Arguments();
        if (enabled) {
            toggle1on(arguments);
            toggle2on(arguments);
            toggle5on(arguments);
            if (user.isCheckExtraLight()) {
                toggle3on(arguments);
                toggle4on(arguments);
            }
        } else {
            toggle1off(arguments);
            toggle2off(arguments);
            toggle5off(arguments);
            toggle7off(arguments);
            if (user.isCheckExtraLight()) {
                toggle3off(arguments);
                toggle4off(arguments);
            }
        }
    }

    private void delayedUpdate() {
        if (future == null || future.isCancelled() || future.isDone()) {
            future = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    dataChanged();
                }
            }, UPDATE_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isOn() {
        return states[1] || states[2];
    }

    public void toggle(Arguments arguments) {
        if (states[1] || states[2] || states[5]) {
            toggle1off(arguments);
            toggle2off(arguments);
            toggle5off(arguments);
        } else {
            toggle1on(arguments);
            toggle2on(arguments);
            toggle5on(arguments);
        }
    }

    public void toggle1(Arguments arguments) {
        if (states[1]) {
            toggle1off(arguments);
        } else {
            toggle1on(arguments);
        }
    }

    public void toggle1off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(1, false));
        states[1] = false;
        dataChanged();
    }

    public void toggle1on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(1, true));
        states[1] = true;
        dataChanged();
    }

    public boolean getToggle1() {
        return states[1];
    }

    public void toggle2(Arguments arguments) {
        if (states[2]) {
            toggle2off(arguments);
        } else {
            toggle2on(arguments);
        }
    }

    public void toggle2off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(2, false));
        states[2] = false;
        dataChanged();
    }

    public void toggle2on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(2, true));
        states[2] = true;
        dataChanged();
    }

    public boolean getToggle2() {
        return states[2];
    }

    public void toggle3(Arguments arguments) {
        if (states[3]) {
            toggle3off(arguments);
        } else {
            toggle3on(arguments);
        }
    }

    public void toggle3off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(3, false));
        states[3] = false;
        dataChanged();
    }

    public void toggle3on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(3, true));
        states[3] = true;
        dataChanged();
    }

    public boolean getToggle3() {
        return states[3];
    }

    public void toggle4(Arguments arguments) {
        if (states[4]) {
            toggle4off(arguments);
        } else {
            toggle4on(arguments);
        }
    }

    public void toggle4off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(4, false));
    }

    public void toggle4on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(4, true));
    }

    public void toggle5(Arguments arguments) {
        if (states[5]) {
            toggle5off(arguments);
        } else {
            toggle5on(arguments);
        }
    }

    public void toggle5on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(5, true));
    }

    public void toggle5off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(5, false));
    }

    public void toggle6(Arguments arguments) {
        if (states[6]) {
            toggle6off(arguments);
        } else {
            toggle6on(arguments);
        }
    }

    public void toggle6on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(6, true));
    }

    public void toggle6off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(6, false));
    }

    public boolean getToggle6() {
        return states[6];
    }

    public void toggle7(Arguments arguments) {
        if (states[7]) {
            toggle7off(arguments);
        } else {
            toggle7on(arguments);
        }
    }

    public void toggle7on(Arguments arguments) {
        Bus.post(arguments.getLightEvent(7, true));
    }

    public void toggle7off(Arguments arguments) {
        Bus.post(arguments.getLightEvent(7, false));
    }

    @Deprecated
    private synchronized int action(String action, String code) {
        return send(ROOT_URL_NORMAL, action, code);
    }

    @Deprecated
    private synchronized int actionLedStrip(Arguments arguments, boolean state) {
        if (!arguments.getRgb().isEmpty()) {
            return send(ROOT_URL_RGB, "led", "rgb=" + arguments.getRgb());
        } else if (!arguments.getCode().isEmpty()) {
            return send(ROOT_URL_RGB, "led", "code=" + arguments.getCode());
        } else if (state) {
            return send(ROOT_URL_RGB, "led", "rgb=255,50,0");
        } else {
            return send(ROOT_URL_RGB, "led", "rgb=0,0,0");
        }
    }

    @Deprecated
    private int send(String urlString, String action, String argument) {
        try {
            URL url = new URL(urlString + action + "?" + argument);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                StringBuilder stringBuffer = new StringBuilder();
                int character;
                while ((character = inputStream.read()) != -1) {
                    stringBuffer.append(character);
                }

                String stringResponse = stringBuffer.toString();
                return 200;
            } else {
                return responseCode;
            }
        } catch (IOException e) {
            return 500;
        }
    }

    @Override
    public Response getResponse() {
        return new LightsResponse(states[1], states[2], states[3], states[4], states[5], states[6], states[7]);
    }

    @Override
    public String getSummaryText() {
        String result =
                (states[1] ? "1, " : "") +
                        (states[2] ? "2, " : "") +
                        (states[3] ? "3, " : "") +
                        (states[4] ? "4, " : "");
        if (result.isEmpty())
            return "The lights are turned off.";
        return "The following lights are turned on: " + result + ".";
    }

    @Override
    public boolean isEnabled(String user) {
        boolean extra = SettingsManager.getInstance().getUser(user).isCheckExtraLight();
        return states[1] || states[2] || (extra && states[3]) || (extra && states[4]);
    }
}
