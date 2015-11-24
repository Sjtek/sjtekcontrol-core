package nl.sjtek.sjtekcontrol.data;

import nl.sjtek.sjtekcontrol.devices.Quotes;
import org.json.JSONObject;

public class JsonResponse {
    private JsonResponse() {
    }

    public static String generate(String music, String lights, String temperature, String tv, String sonarr, String minecraft, String quotes) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", new JSONObject(music));
        jsonObject.put("lights", new JSONObject(lights));
        jsonObject.put("temperature", new JSONObject(temperature));
        jsonObject.put("tv", new JSONObject(tv));
        jsonObject.put("sonarr", new JSONObject(sonarr));
        jsonObject.put("minecraft", new JSONObject(minecraft));
        jsonObject.put("quotes", new JSONObject(quotes));
        return jsonObject.toString();
    }
}
