package com.birbit.jsonapi;

import com.google.gson.annotations.SerializedName;

/**
 * A value object that represents the full details of a Relationship as defined in
 * http://jsonapi.org/format/#document-resource-object-relationships
 */
public class JsonApiRelationship {
    @SerializedName("links")
    private JsonApiLinks links;
    @SerializedName("data")
    private JsonApiResourceIdentifier data;

    public JsonApiLinks getLinks() {
        return links;
    }

    public JsonApiResourceIdentifier getData() {
        return data;
    }
}
