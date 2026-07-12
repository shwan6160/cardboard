/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.annotation.specifier.AllowEmptySelection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.annotation.specifier.DefaultNamespace;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.annotation.specifier.RequireExplicitNamespace;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.MultipleEntitySelector;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.MultiplePlayerSelector;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.BlockPredicateParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.EnchantmentParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.ItemStackParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.ItemStackPredicateParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.MaterialParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.NamespacedKeyParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.OfflinePlayerParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.PlayerParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.WorldParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.Location2DParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SingleEntitySelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SinglePlayerSelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Method;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class BukkitParsers {
    private BukkitParsers() {
    }

    public static <C> void register(CommandManager<C> manager) {
        manager.parserRegistry().registerParser(WorldParser.worldParser()).registerParser(MaterialParser.materialParser()).registerParser(PlayerParser.playerParser()).registerParser(OfflinePlayerParser.offlinePlayerParser()).registerParser(EnchantmentParser.enchantmentParser()).registerParser(LocationParser.locationParser()).registerParser(Location2DParser.location2DParser()).registerParser(ItemStackParser.itemStackParser()).registerParser(SingleEntitySelectorParser.singleEntitySelectorParser()).registerParser(SinglePlayerSelectorParser.singlePlayerSelectorParser());
        manager.parserRegistry().registerAnnotationMapper(AllowEmptySelection.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, annotation.value()));
        manager.parserRegistry().registerParserSupplier(TypeToken.get(MultipleEntitySelector.class), parserParameters -> new MultipleEntitySelectorParser(parserParameters.get(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, true)));
        manager.parserRegistry().registerParserSupplier(TypeToken.get(MultiplePlayerSelector.class), parserParameters -> new MultiplePlayerSelectorParser(parserParameters.get(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, true)));
        if (CraftBukkitReflection.classExists("org.bukkit.NamespacedKey")) {
            BukkitParsers.registerParserSupplierFor(manager, NamespacedKeyParser.class);
            manager.parserRegistry().registerAnnotationMapper(RequireExplicitNamespace.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.REQUIRE_EXPLICIT_NAMESPACE, true));
            manager.parserRegistry().registerAnnotationMapper(DefaultNamespace.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.DEFAULT_NAMESPACE, annotation.value()));
        }
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            BukkitParsers.registerParserSupplierFor(manager, ItemStackPredicateParser.class);
            BukkitParsers.registerParserSupplierFor(manager, BlockPredicateParser.class);
        }
    }

    private static void registerParserSupplierFor(CommandManager<?> manager, @NonNull Class<?> argumentClass) {
        try {
            Method registerParserSuppliers = argumentClass.getDeclaredMethod("registerParserSupplier", CommandManager.class);
            registerParserSuppliers.setAccessible(true);
            registerParserSuppliers.invoke(null, manager);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}

