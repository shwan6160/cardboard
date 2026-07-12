/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerItemMetaMigrator;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigrator;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackDeserializerMigratorBukkit
extends ItemStackDeserializerMigrator
implements Function<Map<String, Object>, ItemStack> {
    private final ItemStackDeserializerItemMetaMigrator metaDeserializer = new ItemStackDeserializerItemMetaMigrator(this);

    ItemStackDeserializerMigratorBukkit() {
        this.register(0, map -> {
            Object type;
            Object damage;
            Map<String, Object> metaArgs;
            Object meta = map.get("meta");
            if (meta instanceof ItemMeta && (metaArgs = this.metaDeserializer.getArgsUsedForMeta((ItemMeta)meta)) != null && (damage = metaArgs.get("Damage")) != null) {
                map.put("damage", damage);
            }
            if ((type = map.get("type")) instanceof String && ((String)type).startsWith("LEGACY_")) {
                map.put("type", ((String)type).substring(7));
                return true;
            }
            String repl = Helper.LEGACY_MAPPING_1_13.get(type);
            if (repl != null) {
                map.put("type", repl);
                return true;
            }
            return false;
        });
        this.register(1519, map -> {
            Object type = map.get("type");
            return !LogicUtil.contains(type, "DEAD_BRAIN_CORAL", "DEAD_BUBBLE_CORAL", "DEAD_FIRE_CORAL", "DEAD_HORN_CORAL", "DEAD_TUBE_CORAL");
        });
        this.register(1628, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(1631, map -> {
            Object type = map.get("type");
            if ("GREEN_DYE".equals(type)) {
                map.put("type", "CACTUS_GREEN");
            } else if ("YELLOW_DYE".equals(type)) {
                map.put("type", "DANDELION_YELLOW");
            } else if ("RED_DYE".equals(type)) {
                map.put("type", "ROSE_RED");
            } else if ("OAK_SIGN".equals(type)) {
                map.put("type", "SIGN");
            } else if ("OAK_WALL_SIGN".equals(type)) {
                map.put("type", "WALL_SIGN");
            }
            return !Helper.ADDED_MC_1_14.contains(type);
        });
        this.register(1952, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(1957, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(1963, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(1968, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(1976, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_15.contains(type);
        });
        this.register(2225, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2227, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2230, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_16.contains(type);
        });
        this.register(2567, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2578, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2580, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2584, map -> {
            ChatText text;
            String displayName;
            Map<String, Object> metaArgs;
            ItemMeta meta = LogicUtil.tryCast(map.get("meta"), ItemMeta.class);
            if (meta != null && meta.hasDisplayName() && (metaArgs = this.metaDeserializer.getArgsUsedForMeta(meta)) != null && (displayName = LogicUtil.tryCast(metaArgs.get("display-name"), String.class)) != null && displayName.startsWith("{") && displayName.endsWith("}") && (text = ChatText.fromJson(displayName)) != null) {
                metaArgs.put("display-name", text.getMessage());
                meta = this.metaDeserializer.apply(metaArgs);
                map.put("meta", meta);
            }
            return true;
        });
        this.register(2586, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_17.contains(type);
        });
        this.register(2724, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2730, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2860, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2865, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(2975, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19.contains(type);
        });
        this.register(3105, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3117, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3120, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19_3.contains(type);
        });
        this.register(3218, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_19_4.contains(type);
        });
        this.register(3337, map -> {
            Object type = map.get("type");
            if ("ARCHER_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_ARCHER");
                return true;
            }
            if ("PRIZE_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_PRIZE");
                return true;
            }
            if ("ARMS_UP_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_ARMS_UP");
                return true;
            }
            if ("SKULL_POTTERY_SHERD".equals(type)) {
                map.put("type", "POTTERY_SHARD_SKULL");
                return true;
            }
            return !Helper.ADDED_MC_1_20.contains(type);
        });
        this.register(3463, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3465, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3578, map -> {
            Object type = map.get("type");
            if ("SHORT_GRASS".equals(type)) {
                map.put("type", "GRASS");
                return true;
            }
            return !Helper.ADDED_MC_1_20_3.contains(type);
        });
        this.register(3698, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3700, map -> {
            Object type = map.get("type");
            if ("TURTLE_SCUTE".equals(type)) {
                map.put("type", "SCUTE");
                return true;
            }
            return !Helper.ADDED_MC_1_20_5.contains(type);
        });
        this.register(3837, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3839, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21.contains(type);
        });
        this.register(3953, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(3955, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_2.contains(type);
        });
        this.register(4080, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4082, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_4.contains(type);
        });
        this.register(4189, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_5.contains(type);
        });
        this.register(4325, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_6.contains(type);
        });
        this.register(4435, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4438, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4440, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_1_21_9.contains(type);
        });
        this.register(4554, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4556, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4671, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_26_1.contains(type);
        });
        this.register(4786, ItemStackDeserializerMigrator.ConverterFunction.NO_CONVERSION);
        this.register(4790, map -> {
            Object type = map.get("type");
            return !Helper.ADDED_MC_26_2.contains(type);
        });
        this.setMaximumDataVersion(4903);
    }

    public ItemStackDeserializerItemMetaMigrator getItemMetaDeserializer() {
        return this.metaDeserializer;
    }

    @Override
    protected void baseMigrate(Map<String, Object> args) {
        ItemStackDeserializerMigratorBukkit.convertNumberToIntegerInMap(args, "amount");
        ItemStackDeserializerMigratorBukkit.convertNumberToIntegerInMap(args, "damage");
        ItemStackDeserializerMigratorBukkit.convertNumberToIntegerInMapValues(args, "enchantments");
        ItemStackDeserializerMigratorBukkit.replaceMapInMap(args, "meta", this.metaDeserializer);
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        this.migrate(args, "v");
        this.fixEntityTagItemMeta(args);
        try {
            ItemStack itemStack = ItemStack.deserialize(args);
            return itemStack;
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            Object typeNameObj = args.get("type");
            if (typeNameObj instanceof String) {
                String typeName = (String)typeNameObj;
                Material type = CommonCapabilities.MATERIAL_ENUM_CHANGES && args.containsKey("v") ? MaterialUtil.getMaterial(typeName) : CommonLegacyMaterials.getLegacyMaterial(typeName);
                if (type != null) {
                    ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
                    throw ex;
                }
            }
            ItemStack itemStack = null;
            return itemStack;
        }
        catch (RuntimeException ex) {
            ItemStackDeserializerMigratorBukkit.logFailDeserialize(args);
            throw ex;
        }
        finally {
            Object metaUnsafe = args.get("meta");
            if (metaUnsafe instanceof ItemMeta) {
                this.metaDeserializer.cleanupArgsUsedForMeta((ItemMeta)metaUnsafe);
            }
        }
    }

    private void fixEntityTagItemMeta(Map<String, Object> args) {
        String newInternalData;
        ItemMeta meta = LogicUtil.tryCast(args.get("meta"), ItemMeta.class);
        if (meta == null || !CraftItemStackHandle.isCorruptedEntityTag(meta)) {
            return;
        }
        String type = LogicUtil.tryCast(args.get("type"), String.class);
        Map<String, Object> metaArgs = this.metaDeserializer.getArgsUsedForMeta(meta);
        if (type == null || metaArgs == null) {
            return;
        }
        CommonTagCompound nbt = CommonTagCompound.fromBase64String(LogicUtil.tryCast(metaArgs.get("internal"), String.class));
        if (nbt == null) {
            return;
        }
        CommonTagCompound entityTag = LogicUtil.tryCast(nbt.get("EntityTag"), CommonTagCompound.class);
        if (entityTag == null) {
            return;
        }
        if (entityTag.containsKey("id")) {
            return;
        }
        String entityTypeId = Helper.TYPE_TO_ENTITY_TAG_ID.get(type);
        if (entityTypeId == null) {
            args.remove("meta");
            return;
        }
        entityTag.putValue("id", entityTypeId);
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();){
            nbt.writeToStream(outStream, true);
            newInternalData = Base64.getEncoder().encodeToString(outStream.toByteArray());
        }
        catch (Exception ex) {
            args.remove("meta");
            return;
        }
        this.metaDeserializer.cleanupArgsUsedForMeta(meta);
        metaArgs.put("internal", newInternalData);
        args.put("meta", this.metaDeserializer.applyWithoutFixes(metaArgs));
    }

    private static class Helper {
        public static final Map<String, String> TYPE_TO_ENTITY_TAG_ID = new HashMap<String, String>();
        public static final Map<String, String> LEGACY_MAPPING_1_13;
        public static final Set<String> ADDED_MC_1_14;
        public static final Set<String> ADDED_MC_1_15;
        public static final Set<String> ADDED_MC_1_16;
        public static final Set<String> ADDED_MC_1_17;
        public static final Set<String> ADDED_MC_1_19;
        public static final Set<String> ADDED_MC_1_19_3;
        public static final Set<String> ADDED_MC_1_19_4;
        public static final Set<String> ADDED_MC_1_20;
        public static final Set<String> ADDED_MC_1_20_3;
        public static final Set<String> ADDED_MC_1_20_5;
        public static final Set<String> ADDED_MC_1_21;
        public static final Set<String> ADDED_MC_1_21_2;
        public static final Set<String> ADDED_MC_1_21_4;
        public static final Set<String> ADDED_MC_1_21_5;
        public static final Set<String> ADDED_MC_1_21_6;
        public static final Set<String> ADDED_MC_1_21_9;
        public static final Set<String> ADDED_MC_26_1;
        public static final Set<String> ADDED_MC_26_2;

        private Helper() {
        }

        @SafeVarargs
        private static <T> Set<T> makeSet(List<T> ... lists) {
            HashSet<T> result = new HashSet<T>();
            for (List<T> list : lists) {
                result.addAll(list);
            }
            return result;
        }

        private static Stream<String> forWoodTypes_1_21_9() {
            return Stream.of("ACACIA", "BAMBOO", "BIRCH", "CHERRY", "CRIMSON", "DARK_OAK", "JUNGLE", "MANGROVE", "OAK", "PALE_OAK", "SPRUCE", "WARPED");
        }

        private static Stream<String> forCopperTypes_1_21_9() {
            return Stream.of("COPPER", "EXPOSED_COPPER", "WEATHERED_COPPER", "OXIDIZED_COPPER", "WAXED_COPPER", "WAXED_EXPOSED_COPPER", "WAXED_WEATHERED_COPPER", "WAXED_OXIDIZED_COPPER");
        }

        private static List<String> makeMaterialsOfWoodCategory(String woodName) {
            return Arrays.asList(woodName + "_PLANKS", woodName + "_SAPLING", woodName + "_LOG", woodName + "_WOOD", woodName + "_LEAVES", woodName + "_SLAB", woodName + "_FENCE", woodName + "_STAIRS", woodName + "_BUTTON", woodName + "_PRESSURE_PLATE", woodName + "_DOOR", woodName + "_TRAPDOOR", woodName + "_FENCE_GATE", woodName + "_BOAT", woodName + "_CHEST_BOAT", woodName + "_SIGN", woodName + "_HANGING_SIGN", woodName + "_WALL_SIGN", woodName + "_WALL_HANGING_SIGN", "POTTED_" + woodName + "_SAPLING", "STRIPPED_" + woodName + "_LOG", "STRIPPED_" + woodName + "_WOOD");
        }

        private static List<String> makeColoredMaterials(String materialName) {
            return Arrays.asList("WHITE_" + materialName, "ORANGE_" + materialName, "MAGENTA_" + materialName, "LIGHT_BLUE_" + materialName, "YELLOW_" + materialName, "LIME_" + materialName, "PINK_" + materialName, "GRAY_" + materialName, "LIGHT_GRAY_" + materialName, "CYAN_" + materialName, "PURPLE_" + materialName, "BLUE_" + materialName, "BROWN_" + materialName, "GREEN_" + materialName, "RED_" + materialName, "BLACK_" + materialName);
        }

        static {
            TYPE_TO_ENTITY_TAG_ID.put("COD_BUCKET", "minecraft:cod");
            TYPE_TO_ENTITY_TAG_ID.put("PUFFERFISH_BUCKET", "minecraft:pufferfish");
            TYPE_TO_ENTITY_TAG_ID.put("SALMON_BUCKET", "minecraft:salmon");
            TYPE_TO_ENTITY_TAG_ID.put("ITEM_FRAME", "minecraft:item_frame");
            TYPE_TO_ENTITY_TAG_ID.put("GLOW_ITEM_FRAME", "minecraft:glow_item_frame");
            TYPE_TO_ENTITY_TAG_ID.put("PAINTING", "minecraft:painting");
            LEGACY_MAPPING_1_13 = new HashMap<String, String>();
            try {
                String mat_cat_path = "/com/bergerkiller/bukkit/common/internal/resources/mat_to_legacy.txt";
                try (InputStream input = ItemStackDeserializerMigratorBukkit.class.getResourceAsStream(mat_cat_path);
                     Scanner scanner = new Scanner(input, "UTF-8");){
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        int splitIdx = line.indexOf(61);
                        if (splitIdx == -1) continue;
                        LEGACY_MAPPING_1_13.put(line.substring(0, splitIdx), line.substring(splitIdx + 1));
                    }
                }
            }
            catch (Throwable t2) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to initialize legacy material conversion table", t2);
            }
            ADDED_MC_1_14 = Helper.makeSet(Arrays.asList("ACACIA_SIGN", "ACACIA_WALL_SIGN", "ANDESITE_SLAB", "ANDESITE_STAIRS", "ANDESITE_WALL", "BAMBOO", "BAMBOO_SAPLING", "BARREL", "BELL", "BIRCH_SIGN", "BIRCH_WALL_SIGN", "BLACK_DYE", "BLAST_FURNACE", "BLUE_DYE", "BRICK_WALL", "BROWN_DYE", "CAMPFIRE", "CARTOGRAPHY_TABLE", "CAT_SPAWN_EGG", "COMPOSTER", "CORNFLOWER", "CREEPER_BANNER_PATTERN", "CROSSBOW", "CUT_RED_SANDSTONE_SLAB", "CUT_SANDSTONE_SLAB", "DARK_OAK_SIGN", "DARK_OAK_WALL_SIGN", "DIORITE_SLAB", "DIORITE_STAIRS", "DIORITE_WALL", "END_STONE_BRICK_SLAB", "END_STONE_BRICK_STAIRS", "END_STONE_BRICK_WALL", "FLETCHING_TABLE", "FLOWER_BANNER_PATTERN", "FOX_SPAWN_EGG", "GLOBE_BANNER_PATTERN", "GRANITE_SLAB", "GRANITE_STAIRS", "GRANITE_WALL", "GRINDSTONE", "JIGSAW", "JUNGLE_SIGN", "JUNGLE_WALL_SIGN", "LANTERN", "LEATHER_HORSE_ARMOR", "LECTERN", "LILY_OF_THE_VALLEY", "LOOM", "MOJANG_BANNER_PATTERN", "MOSSY_COBBLESTONE_SLAB", "MOSSY_COBBLESTONE_STAIRS", "MOSSY_STONE_BRICK_SLAB", "MOSSY_STONE_BRICK_STAIRS", "MOSSY_STONE_BRICK_WALL", "NETHER_BRICK_WALL", "PANDA_SPAWN_EGG", "PILLAGER_SPAWN_EGG", "POLISHED_ANDESITE_SLAB", "POLISHED_ANDESITE_STAIRS", "POLISHED_DIORITE_SLAB", "POLISHED_DIORITE_STAIRS", "POLISHED_GRANITE_SLAB", "POLISHED_GRANITE_STAIRS", "POTTED_BAMBOO", "POTTED_CORNFLOWER", "POTTED_LILY_OF_THE_VALLEY", "POTTED_WITHER_ROSE", "PRISMARINE_WALL", "RAVAGER_SPAWN_EGG", "RED_NETHER_BRICK_SLAB", "RED_NETHER_BRICK_STAIRS", "RED_NETHER_BRICK_WALL", "RED_SANDSTONE_WALL", "SANDSTONE_WALL", "SCAFFOLDING", "SKULL_BANNER_PATTERN", "SMITHING_TABLE", "SMOKER", "SMOOTH_QUARTZ_SLAB", "SMOOTH_QUARTZ_STAIRS", "SMOOTH_RED_SANDSTONE_SLAB", "SMOOTH_RED_SANDSTONE_STAIRS", "SMOOTH_SANDSTONE_SLAB", "SMOOTH_SANDSTONE_STAIRS", "SMOOTH_STONE_SLAB", "SPRUCE_SIGN", "SPRUCE_WALL_SIGN", "STONECUTTER", "STONE_BRICK_WALL", "STONE_STAIRS", "SUSPICIOUS_STEW", "SWEET_BERRIES", "SWEET_BERRY_BUSH", "TRADER_LLAMA_SPAWN_EGG", "WANDERING_TRADER_SPAWN_EGG", "WHITE_DYE", "WITHER_ROSE"));
            ADDED_MC_1_15 = Helper.makeSet(Arrays.asList("BEEHIVE", "BEE_NEST", "BEE_SPAWN_EGG", "HONEYCOMB", "HONEYCOMB_BLOCK", "HONEY_BLOCK", "HONEY_BOTTLE"));
            ADDED_MC_1_16 = new HashSet<String>(Arrays.asList("ANCIENT_DEBRIS", "BASALT", "CHAIN", "CRYING_OBSIDIAN", "BLACKSTONE", "BLACKSTONE_SLAB", "BLACKSTONE_STAIRS", "BLACKSTONE_WALL", "GILDED_BLACKSTONE", "CHISELED_NETHER_BRICKS", "CHISELED_POLISHED_BLACKSTONE", "CRACKED_NETHER_BRICKS", "CRACKED_POLISHED_BLACKSTONE_BRICKS", "CRIMSON_BUTTON", "CRIMSON_DOOR", "CRIMSON_FENCE", "CRIMSON_FENCE_GATE", "CRIMSON_FUNGUS", "CRIMSON_HYPHAE", "CRIMSON_NYLIUM", "CRIMSON_PLANKS", "CRIMSON_PRESSURE_PLATE", "CRIMSON_ROOTS", "CRIMSON_SIGN", "CRIMSON_SLAB", "CRIMSON_STAIRS", "CRIMSON_STEM", "CRIMSON_TRAPDOOR", "CRIMSON_WALL_SIGN", "HOGLIN_SPAWN_EGG", "LODESTONE", "MUSIC_DISC_PIGSTEP", "NETHERITE_AXE", "NETHERITE_BLOCK", "THERITE_BOOTS", "NETHERITE_CHESTPLATE", "NETHERITE_HELMET", "NETHERITE_HOE", "NETHERITE_INGOT", "NETHERITE_LEGGINGS", "NETHERITE_PICKAXE", "NETHERITE_SCRAP", "NETHERITE_SHOVEL", "NETHERITE_SWORD", "NETHER_GOLD_ORE", "NETHER_SPROUTS", "PIGLIN_BANNER_PATTERN", "PIGLIN_SPAWN_EGG", "POLISHED_BASALT", "POLISHED_BLACKSTONE", "POLISHED_BLACKSTONE_BRICKS", "POLISHED_BLACKSTONE_BRICK_SLAB", "POLISHED_BLACKSTONE_BRICK_STAIRS", "POLISHED_BLACKSTONE_BRICK_WALL", "POLISHED_BLACKSTONE_BUTTON", "POLISHED_BLACKSTONE_PRESSURE_PLATE", "POLISHED_BLACKSTONE_SLAB", "POLISHED_BLACKSTONE_STAIRS", "POLISHED_BLACKSTONE_WALL", "POTTED_CRIMSON_FUNGUS", "POTTED_CRIMSON_ROOTS", "POTTED_WARPED_FUNGUS", "POTTED_WARPED_ROOTS", "QUARTZ_BRICKS", "RESPAWN_ANCHOR", "SHROOMLIGHT", "SOUL_CAMPFIRE", "SOUL_FIRE", "SOUL_LANTERN", "SOUL_SOIL", "SOUL_TORCH", "SOUL_WALL_TORCH", "STRIDER_SPAWN_EGG", "STRIPPED_CRIMSON_HYPHAE", "STRIPPED_CRIMSON_STEM", "STRIPPED_WARPED_HYPHAE", "STRIPPED_WARPED_STEM", "TARGET", "TWISTING_VINES", "TWISTING_VINES_PLANT", "WARPED_BUTTON", "WARPED_DOOR", "WARPED_FENCE", "WARPED_FENCE_GATE", "WARPED_FUNGUS", "WARPED_FUNGUS_ON_A_STICK", "WARPED_HYPHAE", "WARPED_NYLIUM", "WARPED_PLANKS", "WARPED_PRESSURE_PLATE", "WARPED_ROOTS", "WARPED_SIGN", "WARPED_SLAB", "WARPED_STAIRS", "WARPED_STEM", "WARPED_TRAPDOOR", "WARPED_WALL_SIGN", "WARPED_WART_BLOCK", "WEEPING_VINES", "WEEPING_VINES_PLANT", "ZOGLIN_SPAWN_EGG", "ZOMBIFIED_PIGLIN_SPAWN_EGG"));
            ADDED_MC_1_17 = Helper.makeSet(Helper.makeColoredMaterials("CANDLE"), Helper.makeColoredMaterials("CANDLE_CAKE"), Arrays.asList("DEEPSLATE", "COBBLED_DEEPSLATE", "POLISHED_DEEPSLATE", "CALCITE", "TUFF", "DRIPSTONE_BLOCK", "ROOTED_DIRT", "DEEPSLATE_COAL_ORE", "DEEPSLATE_IRON_ORE", "COPPER_ORE", "DEEPSLATE_COPPER_ORE", "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE", "EMERALD_ORE", "DEEPSLATE_EMERALD_ORE", "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE", "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", "NETHER_GOLD_ORE", "NETHER_QUARTZ_ORE", "ANCIENT_DEBRIS", "COAL_BLOCK", "RAW_IRON_BLOCK", "RAW_COPPER_BLOCK", "RAW_GOLD_BLOCK", "AMETHYST_BLOCK", "BUDDING_AMETHYST", "IRON_BLOCK", "COPPER_BLOCK", "GOLD_BLOCK", "DIAMOND_BLOCK", "NETHERITE_BLOCK", "EXPOSED_COPPER", "WEATHERED_COPPER", "OXIDIZED_COPPER", "CUT_COPPER", "EXPOSED_CUT_COPPER", "WEATHERED_CUT_COPPER", "OXIDIZED_CUT_COPPER", "CUT_COPPER_STAIRS", "EXPOSED_CUT_COPPER_STAIRS", "WEATHERED_CUT_COPPER_STAIRS", "OXIDIZED_CUT_COPPER_STAIRS", "CUT_COPPER_SLAB", "EXPOSED_CUT_COPPER_SLAB", "WEATHERED_CUT_COPPER_SLAB", "OXIDIZED_CUT_COPPER_SLAB", "WAXED_COPPER_BLOCK", "WAXED_EXPOSED_COPPER", "WAXED_WEATHERED_COPPER", "WAXED_OXIDIZED_COPPER", "WAXED_CUT_COPPER", "WAXED_EXPOSED_CUT_COPPER", "WAXED_WEATHERED_CUT_COPPER", "WAXED_OXIDIZED_CUT_COPPER", "WAXED_CUT_COPPER_STAIRS", "WAXED_EXPOSED_CUT_COPPER_STAIRS", "WAXED_WEATHERED_CUT_COPPER_STAIRS", "WAXED_OXIDIZED_CUT_COPPER_STAIRS", "WAXED_CUT_COPPER_SLAB", "WAXED_EXPOSED_CUT_COPPER_SLAB", "WAXED_WEATHERED_CUT_COPPER_SLAB", "WAXED_OXIDIZED_CUT_COPPER_SLAB", "AZALEA_LEAVES", "FLOWERING_AZALEA_LEAVES", "TINTED_GLASS", "AZALEA", "FLOWERING_AZALEA", "SPORE_BLOSSOM", "MOSS_CARPET", "MOSS_BLOCK", "HANGING_ROOTS", "BIG_DRIPLEAF", "SMALL_DRIPLEAF", "SMOOTH_BASALT", "INFESTED_DEEPSLATE", "DEEPSLATE_BRICKS", "CRACKED_DEEPSLATE_BRICKS", "DEEPSLATE_TILES", "CRACKED_DEEPSLATE_TILES", "CHISELED_DEEPSLATE", "GLOW_LICHEN", "COBBLED_DEEPSLATE_WALL", "POLISHED_DEEPSLATE_WALL", "DEEPSLATE_BRICK_WALL", "DEEPSLATE_TILE_WALL", "LIGHT", "DIRT_PATH", "COBBLED_DEEPSLATE_STAIRS", "POLISHED_DEEPSLATE_STAIRS", "DEEPSLATE_BRICK_STAIRS", "DEEPSLATE_TILE_STAIRS", "COBBLED_DEEPSLATE_SLAB", "POLISHED_DEEPSLATE_SLAB", "DEEPSLATE_BRICK_SLAB", "DEEPSLATE_TILE_SLAB", "LIGHTNING_ROD", "SCULK_SENSOR", "POLISHED_BLACKSTONE_BUTTON", "POLISHED_BLACKSTONE_PRESSURE_PLATE", "RAW_IRON", "RAW_GOLD", "RAW_COPPER", "COPPER_INGOT", "AMETHYST_SHARD", "POWDER_SNOW_BUCKET", "AXOLOTL_BUCKET", "BUNDLE", "SPYGLASS", "GLOW_INK_SAC", "AXOLOTL_SPAWN_EGG", "GLOW_SQUID_SPAWN_EGG", "GOAT_SPAWN_EGG", "GLOW_ITEM_FRAME", "GLOW_BERRIES", "CANDLE", "SMALL_AMETHYST_BUD", "MEDIUM_AMETHYST_BUD", "LARGE_AMETHYST_BUD", "AMETHYST_CLUSTER", "POINTED_DRIPSTONE", "WATER_CAULDRON", "LAVA_CAULDRON", "POWDER_SNOW_CAULDRON", "CANDLE_CAKE", "POWDER_SNOW", "CAVE_VINES", "CAVE_VINES_PLANT", "BIG_DRIPLEAF_STEM", "POTTED_AZALEA_BUSH", "POTTED_FLOWERING_AZALEA_BUSH"));
            ADDED_MC_1_19 = Helper.makeSet(Arrays.asList("MUD", "MANGROVE_PLANKS", "MANGROVE_PROPAGULE", "MANGROVE_LOG", "MANGROVE_ROOTS", "MUDDY_MANGROVE_ROOTS", "STRIPPED_MANGROVE_LOG", "STRIPPED_MANGROVE_WOOD", "MANGROVE_WOOD", "MANGROVE_LEAVES", "MANGROVE_SLAB", "MUD_BRICK_SLAB", "MANGROVE_FENCE", "PACKED_MUD", "MUD_BRICKS", "REINFORCED_DEEPSLATE", "MUD_BRICK_STAIRS", "SCULK", "SCULK_VEIN", "SCULK_CATALYST", "SCULK_SHRIEKER", "MANGROVE_STAIRS", "MUD_BRICK_WALL", "MANGROVE_BUTTON", "MANGROVE_PRESSURE_PLATE", "MANGROVE_DOOR", "MANGROVE_TRAPDOOR", "MANGROVE_FENCE_GATE", "OAK_CHEST_BOAT", "SPRUCE_CHEST_BOAT", "BIRCH_CHEST_BOAT", "JUNGLE_CHEST_BOAT", "ACACIA_CHEST_BOAT", "DARK_OAK_CHEST_BOAT", "MANGROVE_BOAT", "MANGROVE_CHEST_BOAT", "MANGROVE_SIGN", "TADPOLE_BUCKET", "RECOVERY_COMPASS", "ALLAY_SPAWN_EGG", "FROG_SPAWN_EGG", "TADPOLE_SPAWN_EGG", "WARDEN_SPAWN_EGG", "MUSIC_DISC_5", "DISC_FRAGMENT_5", "GOAT_HORN", "OCHRE_FROGLIGHT", "VERDANT_FROGLIGHT", "PEARLESCENT_FROGLIGHT", "FROGSPAWN", "ECHO_SHARD", "MANGROVE_WALL_SIGN", "POTTED_MANGROVE_PROPAGULE"));
            ADDED_MC_1_19_3 = Helper.makeSet(Arrays.asList("BAMBOO_PLANKS", "BAMBOO_MOSAIC", "BAMBOO_BLOCK", "STRIPPED_BAMBOO_BLOCK", "BAMBOO_SLAB", "BAMBOO_MOSAIC_SLAB", "CHISELED_BOOKSHELF", "BAMBOO_FENCE", "SCULK_VEIN", "BAMBOO_STAIRS", "BAMBOO_MOSAIC_STAIRS", "BAMBOO_BUTTON", "BAMBOO_PRESSURE_PLATE", "BAMBOO_DOOR", "BAMBOO_TRAPDOOR", "BAMBOO_FENCE_GATE", "BAMBOO_RAFT", "BAMBOO_CHEST_RAFT", "BAMBOO_SIGN", "BAMBOO_WALL_SIGN", "OAK_HANGING_SIGN", "SPRUCE_HANGING_SIGN", "BIRCH_HANGING_SIGN", "JUNGLE_HANGING_SIGN", "ACACIA_HANGING_SIGN", "DARK_OAK_HANGING_SIGN", "MANGROVE_HANGING_SIGN", "BAMBOO_HANGING_SIGN", "CRIMSON_HANGING_SIGN", "WARPED_HANGING_SIGN", "OAK_WALL_HANGING_SIGN", "SPRUCE_WALL_HANGING_SIGN", "BIRCH_WALL_HANGING_SIGN", "ACACIA_WALL_HANGING_SIGN", "JUNGLE_WALL_HANGING_SIGN", "DARK_OAK_WALL_HANGING_SIGN", "MANGROVE_WALL_HANGING_SIGN", "CRIMSON_WALL_HANGING_SIGN", "WARPED_WALL_HANGING_SIGN", "BAMBOO_WALL_HANGING_SIGN", "CAMEL_SPAWN_EGG", "ENDER_DRAGON_SPAWN_EGG", "IRON_GOLEM_SPAWN_EGG", "SNOW_GOLEM_SPAWN_EGG", "WITHER_SPAWN_EGG", "PIGLIN_HEAD", "PIGLIN_WALL_HEAD"));
            ADDED_MC_1_19_4 = Helper.makeSet(Helper.makeMaterialsOfWoodCategory("CHERRY"), Arrays.asList("SUSPICIOUS_SAND", "TORCHFLOWER", "PINK_PETALS", "DECORATED_POT", "SNIFFER_SPAWN_EGG", "TORCHFLOWER_SEEDS", "BRUSH", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE", "DUNE_ARMOR_TRIM_SMITHING_TEMPLATE", "COAST_ARMOR_TRIM_SMITHING_TEMPLATE", "WILD_ARMOR_TRIM_SMITHING_TEMPLATE", "WARD_ARMOR_TRIM_SMITHING_TEMPLATE", "EYE_ARMOR_TRIM_SMITHING_TEMPLATE", "VEX_ARMOR_TRIM_SMITHING_TEMPLATE", "TIDE_ARMOR_TRIM_SMITHING_TEMPLATE", "SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE", "RIB_ARMOR_TRIM_SMITHING_TEMPLATE", "SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE", "POTTERY_SHARD_ARCHER", "POTTERY_SHARD_PRIZE", "POTTERY_SHARD_ARMS_UP", "POTTERY_SHARD_SKULL", "POTTED_TORCHFLOWER", "TORCHFLOWER_CROP"));
            ADDED_MC_1_20 = Helper.makeSet(Arrays.asList("SUSPICIOUS_GRAVEL", "PITCHER_PLANT", "SNIFFER_EGG", "PITCHER_CROP", "CALIBRATED_SCULK_SENSOR", "PITCHER_POD", "MUSIC_DISC_RELIC", "WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE", "SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE", "SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE", "RAISER_ARMOR_TRIM_SMITHING_TEMPLATE", "HOST_ARMOR_TRIM_SMITHING_TEMPLATE", "ANGLER_POTTERY_SHERD", "ARCHER_POTTERY_SHERD", "ARMS_UP_POTTERY_SHERD", "BLADE_POTTERY_SHERD", "BREWER_POTTERY_SHERD", "BURN_POTTERY_SHERD", "DANGER_POTTERY_SHERD", "EXPLORER_POTTERY_SHERD", "FRIEND_POTTERY_SHERD", "HEART_POTTERY_SHERD", "HEARTBREAK_POTTERY_SHERD", "HOWL_POTTERY_SHERD", "MINER_POTTERY_SHERD", "MOURNER_POTTERY_SHERD", "PLENTY_POTTERY_SHERD", "PRIZE_POTTERY_SHERD", "SHEAF_POTTERY_SHERD", "SHELTER_POTTERY_SHERD", "SKULL_POTTERY_SHERD", "SNORT_POTTERY_SHERD"));
            ADDED_MC_1_20_3 = Helper.makeSet(Arrays.asList("TUFF_SLAB", "TUFF_STAIRS", "TUFF_WALL", "CHISELED_TUFF", "POLISHED_TUFF", "POLISHED_TUFF_SLAB", "POLISHED_TUFF_STAIRS", "POLISHED_TUFF_WALL", "TUFF_BRICKS", "TUFF_BRICK_SLAB", "TUFF_BRICK_STAIRS", "TUFF_BRICK_WALL", "CHISELED_TUFF_BRICKS", "CHISELED_COPPER", "EXPOSED_CHISELED_COPPER", "WEATHERED_CHISELED_COPPER", "OXIDIZED_CHISELED_COPPER", "WAXED_CHISELED_COPPER", "WAXED_EXPOSED_CHISELED_COPPER", "WAXED_WEATHERED_CHISELED_COPPER", "WAXED_OXIDIZED_CHISELED_COPPER", "COPPER_DOOR", "EXPOSED_COPPER_DOOR", "WEATHERED_COPPER_DOOR", "OXIDIZED_COPPER_DOOR", "WAXED_COPPER_DOOR", "WAXED_EXPOSED_COPPER_DOOR", "WAXED_WEATHERED_COPPER_DOOR", "WAXED_OXIDIZED_COPPER_DOOR", "COPPER_TRAPDOOR", "EXPOSED_COPPER_TRAPDOOR", "WEATHERED_COPPER_TRAPDOOR", "OXIDIZED_COPPER_TRAPDOOR", "WAXED_COPPER_TRAPDOOR", "WAXED_EXPOSED_COPPER_TRAPDOOR", "WAXED_WEATHERED_COPPER_TRAPDOOR", "WAXED_OXIDIZED_COPPER_TRAPDOOR", "CRAFTER", "BREEZE_SPAWN_EGG", "COPPER_GRATE", "EXPOSED_COPPER_GRATE", "WEATHERED_COPPER_GRATE", "OXIDIZED_COPPER_GRATE", "WAXED_COPPER_GRATE", "WAXED_EXPOSED_COPPER_GRATE", "WAXED_WEATHERED_COPPER_GRATE", "WAXED_OXIDIZED_COPPER_GRATE", "COPPER_BULB", "EXPOSED_COPPER_BULB", "WEATHERED_COPPER_BULB", "OXIDIZED_COPPER_BULB", "WAXED_COPPER_BULB", "WAXED_EXPOSED_COPPER_BULB", "WAXED_WEATHERED_COPPER_BULB", "WAXED_OXIDIZED_COPPER_BULB", "TRIAL_SPAWNER", "TRIAL_KEY"));
            ADDED_MC_1_20_5 = Helper.makeSet(Arrays.asList("HEAVY_CORE", "TURTLE_SCUTE", "ARMADILLO_SCUTE", "WOLF_ARMOR", "ARMADILLO_SPAWN_EGG", "BOGGED_SPAWN_EGG", "WIND_CHARGE", "MACE", "FLOW_BANNER_PATTERN", "GUSTER_BANNER_PATTERN", "FLOW_ARMOR_TRIM_SMITHING_TEMPLATE", "BOLT_ARMOR_TRIM_SMITHING_TEMPLATE", "FLOW_POTTERY_SHERD", "GUSTER_POTTERY_SHERD", "SCRAPE_POTTERY_SHERD", "OMINOUS_TRIAL_KEY", "VAULT", "OMINOUS_BOTTLE", "BREEZE_ROD"));
            ADDED_MC_1_21 = Helper.makeSet(Arrays.asList("MUSIC_DISC_CREATOR", "MUSIC_DISC_CREATOR_MUSIC_BOX", "MUSIC_DISC_PRECIPICE"));
            ADDED_MC_1_21_2 = Helper.makeSet(Helper.makeMaterialsOfWoodCategory("PALE_OAK"), Helper.makeColoredMaterials("BUNDLE"), Arrays.asList("PALE_MOSS_CARPET", "PALE_HANGING_MOSS", "PALE_MOSS_BLOCK", "CREAKING_HEART", "CREAKING_SPAWN_EGG", "FIELD_MASONED_BANNER_PATTERN", "BORDURE_INDENTED_BANNER_PATTERN"));
            ADDED_MC_1_21_4 = Helper.makeSet(Arrays.asList("OPEN_EYEBLOSSOM", "CLOSED_EYEBLOSSOM", "RESIN_CLUMP", "RESIN_BLOCK", "RESIN_BRICKS", "RESIN_BRICK_STAIRS", "RESIN_BRICK_SLAB", "RESIN_BRICK_WALL", "CHISELED_RESIN_BRICKS", "RESIN_BRICK", "POTTED_OPEN_EYEBLOSSOM", "POTTED_CLOSED_EYEBLOSSOM"));
            ADDED_MC_1_21_5 = Helper.makeSet(Arrays.asList("BUSH", "FIREFLY_BUSH", "SHORT_DRY_GRASS", "TALL_DRY_GRASS", "WILDFLOWERS", "LEAF_LITTER", "CACTUS_FLOWER", "TEST_BLOCK", "TEST_INSTANCE_BLOCK", "BLUE_EGG", "BROWN_EGG"));
            ADDED_MC_1_21_6 = Helper.makeSet(Helper.makeColoredMaterials("HARNESS"), Arrays.asList("DRIED_GHAST", "HAPPY_GHAST_SPAWN_EGG", "MUSIC_DISC_TEARS"));
            ADDED_MC_1_21_9 = Helper.makeSet(Helper.forWoodTypes_1_21_9().map(t -> t + "_SHELF").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().map(t -> t + "_BARS").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().map(t -> t + "_CHAIN").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().map(t -> t + "_LANTERN").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().map(t -> t + "_CHEST").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().map(t -> t + "_GOLEM_STATUE").collect(Collectors.toList()), Helper.forCopperTypes_1_21_9().filter(t -> !t.equals("COPPER")).map(t -> t.substring(0, t.length() - 7) + "_LIGHTNING_ROD").collect(Collectors.toList()), Arrays.asList("COPPER_TORCH", "COPPER_WALL_TORCH", "COPPER_SWORD", "COPPER_SHOVEL", "COPPER_PICKAXE", "COPPER_AXE", "COPPER_HOE", "COPPER_HELMET", "COPPER_CHESTPLATE", "COPPER_LEGGINGS", "COPPER_BOOTS", "COPPER_GOLEM_SPAWN_EGG", "COPPER_HORSE_ARMOR", "COPPER_NUGGET"));
            ADDED_MC_26_1 = Helper.makeSet(Arrays.asList("GOLDEN_DANDELION", "POTTED_GOLDEN_DANDELION"));
            ADDED_MC_26_2 = Helper.makeSet(Arrays.asList("TUFF_BRICK_WALL", "CHISELED_TUFF_BRICKS", "SULFUR", "POTENT_SULFUR", "SULFUR_SLAB", "SULFUR_STAIRS", "SULFUR_WALL", "POLISHED_SULFUR", "POLISHED_SULFUR_SLAB", "POLISHED_SULFUR_STAIRS", "POLISHED_SULFUR_WALL", "SULFUR_BRICKS", "SULFUR_BRICK_SLAB", "SULFUR_BRICK_STAIRS", "SULFUR_BRICK_WALL", "CHISELED_SULFUR", "CINNABAR", "CINNABAR_SLAB", "CINNABAR_STAIRS", "CINNABAR_WALL", "POLISHED_CINNABAR", "POLISHED_CINNABAR_SLAB", "POLISHED_CINNABAR_STAIRS", "POLISHED_CINNABAR_WALL", "CINNABAR_BRICKS", "CINNABAR_BRICK_SLAB", "CINNABAR_BRICK_STAIRS", "CINNABAR_BRICK_WALL", "CHISELED_CINNABAR", "DRIPSTONE_BLOCK", "SULFUR_CUBE_BUCKET", "SULFUR_CUBE_SPAWN_EGG", "MUSIC_DISC_BOUNCE", "SULFUR_SPIKE"));
        }
    }
}

