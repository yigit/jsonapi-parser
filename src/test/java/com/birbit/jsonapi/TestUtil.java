package com.birbit.jsonapi;

import com.birbit.jsonapi.vo.Article;
import com.birbit.jsonapi.vo.ArticleWithFullRelationships;
import com.birbit.jsonapi.vo.Author;
import com.birbit.jsonapi.vo.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestUtil {
    static JsonApiResourceDeserializer<Author> AUTHOR_DESERIALIZER = new JsonApiResourceDeserializer<Author>("authors", Author.class);
    static JsonApiResourceDeserializer<ArticleWithFullRelationships> ARTICLE_WITH_RELATIONSHIP_OBJECTS_DESERIALIZER
            = new JsonApiResourceDeserializer<ArticleWithFullRelationships>("articles", ArticleWithFullRelationships.class);;
    static JsonApiResourceDeserializer<Article> ARTICLE_DESERIALIZER = new JsonApiResourceDeserializer<Article>("articles", Article.class);
    static JsonApiResourceDeserializer<Comment> COMMENT_DESERIALIZER = new JsonApiResourceDeserializer<Comment>("comment", Comment.class);
    static Gson createGson(JsonApiResourceDeserializer... deserializers) {
        JsonApiDeserializer deserializer = new JsonApiDeserializer(deserializers);
        return new GsonBuilder()
                .registerTypeAdapter(JsonApiResponse.class, deserializer)
                .registerTypeAdapter(JsonApiLinks.class, JsonApiLinksDeserializer.INSTANCE)
                .create();
    }

    static String readTestData(String name) throws IOException {
        return FileUtils.readFileToString(new File("test-data/" + name), Charsets.UTF_8);
    }
}
