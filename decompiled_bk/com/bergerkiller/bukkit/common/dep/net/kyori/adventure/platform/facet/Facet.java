/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.SoundStop;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import java.io.Closeable;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface Facet<V> {
    @SafeVarargs
    @NotNull
    public static <V, F extends Facet<? extends V>> Collection<F> of(Supplier<F> ... suppliers) {
        LinkedList<Facet> facets = new LinkedList<Facet>();
        for (Supplier<F> supplier : suppliers) {
            Facet facet;
            try {
                facet = (Facet)supplier.get();
            }
            catch (NoClassDefFoundError error) {
                Knob.logMessage("Skipped facet: %s", supplier.getClass().getName());
                continue;
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed facet: %s", supplier);
                continue;
            }
            if (!facet.isSupported()) {
                Knob.logMessage("Skipped facet: %s", facet);
                continue;
            }
            facets.add(facet);
            Knob.logMessage("Added facet: %s", facet);
        }
        return facets;
    }

    @Nullable
    public static <V, F extends Facet<V>> F of(@Nullable Collection<F> facets, @Nullable V viewer) {
        if (facets == null || viewer == null) {
            return null;
        }
        for (Facet facet : facets) {
            try {
                if (facet.isApplicable(viewer)) {
                    Knob.logMessage("Selected facet: %s for %s", facet, viewer);
                    return (F)facet;
                }
                if (!Knob.DEBUG) continue;
                Knob.logMessage("Not selecting %s for %s", facet, viewer);
            }
            catch (ClassCastException error) {
                if (!Knob.DEBUG) continue;
                Knob.logMessage("Exception while getting facet %s for %s: %s", facet, viewer, error.getMessage());
            }
        }
        return null;
    }

    default public boolean isSupported() {
        return true;
    }

    default public boolean isApplicable(@NotNull V viewer) {
        return true;
    }

    public static interface Pointers<V>
    extends Facet<V> {
        public void contributePointers(V var1, Pointers.Builder var2);
    }

    public static interface TabList<V, M>
    extends Message<V, M> {
        public void send(V var1, @Nullable M var2, @Nullable M var3);
    }

    public static interface FakeEntity<V, P>
    extends Position<V, P>,
    Closeable {
        public void teleport(@NotNull V var1, @Nullable P var2);

        public void metadata(int var1, @NotNull Object var2);

        public void invisible(boolean var1);

        public void health(float var1);

        public void name(@NotNull Component var1);

        @Override
        public void close();
    }

    public static interface BossBarEntity<V, P>
    extends BossBar<V>,
    FakeEntity<V, P> {
        public static final int OFFSET_PITCH = 30;
        public static final int OFFSET_YAW = 0;
        public static final int OFFSET_MAGNITUDE = 40;
        public static final int INVULNERABLE_KEY = 20;
        public static final int INVULNERABLE_TICKS = 890;

        @Override
        default public void bossBarProgressChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, float oldProgress, float newProgress) {
            this.health(newProgress);
        }

        @Override
        default public void bossBarNameChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            this.name(newName);
        }

        @Override
        default public void addViewer(@NotNull V viewer) {
            this.teleport(viewer, this.createPosition(viewer));
        }

        @Override
        default public void removeViewer(@NotNull V viewer) {
            this.teleport(viewer, null);
        }
    }

    public static interface BossBarPacket<V>
    extends BossBar<V> {
        public static final int ACTION_ADD = 0;
        public static final int ACTION_REMOVE = 1;
        public static final int ACTION_HEALTH = 2;
        public static final int ACTION_TITLE = 3;
        public static final int ACTION_STYLE = 4;
        public static final int ACTION_FLAG = 5;

        default public int createColor( @NotNull BossBar.Color color) {
            if (color == BossBar.Color.PURPLE) {
                return 5;
            }
            if (color == BossBar.Color.PINK) {
                return 0;
            }
            if (color == BossBar.Color.BLUE) {
                return 1;
            }
            if (color == BossBar.Color.RED) {
                return 2;
            }
            if (color == BossBar.Color.GREEN) {
                return 3;
            }
            if (color == BossBar.Color.YELLOW) {
                return 4;
            }
            if (color == BossBar.Color.WHITE) {
                return 6;
            }
            Knob.logUnsupported(this, (Object)color);
            return 5;
        }

        default public int createOverlay( @NotNull BossBar.Overlay overlay) {
            if (overlay == BossBar.Overlay.PROGRESS) {
                return 0;
            }
            if (overlay == BossBar.Overlay.NOTCHED_6) {
                return 1;
            }
            if (overlay == BossBar.Overlay.NOTCHED_10) {
                return 2;
            }
            if (overlay == BossBar.Overlay.NOTCHED_12) {
                return 3;
            }
            if (overlay == BossBar.Overlay.NOTCHED_20) {
                return 4;
            }
            Knob.logUnsupported(this, (Object)overlay);
            return 0;
        }

        default public byte createFlag(byte flagBit, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            byte bit = flagBit;
            for (BossBar.Flag flag : flagsAdded) {
                if (flag == BossBar.Flag.DARKEN_SCREEN) {
                    bit = (byte)(bit | 1);
                    continue;
                }
                if (flag == BossBar.Flag.PLAY_BOSS_MUSIC) {
                    bit = (byte)(bit | 2);
                    continue;
                }
                if (flag == BossBar.Flag.CREATE_WORLD_FOG) {
                    bit = (byte)(bit | 4);
                    continue;
                }
                Knob.logUnsupported(this, (Object)flag);
            }
            for (BossBar.Flag flag : flagsRemoved) {
                if (flag == BossBar.Flag.DARKEN_SCREEN) {
                    bit = (byte)(bit & 0xFFFFFFFE);
                    continue;
                }
                if (flag == BossBar.Flag.PLAY_BOSS_MUSIC) {
                    bit = (byte)(bit & 0xFFFFFFFD);
                    continue;
                }
                if (flag == BossBar.Flag.CREATE_WORLD_FOG) {
                    bit = (byte)(bit & 0xFFFFFFFB);
                    continue;
                }
                Knob.logUnsupported(this, (Object)flag);
            }
            return bit;
        }
    }

    public static interface BossBar<V>
    extends BossBar.Listener,
    Closeable {
        public static final int PROTOCOL_BOSS_BAR = 356;

        default public void bossBarInitialized(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar) {
            this.bossBarNameChanged(bar, bar.name(), bar.name());
            this.bossBarColorChanged(bar, bar.color(), bar.color());
            this.bossBarProgressChanged(bar, bar.progress(), bar.progress());
            this.bossBarFlagsChanged(bar, bar.flags(), Collections.emptySet());
            this.bossBarOverlayChanged(bar, bar.overlay(), bar.overlay());
        }

        public void addViewer(@NotNull V var1);

        public void removeViewer(@NotNull V var1);

        public boolean isEmpty();

        @Override
        public void close();

        @FunctionalInterface
        public static interface Builder<V, B extends BossBar<V>>
        extends Facet<V> {
            @NotNull
            public B createBossBar(@NotNull Collection<V> var1);
        }
    }

    public static interface Book<V, M, B>
    extends Message<V, M> {
        @Nullable
        public B createBook(@NotNull String var1, @NotNull String var2, @NotNull Iterable<M> var3);

        public void openBook(@NotNull V var1, @NotNull B var2);
    }

    public static interface EntitySound<V, M>
    extends Facet<V> {
        public M createForSelf(V var1, @NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound var2);

        public M createForEmitter(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound var1,  @NotNull Sound.Emitter var2);

        public void playSound(@NotNull V var1, M var2);
    }

    public static interface Sound<V, P>
    extends Position<V, P> {
        public void playSound(@NotNull V var1, @NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound var2, @NotNull P var3);

        public void stopSound(@NotNull V var1, @NotNull SoundStop var2);
    }

    public static interface Position<V, P>
    extends Facet<V> {
        @Nullable
        public P createPosition(@NotNull V var1);

        @NotNull
        public P createPosition(double var1, double var3, double var5);
    }

    public static interface TitlePacket<V, M, C, T>
    extends Title<V, M, C, T> {
        public static final int ACTION_TITLE = 0;
        public static final int ACTION_SUBTITLE = 1;
        public static final int ACTION_ACTIONBAR = 2;
        public static final int ACTION_TIMES = 3;
        public static final int ACTION_CLEAR = 4;
        public static final int ACTION_RESET = 5;
    }

    public static interface Title<V, M, C, T>
    extends Message<V, M> {
        public static final int PROTOCOL_ACTION_BAR = 310;
        public static final long MAX_SECONDS = 0x666666666666666L;

        @NotNull
        public C createTitleCollection();

        public void contributeTitle(@NotNull C var1, @NotNull M var2);

        public void contributeSubtitle(@NotNull C var1, @NotNull M var2);

        public void contributeTimes(@NotNull C var1, int var2, int var3, int var4);

        @Nullable
        public T completeTitle(@NotNull C var1);

        public void showTitle(@NotNull V var1, @NotNull T var2);

        public void clearTitle(@NotNull V var1);

        public void resetTitle(@NotNull V var1);

        default public int toTicks(@Nullable Duration duration) {
            if (duration == null || duration.isNegative()) {
                return -1;
            }
            if (duration.getSeconds() > 0x666666666666666L) {
                return Integer.MAX_VALUE;
            }
            return (int)(duration.getSeconds() * 20L + (long)(duration.getNano() / 50000000));
        }
    }

    public static interface ActionBar<V, M>
    extends Message<V, M> {
        public void sendMessage(@NotNull V var1, @NotNull M var2);
    }

    public static interface ChatPacket<V, M>
    extends Chat<V, M> {
        public static final byte TYPE_CHAT = 0;
        public static final byte TYPE_SYSTEM = 1;
        public static final byte TYPE_ACTION_BAR = 2;

        default public byte createMessageType(@NotNull MessageType type) {
            if (type == MessageType.CHAT) {
                return 0;
            }
            if (type == MessageType.SYSTEM) {
                return 1;
            }
            Knob.logUnsupported(this, (Object)type);
            return 0;
        }
    }

    public static interface Chat<V, M>
    extends Message<V, M> {
        public void sendMessage(@NotNull V var1, @NotNull Identity var2, @NotNull M var3, @NotNull Object var4);
    }

    public static interface Message<V, M>
    extends Facet<V> {
        public static final int PROTOCOL_HEX_COLOR = 713;
        public static final int PROTOCOL_JSON = 5;

        @Nullable
        public M createMessage(@NotNull V var1, @NotNull Component var2);
    }
}

