package nl.sjtek.control.core.network;

import com.google.gson.Gson;
import nl.sjtek.control.core.modules.BaseModule;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.settings.User;
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

    public static String createData() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonPlaylists = new JSONObject();
        for (Map.Entry<String, User> entry : SettingsManager.getInstance().getUsers().entrySet()) {
            jsonPlaylists.put(entry.getKey(), entry.getValue().getPlaylistSet().toJson());
        }
        jsonObject.put("playlists", jsonPlaylists);
        jsonObject.put("users", new JSONObject(new Gson().toJson(SettingsManager.getInstance().getUsers())));
        return jsonObject.toString();
    }
}
