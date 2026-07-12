/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class NonNullListDeserializer<T>
implements JsonDeserializer<List<T>> {
    NonNullListDeserializer() {
    }

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Type valueType = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
        JsonArray input = json.getAsJsonArray();
        ArrayList result = new ArrayList(input.size());
        for (JsonElement el : input) {
            if (el.isJsonNull()) continue;
            result.add(context.deserialize(el, valueType));
        }
        return result;
    }
}

