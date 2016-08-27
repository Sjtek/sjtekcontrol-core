package nl.sjtek.control.core.events;

import nl.sjtek.control.data.responses.Response;


public class DataChangedEvent {
    private final String key;
    private final Response response;
    private final boolean pushToClients;

    public DataChangedEvent(String key, Response response, boolean pushToClients) {
        this.key = key;
        this.response = response;
        this.pushToClients = pushToClients;
    }

    public String getKey() {
        return key;
    }

    public Response getResponse() {
        return response;
    }

    public boolean shouldPushToClients() {
        return pushToClients;
    }
}
