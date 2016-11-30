package nl.sjtek.control.core.network;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.sjtek.control.core.events.BroadcastEvent;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.ResponseAdapter;

import java.util.HashMap;
import java.util.Map;


public class ResponseCache {

    private static ResponseCache instance = new ResponseCache();
    private Map<String, Response> responseMap = new HashMap<>();

    private ResponseCache() {
        Bus.regsiter(this);
    }

    public synchronized static ResponseCache getInstance() {
        return instance;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Response.class, new ResponseAdapter())
                .setPrettyPrinting()
                .create();
        return gson.toJson(responseMap);
    }

    @Subscribe
    public void onUpdate(DataChangedEvent event) {
        this.responseMap.put(event.getKey(), event.getResponse());
        if (event.shouldPushToClients()) {
            Bus.post(new BroadcastEvent(toJson()));
        }
    }
}
