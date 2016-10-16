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

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonApiResponse<T> {
    private final T data;
    private final Map<String, Map<String, ?>> included;
    private final Map<Class, String> typeMapping;
    private final JsonApiLinks links;
    @Nullable
    private final List<JsonApiError> errors;

    public JsonApiResponse(@Nullable T data, @NonNull Map<String, Map<String, ?>> included,
                           @NonNull Map<Class, String> typeMapping, @NonNull JsonApiLinks links) {
        this.data = data;
        this.errors = null;
        this.included = included;
        this.typeMapping = typeMapping;
        this.links = links;
    }

    public JsonApiResponse(List<JsonApiError> errors, @NonNull Map<Class, String> typeMapping,
                           @NonNull JsonApiLinks links) {
        this.data = null;
        this.included = Collections.emptyMap();
        this.errors = errors;
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

    public List<JsonApiError> getErrors() {
        return errors;
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
