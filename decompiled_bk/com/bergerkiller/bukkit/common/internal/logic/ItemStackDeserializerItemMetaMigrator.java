/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.MapMaker
 *  org.bukkit.block.banner.Pattern
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.potion.PotionEffect
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigratorBukkit;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerUtils;
import com.bergerkiller.bukkit.common.internal.proxy.PlayerProfile_1_8_to_1_18;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class ItemStackDeserializerItemMetaMigrator
extends ItemStackDeserializerUtils
implements Function<Map<String, Object>, ItemMeta> {
    private final ItemStackDeserializerMigratorBukkit itemStackMigrator;
    private final List<Migrator> migrators = new ArrayList<Migrator>();
    private static final boolean IS_ENTITY_TAG_META_SUPPORTED = CommonBootstrap.evaluateMCVersion(">=", "1.16.2");
    private static final boolean IS_LEGACY_SKULL_DECODING_BUSTED = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");
    private static final boolean IS_SKULL_PROFILE_ID_STRING = CommonBootstrap.evaluateMCVersion("<", "1.16");
    private static final boolean IS_SKULL_PROFILE_STRING_ID_MANGLED = CommonBootstrap.evaluateMCVersion(">=", "1.16") && CommonBootstrap.evaluateMCVersion("<=", "1.16.3");
    private static final boolean IS_ITEM_FLAGS_SET = CommonBootstrap.evaluateMCVersion("<", "1.12");
    private final Map<ItemMeta, Map<String, Object>> itemMetaToArgs = new MapMaker().weakKeys().concurrencyLevel(4).makeMap();

    public ItemStackDeserializerItemMetaMigrator(ItemStackDeserializerMigratorBukkit itemStackMigrator) {
        this.itemStackMigrator = itemStackMigrator;
        if (!IS_ENTITY_TAG_META_SUPPORTED) {
            this.migrators.add((mapping, metaType) -> {
                if ("ENTITY_TAG".equals(metaType)) {
                    mapping.put("meta-type", "UNSPECIFIC");
                }
            });
        }
        if (IS_ITEM_FLAGS_SET) {
            this.migrators.add((mapping, metaType) -> mapping.computeIfPresent("ItemFlags", (key, value) -> {
                if (value instanceof List) {
                    List itemFlagsList = (List)value;
                    return new HashSet(itemFlagsList);
                }
                return value;
            }));
        }
        if (!CommonCapabilities.HAS_BUKKIT_PLAYER_PROFILE) {
            this.migrators.add((mapping, metaType) -> {
                String name;
                if (!"SKULL".equals(metaType)) {
                    return;
                }
                Object skullOwnerRaw = mapping.get("skull-owner");
                if (!(skullOwnerRaw instanceof PlayerProfile_1_8_to_1_18)) {
                    return;
                }
                Map<String, Object> skullOwnerMeta = ((PlayerProfile_1_8_to_1_18)skullOwnerRaw).meta;
                CommonTagCompound skullProfileNbt = new CommonTagCompound();
                String uniqueIdStr = LogicUtil.tryCast(skullOwnerMeta.get("uniqueId"), String.class);
                if (uniqueIdStr != null) {
                    try {
                        UUID uuid = UUID.fromString(uniqueIdStr);
                        long most = uuid.getMostSignificantBits();
                        long least = uuid.getLeastSignificantBits();
                        skullProfileNbt.putValue("Id", new int[]{(int)(most >> 32), (int)(most & 0xFFFFFFFFL), (int)(least >> 32), (int)(least & 0xFFFFFFFFL)});
                    }
                    catch (IllegalArgumentException uuid) {
                        // empty catch block
                    }
                }
                if ((name = LogicUtil.tryCast(skullOwnerMeta.get("name"), String.class)) != null) {
                    skullProfileNbt.putValue("Name", name);
                    mapping.put("skull-owner", name);
                } else {
                    mapping.remove("skull-owner");
                }
                Object propertiesRaw = skullOwnerMeta.get("properties");
                if (propertiesRaw instanceof List) {
                    List propertiesList = (List)propertiesRaw;
                    CommonTagCompound propertiesNbt = new CommonTagCompound();
                    for (Object propertyObj : propertiesList) {
                        Object propValue;
                        Map propertyMap;
                        String propName;
                        if (!(propertyObj instanceof Map) || (propName = LogicUtil.tryCast((propertyMap = (Map)propertyObj).get("name"), String.class)) == null || !((propValue = propertyMap.get("value")) instanceof String)) continue;
                        CommonTagCompound propNbt = new CommonTagCompound();
                        propNbt.putValue("Value", (String)propValue);
                        CommonTagList propValues = propertiesNbt.get(propName, CommonTagList.class);
                        if (propValues != null) {
                            propValues.add(propNbt);
                            continue;
                        }
                        propValues = new CommonTagList();
                        propValues.add(propNbt);
                        propertiesNbt.put(propName, propValues);
                    }
                    skullProfileNbt.put("Properties", propertiesNbt);
                }
                CommonTagCompound internalNBT = new CommonTagCompound();
                internalNBT.put("SkullProfile", skullProfileNbt);
                String internalStr = internalNBT.toBase64String();
                mapping.put("v", 2860);
                mapping.put("internal", internalStr);
            });
        }
        if (IS_LEGACY_SKULL_DECODING_BUSTED) {
            this.migrators.add((mapping, metaType) -> {
                CommonTagCompound propertiesNbt;
                if (!"SKULL".equals(metaType)) {
                    return;
                }
                Object skullOwnerRaw = mapping.get("skull-owner");
                if (skullOwnerRaw != null && !(skullOwnerRaw instanceof String)) {
                    return;
                }
                CommonTagCompound internalNbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (internalNbt == null) {
                    return;
                }
                CommonTagCompound skullProfileNbt = internalNbt.get("SkullProfile", CommonTagCompound.class);
                if (skullProfileNbt == null) {
                    return;
                }
                HashMap<String, Object> skullOwnerMeta = new HashMap<String, Object>();
                Object idValue = skullProfileNbt.getValue("Id");
                if (idValue instanceof String) {
                    skullOwnerMeta.put("uniqueId", idValue);
                } else if (idValue instanceof int[] && ((int[])idValue).length == 4) {
                    int[] idInts = (int[])idValue;
                    long most = (long)idInts[0] << 32 | (long)idInts[1] & 0xFFFFFFFFL;
                    long least = (long)idInts[2] << 32 | (long)idInts[3] & 0xFFFFFFFFL;
                    UUID uuid = new UUID(most, least);
                    skullOwnerMeta.put("uniqueId", uuid.toString());
                }
                String name = (String)((Object)skullProfileNbt.getValue("Name", String.class));
                if (name != null) {
                    skullOwnerMeta.put("name", name);
                }
                if ((propertiesNbt = skullProfileNbt.get("Properties", CommonTagCompound.class)) != null) {
                    ArrayList propertiesList = new ArrayList();
                    for (String propName : propertiesNbt.keySet()) {
                        CommonTagList propValues = propertiesNbt.get(propName, CommonTagList.class);
                        if (propValues == null) continue;
                        for (int i = 0; i < propValues.size(); ++i) {
                            String propValue;
                            CommonTagCompound propNbt = (CommonTagCompound)((Object)propValues.getValue(i, CommonTagCompound.class));
                            if (propNbt == null || (propValue = (String)((Object)propNbt.getValue("Value", String.class))) == null) continue;
                            HashMap<String, String> propertyMap = new HashMap<String, String>();
                            propertyMap.put("name", propName);
                            propertyMap.put("value", propValue);
                            propertiesList.add(propertyMap);
                        }
                    }
                    skullOwnerMeta.put("properties", propertiesList);
                }
                mapping.put("skull-owner", ItemStackDeserializerItemMetaMigrator.deserializeSkullOwner(skullOwnerMeta));
                mapping.remove("internal");
            });
        }
        if (IS_SKULL_PROFILE_ID_STRING) {
            this.migrators.add((mapping, metaType) -> {
                int[] idInts;
                Object idRaw;
                CommonTagCompound skullProfileNbt;
                if (!"SKULL".equals(metaType)) {
                    return;
                }
                CommonTagCompound nbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (nbt != null && (skullProfileNbt = nbt.get("SkullProfile", CommonTagCompound.class)) != null && (idRaw = skullProfileNbt.getValue("Id")) instanceof int[] && (idInts = (int[])idRaw).length == 4) {
                    long most = (long)idInts[0] << 32 | (long)idInts[1] & 0xFFFFFFFFL;
                    long least = (long)idInts[2] << 32 | (long)idInts[3] & 0xFFFFFFFFL;
                    UUID uuid = new UUID(most, least);
                    skullProfileNbt.putValue("Id", uuid.toString());
                    mapping.put("internal", nbt.toBase64String());
                }
            });
        }
        if (IS_SKULL_PROFILE_STRING_ID_MANGLED) {
            this.migrators.add((mapping, metaType) -> {
                if (!"SKULL".equals(metaType)) {
                    return;
                }
                CommonTagCompound nbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(mapping.get("internal"), String.class));
                if (nbt == null) {
                    return;
                }
                CommonTagCompound skullProfileNbt = nbt.get("SkullProfile", CommonTagCompound.class);
                if (skullProfileNbt == null) {
                    return;
                }
                Object idRaw = skullProfileNbt.getValue("Id");
                if (idRaw instanceof String) {
                    try {
                        UUID uuid = UUID.fromString((String)idRaw);
                        long most = uuid.getMostSignificantBits();
                        long least = uuid.getLeastSignificantBits();
                        skullProfileNbt.putValue("Id", new int[]{(int)(most >> 32), (int)(most & 0xFFFFFFFFL), (int)(least >> 32), (int)(least & 0xFFFFFFFFL)});
                        mapping.put("internal", nbt.toBase64String());
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        // empty catch block
                    }
                }
            });
        }
    }

    @Override
    public ItemMeta apply(Map<String, Object> mapping) {
        Object value = mapping.get("custom-model-data");
        if (value instanceof Number) {
            mapping.put("custom-model-data", ((Number)value).intValue());
        } else if (value instanceof Map) {
            Map cmdValues = (Map)value;
            if (CraftItemStackHandle.T.deserializeCustomModelData.isAvailable()) {
                mapping.put("custom-model-data", ItemStackDeserializerItemMetaMigrator.deserializeCustomModelData(cmdValues));
            } else {
                Object rawFloats = cmdValues.get("floats");
                if (rawFloats instanceof List) {
                    List floats = (List)rawFloats;
                    if (!floats.isEmpty() && floats.get(0) instanceof Number) {
                        mapping.put("custom-model-data", ((Number)floats.get(0)).intValue());
                    } else {
                        mapping.remove("custom-model-data");
                    }
                } else {
                    mapping.remove("custom-model-data");
                }
            }
        }
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "repair-cost");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "Damage");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "max-damage");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "max-stack-size");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "generation");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "power");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "map-id");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(mapping, "fish-variant");
        ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMapValues(mapping, "enchants");
        ItemStackDeserializerItemMetaMigrator.replaceMapInMap(mapping, "color", ItemStackDeserializerUtils::deserializeColor);
        ItemStackDeserializerItemMetaMigrator.replaceMapInMap(mapping, "display-map-color", ItemStackDeserializerUtils::deserializeColor);
        ItemStackDeserializerItemMetaMigrator.replaceMapInMap(mapping, "custom-color", ItemStackDeserializerUtils::deserializeColor);
        ItemStackDeserializerItemMetaMigrator.replaceMapInMap(mapping, "firework-effect", ItemStackDeserializerUtils::deserializeFireworkEffect);
        ItemStackDeserializerItemMetaMigrator.replaceMapInMap(mapping, "skull-owner", ItemStackDeserializerUtils::deserializeSkullOwner);
        ItemStackDeserializerItemMetaMigrator.replaceListOfMapsInMap(mapping, "firework-effects", ItemStackDeserializerUtils::deserializeFireworkEffect);
        ItemStackDeserializerItemMetaMigrator.replaceListOfMapsInMap(mapping, "patterns", Pattern::new);
        ItemStackDeserializerItemMetaMigrator.replaceListOfMapsInMap(mapping, "charged-projectiles", this.itemStackMigrator);
        ItemStackDeserializerItemMetaMigrator.replaceListOfMapsInMap(mapping, "custom-effects", potionEffect -> {
            ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(potionEffect, "amplifier");
            ItemStackDeserializerItemMetaMigrator.convertNumberToIntegerInMap(potionEffect, "duration");
            return new PotionEffect(potionEffect);
        });
        String metaType = LogicUtil.tryCast(mapping.get("meta-type"), String.class);
        for (Migrator migrator : this.migrators) {
            migrator.migrate(mapping, metaType);
        }
        return this.applyWithoutFixes(mapping);
    }

    public ItemMeta applyWithoutFixes(Map<String, Object> mapping) {
        ItemMeta meta = CraftItemStackHandle.deserializeItemMeta(mapping);
        this.itemMetaToArgs.put(meta, mapping);
        return meta;
    }

    public Map<String, Object> getArgsUsedForMeta(ItemMeta meta) {
        return this.itemMetaToArgs.get(meta);
    }

    public void cleanupArgsUsedForMeta(ItemMeta meta) {
        this.itemMetaToArgs.remove(meta);
    }

    public static interface Migrator {
        public void migrate(Map<String, Object> var1, String var2);
    }
}

