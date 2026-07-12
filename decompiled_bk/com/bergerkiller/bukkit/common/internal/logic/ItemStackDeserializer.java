/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigratorBukkit;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerMigratorNBT;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public class ItemStackDeserializer
implements Function<Map<String, Object>, ItemStack> {
    public static final ItemStackDeserializer INSTANCE = new ItemStackDeserializer();
    private static final boolean ALLOW_PARSE_NBT_ON_SPIGOT = true;
    private final ItemStackDeserializerMigratorBukkit bukkitMigrator = new ItemStackDeserializerMigratorBukkit();
    private final ItemStackDeserializerMigratorNBT nbtMigrator = new ItemStackDeserializerMigratorNBT();
    private final boolean canParseNBT = this.nbtMigrator.getCurrentDataVersion() >= 4325;

    private ItemStackDeserializer() {
    }

    public ItemStackDeserializerMigratorBukkit getBukkitMigrator() {
        return this.bukkitMigrator;
    }

    public ItemStackDeserializerMigratorNBT getNBTMigrator() {
        return this.nbtMigrator;
    }

    @Override
    public ItemStack apply(Map<String, Object> args) {
        boolean isPaperNBTFormat = args.containsKey("schema_version");
        if (isPaperNBTFormat) {
            if (this.canParseNBT) {
                return this.nbtMigrator.apply(args);
            }
            args = this.nbtMigrator.toBukkitEncoding(args);
        }
        return this.bukkitMigrator.apply(args);
    }
}

