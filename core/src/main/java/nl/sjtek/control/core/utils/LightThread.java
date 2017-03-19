package nl.sjtek.control.core.utils;

import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.data.ampq.events.LightEvent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wouter on 19-3-17.
 */
public class LightThread extends Thread {

    private static final OkHttpClient client = new OkHttpClient();
    private final String albumArt;

    private LightThread(String albumArt) {
        this.albumArt = albumArt;
    }

    public static void get(String url) {
        new LightThread(url).start();
    }

    @Override
    public void run() {
        super.run();
        synchronized (client) {
            String body = String.format("{\"url\":\"%s\"}", albumArt);
            Request request = new Request.Builder()
                    .url("http://rgbify:3000/")
                    .post(RequestBody.create(MediaType.parse("application/json"), body))
                    .build();
            okhttp3.Response response = null;
            try {
                response = client.newCall(request).execute();
                String responseString = response.body().string();
                JSONObject jsonObject = new JSONObject(responseString);
                JSONObject rgbify = jsonObject.getJSONObject("rgbify");
                JSONArray colorArray = rgbify.getJSONArray("dominantColor");
                if (colorArray != null) {
                    Bus.post(new LightEvent(1, colorArray.getInt(0), colorArray.getInt(1), colorArray.getInt(2)));
                    Bus.post(new LightEvent(3, colorArray.getInt(0), colorArray.getInt(1), colorArray.getInt(2)));
                    Bus.post(new LightEvent(5, colorArray.getInt(0), colorArray.getInt(1), colorArray.getInt(2)));
                    Bus.post(new LightEvent(6, colorArray.getInt(0), colorArray.getInt(1), colorArray.getInt(2)));
                    Bus.post(new LightEvent(7, colorArray.getInt(0), colorArray.getInt(1), colorArray.getInt(2)));
                }
            } catch (Exception ignored) {

            } finally {
                if (response != null) {
                    response.body().close();
                }
            }
        }
    }
}
