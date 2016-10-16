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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * A link item as defined in http://jsonapi.org/format/#document-links
 */
public class JsonApiLinkItem {
    @SerializedName("href")
    private String href;
    @SerializedName("meta")
    private JsonObject meta;

    // empty constructor for GSON
    public JsonApiLinkItem() {
    }

    public JsonApiLinkItem(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public JsonObject getMeta() {
        return meta;
    }
}
