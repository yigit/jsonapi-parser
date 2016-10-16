package com.birbit.jsonapi;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class JsonApiLinksDeserializer implements JsonDeserializer<JsonApiLinks> {
    public static final JsonApiLinksDeserializer INSTANCE = new JsonApiLinksDeserializer();

    private JsonApiLinksDeserializer() {
    }

    @Override
    public JsonApiLinks deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("JsonApiLinks json element must be an object");
        }
        JsonObject asJsonObject = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = asJsonObject.entrySet();
        if (entries.isEmpty()) {
            return JsonApiLinks.EMPTY;
        }
        Map<String, JsonApiLinkItem> result = new HashMap<String, JsonApiLinkItem>();
        for (Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                result.put(entry.getKey(), new JsonApiLinkItem(entry.getValue().getAsString()));
            } else {
                result.put(entry.getKey(), context.<JsonApiLinkItem>deserialize(entry.getValue(), JsonApiLinkItem.class));
            }
        }
        return new JsonApiLinks(result);
    }
}
