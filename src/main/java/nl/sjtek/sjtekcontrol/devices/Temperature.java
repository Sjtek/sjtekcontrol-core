package nl.sjtek.sjtekcontrol.devices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Temperature {

    private static final int DELAY = 300000;
    private static final String WEATHER_URL_OUTSIDE = "http://api.openweathermap.org/data/2.5/weather?id=2747010?appid=526d01226448149fe241d2991d0637c4";
    private static final String WEATHER_URL_INSIDE = "http://192.168.0.70/cgi-bin/temp";

    private float tempInside = 0;
    private int tempOutside = -100;

    public Temperature() {
        new Thread(new UpdateThread()).start();
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inside", tempInside);
        jsonObject.put("outside", tempOutside);
        return jsonObject.toString();
    }

    private class UpdateThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                tempOutside = parseOutside(download(WEATHER_URL_OUTSIDE));
                tempInside = parseInside(download(WEATHER_URL_INSIDE));
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private int parseOutside(String response) {
        if (!response.isEmpty()) {
            try {
                float temp = new JSONObject(response).getJSONObject("main").getLong("temp");
                return (int) (temp - 273);
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
                return Integer.valueOf(response);
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
}
