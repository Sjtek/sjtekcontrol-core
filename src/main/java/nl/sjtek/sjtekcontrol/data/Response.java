package nl.sjtek.sjtekcontrol.data;

import org.json.JSONObject;

public class Response {

    private int code;
    private JSONObject response;

    public Response() {
        this.response = new JSONObject();
    }

    public Response(int code) {
        this.response = new JSONObject();
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void put(String key, JSONObject response) {
        this.response.put(key, response);
    }

    public void put(String key, String response) {
        this.response.put(key, response);
    }

    @Override
    public String toString() {
        if (code == 404) {
            return "<h1>404 Not Found</h1>No context found for request";
        } else {
            return response.toString();
        }
    }
}
