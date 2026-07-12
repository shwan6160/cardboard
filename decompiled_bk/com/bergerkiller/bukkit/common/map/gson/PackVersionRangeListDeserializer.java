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
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PackVersionRangeListDeserializer
implements JsonDeserializer<List<MapResourcePack.PackVersionRange>> {
    PackVersionRangeListDeserializer() {
    }

    @Override
    public List<MapResourcePack.PackVersionRange> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        List<JsonElement> elements;
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            elements = new ArrayList<JsonElement>(array.size());
            for (int i = 0; i < array.size(); ++i) {
                elements.add(array.get(i));
            }
        } else {
            elements = Collections.singletonList(jsonElement);
        }
        ArrayList<MapResourcePack.PackVersionRange> ranges = new ArrayList<MapResourcePack.PackVersionRange>(elements.size());
        for (JsonElement element : elements) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                JsonElement min_inclusive_el = obj.get("min_inclusive");
                JsonElement max_inclusive_el = obj.get("max_inclusive");
                if (!PackVersionRangeListDeserializer.isNumberPrimitive(min_inclusive_el) || !PackVersionRangeListDeserializer.isNumberPrimitive(max_inclusive_el)) continue;
                ranges.add(MapResourcePack.PackVersionRange.of(min_inclusive_el.getAsInt(), max_inclusive_el.getAsInt()));
                continue;
            }
            if (!PackVersionRangeListDeserializer.isNumberPrimitive(element)) continue;
            ranges.add(MapResourcePack.PackVersionRange.of(element.getAsInt()));
        }
        return Collections.unmodifiableList(ranges);
    }

    private static boolean isNumberPrimitive(JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
}

