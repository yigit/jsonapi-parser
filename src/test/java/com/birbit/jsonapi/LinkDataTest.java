/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.birbit.jsonapi;

import com.birbit.jsonapi.annotations.ResourceId;
import com.birbit.jsonapi.annotations.ResourceLink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class LinkDataTest {
    @Test
    public void fooTest() throws IOException {
        String json = TestUtil.readTestData("with_link_data.json");
        Gson gson = TestUtil.createGson(new JsonApiResourceDeserializer("users", User.class));
        JsonApiResponse<User> response = gson.fromJson(json, new TypeToken<JsonApiResponse<User>>() {}.getType());
        User user = response.getData();
        assertThat(user.id, is("1"));
        assertThat(user.name, is("Yigit"));
        assertThat(user.links, not(nullValue()));
        assertThat(user.links.getUrl("blog"), is("https://blog.example.com"));
        assertThat(user.links.getUrl("search"), is("http:/example.com/?search=123"));
        JsonApiLinkItem searchLink = user.links.get("search");
        assertThat(searchLink.getHref(), is("http:/example.com/?search=123"));
        assertThat(searchLink.getMeta().get("count").getAsInt(), is(10));
    }

    public static class User {
        @ResourceId
        String id;
        String name;
        @ResourceLink
        JsonApiLinks links;
    }
}
