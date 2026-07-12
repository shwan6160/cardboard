/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.utils.ListCallbackCollector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

public class TrainCartsPlayerStore
implements TrainCarts.Provider {
    private final TrainCarts traincarts;
    private final Map<UUID, TrainCartsPlayer> players = new HashMap<UUID, TrainCartsPlayer>();

    public TrainCartsPlayerStore(TrainCarts traincarts) {
        this.traincarts = traincarts;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    public synchronized TrainCartsPlayer get(UUID playerUUID) {
        return this.players.computeIfAbsent(playerUUID, u -> new TrainCartsPlayer(this.traincarts, (UUID)u));
    }

    public synchronized TrainCartsPlayer get(Player player) {
        return this.players.computeIfAbsent(player.getUniqueId(), u -> new TrainCartsPlayer(this.traincarts, player));
    }

    public synchronized List<TrainCartsPlayer> find(Predicate<TrainCartsPlayer> condition) {
        ListCallbackCollector<TrainCartsPlayer> collector = new ListCallbackCollector<TrainCartsPlayer>();
        for (TrainCartsPlayer player : this.players.values()) {
            if (!condition.test(player)) continue;
            collector.accept(player);
        }
        return collector.result();
    }
}

