/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.serialization.ConfigurationSerializable
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.map.gson.EmptyMapSerializer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Collections;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class JsonSerializer {
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(Collections.emptyMap().getClass(), new EmptyMapSerializer()).create();

    public ItemStack fromJsonToItemStack(String json) throws JsonSyntaxException {
        if ("null".equals(json)) {
            return null;
        }
        Map<String, Object> mapping = this.jsonToMap(json);
        Object result = YamlDeserializer.INSTANCE.deserializeMapping(mapping);
        if (result instanceof Map) {
            return ItemStackDeserializer.INSTANCE.apply((Map)result);
        }
        if (result instanceof ItemStack) {
            return (ItemStack)result;
        }
        throw new JsonSyntaxException("Deserialized type is not an ItemStack or mapping: " + result);
    }

    public Map<String, Object> jsonToMap(String json) throws JsonSyntaxException {
        return this.fromJson(json, Map.class);
    }

    public <T> T fromJson(String json, Class<T> type) throws JsonSyntaxException {
        try {
            return this.gson.fromJson(json, type);
        }
        catch (com.bergerkiller.bukkit.common.dep.gson.JsonSyntaxException ex) {
            throw new JsonSyntaxException(ex.getMessage());
        }
    }

    public String itemStackToJson(ItemStack item) {
        if (item == null) {
            return "null";
        }
        Map<String, Object> map = LogicUtil.serializeDeep((ConfigurationSerializable)item);
        map.remove("==");
        return this.mapToJson(map);
    }

    public String mapToJson(Map<String, Object> map) {
        return this.gson.toJson(map);
    }

    public String toJson(Object value) {
        return this.gson.toJson(value);
    }

    public static class JsonSyntaxException
    extends Exception {
        private static final long serialVersionUID = -626531316069912407L;

        public JsonSyntaxException(String message) {
            super(message);
        }
    }
}

