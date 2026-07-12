/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.config.SNBTDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.dep.gson.JsonPrimitive;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerIdToMaterialMapper;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigrator;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigratorBukkit;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerUtils;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public class ItemStackDeserializerMigratorNBT
extends ItemStackDeserializerMigrator
implements Function<Map<String, Object>, ItemStack> {
    private final NBTToBukkit nbtToBukkit = new NBTToBukkit();
    private final boolean isPaperServer = CommonBootstrap.isPaperServer();

    ItemStackDeserializerMigratorNBT() {
        this.register(4325, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4435, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4438, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4440, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4554, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4556, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4671, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4786, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4790, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.setMaximumDataVersion(4903);
    }

    public Map<String, Object> toBukkitEncoding(Map<String, Object> args) {
        this.migrate(args, "DataVersion");
        return this.nbtToBukkit.apply(args);
    }

    @Override
    protected void baseMigrate(Map<String, Object> args) {
        ItemStackDeserializerMigratorNBT.convertNumberToIntegerInMap(args, "count");
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        this.migrate(args, "DataVersion");
        try {
            if (this.isPaperServer) {
                return ItemStack.deserialize(args);
            }
            CommonTagCompound tag = new CommonTagCompound();
            ItemStackDeserializerMigratorNBT.readInteger(args.get("DataVersion")).ifPresent(dataVersion -> tag.putValue("DataVersion", dataVersion));
            ItemStackDeserializerMigratorNBT.readString(args.get("id")).ifPresent(id -> tag.putValue("id", id));
            ItemStackDeserializerMigratorNBT.readInteger(args.get("count")).ifPresent(count -> tag.putValue("count", count));
            ItemStackDeserializerMigratorNBT.readMap(args.get("components")).ifPresent(componentsMap -> {
                CommonTagCompound components = new CommonTagCompound();
                for (Map.Entry component : componentsMap.entrySet()) {
                    String snbt = ItemStackDeserializerMigratorNBT.readString(component.getValue()).orElseThrow(() -> new IllegalStateException("Missing component value for " + (String)component.getKey()));
                    CommonTag componentTag = CommonTag.fromSNBT(snbt).getResultOrThrow(RuntimeException::new);
                    components.put((String)component.getKey(), componentTag);
                }
                tag.put("components", components);
            });
            return CraftItemStackHandle.T.deserializeNBT.invoke(tag);
        }
        catch (RuntimeException ex) {
            ItemStackDeserializerMigratorNBT.logFailDeserialize(args);
            throw ex;
        }
    }

    public static class NBTToBukkit
    implements Function<Map<String, Object>, Map<String, Object>> {
        private final ItemStackDeserializerIdToMaterialMapper idMapper;
        private final NBTChatComponentsToJson chatComponentsToJson;
        private final Map<String, ComponentMapper> componentMappers = new HashMap<String, ComponentMapper>();

        public NBTToBukkit() {
            this.idMapper = new ItemStackDeserializerIdToMaterialMapper();
            this.idMapper.loadMappings();
            this.chatComponentsToJson = new NBTChatComponentsToJson();
            this.componentMappers.put("minecraft:custom_model_data", (result, nbtData) -> {
                LinkedHashMap<String, Object> cmd = new LinkedHashMap<String, Object>();
                cmd.put("==", "CustomModelData");
                cmd.put("floats", ItemStackDeserializerUtils.parseList(nbtData, "floats", o -> Float.valueOf(o instanceof Number ? ((Number)o).floatValue() : 0.0f)));
                cmd.put("flags", ItemStackDeserializerUtils.parseList(nbtData, "flags", o -> o instanceof Number && ((Number)o).intValue() != 0));
                cmd.put("strings", ItemStackDeserializerUtils.parseList(nbtData, "strings", o -> o != null ? o.toString() : ""));
                cmd.put("colors", ItemStackDeserializerUtils.parseList(nbtData, "colors", o -> {
                    int argb = o instanceof Number ? ((Number)o).intValue() : 0;
                    LinkedHashMap<String, Object> s = new LinkedHashMap<String, Object>(5);
                    s.put("==", "Color");
                    s.put("ALPHA", argb >> 24 & 0xFF);
                    s.put("RED", argb >> 16 & 0xFF);
                    s.put("GREEN", argb >> 8 & 0xFF);
                    s.put("BLUE", argb & 0xFF);
                    return s;
                }));
                NBTToBukkit.prepareMetadata(result).put("custom-model-data", cmd);
            });
            this.componentMappers.put("minecraft:unbreakable", (result, nbtData) -> NBTToBukkit.prepareMetadata(result).put("Unbreakable", true));
            this.componentMappers.put("minecraft:damage", (result, nbtData) -> {
                if (nbtData instanceof Number) {
                    NBTToBukkit.prepareMetadata(result).put("Damage", ((Number)nbtData).intValue());
                }
            });
            this.componentMappers.put("minecraft:item_model", (result, nbtData) -> {
                if (nbtData instanceof String) {
                    NBTToBukkit.prepareMetadata(result).put("item-model", nbtData);
                }
            });
            this.componentMappers.put("minecraft:custom_name", (result, nbtData) -> {
                if (nbtData != null) {
                    NBTToBukkit.prepareMetadata(result).put("display-name", this.chatComponentsToJson.toJson(nbtData));
                }
            });
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> args) {
            Optional<Map<String, Object>> components;
            Map idMappings;
            int dataVersion = ItemStackDeserializerUtils.readInteger(args.get("DataVersion")).orElse(4325);
            String id = ItemStackDeserializerUtils.readString(args.get("id")).orElse(null);
            if (id == null) {
                ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
                throw new IllegalArgumentException("ItemStack has no id field set");
            }
            if (id.indexOf(58) == -1) {
                id = "minecraft:" + id;
            }
            if ((idMappings = (Map)this.idMapper.getOrOlder(Integer.toString(dataVersion)).orElse(null)) == null) {
                ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
                throw new IllegalArgumentException("Unsupported data version: " + dataVersion);
            }
            String type = (String)idMappings.get(id);
            if (type == null) {
                type = "UNKNOWN_MATERIAL_TYPE";
            }
            int amount = ItemStackDeserializerUtils.readInteger(args.get("count")).orElse(1);
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("==", "org.bukkit.inventory.ItemStack");
            result.put("v", dataVersion);
            result.put("type", type);
            if (amount != 1) {
                result.put("amount", amount);
            }
            if ((components = ItemStackDeserializerUtils.readMap(args.get("components"))).isPresent()) {
                for (Map.Entry<String, Object> component : components.get().entrySet()) {
                    ComponentMapper mapper;
                    String key = component.getKey();
                    if (key.indexOf(58) == -1) {
                        key = "minecraft:" + key;
                    }
                    if ((mapper = this.componentMappers.get(key)) == null) continue;
                    Object nbtData = component.getValue();
                    if (nbtData != null) {
                        nbtData = SNBTDeserializer.parse(nbtData.toString());
                    }
                    mapper.apply(result, nbtData);
                }
            }
            return result;
        }

        private static Map<String, Object> prepareMetadata(Map<String, Object> result) {
            return (Map)result.computeIfAbsent("meta", m -> {
                LinkedHashMap<String, String> meta = new LinkedHashMap<String, String>();
                meta.put("==", "ItemMeta");
                meta.put("meta-type", "UNSPECIFIC");
                return meta;
            });
        }

        @FunctionalInterface
        private static interface ComponentMapper {
            public void apply(Map<String, Object> var1, Object var2);
        }
    }

    private static class NBTChatComponentsToJson {
        private final Gson gson;

        public NBTChatComponentsToJson() {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter((Type)((Object)Byte.class), (src, type, jsonSerializationContext) -> new JsonPrimitive(src != 0));
            this.gson = builder.create();
        }

        public String toJson(Object nbtData) {
            if (nbtData instanceof String) {
                nbtData = Collections.singletonMap("text", nbtData);
            }
            return this.gson.toJson(nbtData);
        }
    }
}

