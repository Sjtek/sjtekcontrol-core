package nl.sjtek.control.core.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.sjtek.control.core.modules.BaseModule;
import nl.sjtek.control.core.modules.OnDataUpdatedListener;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.ResponseAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 16-7-16.
 */
public class ResponseCache implements OnDataUpdatedListener {

    private Map<String, BaseModule> moduleMap = new HashMap<>();
    private Map<String, Response> responseMap = new HashMap<>();

    public void addModule(String key, BaseModule module) {
        module.setDataUpdatedListener(this);
    }

    public void addModules(Map<String, BaseModule> modules) {
        for (Map.Entry<String, BaseModule> entry : modules.entrySet()) {
            addModule(entry.getKey(), entry.getValue());
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Response.class, new ResponseAdapter())
                .setPrettyPrinting()
                .create();
        return gson.toJson(responseMap);
    }

    @Override
    public void onUpdate(BaseModule module, String key) {
        this.responseMap.put(key, module.getResponse());
    }
}
