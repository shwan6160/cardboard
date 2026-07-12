/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import java.io.IOException;

final class ProfilePropertySerializer
extends TypeAdapter<PlayerHeadObjectContents.ProfileProperty> {
    static final TypeAdapter<PlayerHeadObjectContents.ProfileProperty> INSTANCE = new ProfilePropertySerializer().nullSafe();

    private ProfilePropertySerializer() {
    }

    @Override
    public void write(JsonWriter out, PlayerHeadObjectContents.ProfileProperty property) throws IOException {
        out.beginObject();
        out.name("name");
        out.value(property.name());
        out.name("value");
        out.value(property.value());
        if (property.signature() != null) {
            out.name("signature");
            out.value(property.signature());
        }
        out.endObject();
    }

    @Override
    public PlayerHeadObjectContents.ProfileProperty read(JsonReader in) throws IOException {
        in.beginObject();
        String name = null;
        String value = null;
        String signature = null;
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals("name")) {
                name = in.nextString();
                continue;
            }
            if (fieldName.equals("value")) {
                value = in.nextString();
                continue;
            }
            if (fieldName.equals("signature")) {
                signature = in.nextString();
                continue;
            }
            in.skipValue();
        }
        in.endObject();
        if (name == null || value == null) {
            throw new JsonParseException("A profile property requires both a name and value");
        }
        return PlayerHeadObjectContents.property(name, value, signature);
    }
}

