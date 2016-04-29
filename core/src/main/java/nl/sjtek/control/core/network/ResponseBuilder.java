package nl.sjtek.control.core.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.sjtek.control.core.modules.BaseModule;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.ResponseAdapter;
import nl.sjtek.control.data.settings.DataCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 11-12-15.
 */
public class ResponseBuilder {

    private ResponseBuilder() {

    }

    public static String create(Map<String, BaseModule> map) {
        Map<String, Response> responseMap = new HashMap<>();
        for (Map.Entry<String, BaseModule> entry : map.entrySet()) {
            if (entry.getKey().equals("music-wouter")) continue;
            Response response = entry.getValue().getResponse();
            responseMap.put(entry.getKey(), response);
        }
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Response.class, new ResponseAdapter())
                .setPrettyPrinting()
                .create();
        return gson.toJson(responseMap);
    }

    public static String createData() {
        return new Gson().toJson(new DataCollection(SettingsManager.getInstance().getUsers()));
    }
}
