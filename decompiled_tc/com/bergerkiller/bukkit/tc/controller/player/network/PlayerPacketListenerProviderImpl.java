/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.RunOnceTask
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.RunOnceTask;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerPacketListener;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

final class PlayerPacketListenerProviderImpl
implements PlayerPacketListener.Provider {
    private static final int CLEANUP_INTERVAL = 200;
    private final Map<Set<PacketType>, TypeSetListener> activeTypeSetListeners = new HashMap<Set<PacketType>, TypeSetListener>();
    private final List<Player> recentlyQuitPlayers = new ArrayList<Player>();
    private final TrainCarts traincarts;
    private final Task cleanupTask;
    private boolean disabled = false;

    public PlayerPacketListenerProviderImpl(TrainCarts traincarts) {
        this.traincarts = traincarts;
        this.cleanupTask = new Task((JavaPlugin)traincarts){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                PlayerPacketListenerProviderImpl playerPacketListenerProviderImpl = PlayerPacketListenerProviderImpl.this;
                synchronized (playerPacketListenerProviderImpl) {
                    PlayerPacketListenerProviderImpl.this.recentlyQuitPlayers.clear();
                    PlayerPacketListenerProviderImpl.this.activeTypeSetListeners.values().removeIf(TypeSetListener::isTerminated);
                }
            }
        };
    }

    @Override
    public synchronized <L extends PacketListener> PlayerPacketListener<L> create(Player player, L packetListener, PacketType ... packetTypes) {
        if (this.disabled || player == null || !player.isValid() && Bukkit.getPlayer((UUID)player.getUniqueId()) != player) {
            return PlayerPacketListener.createNoOp(player, packetListener);
        }
        if (!this.recentlyQuitPlayers.isEmpty()) {
            for (Player p : this.recentlyQuitPlayers) {
                if (p != player) continue;
                return PlayerPacketListener.createNoOp(player, packetListener);
            }
        }
        HashSet<PacketType> packetTypesSet = new HashSet<PacketType>(Arrays.asList(packetTypes));
        TypeSetListener typeSetListener = this.activeTypeSetListeners.compute(packetTypesSet, (packets, existing) -> {
            if (existing == null || existing.isTerminated()) {
                return new TypeSetListener(this.traincarts, (Set<PacketType>)packets);
            }
            return existing;
        });
        return typeSetListener.addListener(player, packetListener);
    }

    @Override
    public synchronized void enable() {
        this.disabled = false;
        this.cleanupTask.start(200L, 200L);
        this.traincarts.register(new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPlayerQuit(PlayerQuitEvent event) {
                PlayerPacketListenerProviderImpl.this.recentlyQuitPlayers.add(event.getPlayer());
                PlayerPacketListenerProviderImpl.this.activeTypeSetListeners.values().forEach(l -> l.onPlayerQuit(event.getPlayer()));
            }
        });
    }

    @Override
    public synchronized void disable() {
        this.disabled = true;
        this.cleanupTask.stop();
    }

    private static class TypeSetListener
    implements PacketListener {
        private final TrainCarts traincarts;
        private final Multimap<Player, PlayerPacketListenerImpl<?>> packetListeners = Multimaps.newMultimap(new IdentityHashMap(), ArrayList::new);
        private Map<Player, List<PlayerPacketListenerImpl<?>>> packetListenersVisible = Collections.emptyMap();
        private final RunOnceTask checkTerminated;
        private boolean terminated;

        public TypeSetListener(TrainCarts traincarts, Set<PacketType> packetTypes) {
            this.traincarts = traincarts;
            this.checkTerminated = RunOnceTask.create((Plugin)traincarts, this::tryTerminateIfEmpty);
            this.terminated = false;
            traincarts.register(this, packetTypes.toArray(new PacketType[0]));
        }

        private synchronized void tryTerminateIfEmpty() {
            if (!this.terminated && this.packetListeners.isEmpty()) {
                this.terminate();
            }
        }

        public synchronized void terminate() {
            this.terminated = true;
            this.packetListeners.values().forEach(PlayerPacketListenerImpl::setStateTerminated);
            this.packetListeners.clear();
            this.packetListenersVisible = Collections.emptyMap();
            this.traincarts.unregister(this);
            this.checkTerminated.cancel();
        }

        public synchronized boolean isTerminated() {
            return this.terminated;
        }

        public synchronized void terminateListener(PlayerPacketListenerImpl<?> playerPacketListener) {
            if (this.terminated) {
                return;
            }
            Player player = playerPacketListener.getPlayer();
            Collection listenersForPlayer = this.packetListeners.get((Object)player);
            if (listenersForPlayer.remove(playerPacketListener)) {
                this.updateVisiblePacketListeners(player, listenersForPlayer);
                if (this.packetListeners.isEmpty()) {
                    this.checkTerminated.restart(10L);
                }
            }
        }

        public synchronized <L extends PacketListener> PlayerPacketListenerImpl<L> addListener(Player player, L packetListener) {
            PlayerPacketListenerImpl<L> ppl = new PlayerPacketListenerImpl<L>(this, player, packetListener);
            Collection newListeners = this.packetListeners.get((Object)player);
            newListeners.add(ppl);
            this.updateVisiblePacketListeners(player, newListeners);
            return ppl;
        }

        private void updateVisiblePacketListeners(Player player, Collection<PlayerPacketListenerImpl<?>> newValues) {
            IdentityHashMap map = new IdentityHashMap(this.packetListenersVisible);
            if (newValues.isEmpty()) {
                map.remove(player);
            } else {
                map.put(player, new ArrayList(newValues));
            }
            this.packetListenersVisible = map;
        }

        public synchronized void onPlayerQuit(Player player) {
            Collection listeners = this.packetListeners.removeAll((Object)player);
            listeners.forEach(PlayerPacketListenerImpl::setStateTerminated);
            this.updateVisiblePacketListeners(player, Collections.emptyList());
            if (this.packetListeners.isEmpty()) {
                this.checkTerminated.restart(10L);
            }
        }

        private Iterable<PlayerPacketListenerImpl<?>> iterateListenersFor(Player player) {
            return this.packetListenersVisible.getOrDefault(player, Collections.emptyList());
        }

        public void onPacketReceive(PacketReceiveEvent event) {
            for (PlayerPacketListenerImpl<?> playerPacketListener : this.iterateListenersFor(event.getPlayer())) {
                if (!playerPacketListener.isEnabled()) continue;
                playerPacketListener.getListener().onPacketReceive(event);
            }
        }

        public void onPacketSend(PacketSendEvent event) {
            for (PlayerPacketListenerImpl<?> playerPacketListener : this.iterateListenersFor(event.getPlayer())) {
                if (!playerPacketListener.isEnabled()) continue;
                playerPacketListener.getListener().onPacketSend(event);
            }
        }
    }

    private static final class PlayerPacketListenerImpl<L extends PacketListener>
    implements PlayerPacketListener<L> {
        private final AtomicReference<State> state = new AtomicReference<State>(State.DISABLED);
        private final TypeSetListener typeSetListener;
        private final Player player;
        private final L packetListener;

        public PlayerPacketListenerImpl(TypeSetListener typeSetListener, Player player, L packetListener) {
            this.typeSetListener = typeSetListener;
            this.player = player;
            this.packetListener = packetListener;
        }

        @Override
        public Player getPlayer() {
            return this.player;
        }

        @Override
        public L getListener() {
            return this.packetListener;
        }

        @Override
        public boolean isEnabled() {
            return this.state.get() == State.ENABLED;
        }

        @Override
        public PlayerPacketListener<L> enable() {
            this.state.compareAndSet(State.DISABLED, State.ENABLED);
            return this;
        }

        @Override
        public PlayerPacketListener<L> disable() {
            this.state.compareAndSet(State.ENABLED, State.DISABLED);
            return this;
        }

        public void setStateTerminated() {
            this.state.set(State.TERMINATED);
        }

        @Override
        public void terminate() {
            if (this.state.getAndSet(State.TERMINATED) != State.TERMINATED) {
                this.typeSetListener.terminateListener(this);
            }
        }

        private static enum State {
            DISABLED,
            ENABLED,
            TERMINATED;

        }
    }
}

