package com.birbit.jsonapi;

import com.birbit.jsonapi.vo.Article;
import com.birbit.jsonapi.vo.ArticleWithFullRelationships;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.birbit.jsonapi.TestUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlogTest {
    @Test
    public void blogWithRelationshipIdsTest() throws IOException {
        String json = TestUtil.readTestData("blog.json");
        Gson gson = TestUtil.createGson(AUTHOR_DESERIALIZER, ARTICLE_DESERIALIZER, COMMENT_DESERIALIZER);
        JsonApiResponse<Article[]> response = gson.fromJson(json, new TypeToken<JsonApiResponse<Article[]>>(){}.getType());
        Article[] articles = response.getData();
        assertThat(articles, notNullValue());
        assertThat(articles.length, is(1));
        Article first = articles[0];
        // article data
        assertThat(first, notNullValue());
        assertThat(first.getId(), is("1"));
        assertThat(first.getTitle(), is("JSON API paints my bikeshed!"));
        assertThat(first.getAuthorId(), is("9"));
        assertThat(first.getSelfUrl(), is("http://example.com/articles/1"));
        assertThat(first.getCommentIds(), is(Arrays.asList("5", "12")));

        // article links
        JsonApiLinks allLinks = first.getAllLinks();
        assertThat(allLinks.getUrl("self"), is("http://example.com/articles/1"));

        // response links
        JsonApiLinks links = response.getLinks();
        assertThat(links, is(notNullValue()));
        assertThat(links.getUrl("self"), is("http://example.com/articles"));
        assertThat(links.getUrl("next"), is("http://example.com/articles?page[offset]=2"));
        assertThat(links.getUrl("last"), is("http://example.com/articles?page[offset]=10"));
        assertThat(links.getUrl("nonExistingLink"), is(nullValue()));
    }

    @Test
    public void blogWithRelationshipObjectsTest() throws IOException {
        String json = TestUtil.readTestData("blog.json");
        Gson gson = TestUtil.createGson(AUTHOR_DESERIALIZER, ARTICLE_WITH_RELATIONSHIP_OBJECTS_DESERIALIZER, COMMENT_DESERIALIZER);
        JsonApiResponse<ArticleWithFullRelationships[]> response
                = gson.fromJson(json, new TypeToken<JsonApiResponse<ArticleWithFullRelationships[]>>(){}.getType());
        ArticleWithFullRelationships[] articles = response.getData();
        assertThat(articles, notNullValue());
        assertThat(articles.length, is(1));
        ArticleWithFullRelationships first = articles[0];
        // article data
        assertThat(first, notNullValue());
        assertThat(first.getId(), is("1"));
        assertThat(first.getTitle(), is("JSON API paints my bikeshed!"));
        assertThat(first.getSelfUrl(), is("http://example.com/articles/1"));

        JsonApiRelationshipList commentRelationships = first.getCommentRelationships();
        assertThat(commentRelationships.getData().size(), is(2));
        JsonApiResourceIdentifier firstCommentRelationship = commentRelationships.getData().get(0);
        assertThat(firstCommentRelationship.getId(), is("5"));
        assertThat(firstCommentRelationship.getType(), is("comments"));

        JsonApiResourceIdentifier secondCommentRelationship = commentRelationships.getData().get(1);
        assertThat(secondCommentRelationship.getId(), is("12"));
        assertThat(secondCommentRelationship.getType(), is("comments"));

        assertThat(commentRelationships.getLinks().getUrl("self"), is("http://example.com/articles/1/relationships/comments"));
        assertThat(commentRelationships.getLinks().getUrl("related"), is("http://example.com/articles/1/comments"));


        // article links
        JsonApiLinks allLinks = first.getAllLinks();
        assertThat(allLinks.getUrl("self"), is("http://example.com/articles/1"));

        // response links
        JsonApiLinks links = response.getLinks();
        assertThat(links, is(notNullValue()));
        assertThat(links.getUrl("self"), is("http://example.com/articles"));
        assertThat(links.getUrl("next"), is("http://example.com/articles?page[offset]=2"));
        assertThat(links.getUrl("last"), is("http://example.com/articles?page[offset]=10"));
        assertThat(links.getUrl("nonExistingLink"), is(nullValue()));
    }
}
