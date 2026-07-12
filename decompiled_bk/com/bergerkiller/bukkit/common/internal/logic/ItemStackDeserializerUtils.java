/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.FireworkEffect
 *  org.bukkit.configuration.serialization.ConfigurationSerializable
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ItemStackDeserializerUtils {
    public static Object deserializeSkullOwner(Map<String, Object> values) {
        return CraftItemStackHandle.T.deserializeSkullOwner.invoke(values);
    }

    public static Object deserializeCustomModelData(Map<String, Object> values) {
        ItemStackDeserializerUtils.convertNumberListToFloatInMap(values, "floats");
        ItemStackDeserializerUtils.replaceListOfMapsInMap(values, "colors", ItemStackDeserializerUtils::deserializeColor);
        return CraftItemStackHandle.T.deserializeCustomModelData.invoke(values);
    }

    protected static ConfigurationSerializable deserializeFireworkEffect(Map<String, Object> values) {
        ItemStackDeserializerUtils.replaceListOfMapsInMap(values, "colors", ItemStackDeserializerUtils::deserializeColor);
        ItemStackDeserializerUtils.replaceListOfMapsInMap(values, "fade-colors", ItemStackDeserializerUtils::deserializeColor);
        return FireworkEffect.deserialize(values);
    }

    protected static Color deserializeColor(Map<String, Object> values) {
        ItemStackDeserializerUtils.convertNumberToIntegerInMapValues(values);
        return Color.deserialize(values);
    }

    protected static void replaceListOfMapsInMap(Map<String, Object> map, String key, Function<Map<String, Object>, ?> mapper) {
        Object value = map.get(key);
        if (value instanceof List) {
            LogicUtil.mapListItems((List)value, o -> {
                if (o instanceof Map) {
                    return mapper.apply((Map)o);
                }
                return o;
            });
        }
    }

    protected static void replaceMapInMap(Map<String, Object> map, String key, Function<Map<String, Object>, ?> mapper) {
        map.computeIfPresent(key, (k, value) -> {
            if (value instanceof Map) {
                return mapper.apply((Map)value);
            }
            return value;
        });
    }

    protected static Object convertNumberToInteger(Object key, Object value) {
        if (value instanceof Number && !(value instanceof Integer)) {
            return ((Number)value).intValue();
        }
        return value;
    }

    protected static void convertNumberToIntegerInMapValues(Map<String, Object> map, String key) {
        Object mapAtKey = map.get(key);
        if (mapAtKey instanceof Map) {
            ItemStackDeserializerUtils.convertNumberToIntegerInMapValues((Map)mapAtKey);
        }
    }

    protected static void convertNumberToIntegerInMap(Map<?, ?> map, Object key) {
        map.computeIfPresent(key, ItemStackDeserializerUtils::convertNumberToInteger);
    }

    protected static void convertNumberToIntegerInMapValues(Map<?, ?> map) {
        LogicUtil.mapMapValues(map, ItemStackDeserializerUtils::convertNumberToInteger);
    }

    protected static void convertNumberListToFloatInMap(Map<?, ?> map, String key) {
        Object atKey = map.get(key);
        if (atKey instanceof List) {
            List raw = (List)atKey;
            int size = raw.size();
            for (int i = 0; i < size; ++i) {
                Object rawItem = raw.get(i);
                if (rawItem instanceof Float) continue;
                ArrayList<Float> newList = new ArrayList<Float>(raw);
                while (i < size) {
                    rawItem = newList.get(i);
                    if (!(rawItem instanceof Float) && rawItem instanceof Number) {
                        newList.set(i, Float.valueOf(((Number)rawItem).floatValue()));
                    }
                    ++i;
                }
                map.put(key, newList);
                return;
            }
        }
    }

    protected static <T> List<T> parseList(Object data, String key, Function<Object, T> parser) {
        if (!(data instanceof Map)) {
            return Collections.emptyList();
        }
        Object atKey = ((Map)data).get(key);
        if (!(atKey instanceof List)) {
            return Collections.emptyList();
        }
        List raw = (List)atKey;
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> result = new ArrayList<T>(raw.size());
        for (Object rawValue : raw) {
            result.add(parser.apply(rawValue));
        }
        return result;
    }

    protected static Optional<Map<String, Object>> readMap(Object value) {
        if (value instanceof Map) {
            return Optional.of((Map)value);
        }
        return Optional.empty();
    }

    protected static Optional<String> readString(Object value) {
        if (value instanceof String) {
            return Optional.of((String)value);
        }
        if (value != null) {
            return Optional.of(value.toString());
        }
        return Optional.empty();
    }

    protected static Optional<Integer> readInteger(Object value) {
        if (value instanceof Integer) {
            return Optional.of((Integer)value);
        }
        if (value instanceof Number) {
            return Optional.of(((Number)value).intValue());
        }
        return Optional.empty();
    }
}

