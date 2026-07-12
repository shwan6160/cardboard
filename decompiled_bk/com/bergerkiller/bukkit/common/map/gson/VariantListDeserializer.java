/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import java.lang.reflect.Type;

public class VariantListDeserializer
implements JsonDeserializer<BlockModelState.VariantList> {
    @Override
    public BlockModelState.VariantList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModelState.VariantList list = new BlockModelState.VariantList();
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement arrayElement : array) {
                list.add((BlockModelState.Variant)jsonDeserializationContext.deserialize(arrayElement, (Type)((Object)BlockModelState.Variant.class)));
            }
        } else {
            list.add((BlockModelState.Variant)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)BlockModelState.Variant.class)));
        }
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (BlockModelState.Variant variant : list) {
                if (variant.modelName.startsWith("block/")) continue;
                variant.modelName = "block/" + variant.modelName;
            }
        }
        return list;
    }
}

