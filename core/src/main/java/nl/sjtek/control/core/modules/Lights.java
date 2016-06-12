package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.data.responses.LightsResponse;
import nl.sjtek.control.data.responses.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private static final String ROOT_URL_RGB = "http://10.10.0.4/cgi-bin/";

    private boolean states[] = {false, false, false, false, false};

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
        if (action(SWITCH1OFF, arguments.getCode()) == 200) {
            states[1] = false;
        }
    }

    public void toggle1on(Arguments arguments) {
        if (action(SWITCH1ON, arguments.getCode()) == 200) {
            states[1] = true;
        }
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
        if (action(SWITCH2OFF, arguments.getCode()) == 200) {
            states[2] = false;
        }
    }

    public void toggle2on(Arguments arguments) {
        if (action(SWITCH2ON, arguments.getCode()) == 200) {
            states[2] = true;
        }
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
        if (!arguments.getRgb().isEmpty() && action(arguments.getRgb()) == 200) {
            states[3] = false;
        } else if (action(SWITCH3OFF, arguments.getCode()) == 200) {
            states[3] = false;
        }
    }

    public void toggle3on(Arguments arguments) {
        if (!arguments.getRgb().isEmpty() && action(arguments.getRgb()) == 200) {
            states[3] = true;
        } else if (action(SWITCH3ON, arguments.getCode()) == 200) {
            states[3] = true;
        }
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
        if (action(SWITCH4OFF, arguments.getCode()) == 200) {
            states[4] = false;
        }
    }

    public void toggle4on(Arguments arguments) {
        if (action(SWITCH4ON, arguments.getCode()) == 200) {
            states[4] = true;
        }
    }

    private synchronized int action(String action, String code) {
        return send(ROOT_URL_NORMAL, action, code);
    }

    private synchronized int action(String rgb) {
        return send(ROOT_URL_RGB, "led", rgb);
    }

    private int send(String urlString, String action, String argument) {
        try {
            URL url = new URL(urlString + action + "?" + argument);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("GET - " + url);
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
        return new LightsResponse(states[1], states[2], states[3], states[4]);
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
}
