/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.World
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitBrigadierMapper;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.paper.parser.KeyedWorldParser;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.apiguardian.api.API;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class PaperBrigadierMappings {
    private PaperBrigadierMappings() {
    }

    static <C> void register(@NonNull BukkitBrigadierMapper<C> mapper) {
        Class<World> keyed = CraftBukkitReflection.findClass("org.bukkit.Keyed");
        if (keyed != null && keyed.isAssignableFrom(World.class)) {
            mapper.mapSimpleNMS(new TypeToken<KeyedWorldParser<C>>(){}, "resource_location", true);
        }
    }
}

