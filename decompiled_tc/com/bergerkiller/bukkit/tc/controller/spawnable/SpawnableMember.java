/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Location
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.spawnable;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class SpawnableMember
implements TrainCarts.Provider {
    private static final double DEFAULT_CART_LENGTH = 0.98;
    private final SpawnableGroup group;
    private final ConfigurationNode config;
    private final double length;
    private final double couplerLength;
    private final EntityType entityType;
    private final boolean flipped;

    protected SpawnableMember(SpawnableGroup group, ConfigurationNode config) {
        this.group = group;
        this.config = config;
        this.length = this.config.contains("model.physical.cartLength") ? (Double)this.config.get("model.physical.cartLength", (Object)0.98) : (this.group.getConfig().contains("model.physical.cartLength") ? (Double)this.group.getConfig().get("model.physical.cartLength", (Object)0.98) : 0.98);
        this.couplerLength = (Double)this.config.getOrDefault("model.physical.cartCouplerLength", (Object)(0.5 * TCConfig.cartDistanceGap));
        this.entityType = (EntityType)this.config.get("entityType", (Object)EntityType.MINECART);
        this.flipped = (Boolean)this.config.get("flipped", (Object)false);
    }

    public SpawnableGroup getGroup() {
        return this.group;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.group.getTrainCarts();
    }

    @Deprecated
    public TrainCarts getPlugin() {
        return this.group.getTrainCarts();
    }

    public MinecartMember<?> spawn(Location spawnLoc) {
        return MinecartMemberStore.spawn(this.getTrainCarts(), spawnLoc, this.isFlipped(), this.getEntityType(), this.config);
    }

    public ConfigurationNode getConfig() {
        return this.config;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public double getLength() {
        return this.length;
    }

    public double getCartCouplerLength() {
        return this.couplerLength;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public boolean hasInventoryItems() {
        ConfigurationNode data = this.config.getNodeIfExists("data");
        return data != null && data.contains("contents") && !data.getList("contents").isEmpty();
    }

    public Permission getPermission() {
        switch (this.getEntityType()) {
            case MINECART_CHEST: {
                return Permission.SPAWNER_STORAGE;
            }
            case MINECART_FURNACE: {
                return Permission.SPAWNER_POWERED;
            }
            case MINECART_HOPPER: {
                return Permission.SPAWNER_HOPPER;
            }
            case MINECART_TNT: {
                return Permission.SPAWNER_TNT;
            }
            case MINECART_MOB_SPAWNER: {
                return Permission.SPAWNER_SPAWNER;
            }
            case MINECART_COMMAND: {
                return Permission.SPAWNER_COMMAND;
            }
        }
        return Permission.SPAWNER_REGULAR;
    }

    public SpawnableMember clone() {
        return this.cloneWithGroup(this.group);
    }

    public SpawnableMember cloneReversed() {
        ConfigurationNode config = this.config.clone();
        StandardProperties.reverseSavedCart(config);
        return new SpawnableMember(this.group, config);
    }

    protected SpawnableMember cloneWithGroup(SpawnableGroup group) {
        return new SpawnableMember(group, this.config.clone());
    }

    public String toString() {
        return this.entityType.toString();
    }

    public static class SpawnLocation {
        public final SpawnableMember member;
        public final Location location;
        public final Vector forward;

        public SpawnLocation(SpawnableMember member, Vector forward, Location location) {
            this.member = member;
            this.forward = forward;
            this.location = location;
        }

        public MinecartMember<?> spawn(double initialSpeed) {
            MinecartMember<?> spawnedMember = this.member.spawn(this.location);
            if (initialSpeed != 0.0) {
                ((CommonMinecart)spawnedMember.getEntity()).setVelocity(this.forward.clone().multiply(initialSpeed));
            }
            return spawnedMember;
        }
    }
}

