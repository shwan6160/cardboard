/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.entity.Player
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldguard.LocalPlayer
 *  com.sk89q.worldguard.WorldGuard
 *  com.sk89q.worldguard.protection.ApplicableRegionSet
 *  com.sk89q.worldguard.protection.association.RegionAssociable
 *  com.sk89q.worldguard.protection.flags.BooleanFlag
 *  com.sk89q.worldguard.protection.flags.DoubleFlag
 *  com.sk89q.worldguard.protection.flags.Flag
 *  com.sk89q.worldguard.protection.flags.IntegerFlag
 *  com.sk89q.worldguard.protection.flags.StateFlag
 *  com.sk89q.worldguard.protection.flags.StateFlag$State
 *  com.sk89q.worldguard.protection.flags.StringFlag
 *  com.sk89q.worldguard.protection.flags.registry.FlagRegistry
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 *  com.sk89q.worldguard.session.MoveType
 *  com.sk89q.worldguard.session.Session
 *  com.sk89q.worldguard.session.SessionManager
 *  com.sk89q.worldguard.session.handler.Handler
 *  com.sk89q.worldguard.session.handler.Handler$Factory
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlag;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistry;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryBaseImpl;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagTracker;
import com.bergerkiller.bukkit.common.regionflagtracker.worldguard.WGRegionFlagsChangeTracker;
import com.bergerkiller.bukkit.common.regionflagtracker.worldguard.WGRegionFlagsChangeTrackerFallback;
import com.bergerkiller.bukkit.common.regionflagtracker.worldguard.WGRegionFlagsChangeTrackerFieldHack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

