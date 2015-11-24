package nl.sjtek.sjtekcontrol.data;

import org.json.JSONObject;

public class JsonResponse {
    private JsonResponse() {
    }

    public static String generate(String music, String lights, String temperature, String tv, String sonarr, String minecraft) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", new JSONObject(music));
        jsonObject.put("lights", new JSONObject(lights));
        jsonObject.put("temperature", new JSONObject(temperature));
        jsonObject.put("tv", new JSONObject(tv));
        jsonObject.put("sonarr", new JSONObject(sonarr));
        jsonObject.put("minecraft", new JSONObject(minecraft));
        return jsonObject.toString();
    }
}
