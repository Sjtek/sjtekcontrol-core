package nl.sjtek.sjtekcontrol.devices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Temperature {

    private static final int DELAY = 300000;
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?id=2747010";

    private Thread updateThread;
    private float tempInside = 0;
    private int tempOutside = -100;

    public Temperature() {
        this.updateThread = new Thread(new UpdateThread());
        this.updateThread.start();
    }

    public void setTempInside(float tempInside) {
        this.tempInside = tempInside;
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
                String response = download();
                if (!response.isEmpty()) {
                    try {
                        tempOutside = parseTemp(response);
                    } catch (JSONException e) {
                        tempOutside = -101;
                    }
                } else {
                    tempOutside = -102;
                }

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ignored) { }
            }
        }
    }

    private String download() {
        try {
            URL url = new URL(WEATHER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                StringBuilder stringBuffer = new StringBuilder();
                int character;
                while ((character = inputStream.read()) != -1) {
                    stringBuffer.append((char)character);
                }

               return new String(stringBuffer);
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    private int parseTemp(String response) throws JSONException {
        float temp = new JSONObject(response).getJSONObject("main").getLong("temp");
        return (int) (temp - 273);
    }
}
