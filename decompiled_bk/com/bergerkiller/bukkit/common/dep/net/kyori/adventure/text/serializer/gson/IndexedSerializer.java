/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Index;
import java.io.IOException;

final class IndexedSerializer<E>
extends TypeAdapter<E> {
    private final String name;
    private final Index<String, E> map;
    private final boolean throwOnUnknownKey;

    public static <E> TypeAdapter<E> strict(String name, Index<String, E> map) {
        return new IndexedSerializer<E>(name, map, true).nullSafe();
    }

    public static <E> TypeAdapter<E> lenient(String name, Index<String, E> map) {
        return new IndexedSerializer<E>(name, map, false).nullSafe();
    }

    private IndexedSerializer(String name, Index<String, E> map, boolean throwOnUnknownKey) {
        this.name = name;
        this.map = map;
        this.throwOnUnknownKey = throwOnUnknownKey;
    }

    @Override
    public void write(JsonWriter out, E value) throws IOException {
        out.value(this.map.key(value));
    }

    @Override
    public E read(JsonReader in) throws IOException {
        String string = in.nextString();
        E value = this.map.value(string);
        if (value != null) {
            return value;
        }
        if (this.throwOnUnknownKey) {
            throw new JsonParseException("invalid " + this.name + ":  " + string);
        }
        return null;
    }
}

