/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgument;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import java.io.IOException;
import java.lang.reflect.Type;

final class TranslationArgumentSerializer
extends TypeAdapter<TranslationArgument> {
    private final Gson gson;

    static TypeAdapter<TranslationArgument> create(Gson gson) {
        return new TranslationArgumentSerializer(gson).nullSafe();
    }

    private TranslationArgumentSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, TranslationArgument value) throws IOException {
        Object raw = value.value();
        if (raw instanceof Boolean) {
            out.value((Boolean)raw);
        } else if (raw instanceof Number) {
            out.value((Number)raw);
        } else if (raw instanceof Component) {
            this.gson.toJson(raw, SerializerFactory.COMPONENT_TYPE, out);
        } else {
            throw new IllegalStateException("Unable to serialize translatable argument of type " + raw.getClass() + ": " + raw);
        }
    }

    @Override
    public TranslationArgument read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case BOOLEAN: {
                return TranslationArgument.bool(in.nextBoolean());
            }
            case NUMBER: {
                return TranslationArgument.numeric((Number)this.gson.fromJson(in, (Type)((Object)Number.class)));
            }
        }
        return TranslationArgument.component((ComponentLike)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE));
    }
}

