package com.birbit.jsonapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A value object that represents the full details of a Relationship as defined in
 * http://jsonapi.org/format/#document-resource-object-relationships
 */
public class JsonApiRelationshipList {
    @SerializedName("links")
    private JsonApiLinks links;
    @SerializedName("data")
    private List<JsonApiResourceIdentifier> data;

    public JsonApiLinks getLinks() {
        return links;
    }

    public List<JsonApiResourceIdentifier> getData() {
        return data;
    }
}
