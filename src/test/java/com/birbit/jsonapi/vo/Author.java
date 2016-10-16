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
