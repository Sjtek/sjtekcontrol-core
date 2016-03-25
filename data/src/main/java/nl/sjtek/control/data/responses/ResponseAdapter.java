package nl.sjtek.control.data.responses;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by wouter on 3-2-16.
 */
public class ResponseAdapter implements JsonDeserializer<Response> {
    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        try {
            return context.deserialize(json, Class.forName(type));
        } catch (ClassNotFoundException exception) {
            throw new JsonParseException("Unknown element type: " + type, exception);
        }
    }
}
