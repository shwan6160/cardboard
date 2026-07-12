/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.ShadowColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;

final class ShadowColorSerializer
extends TypeAdapter<ShadowColor> {
    private final boolean emitArray;

    static TypeAdapter<ShadowColor> create(OptionState options) {
        return new ShadowColorSerializer(options.value(JSONOptions.SHADOW_COLOR_MODE) == JSONOptions.ShadowColorEmitMode.EMIT_ARRAY).nullSafe();
    }

    private ShadowColorSerializer(boolean emitArray) {
        this.emitArray = emitArray;
    }

    @Override
    public void write(JsonWriter out, ShadowColor value) throws IOException {
        if (this.emitArray) {
            out.beginArray().value(ShadowColorSerializer.componentAsFloat(value.red())).value(ShadowColorSerializer.componentAsFloat(value.green())).value(ShadowColorSerializer.componentAsFloat(value.blue())).value(ShadowColorSerializer.componentAsFloat(value.alpha())).endArray();
        } else {
            out.value(value.value());
        }
    }

    @Override
    public ShadowColor read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.BEGIN_ARRAY) {
            in.beginArray();
            double r = in.nextDouble();
            double g = in.nextDouble();
            double b = in.nextDouble();
            double a = in.nextDouble();
            if (in.peek() != JsonToken.END_ARRAY) {
                throw new JsonParseException("Failed to parse shadow colour at " + in.getPath() + ": expected end of 4-element array but got " + (Object)((Object)in.peek()) + " instead.");
            }
            in.endArray();
            return ShadowColor.shadowColor(ShadowColorSerializer.componentFromFloat(r), ShadowColorSerializer.componentFromFloat(g), ShadowColorSerializer.componentFromFloat(b), ShadowColorSerializer.componentFromFloat(a));
        }
        return ShadowColor.shadowColor(in.nextInt());
    }

    static float componentAsFloat(int element) {
        return (float)element / 255.0f;
    }

    static int componentFromFloat(double element) {
        return (int)((float)element * 255.0f);
    }
}

