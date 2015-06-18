package nl.sjtek.sjtekcontrol.handlers;

import nl.sjtek.sjtekcontrol.data.Response;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LightsHandler extends SjtekHandler {

    public static final String CONTEXT = "/api/lights";

    private Response response;

    private static boolean[] states = {false, false, false, false};

    public LightsHandler() {
    }

    public Response execute(String path[], boolean useVoice) {
        response = new Response();
        int lightUnit = 0;
        try {
            lightUnit = Integer.parseInt(path[3]);
            parse(path[4], lightUnit, useVoice);
        } catch (NumberFormatException e) {
            if ("off".equals(path[3])) {
                parse("off", 1, useVoice);
                parse("off", 2, useVoice);
            } else if ("on".equals(path[3])) {
                parse("on", 1, useVoice);
                parse("on", 2, useVoice);
            } else if ("info".equals(path[3])) {
                addStates();
            }  else {
                response.setCode(404);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            response.setCode(404);
        }
        addStates();
        return response;
    }

    private void parse(String action, int unit, boolean useVoice) {
        if ("off".equals(action)) {
            states[unit] = false;
            if (useVoice) Speech.speechAsync("Turning light " + unit + " off.");
            action("switch-" + unit + "-off");
        } else if ("on".equals(action)) {
            states[unit] = true;
            if (useVoice) Speech.speechAsync("Turning light " + unit + " on.");
            action("switch-" + unit + "-on");
        } else if ("toggle".equals(action)) {
            if (states[unit]) parse("off", unit, useVoice);
            else parse("on", unit, useVoice);
        } else {
            response.setCode(404);
        }
    }

    private void action(String action) {
        try {
            URL url = new URL("http://192.168.0.70/cgi-bin/" + action);
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

                String piResponse = stringBuffer.toString();
            } else {
                response.setCode(500);
                response.setError("Error while communicating with the Pi. It's response code was: " + responseCode);
            }
        } catch (IOException e) {
            response.setCode(500);
            response.setError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addStates() {
        JSONObject jsonObject = new JSONObject();

        for (int i = 1; i <= 3; i++) {
            jsonObject.put(String.valueOf(i), states[i]);
        }

        response.setLights(jsonObject);
    }

    public static boolean[] getStates() {
        return states;
    }
}
