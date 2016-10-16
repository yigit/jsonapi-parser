package com.birbit.jsonapi.vo;

import com.birbit.jsonapi.JsonApiLinks;
import com.birbit.jsonapi.JsonApiRelationship;
import com.birbit.jsonapi.JsonApiRelationshipList;
import com.birbit.jsonapi.annotations.Relationship;
import com.birbit.jsonapi.annotations.RelationshipId;
import com.birbit.jsonapi.annotations.ResourceId;
import com.birbit.jsonapi.annotations.ResourceLink;

import java.util.List;

public class ArticleWithFullRelationships {
    @ResourceId
    String id;
    String title;
    @Relationship("author")
    JsonApiRelationship authorRelationship;
    @ResourceLink("self")
    String selfUrl;
    @Relationship("comments")
    JsonApiRelationshipList commentRelationships;
    @ResourceLink
    JsonApiLinks allLinks;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public JsonApiLinks getAllLinks() {
        return allLinks;
    }

    public JsonApiRelationship getAuthorRelationship() {
        return authorRelationship;
    }

    public JsonApiRelationshipList getCommentRelationships() {
        return commentRelationships;
    }
}
