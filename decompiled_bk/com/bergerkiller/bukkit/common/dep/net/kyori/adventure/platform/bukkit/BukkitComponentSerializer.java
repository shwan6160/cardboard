/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.CraftBukkitFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.SpigotFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.flattener.ComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class BukkitComponentSerializer {
    private static final boolean IS_1_13 = MinecraftReflection.findEnum(Material.class, "BLUE_ICE") != null;
    private static final boolean IS_1_16 = MinecraftReflection.findEnum(Material.class, "NETHERITE_PICKAXE") != null;
    private static final Collection<FacetComponentFlattener.Translator<Server>> TRANSLATORS = Facet.of(SpigotFacet.Translator::new, CraftBukkitFacet.Translator::new);
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;
    private static final GsonComponentSerializer GSON_SERIALIZER;
    static final ComponentFlattener FLATTENER;

    private BukkitComponentSerializer() {
    }

    @NotNull
    public static LegacyComponentSerializer legacy() {
        return LEGACY_SERIALIZER;
    }

    @NotNull
    public static GsonComponentSerializer gson() {
        return GSON_SERIALIZER;
    }

    static {
        FLATTENER = FacetComponentFlattener.get(Bukkit.getServer(), TRANSLATORS);
        GSON_SERIALIZER = IS_1_13 ? GsonComponentSerializer.builder().options(JSONOptions.byDataVersion().at(Bukkit.getUnsafe().getDataVersion())).build() : GsonComponentSerializer.builder().legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get()).options(JSONOptions.byDataVersion().at(0)).build();
        LEGACY_SERIALIZER = IS_1_16 ? LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().flattener(FLATTENER).build() : LegacyComponentSerializer.builder().character('\u00a7').flattener(FLATTENER).build();
    }
}

