/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.birbit.jsonapi;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class JsonApiDeserializer implements JsonDeserializer<JsonApiResponse> {
    Map<String, JsonApiResourceDeserializer<?>> deserializerMap;
    final Map<Class, String> typeMapping;
    public JsonApiDeserializer(JsonApiResourceDeserializer... deserializers) {
        deserializerMap = new HashMap<String, JsonApiResourceDeserializer<?>>((int) (deserializers.length * 1.25));
        typeMapping = new HashMap<Class, String>();
        for (JsonApiResourceDeserializer deserializer : deserializers) {
            deserializerMap.put(deserializer.apiType, deserializer);
            String previous = typeMapping.put(deserializer.klass, deserializer.apiType);
            if (previous != null) {
                throw new IllegalArgumentException("multiple types map to klass " + deserializer.klass + ". This is " +
                        "not supported. To workaround it, you can create a class that extends the other one. " +
                        "Conflicting types:" + previous + ", " + deserializer.apiType);
            }
        }

    }

    public JsonApiResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("JSON API response root should be a json object");
        }
        if (!(typeOfT instanceof ParameterizedType)) {
            throw new JsonParseException("JSON API response should be requested with a parameterized type where the" +
                    " type parameter represents the `data` field's type");
        }
        ParameterizedType parameterizedType = (ParameterizedType) typeOfT;
        JsonObject jsonObject = json.getAsJsonObject();
        Object data = parseData(context, parameterizedType, jsonObject);

        Map<String, Map<String, Object>> included = parseIncluded(context, jsonObject);
        JsonApiLinks links = parseLinks(context, jsonObject);
        //noinspection unchecked
        return new JsonApiResponse(data, included, typeMapping, links);
    }

    private JsonApiLinks parseLinks(JsonDeserializationContext context, JsonObject jsonObject) {
        JsonElement links = jsonObject.get("links");
        if (links == null || !links.isJsonObject()) {
            return JsonApiLinks.EMPTY;
        }
        return context.deserialize(links, JsonApiLinks.class);
    }

    private Map<String, Map<String, Object>> parseIncluded(JsonDeserializationContext context, JsonObject jsonObject) {
        JsonElement includedElm = jsonObject.get("included");
        Map<String, Map<String, Object>> included;
        if (includedElm != null && includedElm.isJsonArray()) {
            included = new HashMap<String, Map<String, Object>>();
            JsonArray includedArray = includedElm.getAsJsonArray();
            final int size = includedArray.size();
            for (int i = 0; i < size; i ++) {
                ResourceWithIdAndType parsed = parseResource(includedArray.get(i), context);
                if (parsed.resource != null) {
                    Map<String, Object> itemMap = included.get(parsed.apiType);
                    if (itemMap == null) {
                        itemMap = new HashMap<String, Object>();
                        included.put(parsed.apiType, itemMap);
                    }
                    itemMap.put(parsed.id, parsed.resource);
                }
            }
        } else {
            included = Collections.emptyMap();
        }
        return included;
    }

    private Object parseData(JsonDeserializationContext context, ParameterizedType parameterizedType, JsonObject jsonObject) {
        JsonElement dataElm = jsonObject.get("data");
        if (dataElm != null) {
            Type typeArg = parameterizedType.getActualTypeArguments()[0];
            if (dataElm.isJsonArray()) {
                JsonArray jsonArray = dataElm.getAsJsonArray();
                final int size = jsonArray.size();
                boolean isArray = typeArg instanceof GenericArrayType;
                if (isArray) {
                    TypeToken<?> typeToken = TypeToken.get(typeArg);
                    Object[] result = (Object[]) Array.newInstance(typeToken.getRawType().getComponentType(), size);
                    for (int i = 0; i < size; i ++) {
                        ResourceWithIdAndType resourceWithIdAndType = parseResource(jsonArray.get(i), context);
                        result[i] = resourceWithIdAndType.resource;
                    }
                    return result;
                } else {
                    List result = new ArrayList(size);
                    for (int i = 0; i < size; i ++) {
                        ResourceWithIdAndType resourceWithIdAndType = parseResource(jsonArray.get(i), context);
                        //noinspection unchecked
                        result.add(resourceWithIdAndType.resource);
                    }
                    return result;
                }
            } else if (dataElm.isJsonObject()) {
                return parseResource(dataElm, context).resource;
            }
        }
        return null;
    }

    private ResourceWithIdAndType parseResource(JsonElement jsonElement, JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String apiType = jsonObject.get("type").getAsString();
        String id = jsonObject.get("id").getAsString();
        JsonApiResourceDeserializer<?> deserializer = deserializerMap.get(apiType);
        Object resource;
        if (deserializer != null) {
            resource = deserializer.deserialize(id, jsonElement, context);
        } else {
            resource = null;
        }
        return new ResourceWithIdAndType(apiType, id, resource);
    }

    public static GsonBuilder register(GsonBuilder builder, JsonApiResourceDeserializer... deserializers) {
        return builder.registerTypeAdapter(JsonApiResponse.class, new JsonApiDeserializer(deserializers))
                .registerTypeAdapter(JsonApiLinks.class, JsonApiLinksDeserializer.INSTANCE);
    }

    static class ResourceWithIdAndType {
        final String apiType;
        final String id;
        final Object resource;

        public ResourceWithIdAndType(String apiType, String id, Object resource) {
            this.apiType = apiType;
            this.id = id;
            this.resource = resource;
        }
    }
}
