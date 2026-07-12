/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.offline;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorldLoadedChangeListener;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public final class OfflineWorld {
    public static final OfflineWorld NONE;
    private static OfflineWorld cacheLastReturned;
    private static Map<World, OfflineWorld> byBukkitWorld;
    private static Map<UUID, OfflineWorld> worlds;
    private static BukkitWorldSupplierHandler toWorldSupplierFunc;
    private final int hashCode;
    private final UUID worldUUID;
    protected BukkitWorldSupplier loadedWorldSupplier;

    public static LibraryComponent initializeComponent(CommonPlugin bkcommonlibPlugin) {
        return new OfflineWorldLoadedChangeListener((Plugin)bkcommonlibPlugin);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void setLoadedWorldSupplier(BukkitWorldSupplierHandler newToWorldSupplierFunc) {
        Class<OfflineWorld> clazz = OfflineWorld.class;
        synchronized (OfflineWorld.class) {
            toWorldSupplierFunc = newToWorldSupplierFunc;
            for (OfflineWorld world : worlds.values()) {
                world.loadedWorldSupplier = newToWorldSupplierFunc.createBukkitWorldSupplier(world.worldUUID);
            }
            if (!newToWorldSupplierFunc.cacheByBukkitWorld()) {
                OfflineWorld.clearByBukkitWorldCache();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void clearByBukkitWorldCache() {
        Class<OfflineWorld> clazz = OfflineWorld.class;
        synchronized (OfflineWorld.class) {
            byBukkitWorld = new IdentityHashMap<World, OfflineWorld>();
            byBukkitWorld.put(null, NONE);
            cacheLastReturned = NONE;
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static OfflineWorld of(UUID worldUUID) {
        return LogicUtil.synchronizeCopyOnWrite(OfflineWorld.class, () -> worlds, worldUUID, Map::get, (map, key) -> {
            HashMap<UUID, OfflineWorld> copy = new HashMap<UUID, OfflineWorld>((Map<UUID, OfflineWorld>)map);
            OfflineWorld world = copy.computeIfAbsent((UUID)key, OfflineWorld::new);
            worlds = copy;
            return world;
        });
    }

    public static OfflineWorld of(World world) {
        OfflineWorld lastReturned = cacheLastReturned;
        if (lastReturned.loadedWorldSupplier.isWorld(world)) {
            return lastReturned;
        }
        cacheLastReturned = LogicUtil.synchronizeCopyOnWrite(OfflineWorld.class, () -> byBukkitWorld, world, Map::get, (map, w) -> {
            OfflineWorld offlineWorld = OfflineWorld.of(w.getUID());
            if (toWorldSupplierFunc.cacheByBukkitWorld() && offlineWorld.getLoadedWorld() == w) {
                IdentityHashMap<World, OfflineWorld> copy = new IdentityHashMap<World, OfflineWorld>((Map<World, OfflineWorld>)map);
                copy.put((World)w, offlineWorld);
                byBukkitWorld = copy;
            }
            return offlineWorld;
        });
        return cacheLastReturned;
    }

    private OfflineWorld() {
        this.worldUUID = null;
        this.hashCode = 0;
        this.loadedWorldSupplier = new BukkitWorldSupplier(){

            @Override
            public World get() {
                return null;
            }

            @Override
            public boolean isWorld(World world) {
                return world == null;
            }
        };
    }

    private OfflineWorld(UUID worldUUID) {
        this.worldUUID = worldUUID;
        this.hashCode = worldUUID.hashCode();
        this.loadedWorldSupplier = toWorldSupplierFunc.createBukkitWorldSupplier(worldUUID);
    }

    public UUID getUniqueId() {
        return this.worldUUID;
    }

    public boolean isLoaded() {
        return this.loadedWorldSupplier.get() != null;
    }

    public World getLoadedWorld() {
        return this.loadedWorldSupplier.get();
    }

    public OfflineBlock getBlockAt(IntVector3 position) {
        return new OfflineBlock(this, position);
    }

    public OfflineBlock getBlockAt(int x, int y, int z) {
        return new OfflineBlock(this, IntVector3.of(x, y, z));
    }

    public OfflineBlock getBlock(Block bukkitBlock) {
        return new OfflineBlock(this, IntVector3.coordinatesOf(bukkitBlock));
    }

    public Block getLoadedBlockAt(int x, int y, int z) {
        World world = this.loadedWorldSupplier.get();
        return world == null ? null : world.getBlockAt(x, y, z);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        return o == this;
    }

    public String toString() {
        World world = this.loadedWorldSupplier.get();
        if (world != null) {
            return "OfflineWorld{uuid=" + this.worldUUID + ", name=" + world.getName() + "}";
        }
        return "OfflineWorld{uuid=" + this.worldUUID + "}";
    }

    static {
        cacheLastReturned = NONE = new OfflineWorld();
        byBukkitWorld = new IdentityHashMap<World, OfflineWorld>();
        worlds = new HashMap<UUID, OfflineWorld>();
        toWorldSupplierFunc = DefaultBukkitWorldSupplierHandler.INSTANCE;
        worlds.put(null, NONE);
        byBukkitWorld.put(null, NONE);
    }

    static interface BukkitWorldSupplierHandler {
        public BukkitWorldSupplier createBukkitWorldSupplier(UUID var1);

        public boolean cacheByBukkitWorld();
    }

    static interface BukkitWorldSupplier {
        public World get();

        public boolean isWorld(World var1);
    }

    static final class DefaultBukkitWorldSupplierHandler
    implements BukkitWorldSupplierHandler {
        public static final DefaultBukkitWorldSupplierHandler INSTANCE = new DefaultBukkitWorldSupplierHandler();

        DefaultBukkitWorldSupplierHandler() {
        }

        @Override
        public BukkitWorldSupplier createBukkitWorldSupplier(final UUID worldUUID) {
            return new BukkitWorldSupplier(){
                final /* synthetic */ DefaultBukkitWorldSupplierHandler this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public World get() {
                    return Bukkit.getWorld((UUID)worldUUID);
                }

                @Override
                public boolean isWorld(World world) {
                    return world != null && world.getUID().equals(worldUUID);
                }
            };
        }

        @Override
        public boolean cacheByBukkitWorld() {
            return false;
        }
    }
}

