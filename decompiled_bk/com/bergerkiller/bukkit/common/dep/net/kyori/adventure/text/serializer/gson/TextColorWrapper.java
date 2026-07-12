/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.JsonSyntaxException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.TextColorSerializer;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

final class TextColorWrapper {
    @Nullable
    final TextColor color;
    @Nullable
    final TextDecoration decoration;
    final boolean reset;

    TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
        this.color = color;
        this.decoration = decoration;
        this.reset = reset;
    }

    static final class Serializer
    extends TypeAdapter<TextColorWrapper> {
        static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public void write(JsonWriter out, TextColorWrapper value) {
            throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
        }

        @Override
        public TextColorWrapper read(JsonReader in) throws IOException {
            boolean reset;
            String input = in.nextString();
            TextColor color = TextColorSerializer.fromString(input);
            TextDecoration decoration = TextDecoration.NAMES.value(input);
            boolean bl = reset = decoration == null && input.equals("reset");
            if (color == null && decoration == null && !reset) {
                throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath());
            }
            return new TextColorWrapper(color, decoration, reset);
        }
    }
}

