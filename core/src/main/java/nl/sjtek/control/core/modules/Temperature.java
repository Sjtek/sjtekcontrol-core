package nl.sjtek.control.core.modules;

import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.TemperatureResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Temperature extends BaseModule {

    private static final int UPDATE_DELAY = 600000;
    private static final String WEATHER_URL_OUTSIDE = "http://3ddev.nl/watson/api/weather.php?city=Son";
    private static final String WEATHER_URL_INSIDE = "http://10.10.0.2/cgi-bin/temp";
    private static final String LOG_PATH = "/var/sjtekcontrol/log.csv";

    private float tempInside = 0;
    private float tempOutside = -100;
    private float humidity = 0;
    private String description = "error";
    private String icon = "";

    public Temperature() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, UPDATE_DELAY);
    }

    @Override
    public Response getResponse() {
        return new TemperatureResponse(tempOutside, tempInside, humidity, description, icon);
    }

    @Override
    public String getSummaryText() {
        return "The temperature inside is " + tempInside + " degrees, and outside " + tempOutside + " degrees.";
    }

    private float parseOutside(String response) {
        if (!response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                float temp = jsonObject.getLong("temp");
                humidity = jsonObject.getLong("humidity");
                description = jsonObject.getString("description");
                icon = jsonObject.getString("icon");
                return temp;
            } catch (JSONException e) {
                return -101;
            }
        } else {
            return -102;
        }
    }

    private float parseInside(String response) {
        if (!response.isEmpty()) {
            try {
                return Float.valueOf(response);
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

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            tempInside = parseInside(download(WEATHER_URL_INSIDE));
            tempOutside = parseOutside(download(WEATHER_URL_OUTSIDE));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
                String data = String.format("%s;%.2f;%.2f;\n",
                        Calendar.getInstance().getTime().toString(), tempInside, tempOutside);
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
