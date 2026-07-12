/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.api.Via
 *  com.viaversion.viaversion.api.connection.UserConnection
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.SoundCategory
 *  org.bukkit.boss.BarColor
 *  org.bukkit.boss.BarFlag
 *  org.bukkit.boss.BarStyle
 *  org.bukkit.boss.BossBar
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.permission.PermissionChecker;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBase;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetPointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.SoundStop;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.Translator;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.TriState;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.lang.invoke.MethodHandle;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BukkitFacet<V extends CommandSender>
extends FacetBase<V> {
    protected BukkitFacet(@Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    static final class PlayerPointers
    extends BukkitFacet<Player>
    implements Facet.Pointers<Player> {
        private static final MethodHandle LOCALE_SUPPORTED;

        PlayerPointers() {
            super(Player.class);
        }

        @Override
        public void contributePointers(Player viewer, Pointers.Builder builder) {
            builder.withDynamic(Identity.UUID, () -> ((Player)viewer).getUniqueId());
            builder.withDynamic(Identity.DISPLAY_NAME, () -> BukkitComponentSerializer.legacy().deserializeOrNull(viewer.getDisplayName()));
            builder.withDynamic(Identity.LOCALE, () -> {
                if (LOCALE_SUPPORTED != null) {
                    try {
                        Object result = LOCALE_SUPPORTED.invoke(viewer);
                        return result instanceof Locale ? (Locale)result : Translator.parseLocale((String)result);
                    }
                    catch (Throwable error) {
                        Knob.logError(error, "Failed to call getLocale() for %s", viewer);
                    }
                }
                return Locale.getDefault();
            });
            builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.PLAYER);
            builder.withDynamic(FacetPointers.WORLD, () -> Key.key(viewer.getWorld().getName()));
        }

        static {
            MethodHandle asLocale = MinecraftReflection.findMethod(Player.class, "getLocale", Locale.class, new Class[0]);
            MethodHandle asString = MinecraftReflection.findMethod(Player.class, "getLocale", String.class, new Class[0]);
            LOCALE_SUPPORTED = asLocale != null ? asLocale : asString;
        }
    }

    static final class ConsoleCommandSenderPointers
    extends BukkitFacet<ConsoleCommandSender>
    implements Facet.Pointers<ConsoleCommandSender> {
        ConsoleCommandSenderPointers() {
            super(ConsoleCommandSender.class);
        }

        @Override
        public void contributePointers(ConsoleCommandSender viewer, Pointers.Builder builder) {
            builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.CONSOLE);
        }
    }

    static final class CommandSenderPointers
    extends BukkitFacet<CommandSender>
    implements Facet.Pointers<CommandSender> {
        CommandSenderPointers() {
            super(CommandSender.class);
        }

        @Override
        public void contributePointers(CommandSender viewer, Pointers.Builder builder) {
            builder.withDynamic(Identity.NAME, () -> ((CommandSender)viewer).getName());
            builder.withStatic(PermissionChecker.POINTER, perm -> {
                if (viewer.isPermissionSet(perm)) {
                    return viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE;
                }
                return TriState.NOT_SET;
            });
        }
    }

    static final class TabList
    extends Message<Player>
    implements Facet.TabList<Player, String> {
        private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "setPlayerListHeader", String.class);

        TabList() {
            super(Player.class);
        }

        @Override
        public boolean isSupported() {
            return SUPPORTED && super.isSupported();
        }

        @Override
        public void send(Player viewer, @Nullable String header, @Nullable String footer) {
            if (header != null && footer != null) {
                viewer.setPlayerListHeaderFooter(header, footer);
            } else if (header != null) {
                viewer.setPlayerListHeader(header);
            } else if (footer != null) {
                viewer.setPlayerListFooter(footer);
            }
        }
    }

    static final class ViaHook
    implements Function<Player, UserConnection> {
        ViaHook() {
        }

        @Override
        public UserConnection apply(@NotNull Player player) {
            return Via.getManager().getConnectionManager().getConnectedClient(player.getUniqueId());
        }
    }

    static class BossBar
    extends Message<Player>
    implements Facet.BossBar<Player> {
        protected final org.bukkit.boss.BossBar bar = Bukkit.createBossBar((String)"", (BarColor)BarColor.PINK, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);

        protected BossBar(@NotNull Collection<Player> viewers) {
            super(Player.class);
            this.bar.setVisible(false);
            for (Player viewer : viewers) {
                this.bar.addPlayer(viewer);
            }
        }

        @Override
        public void bossBarInitialized(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar) {
            Facet.BossBar.super.bossBarInitialized(bar);
            this.bar.setVisible(true);
        }

        @Override
        public void bossBarNameChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            if (!this.bar.getPlayers().isEmpty()) {
                this.bar.setTitle(this.createMessage((Player)this.bar.getPlayers().get(0), newName));
            }
        }

        @Override
        public void bossBarProgressChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, float oldPercent, float newPercent) {
            this.bar.setProgress((double)newPercent);
        }

        @Override
        public void bossBarColorChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar,  @NotNull BossBar.Color oldColor,  @NotNull BossBar.Color newColor) {
            BarColor color = this.color(newColor);
            if (color != null) {
                this.bar.setColor(color);
            }
        }

        @Nullable
        private BarColor color( @NotNull BossBar.Color color) {
            if (color == BossBar.Color.PINK) {
                return BarColor.PINK;
            }
            if (color == BossBar.Color.BLUE) {
                return BarColor.BLUE;
            }
            if (color == BossBar.Color.RED) {
                return BarColor.RED;
            }
            if (color == BossBar.Color.GREEN) {
                return BarColor.GREEN;
            }
            if (color == BossBar.Color.YELLOW) {
                return BarColor.YELLOW;
            }
            if (color == BossBar.Color.PURPLE) {
                return BarColor.PURPLE;
            }
            if (color == BossBar.Color.WHITE) {
                return BarColor.WHITE;
            }
            Knob.logUnsupported(this, (Object)color);
            return null;
        }

        @Override
        public void bossBarOverlayChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar,  @NotNull BossBar.Overlay oldOverlay,  @NotNull BossBar.Overlay newOverlay) {
            BarStyle style = this.style(newOverlay);
            if (style != null) {
                this.bar.setStyle(style);
            }
        }

        @Nullable
        private BarStyle style( @NotNull BossBar.Overlay overlay) {
            if (overlay == BossBar.Overlay.PROGRESS) {
                return BarStyle.SOLID;
            }
            if (overlay == BossBar.Overlay.NOTCHED_6) {
                return BarStyle.SEGMENTED_6;
            }
            if (overlay == BossBar.Overlay.NOTCHED_10) {
                return BarStyle.SEGMENTED_10;
            }
            if (overlay == BossBar.Overlay.NOTCHED_12) {
                return BarStyle.SEGMENTED_12;
            }
            if (overlay == BossBar.Overlay.NOTCHED_20) {
                return BarStyle.SEGMENTED_20;
            }
            Knob.logUnsupported(this, (Object)overlay);
            return null;
        }

        @Override
        public void bossBarFlagsChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            BarFlag flag;
            for (BossBar.Flag removeFlag : flagsRemoved) {
                flag = this.flag(removeFlag);
                if (flag == null) continue;
                this.bar.removeFlag(flag);
            }
            for (BossBar.Flag addFlag : flagsAdded) {
                flag = this.flag(addFlag);
                if (flag == null) continue;
                this.bar.addFlag(flag);
            }
        }

        @Nullable
        private BarFlag flag( @NotNull BossBar.Flag flag) {
            if (flag == BossBar.Flag.DARKEN_SCREEN) {
                return BarFlag.DARKEN_SKY;
            }
            if (flag == BossBar.Flag.PLAY_BOSS_MUSIC) {
                return BarFlag.PLAY_BOSS_MUSIC;
            }
            if (flag == BossBar.Flag.CREATE_WORLD_FOG) {
                return BarFlag.CREATE_FOG;
            }
            Knob.logUnsupported(this, (Object)flag);
            return null;
        }

        @Override
        public void addViewer(@NotNull Player viewer) {
            this.bar.addPlayer(viewer);
        }

        @Override
        public void removeViewer(@NotNull Player viewer) {
            this.bar.removePlayer(viewer);
        }

        @Override
        public boolean isEmpty() {
            return !this.bar.isVisible() || this.bar.getPlayers().isEmpty();
        }

        @Override
        public void close() {
            this.bar.removeAll();
        }
    }

    static class BossBarBuilder
    extends BukkitFacet<Player>
    implements Facet.BossBar.Builder<Player, BossBar> {
        private static final boolean SUPPORTED = MinecraftReflection.hasClass("org.bukkit.boss.BossBar");

        protected BossBarBuilder() {
            super(Player.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        public @NotNull BossBar createBossBar(@NotNull Collection<Player> viewers) {
            return new BossBar(viewers);
        }
    }

    static class SoundWithCategory
    extends Sound {
        private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", String.class, MinecraftReflection.findClass("org.bukkit.SoundCategory"));

        SoundWithCategory() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        public void playSound(@NotNull Player viewer, @NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound sound, @NotNull Vector vector) {
            SoundCategory category = this.category(sound.source());
            if (category == null) {
                super.playSound(viewer, sound, vector);
            } else {
                String name = SoundWithCategory.name(sound.name());
                viewer.playSound(vector.toLocation(viewer.getWorld()), name, category, sound.volume(), sound.pitch());
            }
        }

        @Override
        public void stopSound(@NotNull Player viewer, @NotNull SoundStop stop) {
            SoundCategory category = this.category(stop.source());
            if (category == null) {
                super.stopSound(viewer, stop);
            } else {
                String name = SoundWithCategory.name(stop.sound());
                viewer.stopSound(name, category);
            }
        }

        @Nullable
        private SoundCategory category( @Nullable Sound.Source source) {
            if (source == null) {
                return null;
            }
            if (source == Sound.Source.MASTER) {
                return SoundCategory.MASTER;
            }
            if (source == Sound.Source.MUSIC) {
                return SoundCategory.MUSIC;
            }
            if (source == Sound.Source.RECORD) {
                return SoundCategory.RECORDS;
            }
            if (source == Sound.Source.WEATHER) {
                return SoundCategory.WEATHER;
            }
            if (source == Sound.Source.BLOCK) {
                return SoundCategory.BLOCKS;
            }
            if (source == Sound.Source.HOSTILE) {
                return SoundCategory.HOSTILE;
            }
            if (source == Sound.Source.NEUTRAL) {
                return SoundCategory.NEUTRAL;
            }
            if (source == Sound.Source.PLAYER) {
                return SoundCategory.PLAYERS;
            }
            if (source == Sound.Source.AMBIENT) {
                return SoundCategory.AMBIENT;
            }
            if (source == Sound.Source.VOICE) {
                return SoundCategory.VOICE;
            }
            Knob.logUnsupported(this, (Object)source);
            return null;
        }
    }

    static class Sound
    extends Position
    implements Facet.Sound<Player, Vector> {
        private static final boolean KEY_SUPPORTED = MinecraftReflection.hasClass("org.bukkit.NamespacedKey");
        private static final boolean STOP_SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", String.class);
        private static final MethodHandle STOP_ALL_SUPPORTED = MinecraftReflection.findMethod(Player.class, "stopAllSounds", Void.TYPE, new Class[0]);

        Sound() {
        }

        @Override
        public void playSound(@NotNull Player viewer, @NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound sound, @NotNull Vector vector) {
            String name = Sound.name(sound.name());
            Location location = vector.toLocation(viewer.getWorld());
            viewer.playSound(location, name, sound.volume(), sound.pitch());
        }

        @Override
        public void stopSound(@NotNull Player viewer, @NotNull SoundStop stop) {
            if (STOP_SUPPORTED) {
                String name = Sound.name(stop.sound());
                if (name.isEmpty() && STOP_ALL_SUPPORTED != null) {
                    try {
                        STOP_ALL_SUPPORTED.invoke(viewer);
                    }
                    catch (Throwable error) {
                        Knob.logError(error, "Could not invoke stopAllSounds on %s", viewer);
                    }
                    return;
                }
                viewer.stopSound(name);
            }
        }

        @NotNull
        protected static String name(@Nullable Key name) {
            if (name == null) {
                return "";
            }
            if (KEY_SUPPORTED) {
                return name.asString();
            }
            return name.value();
        }
    }

    static class Position
    extends BukkitFacet<Player>
    implements Facet.Position<Player, Vector> {
        protected Position() {
            super(Player.class);
        }

        @Override
        @NotNull
        public Vector createPosition(@NotNull Player viewer) {
            return viewer.getLocation().toVector();
        }

        @Override
        @NotNull
        public Vector createPosition(double x, double y, double z) {
            return new Vector(x, y, z);
        }
    }

    static class Chat
    extends Message<CommandSender>
    implements Facet.Chat<CommandSender, String> {
        protected Chat() {
            super(CommandSender.class);
        }

        @Override
        public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull String message, @NotNull Object type) {
            viewer.sendMessage(message);
        }
    }

    static class Message<V extends CommandSender>
    extends BukkitFacet<V>
    implements Facet.Message<V, String> {
        protected Message(@Nullable Class<? extends V> viewerClass) {
            super(viewerClass);
        }

        @Override
        @NotNull
        public String createMessage(@NotNull V viewer, @NotNull Component message) {
            return BukkitComponentSerializer.legacy().serialize(message);
        }
    }
}

