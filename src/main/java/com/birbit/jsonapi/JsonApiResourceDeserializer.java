package com.birbit.jsonapi;

import com.birbit.jsonapi.annotations.Relationship;
import com.birbit.jsonapi.annotations.ResourceId;
import com.birbit.jsonapi.annotations.ResourceLink;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class JsonApiResourceDeserializer<T> {
    final Class<T> klass;
    private Map<String, Setter> relationshipSetters;
    private Map<String, Setter> linkSetters;
    private Setter idSetter;
    final String apiType;

    @SuppressWarnings("WeakerAccess")
    public JsonApiResourceDeserializer(String apiType, Class<T> klass) {
        this.klass = klass;
        this.apiType = apiType;

        for (Field field : klass.getDeclaredFields()) {
            ResourceId resourceId = field.getAnnotation(ResourceId.class);
            if (resourceId != null) {
                validateResourceId(field.getType());
                idSetter = new FieldSetter(field);
            }
            Relationship relationship = field.getAnnotation(Relationship.class);
            if (relationship != null) {
                String name = validateRelationship(field.getType(), relationship);
                relationshipSetters.put(name, new FieldSetter(field));
            }
            ResourceLink resourceLink = field.getAnnotation(ResourceLink.class);
            if (resourceLink != null) {
                String name = validateResourceLink(field.getType(), resourceLink);
                linkSetters.put(name, new FieldSetter(field));
            }
        }

        for (Method method : klass.getDeclaredMethods()) {
            ResourceId resourceId = method.getAnnotation(ResourceId.class);
            if (resourceId != null) {
                validateMethodParameters(ResourceId.class, method);
                idSetter = new MethodSetter(method);
            }
            Relationship relationship = method.getAnnotation(Relationship.class);
            if (relationship != null) {
                Class<?> parameter = validateMethodParameters(Relationship.class, method);
                String name = validateRelationship(parameter, relationship);
                relationshipSetters.put(name, new MethodSetter(method));
            }
            ResourceLink resourceLink = method.getAnnotation(ResourceLink.class);
            if (resourceLink != null) {
                Class<?> parameter = validateMethodParameters(ResourceLink.class, method);
                String name = validateResourceLink(parameter, resourceLink);
                linkSetters.put(name, new MethodSetter(method));
            }
        }
        if (idSetter == null) {
            throw new IllegalStateException("Must provide a ResourceId for " + klass);
        }
    }

    private Class<?> validateMethodParameters(Class annotation, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalStateException(annotation.getSimpleName() + " method must receive a single parameter");
        }
        Class<?> parameter = parameterTypes[0];
        if (annotation == Relationship.class) {
            if (!parameter.isAssignableFrom(JsonApiRelationship.class)) {
                throw new IllegalStateException(annotation.getSimpleName() + " method must receive a " +
                        "JSonApiRelationship parameter");
            }
        } else {
            if (!parameter.isAssignableFrom(String.class)) {
                throw new IllegalStateException(annotation.getSimpleName() + " method must receive a string parameter");
            }
        }
        return parameter;
    }

    private void validateResourceId(Class<?> type) {
        if (!type.isAssignableFrom(String.class)) {
            throw new IllegalStateException("Id type must be a string");
        }
        if (idSetter != null) {
            throw new IllegalStateException("Cannot have multiple ResourceId annotations in " + klass);
        }
    }

    private String validateResourceLink(Class<?> type, ResourceLink link) {
        String name = link.value().trim();
        if (name.length() == 0) {
            if (!type.isAssignableFrom(JsonApiLinks.class)) {
                throw new IllegalArgumentException("If ResourceLink value is empty, it must be of type JsonApiLinks");
            }
        } else {
            if (!type.isAssignableFrom(String.class)) {
                // TODO accept a Link array
                throw new IllegalStateException("If ResourceLink value is not empty, it must be of type String");
            }
        }

        if (linkSetters == null) {
            linkSetters = new HashMap<String, Setter>();
        } else if (linkSetters.containsKey(name)) {
            throw new IllegalStateException("Two different fields in " + klass + " has resource link " + name);
        }
        return name;
    }

    private String validateRelationship(Class<?> type, Relationship relationship) {
        if (!type.isAssignableFrom(JsonApiRelationshipList.class) && !type.isAssignableFrom(JsonApiRelationship.class)
                && !type.isAssignableFrom(String.class)
                && !type.isAssignableFrom(List.class)) {
            throw new IllegalStateException("Relationship type must be a JsonApiRelationship, " +
                    "JsonApiRelationshipList, String or List<String>");
        }
        String name = relationship.value().trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("Relationship value cannot be empty string");
        }
        if (relationshipSetters == null) {
            relationshipSetters = new HashMap<String, Setter>();
        } else if (relationshipSetters.containsKey(name)) {
            throw new IllegalStateException("Two different fields in " + klass + " has relationship " + name);
        }
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public T deserialize(String id, JsonElement json, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("expected a json object to parse into " + klass + " but received " + json);
        }
        T t = null;
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            t = parseObject(context, id, jsonObject);
            parseRelationships(context, t, jsonObject);
            parseLinks(context, t, jsonObject);
        } catch (IllegalAccessException e) {
            throw new JsonParseException("Cannot set ID/link on " + t, e);
        } catch (InvocationTargetException e) {
            throw new JsonParseException("Cannot set ID/link on " + t, e);
        } catch (InstantiationException e) {
            throw new JsonParseException("Cannot create an instance of " + klass + ". Make sure it has a no-arg" +
                    " constructor", e);
        }
        return t;
    }

    private void parseLinks(JsonDeserializationContext context, T t, JsonObject jsonObject)
            throws IllegalAccessException, InvocationTargetException {
        JsonElement links = jsonObject.get("links");
        if (links != null && links.isJsonObject()) {
            JsonObject linksObject = links.getAsJsonObject();
            Setter linksObjectSetter = linkSetters.get("");
            if (linksObjectSetter != null) {
                linksObjectSetter.setOnObject(t, context.deserialize(links, JsonApiLinks.class));
            }
            for (Map.Entry<String, Setter> entry : linkSetters.entrySet()) {
                JsonElement link = linksObject.get(entry.getKey());
                if (link != null && link.isJsonPrimitive()) {
                    entry.getValue().setOnObject(t, link.getAsString());
                }
            }
        }
    }

    private void parseRelationships(JsonDeserializationContext context, T t, JsonObject jsonObject)
            throws IllegalAccessException, InvocationTargetException {
        JsonElement relationships = jsonObject.get("relationships");
        if (relationships != null && relationships.isJsonObject()) {
            JsonObject relationshipsObject = relationships.getAsJsonObject();
            for (Map.Entry<String, Setter> entry : relationshipSetters.entrySet()) {
                JsonElement relationship = relationshipsObject.get(entry.getKey());
                if (relationship != null && relationship.isJsonObject()) {
                    if (entry.getValue().type() == JsonApiRelationshipList.class) {
                        entry.getValue().setOnObject(t, context.deserialize(relationship, JsonApiRelationshipList.class));
                    } else if (entry.getValue().type() == JsonApiRelationship.class) { // JsonApiRelationship
                        entry.getValue().setOnObject(t, context.deserialize(relationship, JsonApiRelationship.class));
                    } else { // String list or id
                        JsonElement data = relationship.getAsJsonObject().get("data");
                        if (data != null) {
                            if (data.isJsonObject()) {
                                JsonElement relationshipIdElement = data.getAsJsonObject().get("id");
                                if (relationshipIdElement != null) {
                                    if (relationshipIdElement.isJsonPrimitive()) {
                                        entry.getValue().setOnObject(t, relationshipIdElement.getAsString());
                                    }
                                }
                            } else if (data.isJsonArray()) {
                                List<String> idList = parseIds(data.getAsJsonArray());
                                entry.getValue().setOnObject(t, idList);
                            }
                        }
                    }
                }
            }
        }
    }

    private T parseObject(JsonDeserializationContext context, String id, JsonObject jsonObject)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        JsonElement attributesElement = jsonObject.get("attributes");
        T object;
        if (attributesElement != null && attributesElement.isJsonObject()) {
            object = context.deserialize(attributesElement, klass);
        } else {
            object = klass.newInstance();
        }
        idSetter.setOnObject(object, id);
        return object;
    }

    private List<String> parseIds(JsonArray jsonArray) {
        List<String> result = new ArrayList<String>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement item = jsonArray.get(i);
            if (item.isJsonObject()) {
                JsonElement idField = item.getAsJsonObject().get("id");
                if (idField != null && idField.isJsonPrimitive()) {
                    result.add(idField.getAsString());
                }
            }
        }
        return result;
    }

    interface Setter {
        Class type();

        void setOnObject(Object object, Object id) throws IllegalAccessException, InvocationTargetException;
    }

    @SuppressWarnings("WeakerAccess")
    static class FieldSetter implements Setter {
        final Field field;

        FieldSetter(Field field) {
            this.field = field;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        }

        @Override
        public Class type() {
            return field.getType();
        }

        public void setOnObject(Object object, Object id) throws IllegalAccessException {
            field.set(object, id);
        }


    }

    @SuppressWarnings("WeakerAccess")
    static class MethodSetter implements Setter {
        final Method method;

        MethodSetter(Method method) {
            this.method = method;
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        }

        @Override
        public Class type() {
            return method.getParameterTypes()[0];
        }

        public void setOnObject(Object object, Object id) throws IllegalAccessException, InvocationTargetException {
            method.invoke(object, id);
        }


    }
}
