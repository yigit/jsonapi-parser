package com.birbit.jsonapi;

import com.google.gson.annotations.SerializedName;

/**
 * Object that represents a resource as defined in http://jsonapi.org/format/#document-resource-identifier-objects
 */
public class JsonApiResourceIdentifier {
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
