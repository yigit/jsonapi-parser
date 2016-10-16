package com.birbit.jsonapi;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonApiResponse<T> {
    private T data;
    private Map<String, Map<String, ?>> included;
    private final Map<Class, String> typeMapping;
    private JsonApiLinks links;

    public JsonApiResponse(@Nullable T data, @NonNull Map<String, Map<String, ?>> included,
                           @NonNull Map<Class, String> typeMapping, @NonNull JsonApiLinks links) {
        this.data = data;
        this.included = included;
        this.typeMapping = typeMapping;
        this.links = links;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @NonNull
    public <K> Map<String, K> getIncluded(Class<K> type) {
        String mapping = typeMapping.get(type);
        if (mapping == null) {
            return Collections.emptyMap();
        }
        //noinspection unchecked
        return (Map<String, K>) included.get(mapping);
    }

    @Nullable
    public <K> K getIncluded(Class<K> type, String id) {
        return getIncluded(type).get(id);
    }

    @NonNull
    public Map<String, Map<String, ?>> getIncluded() {
        return included;
    }

    @NonNull
    public JsonApiLinks getLinks() {
        return links;
    }
}
