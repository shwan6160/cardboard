/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.reflect.TypeToken;

public interface TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2);
}

