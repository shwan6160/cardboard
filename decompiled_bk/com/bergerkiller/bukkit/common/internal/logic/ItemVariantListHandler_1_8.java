/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ItemVariantListHandler_1_8
extends ItemVariantListHandler {
    private static final VariantProducer DEFAULT_PRODUCER = (type, result) -> result.add(new ItemStack(type));
    private final HashMap<Material, VariantProducer> variants = new HashMap();

    ItemVariantListHandler_1_8() {
    }

    @Override
    public void enable() throws Throwable {
        VariantProducer emptyProducer = (type, result) -> {};
        for (String name : new String[]{"AIR", "MOB_SPAWNER", "SOIL", "HUGE_MUSHROOM_1", "HUGE_MUSHROOM_2", "DRAGON_EGG", "COMMAND", "BARRIER", "GRASS_PATH", "COMMAND_REPEATING", "COMMAND_CHAIN", "STRUCTURE_VOID", "STRUCTURE_BLOCK", "MAP", "WRITTEN_BOOK", "FIREWORK", "COMMAND_MINECART", "KNOWLEDGE_BOOK"}) {
            this.register(name, emptyProducer);
        }
        this.registerRange("STONE", 0, 6);
        this.registerRange("DIRT", 0, 2);
        this.registerRange("WOOD", 0, 5);
        this.registerRange("SAPLING", 0, 5);
        this.registerRange("SAND", 0, 1);
        this.registerRange("LOG", 0, 3);
        this.registerRange("LEAVES", 0, 3);
        this.registerRange("SPONGE", 0, 1);
        this.registerRange("SANDSTONE", 0, 2);
        this.registerRange("LONG_GRASS", 1, 2);
        this.registerRange("WOOL", 0, 15);
        this.registerRange("RED_ROSE", 0, 8);
        this.registerRange("STAINED_GLASS", 0, 15);
        this.registerRange("MONSTER_EGGS", 0, 5);
        this.registerRange("SMOOTH_BRICK", 0, 3);
        this.registerRange("WOOD_STEP", 0, 5);
        this.registerRange("COBBLE_WALL", 0, 1);
        this.registerRange("ANVIL", 0, 2);
        this.registerRange("QUARTZ_BLOCK", 0, 2);
        this.registerRange("STAINED_CLAY", 0, 15);
        this.registerRange("STAINED_GLASS_PANE", 0, 15);
        this.registerRange("LEAVES_2", 0, 1);
        this.registerRange("LOG_2", 0, 1);
        this.registerRange("PRISMARINE", 0, 2);
        this.registerRange("CARPET", 0, 15);
        this.registerRange("DOUBLE_PLANT", 0, 5);
        this.registerRange("RED_SANDSTONE", 0, 2);
        this.registerRange("CONCRETE", 0, 15);
        this.registerRange("CONCRETE_POWDER", 0, 15);
        this.registerRange("COAL", 0, 1);
        this.registerRange("GOLDEN_APPLE", 0, 1);
        this.registerRange("RAW_FISH", 0, 3);
        this.registerRange("COOKED_FISH", 0, 1);
        this.registerRange("INK_SACK", 0, 15);
        this.registerRange("BED", 0, 15);
        this.registerRange("SKULL_ITEM", 0, 5);
        this.registerRange("BANNER", 15, 0);
        this.register("STEP", (type, result) -> {
            result.add(new ItemStack(type, 1, 0));
            result.add(new ItemStack(type, 1, 1));
            result.add(new ItemStack(type, 1, 3));
            result.add(new ItemStack(type, 1, 4));
            result.add(new ItemStack(type, 1, 5));
            result.add(new ItemStack(type, 1, 6));
            result.add(new ItemStack(type, 1, 7));
        });
    }

    @Override
    public List<ItemStack> getVariants(Object nmsItem) {
        Material type = WrapperConversion.toMaterialFromItemHandle(nmsItem);
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        this.variants.getOrDefault(type, DEFAULT_PRODUCER).addVariants(type, result);
        return result;
    }

    private void registerRange(String name, int start, int end) {
        this.register(name, new StandardVariantRange(start, end));
    }

    private void register(String name, VariantProducer producer) {
        for (Material m : MaterialsByName.getAllMaterials()) {
            if (!m.name().equals(name)) continue;
            this.variants.put(m, producer);
            return;
        }
    }

    private static interface VariantProducer {
        public void addVariants(Material var1, List<ItemStack> var2);
    }

    private static class StandardVariantRange
    implements VariantProducer {
        public final int start;
        public final int end;

        public StandardVariantRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void addVariants(Material type, List<ItemStack> result) {
            for (int durability = this.start; durability <= this.end; ++durability) {
                result.add(new ItemStack(type, 1, (short)durability));
            }
        }
    }
}

