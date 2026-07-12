/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import java.lang.reflect.Type;

public interface JsonDeserializer<T> {
    public T deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException;
}

