package com.birbit.jsonapi.vo;

import com.birbit.jsonapi.annotations.Relationship;
import com.birbit.jsonapi.annotations.ResourceId;

public class Comment {
    @ResourceId
    String id;
    String body;
    @Relationship("author")
    String authorId;
}
