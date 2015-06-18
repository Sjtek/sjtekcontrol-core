package nl.sjtek.sjtekcontrol.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class Response {

    private int code = 200;
    private JSONObject error;
    private JSONObject lights;
    private JSONObject song;
    private JSONObject volume;

    public Response() {
    }

    public Response(int code) {
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setError(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        this.error = jsonObject;
    }

    public void setLights(JSONObject lights) {
        this.lights = lights;
    }

    public void setSong(JSONObject song) {
        this.song = song;
    }

    public void setVolume(JSONObject volume) {
        this.volume = volume;
    }

    public JSONObject getError() {
        return error;
    }

    public JSONObject getLights() {
        return lights;
    }

    public JSONObject getSong() {
        return song;
    }

    public JSONObject getVolume() {
        return volume;
    }

    /*
    public void put(String key, JSONObject response) {
        this.response.put(key, response);
    }

    public void put(String key, JSONArray response) {
        this.response.put(key, response);
    }

    public void put(String key, String response) {
        this.response.put(key, response);
    }
    */

    @Override
    public String toString() {
        if (code == 404) {
            return "<h1>404 Not Found</h1>No context found for request";
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", error);
            jsonObject.put("song", song);
            jsonObject.put("lights", lights);
            jsonObject.put("volume", volume);
            return jsonObject.toString();
        }
    }

    public void combine(Response otherResponse) {
        if (this.error == null) {
            this.error = otherResponse.getError();
        }

        if (this.lights == null) {
            this.lights = otherResponse.getLights();
        }

        if (this.song == null) {
            this.song = otherResponse.getSong();
        }

        if (this.volume == null) {
            this.volume = otherResponse.getVolume();
        }
    }
}
