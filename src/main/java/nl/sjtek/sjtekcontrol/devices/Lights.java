package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Lights {

    private static final String SWITCH1ON  = "switch-1-on";
    private static final String SWITCH1OFF = "switch-1-off";
    private static final String SWITCH2ON  = "switch-2-on";
    private static final String SWITCH2OFF = "switch-2-off";
    private static final String SWITCH3ON  = "switch-3-on";
    private static final String SWITCH3OFF = "switch-3-off";

    private static final String ROOT_URL = "http://192.168.0.70/cgi-bin/";

    private boolean states[] = { false, false, false, false };

    public Lights() {

    }

    public boolean isOn() {
        return states[1] || states[2];
    }

    public void toggle(Arguments arguments) {
        if (states[1] || states[2]) {
            toggle1off(arguments);
            toggle2off(arguments);
        } else {
            toggle1on(arguments);
            toggle2on(arguments);
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
        if (action(SWITCH1OFF) == 200) {
            states[1] = false;
        }
    }

    public void toggle1on(Arguments arguments) {
        if (action(SWITCH1ON) == 200) {
            states[1] = true;
        }
    }

    public void toggle2(Arguments arguments) {
        if (states[2]) {
            toggle2off(arguments);
        } else {
            toggle2on(arguments);
        }
    }

    public void toggle2off(Arguments arguments) {
        if (action(SWITCH2OFF) == 200) {
            states[2] = false;
        }
    }

    public void toggle2on(Arguments arguments) {
        if (action(SWITCH2ON) == 200) {
            states[2] = true;
        }
    }

    public void toggle3(Arguments arguments) {
        if (states[3]) {
            toggle3off(arguments);
        } else {
            toggle3on(arguments);
        }
    }

    public void toggle3off(Arguments arguments) {
        if (action(SWITCH3OFF) == 200) {
            states[3] = false;
        }
    }

    public void toggle3on(Arguments arguments) {
        if (action(SWITCH3ON) == 200) {
            states[3] = true;
        }
    }

    private synchronized int action(String action) {
        try {
            URL url = new URL(ROOT_URL + action);
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
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", states[1]);
        jsonObject.put("2", states[2]);
        jsonObject.put("3", states[3]);
        return jsonObject.toString();
    }
}
