package nl.sjtek.control.core.utils;

import io.habets.javautils.Log;
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
    private static final String DEBUG = LightThread.class.getSimpleName();
    private final String albumArt;
    private final int lights[];

    public LightThread(String albumArt, int... lights) {
        this.albumArt = albumArt;
        this.lights = lights;
    }

    @Override
    public void run() {
        super.run();
        if (lights == null || lights.length == 0 || (lights.length == 1 && lights[0] == 0)) {
            return;
        }
        synchronized (client) {
            String body = String.format("{\"url\":\"%s\"}", albumArt);
            Request request = new Request.Builder()
                    .url("http://10.10.0.1:3000/")
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
                    int r = colorArray.getInt(0);
                    int g = colorArray.getInt(1);
                    int b = colorArray.getInt(2);
                    for (int light : lights) {
                        Bus.post(new LightEvent(light, r, g, b));
                    }
                }
            } catch (Exception e) {
                Log.e(DEBUG, "Error", e);
            } finally {
                if (response != null) {
                    response.body().close();
                }
            }
        }
    }
}
