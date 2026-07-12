/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonPrimitive;
import com.bergerkiller.bukkit.common.dep.gson.JsonSyntaxException;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.ConditionalDeserializer;
import com.bergerkiller.bukkit.common.map.gson.NonNullListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.PackVersionDeserializer;
import com.bergerkiller.bukkit.common.map.gson.PackVersionRangeListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.VariantListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3Deserializer;
import com.bergerkiller.bukkit.common.map.gson.types.ResourcePackDescription;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.block.BlockFace;

public final class MapResourcePackDeserializer {
    public final Gson gson;

    public static MapResourcePackDeserializer create() {
        return new MapResourcePackDeserializer();
    }

    private MapResourcePackDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter((Type)((Object)Vector3.class), new Vector3Deserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)BlockFace.class), new BlockFaceDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)BlockModelState.VariantList.class), new VariantListDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)BlockModelState.Condition.class), new ConditionalDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)List.class), new NonNullListDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)MapResourcePack.PackVersion.class), new PackVersionDeserializer());
        gsonBuilder.registerTypeAdapter(new TypeToken<List<MapResourcePack.PackVersionRange>>(){}.getType(), new PackVersionRangeListDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)ResourcePackDescription.class), new ResourcePackDescription.Deserializer());
        ItemModel.registerDeserializers(gsonBuilder);
        this.gson = gsonBuilder.create();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public <T> T readGsonObject(Class<T> objectType, InputStream inputStream, String optPath) {
        if (inputStream == null) {
            return null;
        }
        try {
            try {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                T result = this.gson.fromJson((Reader)reader, objectType);
                if (result == null) {
                    String s = optPath == null ? "" : " at " + optPath;
                    throw new IOException("Failed to parse JSON for " + objectType.getSimpleName() + s);
                }
                T s = result;
                return s;
            }
            finally {
                inputStream.close();
            }
        }
        catch (JsonSyntaxException ex) {
            String s2 = optPath == null ? "" : " at " + optPath;
            String msg = ex.getMessage();
            msg = StringUtil.trimStart(msg, "com.bergerkiller.bukkit.common.dep.gson.stream.MalformedJsonException: ");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to parse GSON for " + objectType.getSimpleName() + s2 + ": " + msg);
            return null;
        }
        catch (IOException ex) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Unhandled IO Exception", ex);
        }
        return null;
    }

    public static Optional<Integer> tryParseAsInt(JsonElement jsonElement) {
        try {
            return Optional.of(jsonElement.getAsInt());
        }
        catch (IllegalStateException | NumberFormatException | UnsupportedOperationException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Double> tryParseAsDouble(JsonElement jsonElement) {
        try {
            return Optional.of(jsonElement.getAsDouble());
        }
        catch (IllegalStateException | NumberFormatException | UnsupportedOperationException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> tryParseAsBoolean(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return Optional.empty();
        }
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            if (array.size() == 1) {
                jsonElement = array.get(0);
            } else {
                return Optional.empty();
            }
        }
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return Optional.of(jsonElement.getAsBoolean());
            }
            if (primitive.isNumber()) {
                return Optional.of(jsonElement.getAsInt() != 0);
            }
            if (primitive.isString()) {
                String value;
                switch (value = jsonElement.getAsString().toLowerCase()) {
                    case "1": 
                    case "true": {
                        return Optional.of(Boolean.TRUE);
                    }
                    case "0": 
                    case "false": {
                        return Optional.of(Boolean.FALSE);
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}

