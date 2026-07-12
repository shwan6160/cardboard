/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import java.lang.reflect.Type;
import org.bukkit.block.BlockFace;

public class BlockFaceDeserializer
implements JsonDeserializer<BlockFace> {
    @Override
    public BlockFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ParseUtil.parseEnum(BlockFace.class, jsonElement.getAsString(), null);
    }
}

