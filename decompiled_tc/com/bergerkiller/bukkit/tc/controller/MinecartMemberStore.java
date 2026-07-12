/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.collections.ClassMap
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.controller.DefaultEntityController
 *  com.bergerkiller.bukkit.common.controller.EntityNetworkController
 *  com.bergerkiller.bukkit.common.conversion.Conversion
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.CommonEntityType
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartCommandBlock
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartHopper
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartMobSpawner
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartRideable
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartTNT
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.controller;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartCommandBlock;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartHopper;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartMobSpawner;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartRideable;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartTNT;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberNetwork;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberCommandBlock;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberHopper;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberMobSpawner;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberRideable;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberTNT;
import com.bergerkiller.bukkit.tc.events.MemberSpawnEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.PaperRedstonePhysicsChecker;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MinecartMemberStore {
    private static ClassMap<Function<TrainCarts, ? extends MinecartMember<?>>> controllers = new ClassMap();

    public static void convertAllAutomatically(TrainCarts plugin) {
        ArrayList<Minecart> minecarts = new ArrayList<Minecart>();
        for (World world : WorldUtil.getWorlds()) {
            if (TrainCarts.isWorldDisabled(world)) continue;
            for (Entity entity : WorldUtil.getEntities((World)world)) {
                if (!MinecartMemberStore.canConvertAutomatically(entity)) continue;
                minecarts.add((Minecart)entity);
            }
        }
        for (Minecart minecart : minecarts) {
            MinecartMemberStore.convert(plugin, minecart);
        }
        minecarts.clear();
    }

    public static boolean canConvertAutomatically(Entity minecart) {
        if (!MinecartMemberStore.canConvert(minecart)) {
            return false;
        }
        TrainCarts traincarts = TrainCarts.plugin;
        return traincarts == null || TCConfig.allMinecartsAreTrainCarts || traincarts.getOfflineGroups().containsMinecart(minecart.getUniqueId());
    }

    public static boolean canConvert(Entity minecart) {
        if (!(minecart instanceof Minecart)) {
            return false;
        }
        if (TrainCarts.isWorldDisabled(minecart.getWorld())) {
            return false;
        }
        TrainCarts traincarts = TrainCarts.plugin;
        if (traincarts != null && traincarts.getOfflineGroups().isDestroyingGroupOf((Minecart)minecart)) {
            return false;
        }
        CommonEntity common = CommonEntity.get((Entity)minecart);
        return common.hasControllerSupport() && common.getController() instanceof DefaultEntityController;
    }

    public static MinecartMember<?> convert(TrainCarts plugin, Minecart source) {
        if (plugin == null) {
            throw new IllegalArgumentException("TrainCarts plugin instance cannot be null");
        }
        if (source.isDead()) {
            return null;
        }
        CommonEntity entity = CommonEntity.get((Entity)source);
        if (entity.getController() instanceof MinecartMember) {
            MinecartMember member = (MinecartMember)entity.getController();
            member.updateUnloaded();
            return member;
        }
        if (!MinecartMemberStore.canConvert((Entity)source)) {
            return null;
        }
        MinecartMember<?> newController = MinecartMemberStore.createController(plugin, entity);
        if (newController == null) {
            return null;
        }
        entity.setController(newController);
        entity.setNetworkController(MinecartMemberStore.createNetworkController());
        newController.updateUnloaded();
        if (!newController.isUnloaded() && !plugin.getOfflineGroups().containsMinecart(entity.getUniqueId())) {
            newController.getGroup().getProperties().setDefault();
        }
        PaperRedstonePhysicsChecker.check(source.getWorld());
        return newController;
    }

    public static EntityNetworkController<?> createNetworkController() {
        return new MinecartMemberNetwork(TrainCarts.plugin);
    }

    public static MinecartMember<?> createController(TrainCarts plugin, EntityType entityType) {
        if (plugin == null) {
            throw new IllegalArgumentException("TrainCarts plugin cannot be null");
        }
        try {
            Class commonType = CommonEntityType.byEntityType((EntityType)entityType).commonType.getType();
            Function controllerConstr = (Function)controllers.get(commonType);
            if (controllerConstr != null) {
                return (MinecartMember)controllerConstr.apply(plugin);
            }
            return null;
        }
        catch (Throwable t) {
            plugin.handle(t);
            return null;
        }
    }

    public static MinecartMember<?> createController(TrainCarts plugin, CommonEntity<?> entity) {
        if (plugin == null) {
            throw new IllegalArgumentException("TrainCarts plugin cannot be null");
        }
        Function controllerConstr = (Function)controllers.get(entity);
        if (controllerConstr == null) {
            return null;
        }
        try {
            return (MinecartMember)controllerConstr.apply(plugin);
        }
        catch (Throwable t) {
            plugin.handle(t);
            return null;
        }
    }

    public static MinecartMember<?> spawnBy(TrainCarts plugin, Location at, Player player) {
        MinecartMember<?> spawned;
        ItemStack item = HumanHand.getItemInMainHand((HumanEntity)player);
        if (LogicUtil.nullOrEmpty((ItemStack)item)) {
            return null;
        }
        EntityType type = (EntityType)Conversion.toMinecartType.convert((Object)item.getType());
        if (type == null) {
            return null;
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemUtil.subtractAmount((ItemStack)item, (int)1);
            if (LogicUtil.nullOrEmpty((ItemStack)item)) {
                HumanHand.setItemInMainHand((HumanEntity)player, null);
            } else {
                HumanHand.setItemInMainHand((HumanEntity)player, (ItemStack)item);
            }
        }
        if ((spawned = MinecartMemberStore.spawn(plugin, at, type)) != null && !((CommonMinecart)spawned.getEntity()).isRemoved()) {
            spawned.getGroup().getProperties().setDefault(player);
            if (TCConfig.setOwnerOnPlacement) {
                spawned.getProperties().setOwner(player);
            }
            plugin.getPlayer(player).editMember(spawned);
        }
        return spawned;
    }

    public static MinecartMember<?> spawn(TrainCarts plugin, Location at, EntityType type) {
        return MinecartMemberStore.spawn(plugin, at, type, null);
    }

    public static MinecartMember<?> spawn(TrainCarts plugin, Location at, EntityType type, ConfigurationNode config) {
        return MinecartMemberStore.spawn(plugin, at, false, type, config);
    }

    public static MinecartMember<?> spawn(TrainCarts plugin, Location at, boolean flipped, EntityType type, ConfigurationNode config) {
        EntityNetworkController<?> networkController;
        boolean disableDefaultModel;
        MinecartMember<?> controller = MinecartMemberStore.createController(plugin, type);
        if (controller == null) {
            throw new IllegalArgumentException("No suitable MinecartMember type for " + type);
        }
        boolean bl = disableDefaultModel = config != null && config.isNode("model");
        if (disableDefaultModel) {
            controller.getAttachments().setHidden(true);
        }
        if (flipped) {
            at = Util.invertRotation(at.clone());
        }
        if ((networkController = MinecartMemberStore.createNetworkController()) instanceof MinecartMemberNetwork) {
            ((MinecartMemberNetwork)networkController).setInProcessOfSpawning(true);
        }
        CommonEntity.spawn((EntityType)type, (Location)at, controller, networkController);
        if (networkController instanceof MinecartMemberNetwork) {
            ((MinecartMemberNetwork)networkController).setInProcessOfSpawning(false);
        }
        controller.setDirectionForward(flipped);
        controller.updateDirection();
        MinecartMember<?> result = MemberSpawnEvent.call(controller).getMember();
        if (config != null) {
            controller.setUnloaded(true);
            controller.getProperties().load(config);
            ConfigurationNode dataNode = config.getNodeIfExists("data");
            if (dataNode != null) {
                controller.onTrainSpawned(dataNode);
            }
        }
        if (disableDefaultModel) {
            controller.getAttachments().setHidden(false);
        }
        controller.setUnloaded(false);
        PaperRedstonePhysicsChecker.check(at.getWorld());
        return result;
    }

    @ConverterMethod
    public static MinecartMember<?> getFromUID(UUID uuid) {
        for (World world : WorldUtil.getWorlds()) {
            MinecartMember<?> member;
            if (TrainCarts.isWorldDisabled(world) || (member = MinecartMemberStore.getFromEntity(EntityUtil.getEntity((World)world, (UUID)uuid))) == null || member.getEntity() == null) continue;
            return member;
        }
        return null;
    }

    @ConverterMethod
    public static MinecartMember<?> getFromEntity(Entity entity) {
        CommonEntity commonEntity;
        MinecartMember result;
        if (entity instanceof Minecart && (result = (MinecartMember)(commonEntity = CommonEntity.get((Entity)((Minecart)entity))).getController(MinecartMember.class)) != null && !result.isUnloaded()) {
            return result;
        }
        return null;
    }

    @Deprecated
    public static MinecartMember<?> getAt(Block block) {
        List<MinecartMember<?>> members = RailLookup.findMembersOnRail(OfflineBlock.of((Block)block));
        return members.isEmpty() ? null : members.get(0);
    }

    @Deprecated
    public static MinecartMember<?> getAt(World world, IntVector3 coord) {
        return MinecartMemberStore.getAt(BlockUtil.getBlock((World)world, (IntVector3)coord));
    }

    public static MinecartMember<?> getAt(Location at) {
        RailPiece piece = RailType.findRailPiece(at);
        if (piece == null) {
            return null;
        }
        List<MinecartMember<?>> members = piece.members();
        return members.isEmpty() ? null : members.get(0);
    }

    public static MinecartMember<?> getAt(Location at, MinecartGroup in) {
        return MinecartMemberStore.getAt(at, in, 0.999);
    }

    public static MinecartMember<?> getAt(Location at, MinecartGroup in, double searchRadius) {
        if (at == null || TrainCarts.isWorldDisabled(at.getWorld())) {
            return null;
        }
        MinecartMember<?> result = null;
        double distSquared = searchRadius * searchRadius;
        for (Entity e : WorldUtil.getNearbyEntities((Location)at, (double)searchRadius, (double)searchRadius, (double)searchRadius)) {
            MinecartMember<?> mm = MinecartMemberStore.getFromEntity(e);
            if (mm == null || in != null && mm.getGroup() != in || ((CommonMinecart)mm.getEntity()).loc.distanceSquared(at) > distSquared) continue;
            result = mm;
            if (!mm.isHeadingTo(at)) continue;
            return result;
        }
        return result;
    }

    public static MinecartMember<?> getFromHitTest(Location eyeLocation) {
        MinecartMember best = null;
        double best_dist = 4.5;
        block2: for (MinecartGroup group : MinecartGroupStore.getGroups().cloneAsIterable()) {
            if (group.getWorld() != eyeLocation.getWorld()) continue;
            for (int i = 0; i < group.size(); ++i) {
                double dist_hit;
                MinecartMember member;
                try {
                    member = (MinecartMember)group.get(i);
                }
                catch (IndexOutOfBoundsException ex) {
                    continue block2;
                }
                double max_rad = 2.0 * (double)((CommonMinecart)member.getEntity()).getWidth();
                double dist_sq = ((CommonMinecart)member.getEntity()).loc.distanceSquared(eyeLocation);
                if (dist_sq > max_rad * max_rad || (dist_hit = member.getHitBox().hitTest(eyeLocation)) >= best_dist) continue;
                best_dist = dist_hit;
                best = member;
            }
        }
        return best;
    }

    static {
        controllers.put(CommonMinecartRideable.class, MinecartMemberRideable::new);
        controllers.put(CommonMinecartFurnace.class, MinecartMemberFurnace::new);
        controllers.put(CommonMinecartChest.class, MinecartMemberChest::new);
        controllers.put(CommonMinecartHopper.class, MinecartMemberHopper::new);
        controllers.put(CommonMinecartTNT.class, MinecartMemberTNT::new);
        controllers.put(CommonMinecartMobSpawner.class, MinecartMemberMobSpawner::new);
        controllers.put(CommonMinecartCommandBlock.class, MinecartMemberCommandBlock::new);
    }
}

