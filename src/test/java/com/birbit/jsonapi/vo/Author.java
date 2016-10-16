package com.birbit.jsonapi.vo;

import com.birbit.jsonapi.annotations.ResourceId;
import com.google.gson.annotations.SerializedName;

public class Author {
    @ResourceId
    String id;
    @SerializedName("first-name")
    String firstName;
    @SerializedName("last-name")
    String lastName;
    @SerializedName("twitter")
    String twitter;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTwitter() {
        return twitter;
    }
}
