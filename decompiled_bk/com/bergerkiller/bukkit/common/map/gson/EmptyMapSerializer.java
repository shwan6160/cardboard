/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.dep.gson.JsonSerializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;

public class EmptyMapSerializer
implements JsonSerializer<Map<?, ?>> {
    @Override
    public JsonElement serialize(Map<?, ?> o, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonObject();
    }
}

