package nl.sjtek.sjtekcontrol.network;

import nl.sjtek.sjtekcontrol.modules.BaseModule;
import org.json.JSONObject;

/**
 * Created by wouter on 11-12-15.
 */
public class Response {

    private Response() {

    }

    public static String create(BaseModule... modules) {
        JSONObject jsonObject = new JSONObject();
        for (BaseModule module : modules) {
            jsonObject.put(module.getClass().getSimpleName().toLowerCase(), module.toString());
        }
        return jsonObject.toString();
    }
}
