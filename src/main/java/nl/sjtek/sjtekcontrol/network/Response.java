package nl.sjtek.sjtekcontrol.network;

import nl.sjtek.sjtekcontrol.modules.BaseModule;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wouter on 11-12-15.
 */
public class Response {

    private Response() {

    }

    public static String create(Map<String, BaseModule> map) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, BaseModule> set : map.entrySet()) {
            jsonObject.put(set.getKey(), set.getValue().toJson());
        }
        return jsonObject.toString();
    }
}