class RegionFlagRegistryWorldGuard
extends RegionFlagRegistryBaseImpl {
    private Plugin libraryPlugin = null;
    private final Map<RegionFlag.Type, FlagMapper<?, ?>> flagMappers = new EnumMap(RegionFlag.Type.class);
    private final Map<ProtectedRegion, TrackedProtectedRegion> trackedRegions = new IdentityHashMap<ProtectedRegion, TrackedProtectedRegion>();

    public RegionFlagRegistryWorldGuard() {
        this.flagMappers.put(RegionFlag.Type.BOOLEAN, new UnaryFlagMapper<Boolean>(){

            @Override
            public Flag<Boolean> adapt(Flag<?> flag) {
                return flag instanceof BooleanFlag ? (BooleanFlag)flag : null;
            }

            @Override
            public Flag<Boolean> create(String name) {
                return new BooleanFlag(name);
            }
        });
        this.flagMappers.put(RegionFlag.Type.STATE, new FlagMapper<StateFlag.State, RegionFlag.State>(){

            @Override
            public Flag<StateFlag.State> adapt(Flag<?> flag) {
                return flag instanceof StateFlag ? (StateFlag)flag : null;
            }

            @Override
            public Flag<StateFlag.State> create(String name) {
                return new StateFlag(name, false);
            }

            @Override
            public RegionFlag.State marshalValue(StateFlag.State value) {
                return value == StateFlag.State.ALLOW ? RegionFlag.State.ALLOW : RegionFlag.State.DENY;
            }
        });
        this.flagMappers.put(RegionFlag.Type.INTEGER, new UnaryFlagMapper<Integer>(){

            @Override
            public Flag<Integer> adapt(Flag<?> flag) {
                return flag instanceof IntegerFlag ? (IntegerFlag)flag : null;
            }

            @Override
            public Flag<Integer> create(String name) {
                return new IntegerFlag(name);
            }
        });
        this.flagMappers.put(RegionFlag.Type.DOUBLE, new UnaryFlagMapper<Double>(){

            @Override
            public Flag<Double> adapt(Flag<?> flag) {
                return flag instanceof DoubleFlag ? (DoubleFlag)flag : null;
            }

            @Override
            public Flag<Double> create(String name) {
                return new DoubleFlag(name);
            }
        });
        this.flagMappers.put(RegionFlag.Type.STRING, new UnaryFlagMapper<String>(){

            @Override
            public Flag<String> adapt(Flag<?> flag) {
                return flag instanceof StringFlag ? (StringFlag)flag : null;
            }

            @Override
            public Flag<String> create(String name) {
                return new StringFlag(name);
            }
        });
    }

    @Override
    protected boolean isStateReady() {
        return RegionFlagRegistryWorldGuard.findPlugin("WorldGuard", Plugin::isEnabled) != null;
    }

    @Override
    protected void onStateIsReady(Plugin libraryPlugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(libraryPlugin, this::updateTrackedRegions, 1L, 1L);
        libraryPlugin.getLogger().info("[RegionFlagTracker] Region flags will be tracked from WorldGuard");
    }

    @Override
    public synchronized void enable(Plugin libraryPlugin) {
        this.libraryPlugin = libraryPlugin;
        super.enable(libraryPlugin);
    }

    @Override
    public synchronized void disable() {
        super.disable();
        this.trackedRegions.clear();
    }

    private void updateTrackedRegions() {
        Set<ValueTrackerHandler> changedHandlers = Collections.emptySet();
        Iterator<TrackedProtectedRegion> iter = this.trackedRegions.values().iterator();
        while (iter.hasNext()) {
            TrackedProtectedRegion.UpdateResult result = iter.next().update();
            if (result.cleanupRegion) {
                iter.remove();
                continue;
            }
            if (result.handlersToRefresh.isEmpty()) continue;
            if (changedHandlers.isEmpty()) {
                changedHandlers = new HashSet();
            }
            changedHandlers.addAll(result.handlersToRefresh);
        }
        changedHandlers.forEach(ValueTrackerHandler::refresh);
    }

    @Override
    protected <T> RegionFlagRegistry.RegisteredRegionFlag<T> createNewFlag(Plugin plugin, RegionFlag<T> flag) {
        FlagMapper<?, ?> mapper = this.flagMappers.get((Object)flag.type());
        try {
            return this.createNewFlagUnsafe(plugin, mapper, flag);
        }
        catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Region Flag '" + flag.name() + " of type " + flag.type().name() + " could not be registered", t);
            return new RegionFlagRegistry.RegisteredRegionFlag<T>(plugin, flag);
        }
    }

    private <T, R> RegionFlagRegistry.RegisteredRegionFlag<T> createNewFlagUnsafe(Plugin plugin, FlagMapper<R, T> mapper, RegionFlag<T> flag) {
        Flag<R> worldguardFlag = this.findOrRegisterFlag(plugin, mapper, flag);
        return new RegisteredWorldGuardRegionFlag<T, R>(this, plugin, flag, worldguardFlag, mapper);
    }

    private <T, R> Flag<R> findOrRegisterFlag(Plugin plugin, FlagMapper<R, T> mapper, RegionFlag<T> flag) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for (Flag worldguardFlag : registry.getAll()) {
            if (!worldguardFlag.getName().equals(flag.name())) continue;
            Flag<R> adapted = mapper.adapt(worldguardFlag);
            if (adapted != null) {
                return adapted;
            }
            throw new IllegalStateException("Region Flag " + flag.name() + " by plugin " + plugin.getName() + " is already in use by another plugin with a conflicting type");
        }
        Flag<R> worldguardFlag = mapper.create(flag.name());
        registry.register(worldguardFlag);
        return worldguardFlag;
    }

    private Optional<TrackedProtectedRegion> trackRegionIfExists(ProtectedRegion region) {
        return Optional.ofNullable(this.trackedRegions.get(region));
    }

    private TrackedProtectedRegion trackRegion(ProtectedRegion region) {
        if (this.libraryPlugin == null) {
            throw new IllegalStateException("Region tracking begun before enable()");
        }
        return this.trackedRegions.computeIfAbsent(region, r -> new TrackedProtectedRegion(this.libraryPlugin, region));
    }

    private static final class TrackedProtectedRegion {
        private static boolean IS_OPTIMIZED_FLAG_TRACKER_WORKING = true;
        public final ProtectedRegion region;
        public final Set<ValueTrackerHandler<?, ?>> handlers = new HashSet();
        private WGRegionFlagsChangeTracker flagChangeTracker;
        private int checkPlayersQuitCounter = 0;

        public TrackedProtectedRegion(Plugin libraryPlugin, ProtectedRegion region) {
            this.region = region;
            this.flagChangeTracker = TrackedProtectedRegion.initFlagChangeTracker(libraryPlugin, region);
        }

        private static WGRegionFlagsChangeTracker initFlagChangeTracker(Plugin libraryPlugin, ProtectedRegion region) {
            if (IS_OPTIMIZED_FLAG_TRACKER_WORKING) {
                try {
                    return new WGRegionFlagsChangeTrackerFieldHack(region);
                }
                catch (WGRegionFlagsChangeTrackerFieldHack.OptimizationNotSupportedException ex) {
                    IS_OPTIMIZED_FLAG_TRACKER_WORKING = false;
                    libraryPlugin.getLogger().log(Level.WARNING, "[RegionFlagTracker] Could not optimize detection of region flag changes", ex);
                }
            }
            return new WGRegionFlagsChangeTrackerFallback(region);
        }

        public UpdateResult update() {
            if (++this.checkPlayersQuitCounter >= 40) {
                this.checkPlayersQuitCounter = 0;
                this.handlers.removeIf(handler -> ((ValueTrackerHandler)handler).tracker != null && RegionFlagRegistry.hasPlayerQuit(((ValueTrackerHandler)handler).tracker.getPlayer()));
            }
            if (this.handlers.isEmpty()) {
                return UpdateResult.DEFAULT_CLEANUP;
            }
            if (this.flagChangeTracker.update(this.region)) {
                return new UpdateResult(false, this.handlers);
            }
            return UpdateResult.DEFAULT_KEEP;
        }

        private static class UpdateResult {
            public static final UpdateResult DEFAULT_CLEANUP = new UpdateResult(true, Collections.emptySet());
            public static final UpdateResult DEFAULT_KEEP = new UpdateResult(false, Collections.emptySet());
            public final boolean cleanupRegion;
            public final Set<ValueTrackerHandler<?, ?>> handlersToRefresh;

            public UpdateResult(boolean cleanupRegion, Set<ValueTrackerHandler<?, ?>> handlersToRefresh) {
                this.cleanupRegion = cleanupRegion;
                this.handlersToRefresh = handlersToRefresh;
            }
        }
    }

    private static interface FlagMapper<R, T> {
        public Flag<R> adapt(Flag<?> var1);

        public Flag<R> create(String var1);

        public T marshalValue(R var1);
    }

    private static class RegisteredWorldGuardRegionFlag<T, R>
    extends RegionFlagRegistry.RegisteredRegionFlag<T> {
        public final RegionFlagRegistryWorldGuard registry;
        public final Flag<R> worldguardFlag;
        public final FlagMapper<R, T> mapper;
        private final ValueTrackerFactory<T, R> sessionFactory;

        public RegisteredWorldGuardRegionFlag(RegionFlagRegistryWorldGuard registry, Plugin plugin, RegionFlag<T> flag, Flag<R> worldguardFlag, FlagMapper<R, T> mapper) {
            super(plugin, flag);
            this.registry = registry;
            this.worldguardFlag = worldguardFlag;
            this.mapper = mapper;
            this.sessionFactory = new ValueTrackerFactory(this);
        }

        @Override
        public void registerHandler() {
            SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
            sessionManager.registerHandler(this.sessionFactory, null);
        }

        @Override
        public void unregisterHandler() {
            SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
            sessionManager.unregisterHandler(this.sessionFactory);
        }
    }

    private static class ValueTrackerHandler<T, R>
    extends Handler {
        private final RegisteredWorldGuardRegionFlag<T, R> flag;
        private LocalPlayer lastLocalPlayer = null;
        private RegionFlagTracker<T> tracker = null;
        private ApplicableRegionSet currentRegionSet = null;
        private R lastValue;

        protected ValueTrackerHandler(Session session, RegisteredWorldGuardRegionFlag<T, R> flag) {
            super(session);
            this.flag = flag;
        }

        private void updateTracker(LocalPlayer player) {
            if (this.lastLocalPlayer != player) {
                this.lastLocalPlayer = player;
                this.tracker = this.flag.registry.track(BukkitAdapter.adapt((Player)player), this.flag.flag);
            }
        }

        public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
            Object currentValue = set.queryValue((RegionAssociable)player, this.flag.worldguardFlag);
            this.lastValue = currentValue;
            if (this.currentRegionSet != null) {
                for (ProtectedRegion region : this.currentRegionSet) {
                    this.flag.registry.trackRegionIfExists(region).ifPresent(tr -> tr.handlers.remove((Object)this));
                }
            }
            this.currentRegionSet = set;
            this.updateTracker(player);
            for (ProtectedRegion region : set) {
                ((RegionFlagRegistryWorldGuard)this.flag.registry).trackRegion((ProtectedRegion)region).handlers.add(this);
            }
            if (currentValue == null) {
                this.tracker.updateValue(null);
            } else {
                this.tracker.updateValue(this.flag.mapper.marshalValue(currentValue));
            }
        }

        public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
            if (entered.isEmpty() && exited.isEmpty() && from.getExtent().equals((Object)to.getExtent())) {
                return true;
            }
            this.currentRegionSet = toSet;
            this.updateTracker(player);
            for (ProtectedRegion region : exited) {
                this.flag.registry.trackRegionIfExists(region).ifPresent(tr -> tr.handlers.remove((Object)this));
            }
            for (ProtectedRegion region : entered) {
                ((RegionFlagRegistryWorldGuard)this.flag.registry).trackRegion((ProtectedRegion)region).handlers.add(this);
            }
            this.updateValue(toSet.queryValue((RegionAssociable)player, this.flag.worldguardFlag));
            return true;
        }

        public void refresh() {
            if (this.lastLocalPlayer == null || this.tracker == null || this.currentRegionSet == null) {
                return;
            }
            this.updateValue(this.currentRegionSet.queryValue((RegionAssociable)this.lastLocalPlayer, this.flag.worldguardFlag));
        }

        private void updateValue(R currentValue) {
            if (currentValue == null && this.lastValue != null) {
                this.tracker.updateValue(null);
            } else if (currentValue != null && currentValue != this.lastValue) {
                this.tracker.updateValue(this.flag.mapper.marshalValue(currentValue));
            }
            this.lastValue = currentValue;
        }
    }

    private static class ValueTrackerFactory<T, R>
    extends Handler.Factory<ValueTrackerHandler<T, R>> {
        private final RegisteredWorldGuardRegionFlag<T, R> flag;

        public ValueTrackerFactory(RegisteredWorldGuardRegionFlag<T, R> flag) {
            this.flag = flag;
        }

        public ValueTrackerHandler<T, R> create(Session session) {
            return new ValueTrackerHandler<T, R>(session, this.flag);
        }
    }

    private static interface UnaryFlagMapper<T>
    extends FlagMapper<T, T> {
        @Override
        default public T marshalValue(T value) {
            return value;
        }
    }
}

