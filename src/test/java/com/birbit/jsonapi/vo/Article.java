package com.birbit.jsonapi.vo;

import com.birbit.jsonapi.JsonApiLinks;
import com.birbit.jsonapi.JsonApiRelationship;
import com.birbit.jsonapi.JsonApiRelationshipList;
import com.birbit.jsonapi.annotations.Relationship;
import com.birbit.jsonapi.annotations.RelationshipId;
import com.birbit.jsonapi.annotations.ResourceId;
import com.birbit.jsonapi.annotations.ResourceLink;

import java.util.List;

public class Article {
    @ResourceId
    String id;
    String title;
    @Relationship("author")
    String authorId;
    @ResourceLink("self")
    String selfUrl;
    @Relationship("comments")
    List<String> commentIds;
    @ResourceLink
    JsonApiLinks allLinks;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public List<String> getCommentIds() {
        return commentIds;
    }

    public JsonApiLinks getAllLinks() {
        return allLinks;
    }
}
