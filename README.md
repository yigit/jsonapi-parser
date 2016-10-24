## JSONAPI Parser for GSON

This repository includes a sample parser for [JSON API][1] which is a specification for build JSON APIs.

It is not a fully featured parser yet (hence no maven artifact). It is rather a supplementary document for my [blog post][2] about JSON API and how well it works for building offline ready mobile apps.


### Motivation

APIs which return objects nested are hard to handle on the client side because you cannot map it directly to an SQL database w/o massaging your data or using a relational object mapping solution. In my opinion (any many others), relational object mapping is not good for client side development because it either means you are creating more objects than you need or you are implementing mines into your codebase which may trigger main thread queries or both.

JSONAPI specs are great because they send the data to the client as a giant map with references between objects which is very straightforward to persist to an SQL database as is. Then you just fetch necessary data for your views as view models.

Please check the [blog post][2] for more details.


### Example Usage

Assume you have a GSON that looks like [this][3].

You can model it as follows:

``` java
public class Article {
    @ResourceId // This is the ID field for the object
    String id;
    String title;
    @Relationship("author") // This is a relationship
    String authorId;
    @ResourceLink("self") // A link identifier for this object
    String selfUrl;
    @Relationship("comments") // A relationship can be a lsit of String ids
    List<String> commentIds;
    @ResourceLink // All links for the Resource as defined in http://jsonapi.org/format/#document-links
    JsonApiLinks allLinks;
}

public class Author {
    @ResourceId
    String id;
    @SerializedName("first-name")
    String firstName;
    @SerializedName("last-name")
    String lastName;
    @SerializedName("twitter")
    String twitter;
}

public class Comment {
    @ResourceId
    String id;
    String body;
    @Relationship("author")
    String authorId;
}

````

The you can register this library to your `GsonBuilder` via:

``` java
static JsonApiResourceDeserializer<Author> AUTHOR_DESERIALIZER = new JsonApiResourceDeserializer<Author>("authors", Author.class);
static JsonApiResourceDeserializer<Article> ARTICLE_DESERIALIZER = new JsonApiResourceDeserializer<Article>("articles", Article.class);
static JsonApiResourceDeserializer<Comment> COMMENT_DESERIALIZER = new JsonApiResourceDeserializer<Comment>("comment", Comment.class);
  
JsonApiDeserializer.register(new GsonBuilder(), AUTHOR_DESERIALIZER, ARTICLE_DESERIALIZER, COMMENT_DESERIALIZER).create();
```

And finally, usage:
``` java
JsonApiResponse<Article[]> response = gson.fromJson(json, new TypeToken<JsonApiResponse<Article[]>>(){}.getType());
```

The response class has 3 main parts:
``` java
T getData() // the data field represented in the response
Map<String, Map<String, ?>> getIncluded() // the data passed in the included array
JsonApiLinks getLinks() // the links object returned in the response
List<JsonApiError> getErrors() // list of errors in the response
```

And it also provides convenience methods to get data from the response:
``` java
<K> Map<String, K> getIncluded(Class<K> type) // get all items of the given class that was registered above
<K> K getIncluded(Class<K> type, String id) // get a single item from the included array
```

For instance, for the example above, you can get the author of the first article as follows:
``` java
JsonApiResponse<Article[]> response = gson.fromJson(json, new TypeToken<JsonApiResponse<Article[]>>(){}.getType());
Article firstArticle = response.getData()[0];
Author author = response.getIncluded(Author.class, firstArticle.getAuthorId());
```

The best part of this structure is that, you only need 1 method that can get any JSONApi response and write everything included there into your model.

``` java
void saveData(final JsonApiResponse response) {
  db.inTransaction(() -> {
   for (Map<String, Map<String, ?>> entry : response.getIncluded()) {
     Model model = findModelForType(entry.getValue());
     model.saveItems(entry.getValue().values());
   }
  }
}
```

Please check the [blog post][2] how this helps writing an offline app when combined with ViewModels.
#### Disclaimer

This is not an official Google product. It is just owned by Google.


[1]: http://jsonapi.org/
[2]: http://www.birbit.com/jsonapi-is-a-blessing-for-offline-ready-apps/
[3]: https://gist.github.com/yigit/757216d346986b979fcfa39b0d832003
