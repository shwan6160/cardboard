/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson.types;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import java.lang.reflect.Type;

public class ResourcePackDescription {
    public final String plainContent;

    public ResourcePackDescription(String plainContent) {
        this.plainContent = plainContent;
    }

    public static class Deserializer
    implements JsonDeserializer<ResourcePackDescription> {
        private String deserializePart(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement el = jsonObject.get("text");
                return el == null ? "" : el.getAsString();
            }
            return jsonElement.getAsString();
        }

        @Override
        public ResourcePackDescription deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); ++i) {
                    str.append(this.deserializePart(jsonArray.get(i)));
                }
                return new ResourcePackDescription(str.toString());
            }
            return new ResourcePackDescription(this.deserializePart(jsonElement));
        }
    }
}

