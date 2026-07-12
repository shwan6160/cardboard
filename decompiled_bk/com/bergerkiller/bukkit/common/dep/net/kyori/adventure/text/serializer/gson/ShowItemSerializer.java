/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonNull;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.api.BinaryTagHolder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class ShowItemSerializer
extends TypeAdapter<HoverEvent.ShowItem> {
    private static final String LEGACY_SHOW_ITEM_TAG = "tag";
    private static final String DATA_COMPONENT_REMOVAL_PREFIX = "!";
    private final Gson gson;
    private final boolean emitDefaultQuantity;
    private final JSONOptions.ShowItemHoverDataMode itemDataMode;

    static TypeAdapter<HoverEvent.ShowItem> create(Gson gson, OptionState opt) {
        return new ShowItemSerializer(gson, opt.value(JSONOptions.EMIT_DEFAULT_ITEM_HOVER_QUANTITY), opt.value(JSONOptions.SHOW_ITEM_HOVER_DATA_MODE)).nullSafe();
    }

    private ShowItemSerializer(Gson gson, boolean emitDefaultQuantity, JSONOptions.ShowItemHoverDataMode itemDataMode) {
        this.gson = gson;
        this.emitDefaultQuantity = emitDefaultQuantity;
        this.itemDataMode = itemDataMode;
    }

    @Override
    public HoverEvent.ShowItem read(JsonReader in) throws IOException {
        in.beginObject();
        Key key = null;
        int count = 1;
        BinaryTagHolder nbt = null;
        HashMap<Key, DataComponentValue.Removed> dataComponents = null;
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals("id")) {
                key = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("count")) {
                count = in.nextInt();
                continue;
            }
            if (fieldName.equals(LEGACY_SHOW_ITEM_TAG)) {
                JsonToken token = in.peek();
                if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
                    nbt = BinaryTagHolder.binaryTagHolder(in.nextString());
                    continue;
                }
                if (token == JsonToken.BOOLEAN) {
                    nbt = BinaryTagHolder.binaryTagHolder(String.valueOf(in.nextBoolean()));
                    continue;
                }
                if (token == JsonToken.NULL) {
                    in.nextNull();
                    continue;
                }
                throw new JsonParseException("Expected tag to be a string");
            }
            if (fieldName.equals("components")) {
                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    boolean removed;
                    Key id;
                    String name = in.nextName();
                    if (name.startsWith(DATA_COMPONENT_REMOVAL_PREFIX)) {
                        id = Key.key(name.substring(1));
                        removed = true;
                    } else {
                        id = Key.key(name);
                        removed = false;
                    }
                    JsonElement tree = (JsonElement)this.gson.fromJson(in, (Type)((Object)JsonElement.class));
                    if (dataComponents == null) {
                        dataComponents = new HashMap<Key, DataComponentValue.Removed>();
                    }
                    dataComponents.put(id, (DataComponentValue.Removed)(removed ? DataComponentValue.removed() : GsonDataComponentValue.gsonDataComponentValue(tree)));
                }
                in.endObject();
                continue;
            }
            in.skipValue();
        }
        if (key == null) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
        }
        in.endObject();
        if (dataComponents != null) {
            return HoverEvent.ShowItem.showItem(key, count, dataComponents);
        }
        return HoverEvent.ShowItem.showItem(key, count, nbt);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
        Map<Key, DataComponentValue> dataComponents;
        out.beginObject();
        out.name("id");
        this.gson.toJson((Object)value.item(), SerializerFactory.KEY_TYPE, out);
        int count = value.count();
        if (count != 1 || this.emitDefaultQuantity) {
            out.name("count");
            out.value(count);
        }
        if (!(dataComponents = value.dataComponents()).isEmpty() && this.itemDataMode != JSONOptions.ShowItemHoverDataMode.EMIT_LEGACY_NBT) {
            out.name("components");
            out.beginObject();
            for (Map.Entry<Key, GsonDataComponentValue> entry : value.dataComponentsAs(GsonDataComponentValue.class).entrySet()) {
                JsonElement el = entry.getValue().element();
                if (el instanceof JsonNull) {
                    out.name(DATA_COMPONENT_REMOVAL_PREFIX + entry.getKey().asString());
                    out.beginObject().endObject();
                    continue;
                }
                out.name(entry.getKey().asString());
                this.gson.toJson(el, out);
            }
            out.endObject();
        } else if (this.itemDataMode != JSONOptions.ShowItemHoverDataMode.EMIT_DATA_COMPONENTS) {
            ShowItemSerializer.maybeWriteLegacy(out, value);
        }
        out.endObject();
    }

    private static void maybeWriteLegacy(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
        @Nullable BinaryTagHolder nbt = value.nbt();
        if (nbt != null) {
            out.name(LEGACY_SHOW_ITEM_TAG);
            out.value(nbt.string());
        }
    }
}

