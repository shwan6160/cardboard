/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import java.lang.reflect.Type;

class PackVersionDeserializer
implements JsonDeserializer<MapResourcePack.PackVersion> {
    PackVersionDeserializer() {
    }

    @Override
    public MapResourcePack.PackVersion deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            if (array.size() == 1) {
                return MapResourcePack.PackVersion.of(array.get(0).getAsInt());
            }
            return MapResourcePack.PackVersion.of(array.get(0).getAsInt(), array.get(1).getAsInt());
        }
        return MapResourcePack.PackVersion.of(jsonElement.getAsInt());
    }
}

