/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.commands.selector;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.utils.BoundingRange;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

class TCSelectorLocationFilter {
    private static final Map<String, SelectorConsumer> CONSUMERS = new HashMap<String, SelectorConsumer>();
    private CommandSender sender = null;
    private World world = null;
    private BoundingRange.Axis range = null;
    private BoundingRange distanceSquared = null;

    TCSelectorLocationFilter() {
    }

    public void read(CommandSender sender, List<SelectorCondition> conditions) throws SelectorException {
        this.sender = sender;
        Iterator<SelectorCondition> iter = conditions.iterator();
        while (iter.hasNext()) {
            SelectorCondition condition = iter.next();
            SelectorConsumer consumer = CONSUMERS.get(condition.getKey());
            if (consumer == null) continue;
            consumer.accept(this, condition);
            iter.remove();
        }
        if (this.range != null) {
            if (this.world == null) {
                this.world = this.range.world;
            }
            if (this.world == null) {
                throw new SelectorException("World must be specified when selecting trains by coordinates");
            }
            if (this.range.x == null) {
                throw new SelectorException("No X-coordinate was specified");
            }
            if (this.range.y == null) {
                throw new SelectorException("No Y-coordinate was specified");
            }
            if (this.range.z == null) {
                throw new SelectorException("No Z-coordinate was specified");
            }
        }
    }

    private BoundingRange.Axis initAxis() {
        if (this.range == null) {
            this.range = BoundingRange.Axis.forSender(this.sender);
        }
        return this.range;
    }

    public boolean hasFilters() {
        return this.world != null || this.distanceSquared != null || this.range != null;
    }

    public boolean filter(TrainProperties properties) {
        if (this.range == null) {
            return TCSelectorLocationFilter.isOnWorld(properties, this.world);
        }
        return TCSelectorLocationFilter.forAllCartPositions(properties, this.world, this::matchCart);
    }

    public static boolean isOnWorld(TrainProperties properties, World world) {
        MinecartGroup group = properties.getHolder();
        if (group != null) {
            return group.getWorld() == world;
        }
        OfflineGroup offlineGroup = properties.getTrainCarts().getOfflineGroups().findGroup(properties.getTrainName());
        return offlineGroup != null && world.getUID().equals(offlineGroup.world.getUniqueId());
    }

    public static boolean forAllCartPositions(TrainProperties properties, World world, CartPositionSink func) {
        MinecartGroup group = properties.getHolder();
        if (group != null) {
            if (group.getWorld() != world) {
                return false;
            }
            for (MinecartMember<?> member : group) {
                if (!func.apply(((CommonMinecart)member.getEntity()).loc.vector())) continue;
                return true;
            }
            return false;
        }
        OfflineGroup offlineGroup = properties.getTrainCarts().getOfflineGroups().findGroup(properties.getTrainName());
        if (offlineGroup == null) {
            return false;
        }
        if (!world.getUID().equals(offlineGroup.world.getUniqueId())) {
            return false;
        }
        for (OfflineMember member : offlineGroup.members) {
            Vector cartPosition = new Vector((double)((member.cx << 4) + 8), 128.0, (double)((member.cz << 4) + 8));
            if (!func.apply(cartPosition)) continue;
            return true;
        }
        return false;
    }

    private boolean matchCart(Vector cartPosition) {
        if (this.distanceSquared == null) {
            return this.range.isInside(cartPosition);
        }
        return this.distanceSquared.isInside(this.range.distanceSquared(cartPosition));
    }

    static {
        CONSUMERS.put("world", (filter, condition) -> {
            filter.world = Bukkit.getWorld((String)condition.getValue());
            if (filter.world == null) {
                throw new SelectorException("World '" + condition.getValue() + "' does not exist");
            }
        });
        CONSUMERS.put("x", (filter, condition) -> {
            filter.initAxis().x = condition.getBoundingRange();
        });
        CONSUMERS.put("y", (filter, condition) -> {
            filter.initAxis().y = condition.getBoundingRange();
        });
        CONSUMERS.put("z", (filter, condition) -> {
            filter.initAxis().z = condition.getBoundingRange();
        });
        CONSUMERS.put("dx", (filter, condition) -> {
            BoundingRange.Axis range = filter.initAxis();
            if (range.x == null) {
                throw new SelectorException("No X-coordinate was specified");
            }
            range.x = range.x.add(condition.getBoundingRange());
        });
        CONSUMERS.put("dy", (filter, condition) -> {
            BoundingRange.Axis range = filter.initAxis();
            if (range.y == null) {
                throw new SelectorException("No Y-coordinate was specified");
            }
            range.y = range.y.add(condition.getBoundingRange());
        });
        CONSUMERS.put("dz", (filter, condition) -> {
            BoundingRange.Axis range = filter.initAxis();
            if (range.z == null) {
                throw new SelectorException("No Z-coordinate was specified");
            }
            range.z = range.z.add(condition.getBoundingRange());
        });
        CONSUMERS.put("distance", (filter, condition) -> {
            filter.initAxis();
            filter.distanceSquared = condition.getBoundingRange().squared();
        });
    }

    @FunctionalInterface
    private static interface SelectorConsumer {
        public void accept(TCSelectorLocationFilter var1, SelectorCondition var2) throws SelectorException;
    }

    @FunctionalInterface
    public static interface CartPositionSink {
        public boolean apply(Vector var1);
    }
}

