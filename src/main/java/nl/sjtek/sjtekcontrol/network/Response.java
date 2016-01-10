package nl.sjtek.sjtekcontrol.network;

import nl.sjtek.sjtekcontrol.modules.BaseModule;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.settings.User;
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
        jsonPlaylists.put("sjtek", SettingsManager.getInstance().getMusic().getPlaylistSet().toJson());
        for (Map.Entry<String, User> entry : SettingsManager.getInstance().getUsers().entrySet()) {
            jsonPlaylists.put(entry.getKey(), entry.getValue().getPlaylistSet().toJson());
        }
        jsonObject.put("playlists", jsonPlaylists);
        return jsonObject.toString();
    }
}
