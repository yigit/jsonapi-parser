package com.birbit.jsonapi;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A links object defined as in http://jsonapi.org/format/#document-links
 */
public class JsonApiLinks {
    public static final JsonApiLinks EMPTY = new JsonApiLinks(Collections.<String, JsonApiLinkItem>emptyMap());

    private Map<String, JsonApiLinkItem> links;

    public JsonApiLinks(@NonNull Map<String, JsonApiLinkItem> links) {
        this.links = links;
    }

    @NonNull
    public Map<String, JsonApiLinkItem> getLinks() {
        return links;
    }

    @Nullable
    public JsonApiLinkItem get(String name) {
        return links.get(name);
    }

    @Nullable
    public String getUrl(String name) {
        JsonApiLinkItem linkItem = links.get(name);
        return linkItem == null ? null : linkItem.getHref();
    }
}
