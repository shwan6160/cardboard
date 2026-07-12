/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class ConditionalDeserializer
implements JsonDeserializer<BlockModelState.Condition> {
    private static BlockModelState.Condition createSelfCondition(String key, String value) {
        String[] parts = value.split("\\|");
        if (parts.length > 1) {
            BlockModelState.Condition orCondition = new BlockModelState.Condition();
            orCondition.mode = BlockModelState.Condition.Mode.OR;
            orCondition.conditions = new ArrayList<BlockModelState.Condition>(parts.length);
            for (String part : parts) {
                BlockModelState.Condition condition = new BlockModelState.Condition();
                condition.mode = BlockModelState.Condition.Mode.SELF;
                condition.conditions = Collections.emptyList();
                condition.key = key;
                condition.value = part;
                orCondition.conditions.add(condition);
            }
            return orCondition;
        }
        BlockModelState.Condition condition = new BlockModelState.Condition();
        condition.mode = BlockModelState.Condition.Mode.SELF;
        condition.conditions = Collections.emptyList();
        condition.key = key;
        condition.value = value;
        return condition;
    }

    @Override
    public BlockModelState.Condition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModelState.Condition result = new BlockModelState.Condition();
        result.mode = BlockModelState.Condition.Mode.AND;
        result.conditions = new ArrayList<BlockModelState.Condition>(1);
        if (jsonElement.isJsonPrimitive()) {
            String options_str = jsonElement.getAsString();
            if (options_str.isEmpty()) {
                options_str = "normal";
            }
            BlockRenderOptions options = new BlockRenderOptions(BlockData.AIR, options_str);
            for (Map.Entry option : options.entrySet()) {
                result.conditions.add(ConditionalDeserializer.createSelfCondition((String)option.getKey(), (String)option.getValue()));
            }
        } else {
            JsonObject obj = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                BlockModelState.Condition subCondition = new BlockModelState.Condition();
                subCondition.mode = BlockModelState.Condition.Mode.SELF;
                for (BlockModelState.Condition.Mode mode : BlockModelState.Condition.Mode.values()) {
                    if (!entry.getKey().equals(mode.name())) continue;
                    subCondition.mode = mode;
                    break;
                }
                if (subCondition.mode == BlockModelState.Condition.Mode.SELF) {
                    subCondition = ConditionalDeserializer.createSelfCondition(entry.getKey(), entry.getValue().getAsString());
                } else if (entry.getValue().isJsonArray()) {
                    JsonArray condArr = entry.getValue().getAsJsonArray();
                    subCondition.conditions = new ArrayList<BlockModelState.Condition>(condArr.size());
                    for (JsonElement condElem : condArr) {
                        subCondition.conditions.add(this.deserialize(condElem, type, jsonDeserializationContext));
                    }
                } else {
                    subCondition.conditions = Arrays.asList(this.deserialize(entry.getValue(), type, jsonDeserializationContext));
                }
                result.conditions.add(subCondition);
            }
        }
        switch (result.conditions.size()) {
            case 0: {
                result.mode = BlockModelState.Condition.Mode.SELF;
                result.conditions = Collections.emptyList();
                result.key = "normal";
                result.value = "";
                return result;
            }
            case 1: {
                return result.conditions.get(0);
            }
        }
        return result;
    }
}

