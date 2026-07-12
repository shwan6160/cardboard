/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.api.connection.UserConnection
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.CraftBukkitFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.PaperFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetAudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.viaversion.ViaFacet;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.Collection;
import java.util.function.Function;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

final class BukkitAudience
extends FacetAudience<CommandSender> {
    static final ThreadLocal<Plugin> PLUGIN = new ThreadLocal();
    private static final Function<Player, UserConnection> VIA = new BukkitFacet.ViaHook();
    private static final Collection<Facet.Chat<? extends CommandSender, ?>> CHAT = Facet.of(() -> new ViaFacet.Chat<Player>(Player.class, VIA), () -> new CraftBukkitFacet.Chat1_19_3(), () -> new CraftBukkitFacet.Chat(), () -> new BukkitFacet.Chat());
    private static final Collection<Facet.ActionBar<Player, ?>> ACTION_BAR = Facet.of(() -> new ViaFacet.ActionBarTitle<Player>(Player.class, VIA), () -> new ViaFacet.ActionBar<Player>(Player.class, VIA), () -> new CraftBukkitFacet.ActionBar_1_17(), () -> new CraftBukkitFacet.ActionBar(), () -> new CraftBukkitFacet.ActionBarLegacy());
    private static final Collection<Facet.Title<Player, ?, ?, ?>> TITLE = Facet.of(() -> new ViaFacet.Title<Player>(Player.class, VIA), () -> new CraftBukkitFacet.Title_1_17(), () -> new CraftBukkitFacet.Title());
    private static final Collection<Facet.Sound<Player, Vector>> SOUND = Facet.of(() -> new BukkitFacet.SoundWithCategory(), () -> new BukkitFacet.Sound());
    private static final Collection<Facet.EntitySound<Player, Object>> ENTITY_SOUND = Facet.of(() -> new CraftBukkitFacet.EntitySound_1_19_3(), () -> new CraftBukkitFacet.EntitySound());
    private static final Collection<Facet.Book<Player, ?, ?>> BOOK = Facet.of(() -> new CraftBukkitFacet.Book_1_20_5(), () -> new CraftBukkitFacet.BookPost1_13(), () -> new CraftBukkitFacet.Book1_13(), () -> new CraftBukkitFacet.BookPre1_13());
    private static final Collection<Facet.BossBar.Builder<Player, ?>> BOSS_BAR = Facet.of(() -> new ViaFacet.BossBar.Builder<Player>(Player.class, VIA), () -> new ViaFacet.BossBar.Builder1_9_To_1_15<Player>(Player.class, VIA), () -> new CraftBukkitFacet.BossBar.Builder(), () -> new BukkitFacet.BossBarBuilder(), () -> new CraftBukkitFacet.BossBarWither.Builder());
    private static final Collection<Facet.TabList<Player, ?>> TAB_LIST = Facet.of(() -> new ViaFacet.TabList<Player>(Player.class, VIA), () -> new PaperFacet.TabList(), () -> new CraftBukkitFacet.TabList(), () -> new BukkitFacet.TabList());
    private static final Collection<Facet.Pointers<? extends CommandSender>> POINTERS = Facet.of(() -> new BukkitFacet.CommandSenderPointers(), () -> new BukkitFacet.ConsoleCommandSenderPointers(), () -> new BukkitFacet.PlayerPointers());
    @NotNull
    private final Plugin plugin;

    BukkitAudience(@NotNull Plugin plugin, FacetAudienceProvider<?, ?> provider, @NotNull Collection<CommandSender> viewers) {
        super(provider, viewers, CHAT, ACTION_BAR, TITLE, SOUND, ENTITY_SOUND, BOOK, BOSS_BAR, TAB_LIST, POINTERS);
        this.plugin = plugin;
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        PLUGIN.set(this.plugin);
        super.showBossBar(bar);
        PLUGIN.set(null);
    }
}

