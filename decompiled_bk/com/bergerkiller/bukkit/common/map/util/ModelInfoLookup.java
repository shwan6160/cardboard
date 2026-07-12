/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ModelInfoLookup {
    private static final IdentifierHandle MISSING_MODEL_NAME = IdentifierHandle.createNew("minecraft", "builtin/missing");
    private static final Map<Material, List<ModelNameEntry>> byMaterial = new EnumMap<Material, List<ModelNameEntry>>(Material.class);
    private static final Map<String, ModelNameEntry> byItemName = new HashMap<String, ModelNameEntry>();
    private static final Map<String, ModelNameEntry> byBlockName = new HashMap<String, ModelNameEntry>();

    public static ItemRenderOptions lookupItemRenderOptions(ItemStack item) {
        return ModelInfoLookup.lookupItemRenderOptions(CommonItemStack.of(item));
    }

    public static ItemRenderOptions lookupItemRenderOptions(CommonItemStack item) {
        Material type = item.getType();
        if (MaterialUtil.isBlock(type)) {
            BlockRenderOptions blockOpt = BlockData.fromItemStack(item).getDefaultRenderOptions();
            return new ItemRenderOptions(item, (Map<String, String>)blockOpt);
        }
        ItemStackHandle handle = item.getHandle().orElseThrow(() -> new IllegalStateException("Item should not be empty"));
        ItemRenderOptions options = new ItemRenderOptions(item, "");
        if (MaterialUtil.ISLEATHERARMOR.get(type).booleanValue()) {
            options.put("layer0tint", String.format("#%06x", handle.getLeatherArmorColor()));
        }
        if (MaterialUtil.ISPOTION.get(type).booleanValue()) {
            options.put("layer0tint", String.format("#%06x", handle.getPotionColor()));
        }
        if (item.isDamageSupported()) {
            options.put("damaged", item.isUnbreakable() ? "0" : "1");
            options.put("damage", Double.toString((double)item.getDamage() / (double)(item.getMaxDamage() + 1)));
        }
        if (CommonCapabilities.HAS_CUSTOM_MODEL_DATA && item.hasCustomModelData()) {
            options.put("custom_model_data", Integer.toString(item.getCustomModelData()));
        }
        return options;
    }

    public static int getPotionColor(int durability) {
        switch (durability & 0xF) {
            case 1: {
                return 0xFF68FF;
            }
            case 2: {
                return 8105670;
            }
            case 3: {
                return 14916153;
            }
            case 4: {
                return 5149488;
            }
            case 5: {
                return 16196386;
            }
            case 6: {
                return 2039712;
            }
            case 8: {
                return 0x484D48;
            }
            case 9: {
                return 0x932322;
            }
            case 10: {
                return 5925761;
            }
            case 11: {
                return 2228044;
            }
            case 12: {
                return 4393481;
            }
            case 13: {
                return 2970265;
            }
            case 14: {
                return 8291218;
            }
        }
        return 3694022;
    }

    public static Optional<CommonItemStack> findItemStackByModelName(String itemModelName) {
        IdentifierHandle key;
        int nameSpace = itemModelName.indexOf(58);
        ModelNameEntry entry = nameSpace == -1 ? byItemName.get(itemModelName) : (itemModelName.startsWith("minecraft") ? byItemName.get(itemModelName.substring(10)) : null);
        if (entry != null) {
            return entry.toItemStack();
        }
        if (CommonCapabilities.HAS_ITEM_MODEL_COMPONENT && (key = IdentifierHandle.createNew(itemModelName)) != null) {
            Material type = CommonPlugin.hasInstance() ? CommonPlugin.getInstance().getFallbackItemModelType() : CommonPlugin.getDefaultFallbackItemModelType();
            return Optional.of(CommonItemStack.create(type, 1).setItemModel(key));
        }
        return Optional.empty();
    }

    public static Optional<Material> findBlockMaterialByModelName(String blockModelName) {
        ModelNameEntry entry = byBlockName.get(blockModelName);
        if (entry != null) {
            return Optional.of(entry.getMaterial());
        }
        return Optional.empty();
    }

    public static String lookupBlock(BlockRenderOptions options) {
        return byMaterial.getOrDefault(options.getBlockData().getType(), ModelNameEntry.UNKNOWN).get(0).getBlockName().orElse("unknown");
    }

    public static String lookupItem(ItemRenderOptions options) {
        return ModelInfoLookup.lookupItem(options.getCommonItem());
    }

    public static String lookupItem(CommonItemStack itemStack) {
        return itemStack.getItemModel().toShortString();
    }

    public static IdentifierHandle lookupVanillaItemModel(CommonItemStack itemStack) {
        return ModelInfoLookup.lookupItemModelNameEntry(itemStack).getItemNameKey().orElse(MISSING_MODEL_NAME);
    }

    private static ModelNameEntry lookupItemModelNameEntry(CommonItemStack itemStack) {
        List<ModelNameEntry> entries = byMaterial.get(itemStack.getType());
        if (entries.size() > 1) {
            for (ModelNameEntry entry : entries) {
                if (!entry.matchDynamicItem(itemStack)) continue;
                return entry;
            }
        }
        return entries.get(0);
    }

    static {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (Material material : CommonLegacyMaterials.getAllMaterials()) {
                if (MaterialUtil.isLegacyType(material)) continue;
                byMaterial.put(material, ModelNameEntry.createEntriesFor(material));
            }
            for (Material legacyMaterial : CommonLegacyMaterials.getAllLegacyMaterials()) {
                if (MaterialUtil.isBlock(legacyMaterial)) {
                    BlockData blockData = BlockData.fromMaterial(legacyMaterial);
                    if (blockData == null) continue;
                    List<ModelNameEntry> existing = byMaterial.get(blockData.getType());
                    if (existing != null) {
                        byMaterial.put(legacyMaterial, existing);
                        continue;
                    }
                } else {
                    Material actualMaterial;
                    ItemHandle itemHandle = ItemHandle.fromMaterial(legacyMaterial);
                    if (itemHandle == null || (actualMaterial = CraftMagicNumbersHandle.getMaterialFromItem(itemHandle.getRaw())) == null) continue;
                    List<ModelNameEntry> existing = byMaterial.get(actualMaterial);
                    if (existing != null) {
                        byMaterial.put(legacyMaterial, existing);
                        continue;
                    }
                }
                byMaterial.put(legacyMaterial, ModelNameEntry.createEntriesFor(legacyMaterial));
            }
        } else {
            for (Material material : CommonLegacyMaterials.getAllMaterials()) {
                byMaterial.put(material, ModelNameEntry.createEntriesFor(material));
            }
        }
        for (List list : byMaterial.values()) {
            for (ModelNameEntry entry : list) {
                entry.getItemName().ifPresent(name -> byItemName.put((String)name, entry));
                entry.getBlockName().ifPresent(name -> byBlockName.put((String)name, entry));
            }
        }
        ModelNameEntry e = byItemName.get("bottle_drinkable");
        if (e != null) {
            byItemName.put("potion", e);
        }
    }

    private static interface ModelNameEntry {
        public static final List<ModelNameEntry> UNKNOWN = Collections.singletonList(new ModelNameEntry(){

            @Override
            public Material getMaterial() {
                throw new UnsupportedOperationException("UNKNOWN has no material");
            }

            @Override
            public Optional<String> getItemName() {
                return Optional.empty();
            }

            @Override
            public Optional<IdentifierHandle> getItemNameKey() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getBlockName() {
                return Optional.empty();
            }

            @Override
            public Optional<CommonItemStack> toItemStack() {
                return Optional.empty();
            }
        });

        public Material getMaterial();

        public Optional<String> getItemName();

        public Optional<IdentifierHandle> getItemNameKey();

        public Optional<String> getBlockName();

        public Optional<CommonItemStack> toItemStack();

        default public boolean matchDynamicItem(CommonItemStack item) {
            return true;
        }

        public static List<ModelNameEntry> createEntriesFor(Material material) {
            if (MaterialUtil.isBlock(material)) {
                return Collections.singletonList(new BlockDataModelNameEntry(BlockData.fromMaterial(material)));
            }
            if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                String typeName = CommonLegacyMaterials.getMaterialName(material);
                if ("LEGACY_COOKED_FISH".equals(typeName)) {
                    return LegacyFishItemModelNameEntry.createLegacyFishMaterials(material, true);
                }
                if ("LEGACY_RAW_FISH".equals(typeName)) {
                    return LegacyFishItemModelNameEntry.createLegacyFishMaterials(material, false);
                }
                if ("LEGACY_INK_SACK".equals(typeName)) {
                    return LegacyInkSacItemModelNameEntry.createLegacyInkSacMaterials(material);
                }
            }
            return Collections.singletonList(new ItemModelNameEntry(material));
        }
    }

    private static class BlockDataModelNameEntry
    implements ModelNameEntry {
        public final BlockData blockData;
        public final String blockName;
        public final String itemName;
        public final IdentifierHandle itemNameKey;

        public BlockDataModelNameEntry(BlockData blockData) {
            this.blockData = blockData;
            this.blockName = BlockDataModelNameEntry.lookupBlockResourceName(blockData.getDefaultRenderOptions());
            this.itemName = this.blockName.equals("fence") ? "oak_fence" : (this.blockName.equals("fence_gate") ? "oak_fence_gate" : (this.blockName.equals("wooden_door") ? "oak_door" : this.blockName));
            this.itemNameKey = IdentifierHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return this.blockData.getType();
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(this.itemName);
        }

        @Override
        public Optional<IdentifierHandle> getItemNameKey() {
            return Optional.of(this.itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.of(this.blockName);
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(this.blockData.createCommonItem(1));
        }

        private static String lookupBlockResourceName(BlockRenderOptions options) {
            String type;
            String name = options.getBlockData().getBlockName();
            String variant = options.get("variant");
            if (!CommonCapabilities.HAS_MATERIAL_SIGN_TYPES) {
                if (name.equals("sign") || name.equals("standing_sign")) {
                    name = "legacy_sign";
                } else if (name.equals("wall_sign")) {
                    name = "legacy_wall_sign";
                }
            }
            if (CommonCapabilities.BLOCK_SLAB_HAS_OWN_BLOCK) {
                variant = null;
            } else if (name.equals("purpur_slab") || name.equals("purpur_double_slab")) {
                variant = null;
            } else if (name.contains("_slab")) {
                if (variant == null) {
                    variant = "stone";
                }
                name = "slab";
            }
            if (name.equals("sapling")) {
                type = options.get("type");
                if (type == null) {
                    type = "oak";
                }
                name = type + "_" + name;
            }
            if (name.equals("wool") || name.equals("carpet") || name.equals("concrete") || name.equals("concrete_powder") || name.equals("stained_hardened_clay") || name.equals("stained_glass_pane") || name.equals("stained_glass")) {
                String color = options.get("color");
                if (color == null) {
                    color = "white";
                }
                name = color + "_" + name;
            }
            if (name.equals("log2")) {
                if (variant == null) {
                    variant = "oak";
                }
                name = "log";
            }
            if (name.equals("leaves2")) {
                name = "leaves";
            }
            if (name.equals("double_plant")) {
                name = "";
                if (variant == null) {
                    variant = "double_grass";
                }
            }
            if (name.equals("tallgrass")) {
                name = "tall_grass";
            }
            if (name.equals("deadbush")) {
                name = "dead_bush";
            }
            if (name.equals("red_flower") || name.equals("yellow_flower")) {
                type = options.get("type");
                if (type == null) {
                    type = "red_tulip";
                }
                name = type;
            }
            if (name.equals("brown_mushroom_block") || name.equals("red_mushroom_block") || name.equals("stonebrick") || name.equals("cobblestone_wall") || name.equals("stone") || name.equals("prismarine") || name.equals("purpur_slab") || name.equals("quartz_block") || name.equals("dirt") || name.equals("sand")) {
                variant = null;
            }
            if (name.equals("end_gateway")) {
                name = "end_portal_frame";
            }
            if (variant != null) {
                name = name.length() > 0 ? variant + "_" + name : variant;
            }
            return name;
        }
    }

    private static class ItemModelNameEntry
    implements ModelNameEntry {
        public final Material itemMaterial;
        public final String itemName;
        private final IdentifierHandle itemNameKey;

        public ItemModelNameEntry(Material itemMaterial) {
            this.itemMaterial = itemMaterial;
            this.itemName = ItemModelNameEntry.lookupItemResourceName(itemMaterial);
            this.itemNameKey = IdentifierHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return this.itemMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(this.itemName);
        }

        @Override
        public Optional<IdentifierHandle> getItemNameKey() {
            return Optional.of(this.itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(this.itemMaterial, 1));
        }

        private static String lookupItemResourceName(Material itemMaterial) {
            Object itemHandle = HandleConversion.toItemHandle(itemMaterial);
            Object minecraftKey = MappedRegistryHandle.T.getKey.invoke(ItemHandle.getRegistry(), itemHandle);
            String itemName = IdentifierHandle.T.getName.invoke(minecraftKey);
            String typeName = CommonLegacyMaterials.getMaterialName(itemMaterial);
            if (LogicUtil.contains(typeName, "POTION", "LEGACY_POTION")) {
                itemName = "bottle_drinkable";
            } else if (LogicUtil.contains(typeName, "LINGERING_POTION", "LEGACY_LINGERING_POTION")) {
                itemName = "bottle_lingering";
            } else if (LogicUtil.contains(typeName, "SPLASH_POTION", "LEGACY_SPLASH_POTION")) {
                itemName = "bottle_splash";
            } else if (LogicUtil.contains(typeName, "LEGACY_WOOD_DOOR")) {
                itemName = "oak_door";
            } else if (LogicUtil.contains(typeName, "LEGACY_BOAT")) {
                itemName = "oak_boat";
            } else if (LogicUtil.contains(typeName, "TOTEM_OF_UNDYING", "LEGACY_TOTEM")) {
                itemName = "totem";
            }
            return itemName;
        }
    }

    private static class LegacyFishItemModelNameEntry
    implements ModelNameEntry {
        private final Material fishItemMaterial;
        private final int damageValue;
        private final String itemName;
        private final IdentifierHandle itemNameKey;

        public static List<ModelNameEntry> createLegacyFishMaterials(Material fishItemMaterial, boolean cooked) {
            return Arrays.asList(new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "cod", 0), new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "salmon", 1), new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "clownfish", 2), new LegacyFishItemModelNameEntry(fishItemMaterial, cooked, "pufferfish", 3));
        }

        private LegacyFishItemModelNameEntry(Material fishItemMaterial, boolean cooked, String fishName, int damageValue) {
            this.fishItemMaterial = fishItemMaterial;
            this.damageValue = damageValue;
            this.itemName = (cooked ? "cooked_" : "") + fishName;
            this.itemNameKey = IdentifierHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return this.fishItemMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(this.itemName);
        }

        @Override
        public Optional<IdentifierHandle> getItemNameKey() {
            return Optional.of(this.itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(this.fishItemMaterial, 1).setDamage(this.damageValue));
        }

        @Override
        public boolean matchDynamicItem(CommonItemStack item) {
            return item.getDamage() == this.damageValue;
        }
    }

    private static class LegacyInkSacItemModelNameEntry
    implements ModelNameEntry {
        private final Material inkSacMaterial;
        private final int damageValue;
        private final String itemName;
        private final IdentifierHandle itemNameKey;

        public static List<ModelNameEntry> createLegacyInkSacMaterials(Material inkSacItemMaterial) {
            return Arrays.asList(new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 0, "black"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 1, "red"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 2, "green"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 3, "brown"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 4, "blue"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 5, "purple"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 6, "cyan"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 7, "silver"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 8, "gray"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 9, "pink"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 10, "lime"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 11, "yellow"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 12, "light_blue"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 13, "magenta"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 14, "orange"), new LegacyInkSacItemModelNameEntry(inkSacItemMaterial, 15, "white"));
        }

        private LegacyInkSacItemModelNameEntry(Material inkSacMaterial, int damageValue, String colorName) {
            this.inkSacMaterial = inkSacMaterial;
            this.damageValue = damageValue;
            this.itemName = "dye_" + colorName;
            this.itemNameKey = IdentifierHandle.createNew("minecraft", this.itemName);
        }

        @Override
        public Material getMaterial() {
            return this.inkSacMaterial;
        }

        @Override
        public Optional<String> getItemName() {
            return Optional.of(this.itemName);
        }

        @Override
        public Optional<IdentifierHandle> getItemNameKey() {
            return Optional.of(this.itemNameKey);
        }

        @Override
        public Optional<String> getBlockName() {
            return Optional.empty();
        }

        @Override
        public Optional<CommonItemStack> toItemStack() {
            return Optional.of(CommonItemStack.create(this.inkSacMaterial, 1).setDamage(this.damageValue));
        }

        @Override
        public boolean matchDynamicItem(CommonItemStack item) {
            return item.getDamage() == this.damageValue;
        }
    }
}

