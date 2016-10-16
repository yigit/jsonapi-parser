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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class ErrorTest {
    @Test
    public void testError() throws IOException {
        String json = TestUtil.readTestData("error.json");
        Gson gson = TestUtil.createGson();
        JsonApiResponse<Dummy> response = gson.fromJson(json, new TypeToken<JsonApiResponse<Dummy>>(){}.getType());
        assertThat(response.getErrors().size(), is(3));
        assertThat(response.getErrors().get(0).code, is("123"));
        assertThat(response.getErrors().get(0).title, is("Value is too short"));
        assertThat(response.getErrors().get(0).detail, is("First name must contain at least three characters."));

        assertThat(response.getErrors().get(1).code, is("225"));
        assertThat(response.getErrors().get(1).title, is("Passwords must contain a letter, number, and punctuation character."));
        assertThat(response.getErrors().get(1).detail, is("The password provided is missing a punctuation character."));

        assertThat(response.getErrors().get(2).code, is("226"));
        assertThat(response.getErrors().get(2).title, is("Password and password confirmation do not match."));
        assertThat(response.getErrors().get(2).detail, is(nullValue()));
    }

    @SuppressWarnings("WeakerAccess")
    public static class Dummy {
        @ResourceId
        String id;
    }
}
