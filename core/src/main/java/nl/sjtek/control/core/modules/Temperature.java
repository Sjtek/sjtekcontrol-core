package nl.sjtek.control.core.modules;

import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.TemperatureResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Temperature extends BaseModule {

    private static final int UPDATE_DELAY = 900000;
    private static final String WEATHER_URL_OUTSIDE = "http://3ddev.nl/watson/api/weather.php?city=Son";
    private static final String WEATHER_URL_INSIDE = "http://10.10.0.2/cgi-bin/temp";
    private static final String LOG_PATH = "/var/sjtekcontrol/log.csv";

    private int errorsOutside = 0;
    private int tempInside = 0;
    private int tempOutside = -100;
    private float humidity = 0;
    private String description = "error";
    private String icon = "";

    public Temperature(String key) {
        super(key);
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

    private void parseOutside(String response) {
        if (!response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                float tempOutside = jsonObject.getLong("temp");
                float humidity = jsonObject.getLong("humidity");
                String description = jsonObject.getString("description");
                String icon = jsonObject.getString("icon");

                this.tempOutside = (int) tempOutside;
                this.humidity = humidity;
                this.description = description;
                this.icon = icon;
                errorsOutside = 0;
                return;
            } catch (JSONException ignored) {
            }
        }
        System.out.println("Temperature outside error: " + response);
        errorsOutside++;
        if (errorsOutside > 1) {
            tempOutside = -100;
        }
    }

    private void parseInside(String response) {
        if (!response.isEmpty()) {
            try {
                this.tempInside = (int) (float) Float.valueOf(response);
                return;
            } catch (NumberFormatException ignored) {
            }
        }
        tempInside = -100;
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

    public String getLogData() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        String rowTemplate = "[\"%s\", %s, %s]";
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_PATH))) {
            String line;
            String prefix = "";
            while ((line = reader.readLine()) != null) {
                String row[] = line.split(";");
                builder.append(prefix);
                prefix = ",";
                builder.append(String.format(rowTemplate, row[0], row[1].replace(',', '.'), row[2].replace(',', '.')));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.append("]");
        return builder.toString();
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            parseInside(download(WEATHER_URL_INSIDE));
            parseOutside(download(WEATHER_URL_OUTSIDE));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH, true))) {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());
                String data = String.format(Locale.GERMAN, "%s;%d;%d;\n",
                        nowAsISO, tempInside, tempOutside);
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
