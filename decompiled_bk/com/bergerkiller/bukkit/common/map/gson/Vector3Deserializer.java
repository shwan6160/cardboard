/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.math.Vector3;
import java.lang.reflect.Type;

public class Vector3Deserializer
implements JsonDeserializer<Vector3> {
    @Override
    public Vector3 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray arr = jsonElement.getAsJsonArray();
        return new Vector3(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
    }
}

