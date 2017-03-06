package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.ampq.events.TemperatureEvent;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.TemperatureResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Temperature extends BaseModule {

    private static final int UPDATE_DELAY = 900000;
    private static final String WEATHER_URL_OUTSIDE = String.format("https://api.darksky.net/forecast/%s/51.5121298,5.4924242", SettingsManager.getInstance().getWeather().getApiKey());
    private static final String WEATHER_URL_INSIDE = "http://10.10.0.2/cgi-bin/temp";
    private static final String LOG_PATH = "/var/sjtekcontrol/log.csv";
    private static final int ID_INSIDE = 1;

    private final OkHttpClient httpClient = new OkHttpClient();

    private int errorsOutside = 0;
    private int tempInside = 0;
    private int tempOutside = -100;
    private float humidity = 0;
    private String description = "error";
    private String icon = "";

    public Temperature(String key) {
        super(key);
        Bus.regsiter(this);
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

    @Subscribe
    public void onTemperatureEvent(TemperatureEvent event) {
        System.out.println("ayyyyyy dikke aight " + event.toString());
        switch (event.getId()) {
            case ID_INSIDE:
                tempInside = (int) event.getTemperature();
                dataChanged(false);
                break;
        }
    }

    private void parseOutside(String response) {
        if (!response.isEmpty()) {
            try {
                // Powered by darksky.net :)
                JSONObject jsonObject = new JSONObject(response);
                JSONObject currently = jsonObject.getJSONObject("currently");
                float temp = currently.getLong("temperature");
                double humidity = currently.getDouble("humidity");
                String description = currently.getString("summary");

                temp = ((temp - 32) * 5) / 9;

                this.tempOutside = (int) temp;
                this.humidity = (float) humidity;
                this.description = description;
                this.icon = "";
                errorsOutside = 0;
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Temperature outside error: " + response);
        errorsOutside++;
        if (errorsOutside > 1) {
            tempOutside = -100;
        }
    }

    @Deprecated
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

        okhttp3.Response response = null;
        try {
            response = httpClient.newCall(new Request.Builder().url(stringUrl).build()).execute();
            System.out.println("" + response.code() + " - downloaded " + stringUrl);
            if (response.code() == 200) {
                ResponseBody body = response.body();
                return body.string();
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        } finally {
            if (response != null) response.body().close();
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
            // parseInside(download(WEATHER_URL_INSIDE));
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
            dataChanged();
        }
    }
}
