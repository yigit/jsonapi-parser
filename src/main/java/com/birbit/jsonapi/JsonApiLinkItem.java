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
