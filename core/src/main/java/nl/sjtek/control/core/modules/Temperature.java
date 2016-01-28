package nl.sjtek.control.core.modules;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Temperature extends BaseModule {

    private static final int DELAY_INSIDE = 300000;
    private static final int DELAY_OUTSIDE = 3600000;
    private static final String WEATHER_URL_OUTSIDE = "http://3ddev.nl/watson/api/weather.php?city=Son";
    private static final String WEATHER_URL_INSIDE = "http://192.168.0.70/cgi-bin/temp";

    private float tempInside = 0;
    private int tempOutside = -100;
    private float humidity = 0;
    private String description = "error";
    private String icon = "";

    public Temperature() {
        Timer timerInside = new Timer();
        timerInside.scheduleAtFixedRate(new InsideTask(), 0, DELAY_INSIDE);
        Timer timerOutside = new Timer();
        timerOutside.scheduleAtFixedRate(new OutsideTask(), 0, DELAY_OUTSIDE);
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inside", tempInside);
        jsonObject.put("outside", tempOutside);
        jsonObject.put("humidity", humidity);
        jsonObject.put("description", description);
        jsonObject.put("icon", icon);
        return jsonObject;
    }

    @Override
    public String getSummaryText() {
        return "The temperature inside is " + tempInside + " degrees, and outside " + tempOutside + " degrees.";
    }

    private int parseOutside(String response) {
        if (!response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                float temp = jsonObject.getLong("temp");
                humidity = jsonObject.getLong("humidity");
                description = jsonObject.getString("description");
                icon = jsonObject.getString("icon");
                return (int) temp;
            } catch (JSONException e) {
                return -101;
            }
        } else {
            return -102;
        }
    }

    private int parseInside(String response) {
        if (!response.isEmpty()) {
            try {
                float temp = Float.valueOf(response);
                return (int) temp;
            } catch (NumberFormatException e) {
                return -101;
            }
        } else {
            return -102;
        }
    }

    private String download(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println("" + responseCode + " - downloaded " + stringUrl);
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                StringBuilder stringBuffer = new StringBuilder();
                int character;
                while ((character = inputStream.read()) != -1) {
                    stringBuffer.append((char) character);
                }

                return new String(stringBuffer);
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    private class InsideTask extends TimerTask {

        @Override
        public void run() {
            tempInside = parseInside(download(WEATHER_URL_INSIDE));
        }
    }

    private class OutsideTask extends TimerTask {

        @Override
        public void run() {
            tempOutside = parseOutside(download(WEATHER_URL_OUTSIDE));
        }
    }
}
