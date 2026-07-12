/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;
import java.util.UUID;

final class UUIDSerializer
extends TypeAdapter<UUID> {
    private final boolean emitIntArray;

    static TypeAdapter<UUID> uuidSerializer(OptionState features) {
        return new UUIDSerializer(features.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY)).nullSafe();
    }

    private UUIDSerializer(boolean emitIntArray) {
        this.emitIntArray = emitIntArray;
    }

    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        if (this.emitIntArray) {
            int msb0 = (int)(value.getMostSignificantBits() >> 32);
            int msb1 = (int)(value.getMostSignificantBits() & 0xFFFFFFFFL);
            int lsb0 = (int)(value.getLeastSignificantBits() >> 32);
            int lsb1 = (int)(value.getLeastSignificantBits() & 0xFFFFFFFFL);
            out.beginArray().value(msb0).value(msb1).value(lsb0).value(lsb1).endArray();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.BEGIN_ARRAY) {
            in.beginArray();
            int msb0 = in.nextInt();
            int msb1 = in.nextInt();
            int lsb0 = in.nextInt();
            int lsb1 = in.nextInt();
            in.endArray();
            return new UUID((long)msb0 << 32 | (long)msb1 & 0xFFFFFFFFL, (long)lsb0 << 32 | (long)lsb1 & 0xFFFFFFFFL);
        }
        return UUID.fromString(in.nextString());
    }
}

