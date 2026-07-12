/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.InvalidKeyException;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

final class ShowEntitySerializer
extends TypeAdapter<HoverEvent.ShowEntity> {
    private final Gson gson;
    private final boolean emitKeyAsTypeAndUuidAsId;

    static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson, OptionState opt) {
        return new ShowEntitySerializer(gson, opt.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID)).nullSafe();
    }

    private ShowEntitySerializer(Gson gson, boolean emitKeyAsTypeAndUuidAsId) {
        this.gson = gson;
        this.emitKeyAsTypeAndUuidAsId = emitKeyAsTypeAndUuidAsId;
    }

    @Override
    public HoverEvent.ShowEntity read(JsonReader in) throws IOException {
        in.beginObject();
        Key type = null;
        UUID id = null;
        Component name = null;
        while (in.hasNext()) {
            String fieldName;
            switch (fieldName = in.nextName()) {
                case "id": {
                    if (in.peek() == JsonToken.BEGIN_ARRAY) {
                        id = (UUID)this.gson.fromJson(in, (Type)((Object)UUID.class));
                        break;
                    }
                    String string = in.nextString();
                    if (string.contains(":")) {
                        type = Key.key(string);
                    }
                    try {
                        id = UUID.fromString(string);
                    }
                    catch (IllegalArgumentException ignored) {
                        try {
                            type = Key.key(string);
                        }
                        catch (InvalidKeyException invalidKeyException) {}
                    }
                    break;
                }
                case "type": {
                    type = (Key)this.gson.fromJson(in, (Type)((Object)Key.class));
                    break;
                }
                case "uuid": {
                    id = (UUID)this.gson.fromJson(in, (Type)((Object)UUID.class));
                    break;
                }
                case "name": {
                    name = (Component)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
                    break;
                }
                default: {
                    in.skipValue();
                }
            }
        }
        if (type == null || id == null) {
            throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
        }
        in.endObject();
        return HoverEvent.ShowEntity.showEntity(type, id, name);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowEntity value) throws IOException {
        out.beginObject();
        out.name(this.emitKeyAsTypeAndUuidAsId ? "type" : "id");
        this.gson.toJson((Object)value.type(), SerializerFactory.KEY_TYPE, out);
        out.name(this.emitKeyAsTypeAndUuidAsId ? "id" : "uuid");
        this.gson.toJson((Object)value.id(), SerializerFactory.UUID_TYPE, out);
        @Nullable Component name = value.name();
        if (name != null) {
            out.name("name");
            this.gson.toJson((Object)name, SerializerFactory.COMPONENT_TYPE, out);
        }
        out.endObject();
    }
}

