/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.ExtendedEntity
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.CommonEntityController
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.inventory.MergedInventory
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet$LongIterator
 *  com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.inventory.MergedInventory;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchDirection;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTrackerGroup;
import com.bergerkiller.bukkit.tc.controller.components.AnimationController;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerGroup;
import com.bergerkiller.bukkit.tc.controller.components.ObstacleTracker;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.controller.components.RailTrackerGroup;
import com.bergerkiller.bukkit.tc.controller.components.SignTracker;
import com.bergerkiller.bukkit.tc.controller.components.SignTrackerGroup;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatusProvider;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.events.GroupRemoveEvent;
import com.bergerkiller.bukkit.tc.events.GroupUnloadEvent;
import com.bergerkiller.bukkit.tc.events.MemberAddEvent;
import com.bergerkiller.bukkit.tc.events.MemberBlockChangeEvent;
import com.bergerkiller.bukkit.tc.events.MemberRemoveEvent;
import com.bergerkiller.bukkit.tc.exception.GroupUnloadedException;
import com.bergerkiller.bukkit.tc.exception.MemberMissingException;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupManager;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.SaveLockOrientationMode;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.CartLockOrientation;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import com.bergerkiller.bukkit.tc.utils.ChunkArea;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

public class MinecartGroup
extends MinecartGroupStore
implements IPropertiesHolder,
AnimationController,
TrainStatusProvider,
TrainCarts.Provider {
    private static final long serialVersionUID = 3L;
    private static final LongHashSet chunksBuffer = new LongHashSet(50);
    private final TrainCarts traincarts;
    protected final ChunkArea chunkArea = new ChunkArea();
    private boolean chunkAreaValid = false;
    private final SignTrackerGroup signTracker = new SignTrackerGroup(this);
    private final RailTrackerGroup railTracker = new RailTrackerGroup(this);
    private final ActionTrackerGroup actionTracker = new ActionTrackerGroup(this);
    private final ObstacleTracker obstacleTracker = new ObstacleTracker(this);
    private final AttachmentControllerGroup attachmentController = new AttachmentControllerGroup(this);
    protected long lastSync = Long.MIN_VALUE;
    private TrainProperties prop = null;
    private boolean breakPhysics = false;
    private int teleportImmunityTick = 0;
    private double updateSpeedFactor = 1.0;
    private int updateStepCount = 1;
    private int updateStepNr = 1;
    private boolean unloaded = false;

    protected MinecartGroup(TrainCarts traincarts) {
        this.traincarts = traincarts;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    @Override
    public TrainProperties getProperties() {
        if (this.prop == null) {
            if (this.isUnloaded()) {
                throw new IllegalStateException("Group is unloaded");
            }
            this.prop = TrainPropertiesStore.create();
            for (MinecartMember<?> member : this) {
                this.prop.add(member.getProperties());
            }
            TrainPropertiesStore.bindGroupToProperties(this.prop, this);
        }
        return this.prop;
    }

    public void setProperties(TrainProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Can not set properties to null");
        }
        if (this.isUnloaded()) {
            throw new IllegalStateException("Group is unloaded");
        }
        if (this.prop == properties) {
            return;
        }
        if (this.prop != null) {
            TrainPropertiesStore.remove(this.prop.getTrainName());
            TrainPropertiesStore.unbindGroupFromProperties(this.prop, this);
        }
        this.prop = properties;
        TrainPropertiesStore.bindGroupToProperties(this.prop, this);
    }

    public ConfigurationNode exportConfig() {
        ConfigurationNode exportedConfig = this.saveConfig();
        exportedConfig.remove("claims");
        exportedConfig.set("usedModels", (Object)this.getAttachments().getUsedModelsAsExport());
        return exportedConfig;
    }

    public ConfigurationNode saveConfig() {
        return this.saveConfig(SaveLockOrientationMode.AUTOMATIC);
    }

    public ConfigurationNode saveConfig(SaveLockOrientationMode setSaveLockMode) {
        ConfigurationNode savedConfig = this.getProperties().saveToConfig().clone();
        savedConfig.remove("carts");
        List carts = this.stream().map(MinecartMember::saveConfig).collect(Collectors.toCollection(ArrayList::new));
        if (setSaveLockMode == SaveLockOrientationMode.DISABLED) {
            for (ConfigurationNode cart : carts) {
                StandardProperties.LOCK_ORIENTATION_FLIPPED.writeToConfig(cart, Optional.empty());
            }
        } else if (setSaveLockMode == SaveLockOrientationMode.ENABLED_OVERRIDE) {
            for (ConfigurationNode cart : carts) {
                StandardProperties.LOCK_ORIENTATION_FLIPPED.writeToConfig(cart, Optional.of(CartLockOrientation.locked((Boolean)cart.get("flipped", (Object)false))));
            }
        } else if (setSaveLockMode == SaveLockOrientationMode.ENABLED || setSaveLockMode == SaveLockOrientationMode.AUTOMATIC && this.isSavedTrainOrientationLocked()) {
            int trainFlippedCounter = 0;
            for (ConfigurationNode cart : carts) {
                CartLockOrientation ori = StandardProperties.LOCK_ORIENTATION_FLIPPED.readFromConfig(cart).orElse(CartLockOrientation.NONE);
                if (ori == CartLockOrientation.NONE) continue;
                if (ori.isFlipped() == ((Boolean)cart.get("flipped", (Object)false)).booleanValue()) {
                    --trainFlippedCounter;
                    continue;
                }
                ++trainFlippedCounter;
            }
            if (trainFlippedCounter > 0) {
                carts.forEach(StandardProperties::reverseSavedCart);
                Collections.reverse(carts);
            }
            for (ConfigurationNode cart : carts) {
                StandardProperties.LOCK_ORIENTATION_FLIPPED.writeToConfig(cart, Optional.of(CartLockOrientation.locked((Boolean)cart.get("flipped", (Object)false))));
            }
        }
        savedConfig.setNodeList("carts", carts);
        return savedConfig;
    }

    public boolean isSavedTrainOrientationLocked() {
        for (MinecartMember<?> member : this) {
            if (member.getProperties().get(StandardProperties.LOCK_ORIENTATION_FLIPPED) == CartLockOrientation.NONE) continue;
            return true;
        }
        return false;
    }

    public SignTrackerGroup getSignTracker() {
        return this.signTracker;
    }

    public ActionTrackerGroup getActions() {
        return this.actionTracker;
    }

    public RailTrackerGroup getRailTracker() {
        return this.railTracker;
    }

    public AttachmentControllerGroup getAttachments() {
        return this.attachmentController;
    }

    public MinecartMember<?> head(int index) {
        return (MinecartMember)this.get(index);
    }

    public MinecartMember<?> head() {
        return this.head(0);
    }

    public MinecartMember<?> tail(int index) {
        return (MinecartMember)this.get(this.size() - 1 - index);
    }

    public MinecartMember<?> tail() {
        return this.tail(0);
    }

    public MinecartMember<?> middle() {
        return (MinecartMember)this.get((int)Math.floor((double)this.size() / 2.0));
    }

    @Override
    public Iterator<MinecartMember<?>> iterator() {
        final Iterator listIter = super.iterator();
        return new Iterator<MinecartMember<?>>(){
            final /* synthetic */ MinecartGroup this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean hasNext() {
                return listIter.hasNext();
            }

            @Override
            public MinecartMember<?> next() {
                try {
                    return (MinecartMember)listIter.next();
                }
                catch (ConcurrentModificationException ex) {
                    throw new MemberMissingException();
                }
            }

            @Override
            public void remove() {
                listIter.remove();
            }
        };
    }

    public MinecartMember<?>[] toArray() {
        return super.toArray(new MinecartMember[0]);
    }

    public boolean connect(MinecartMember<?> contained, MinecartMember<?> with) {
        if (this.size() <= 1) {
            this.add(with);
        } else if (this.head() == contained && this.canConnect(with, 0)) {
            this.add(0, with);
        } else if (this.tail() == contained && this.canConnect(with, this.size() - 1)) {
            this.add(with);
        } else {
            return false;
        }
        return true;
    }

    public boolean containsIndex(int index) {
        return !this.isEmpty() && index >= 0 && index < this.size();
    }

    @Override
    public World getWorld() {
        return this.isEmpty() ? null : ((MinecartMember)this.get(0)).getWorld();
    }

    public int size(EntityType carttype) {
        int rval = 0;
        for (MinecartMember<?> mm : this) {
            if (((CommonMinecart)mm.getEntity()).getType() != carttype) continue;
            ++rval;
        }
        return rval;
    }

    public boolean isValid() {
        return !this.isEmpty() && (this.size() == 1 || !this.getProperties().isPoweredMinecartRequired() || this.size(EntityType.MINECART_FURNACE) > 0);
    }

    @Override
    public void add(int index, MinecartMember<?> member) {
        if (member.isUnloaded()) {
            throw new IllegalArgumentException("Can not add unloaded members to groups");
        }
        super.add(index, member);
        this.fireMemberAddEvent(member);
        this.onMemberAdded(member);
    }

    @Override
    public boolean add(MinecartMember<?> member) {
        if (member.isUnloaded()) {
            throw new IllegalArgumentException("Can not add unloaded members to groups");
        }
        super.add(member);
        this.fireMemberAddEvent(member);
        this.onMemberAdded(member);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends MinecartMember<?>> members) {
        MinecartMember[] memberArr;
        super.addAll(index, members);
        for (MinecartMember m : memberArr = members.toArray(new MinecartMember[0])) {
            if (m.isUnloaded()) {
                throw new IllegalArgumentException("Can not add unloaded members to groups");
            }
            this.fireMemberAddEvent(m);
        }
        for (MinecartMember member : memberArr) {
            this.onMemberAdded(member);
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends MinecartMember<?>> members) {
        MinecartMember[] memberArr;
        super.addAll(members);
        for (MinecartMember m : memberArr = members.toArray(new MinecartMember[0])) {
            if (m.isUnloaded()) {
                throw new IllegalArgumentException("Can not add unloaded members to groups");
            }
            this.fireMemberAddEvent(m);
        }
        for (MinecartMember member : memberArr) {
            this.onMemberAdded(member);
        }
        return true;
    }

    public boolean removeSilent(MinecartMember<?> member) {
        int index = this.indexOf(member);
        if (index == -1) {
            return false;
        }
        this.removeMember(index);
        if (this.isEmpty()) {
            this.remove();
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = this.indexOf(o);
        return index != -1 && this.remove(index) != null;
    }

    @Override
    public MinecartMember<?> remove(int index) {
        MinecartMember<?> removed = this.removeMember(index);
        if (this.isEmpty()) {
            this.remove();
        } else {
            if (TCConfig.playHissWhenCartRemoved) {
                removed.playLinkEffect();
            }
            this.split(index);
        }
        return removed;
    }

    private MinecartMember<?> removeMember(int index) {
        this.chunkAreaValid = false;
        MinecartGroup.notifyPhysicsChange();
        MinecartMember member = (MinecartMember)super.get(index);
        MemberRemoveEvent.call(member);
        super.remove(index);
        this.getActions().removeActions(member);
        this.onMemberRemoved(member);
        member.group = null;
        return member;
    }

    private void onCompositionChanged() {
        this.chunkAreaValid = false;
        this.attachmentController.notifyGroupCompositionChanged();
    }

    private void fireMemberAddEvent(MinecartMember<?> member) {
        boolean wasGroupNull = false;
        if (member.group == null) {
            member.group = this;
            wasGroupNull = true;
        }
        CommonUtil.callEvent((Event)new MemberAddEvent(member, this));
        if (wasGroupNull && member.group == this) {
            member.group = null;
        }
    }

    private void onMemberAdded(MinecartMember<?> member) {
        this.onCompositionChanged();
        MinecartGroup.notifyPhysicsChange();
        member.setGroup(this);
        this.getSignTracker().updatePosition();
        this.getProperties().add(member.getProperties());
    }

    private void onMemberRemoved(MinecartMember<?> member) {
        this.onCompositionChanged();
        this.getSignTracker().onMemberRemoved(member);
        this.getProperties().remove(member.getProperties());
        this.getRailTracker().removeMemberRails(member);
        RailLookup.removeMemberFromAll(member);
    }

    public MinecartGroup split(int at) {
        Util.checkMainThread("MinecartGroup::split()");
        if (at <= 0) {
            return this;
        }
        if (at >= this.size()) {
            return null;
        }
        ArrayList splitMembers = new ArrayList();
        int count = this.size();
        for (int i = at; i < count; ++i) {
            splitMembers.add(this.removeMember(this.size() - 1));
        }
        MinecartGroup gnew = MinecartGroupStore.createSplitFrom(this.getProperties(), splitMembers.toArray(new MinecartMember[0]));
        if (!this.isValid()) {
            this.remove();
        } else {
            this.onGroupCreated();
        }
        return gnew;
    }

    @Override
    public void clear() {
        this.unregisterFromServer(false);
        TrainProperties properties = this.getProperties();
        for (MinecartMember<?> mm : this.toArray()) {
            properties.remove(mm.getProperties());
            if (((CommonMinecart)mm.getEntity()).isRemoved()) {
                mm.onDie(true);
                continue;
            }
            mm.group = null;
            mm.group = MinecartGroupStore.createSplitFrom(properties, mm);
        }
        super.clear();
    }

    public void remove() {
        Util.checkMainThread("MinecartGroup::remove()");
        if (!groups.remove((Object)this)) {
            return;
        }
        GroupRemoveEvent.call(this);
        this.clear();
        if (this.prop != null) {
            TrainPropertiesStore.remove(this.prop.getTrainName());
            TrainPropertiesStore.unbindGroupFromProperties(this.prop, this);
            this.prop = null;
        }
    }

    public void destroy() {
        ArrayList copy = new ArrayList(this);
        for (MinecartMember mm : copy) {
            ((CommonMinecart)mm.getEntity()).remove();
        }
        this.remove();
    }

    public boolean isUnloaded() {
        return this.unloaded;
    }

    public void unload() {
        if (this.unloaded) {
            return;
        }
        Util.checkMainThread("MinecartGroup::unload()");
        this.unloaded = true;
        try {
            for (MinecartMember<?> member : this) {
                member.group = this;
                member.setUnloaded(false);
            }
            GroupUnloadEvent.call(this);
            OfflineGroup offlineGroup = OfflineGroupManager.saveGroup(this);
            this.unregisterFromServer(true);
            if (offlineGroup != null) {
                this.traincarts.getOfflineGroups().storeGroup(offlineGroup);
            }
            this.stop(false);
        }
        finally {
            groups.remove((Object)this);
        }
        for (MinecartMember<?> member : this) {
            member.group = null;
            member.unloadedLastPlayerTakable = this.getProperties().isPlayerTakeable();
            member.setUnloaded(true);
            ((CommonMinecart)member.getEntity()).doPostTick();
        }
        super.clear();
        if (this.prop != null) {
            TrainPropertiesStore.unbindGroupFromProperties(this.prop, this);
        }
        this.prop = null;
    }

    private void unregisterFromServer(boolean unloaded) {
        this.getSignTracker().unload(unloaded ? SignTracker.ClearMode.UNLOAD : SignTracker.ClearMode.LEAVE);
        this.getRailTracker().unload();
        this.getActions().clear();
        MutexZoneCache.unloadGroupInSlots(this);
        this.chunkArea.reset();
        this.chunkAreaValid = false;
        this.onCompositionChanged();
    }

    public void respawn() {
        for (MinecartMember<?> mm : this) {
            mm.respawn();
        }
    }

    public void playLinkEffect() {
        for (MinecartMember<?> mm : this) {
            mm.playLinkEffect();
        }
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(boolean cancelLocationChange) {
        for (MinecartMember<?> m : this) {
            m.stop(cancelLocationChange);
        }
    }

    public void limitSpeed() {
        for (MinecartMember<?> mm : this) {
            mm.limitSpeed();
        }
    }

    public void eject() {
        for (MinecartMember<?> mm : this) {
            mm.eject();
        }
    }

    public void teleportAndGo(Block railBlock, BlockFace direction) {
        double force = this.getAverageForce();
        this.teleport(railBlock, direction);
        this.stop();
        this.getActions().clear();
        if (Math.abs(force) > 0.01) {
            this.tail().getActions().addActionLaunch(direction, 1.0, force);
        }
    }

    public void teleportAndGo(Block railBlock, Vector direction) {
        double forwardVelocity = this.getAverageForce();
        this.teleport(railBlock, direction);
        this.stop();
        this.getActions().clear();
        if (Math.abs(forwardVelocity) > 0.01) {
            MemberActionLaunchDirection action = new MemberActionLaunchDirection();
            action.initDistance(1.0, forwardVelocity, direction);
            this.tail().getActions().addGroupAction(action);
        }
    }

    public void teleport(Block startRailBlock, BlockFace direction) {
        this.teleport(startRailBlock, FaceUtil.faceToVector((BlockFace)direction));
    }

    public void teleport(Block startRailBlock, Vector direction) {
        Location[] locations = new Location[this.size()];
        TrackWalkingPoint walker = new TrackWalkingPoint(startRailBlock, direction);
        walker.skipFirst();
        for (int i = 0; i < locations.length; ++i) {
            boolean canMove = i == 0 ? walker.move(0.0) : walker.move(((MinecartMember)this.get(i - 1)).getPreferredDistance((MinecartMember)this.get(i)));
            if (canMove) {
                locations[i] = walker.state.positionLocation();
                continue;
            }
            if (i > 0) {
                locations[i] = locations[i - 1].clone();
                continue;
            }
            return;
        }
        this.teleport(locations, true);
    }

    public void teleport(Location[] locations) {
        this.teleport(locations, false);
    }

    public void teleport(Location[] locations, boolean reversed) {
        int i;
        if (LogicUtil.nullOrEmpty((Object[])locations) || locations.length != this.size()) {
            return;
        }
        this.teleportImmunityTick = 10;
        this.getSignTracker().clear();
        this.getSignTracker().updatePosition();
        this.breakPhysics();
        for (MinecartMember<?> member : this) {
            member.getAttachments().startTeleport();
        }
        locations = (Location[])locations.clone();
        for (i = 0; i < this.size(); ++i) {
            if (!((MinecartMember)this.get(i)).isOrientationInverted()) continue;
            int locIndx = reversed ? locations.length - i - 1 : i;
            locations[locIndx] = Util.invertRotation(locations[locIndx].clone());
        }
        if (reversed) {
            for (i = 0; i < locations.length; ++i) {
                this.teleportMember((MinecartMember)this.get(i), locations[locations.length - i - 1]);
            }
        } else {
            for (i = 0; i < locations.length; ++i) {
                this.teleportMember((MinecartMember)this.get(i), locations[i]);
            }
        }
        this.updateDirection();
        this.updateChunkInformation(!this.canUnload(), false);
        this.updateWheels();
        this.getSignTracker().updatePosition();
        for (MinecartMember<?> member : this) {
            member.getAttachments().finishTeleport();
        }
    }

    private void teleportMember(MinecartMember<?> member, Location location) {
        member.getWheels().startTeleport();
        ((CommonMinecart)member.getEntity()).teleport(location);
        member.getOrientation();
    }

    public void flipOrientation() {
        RailState current;
        if (this.isEmpty()) {
            return;
        }
        if (this.size() == 1) {
            this.head().flipOrientation();
            return;
        }
        double shiftDistance = 0.5 * ((double)((CommonMinecart)this.tail().getEntity()).getWidth() - (double)((CommonMinecart)this.head().getEntity()).getWidth());
        FlippedMember currentMember = null;
        boolean areAllCartsReachable = true;
        for (int i = this.size() - 1; i >= 1; --i) {
            double distance = this.head(i).calculateRailDistanceToMemberAhead(this.head(i - 1));
            if (Double.isNaN(distance)) {
                distance = this.head(i).getPreferredDistance(this.head(i - 1));
                areAllCartsReachable = false;
            }
            FlippedMember next = new FlippedMember(this.head(i), distance);
            next.next = currentMember;
            currentMember = next;
        }
        FlippedMember next = new FlippedMember(this.head(), Math.max(0.0, -shiftDistance));
        next.next = currentMember;
        FlippedMember rootMember = currentMember = next;
        if (areAllCartsReachable) {
            RailTracker.TrackedRailWalker walker = this.tail().getRailTracker().getTrackedRailWalker();
            if (shiftDistance > 0.0) {
                walker.invertMotion();
                shiftDistance -= walker.move(shiftDistance);
                walker.invertMotion();
            }
            if (shiftDistance > 0.0) {
                walker.invertMotion();
                walker.state().initEnterDirection();
                TrackWalkingPoint p = new TrackWalkingPoint(walker.state());
                p.skipFirst();
                p.move(shiftDistance);
                current = p.state;
                current.position().invertMotion();
                current.initEnterDirection();
            } else {
                do {
                    currentMember.distanceRemaining -= walker.move(currentMember.distanceRemaining);
                    if (!(currentMember.distanceRemaining <= 0.0)) break;
                    currentMember.flippedState = walker.state().clone();
                    currentMember.flippedState.initEnterDirection();
                } while ((currentMember = currentMember.next) != null);
                current = walker.state();
                current.initEnterDirection();
            }
        } else {
            RailTracker.TrackedRail currentRail = null;
            for (int i = this.size() - 1; i >= 0; --i) {
                MinecartMember member = (MinecartMember)this.get(i);
                if (member.isDerailed()) continue;
                currentRail = member.getRailTracker().getRail();
                break;
            }
            if (currentRail == null) {
                this.flipOrientationFallback();
                return;
            }
            current = currentRail.state.clone();
            current.initEnterDirection();
        }
        if (currentMember != null) {
            TrackWalkingPoint p = new TrackWalkingPoint(current);
            p.skipFirst();
            do {
                if (!p.move(currentMember.distanceRemaining)) {
                    this.flipOrientationFallback();
                    return;
                }
                currentMember.flippedState = p.state.clone();
            } while ((currentMember = currentMember.next) != null);
        }
        this.applyFlippedStates(rootMember);
    }

    private void flipOrientationFallback() {
        FlippedMember current = null;
        for (int i = 0; i < this.size(); ++i) {
            MinecartMember<?> swapped;
            MinecartMember<?> member = this.head(i);
            if (member == (swapped = this.tail(i))) continue;
            FlippedMember flipped = new FlippedMember(member, 0.0);
            flipped.flippedState = swapped.getRailTracker().getState().clone();
            flipped.next = current;
            current = flipped;
        }
        this.applyFlippedStates(current);
    }

    private void applyFlippedStates(FlippedMember rootMember) {
        FlippedMember currentMember = rootMember;
        while (currentMember != null) {
            currentMember.apply();
            currentMember = currentMember.next;
        }
        this.updateDirection();
        this.updateWheels();
        this.getAttachments().syncRespawn();
    }

    public boolean isTeleportImmune() {
        return this.teleportImmunityTick > 0;
    }

    public void shareForce() {
        double f = this.getAverageForce();
        for (MinecartMember<?> m : this) {
            m.setForwardForce(f);
        }
    }

    public void setForwardForce(double force) {
        for (MinecartMember<?> mm : this) {
            double currvel = mm.getForce();
            if (currvel <= 0.01 || Math.abs(force) < 0.01) {
                mm.setForwardForce(force);
                continue;
            }
            ((CommonMinecart)mm.getEntity()).vel.multiply(force / currvel);
        }
    }

    @Override
    public List<String> getAnimationNames() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.size() == 1) {
            return ((MinecartMember)this.get(0)).getAnimationNames();
        }
        return Collections.unmodifiableList(this.stream().flatMap(m -> m.getAnimationNames().stream()).distinct().collect(Collectors.toList()));
    }

    public Set<String> getAnimationScenes(String animationName) {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        if (this.size() == 1) {
            return ((MinecartMember)this.get(0)).getAnimationScenes(animationName);
        }
        return Collections.unmodifiableSet(this.stream().flatMap(m -> m.getAnimationScenes(animationName).stream()).collect(Collectors.toSet()));
    }

    @Override
    public boolean playNamedAnimation(String name) {
        return AnimationController.super.playNamedAnimation(name);
    }

    @Override
    public boolean playNamedAnimation(AnimationOptions options) {
        boolean success = false;
        for (MinecartMember<?> member : this) {
            success |= member.playNamedAnimation(options);
        }
        return success;
    }

    @Override
    public boolean playNamedAnimationFor(int[] targetPath, AnimationOptions options) {
        boolean success = false;
        for (MinecartMember<?> member : this) {
            success |= member.playNamedAnimationFor(targetPath, options);
        }
        return success;
    }

    @Override
    public boolean playAnimationFor(int[] targetPath, Animation animation) {
        boolean success = false;
        for (MinecartMember<?> member : this) {
            success |= member.playAnimationFor(targetPath, animation);
        }
        return success;
    }

    public boolean canConnect(MinecartMember<?> mm, int at) {
        CommonMinecart otherEnd;
        CommonMinecart connectedEnd;
        if (this.size() == 1) {
            return true;
        }
        if (this.size() == 0) {
            return false;
        }
        if (at == 0) {
            if (!this.head().isNearOf(mm)) {
                return false;
            }
            connectedEnd = (CommonMinecart)this.head().getEntity();
            otherEnd = (CommonMinecart)this.tail().getEntity();
        } else if (at == this.size() - 1) {
            if (!this.tail().isNearOf(mm)) {
                return false;
            }
            connectedEnd = (CommonMinecart)this.tail().getEntity();
            otherEnd = (CommonMinecart)this.head().getEntity();
        } else {
            return false;
        }
        return connectedEnd.loc.distanceSquared(mm.getEntity()) < otherEnd.loc.distanceSquared(mm.getEntity());
    }

    private void refreshRailTrackerIfChanged() {
        for (MinecartMember<?> member : this) {
            hasPhysicsChanges |= member.railDetectPositionChange();
        }
        if (hasPhysicsChanges) {
            hasPhysicsChanges = false;
            this.getRailTracker().refresh();
        }
    }

    public void updateDirection() {
        if (this.size() == 1) {
            this.refreshRailTrackerIfChanged();
            this.head().updateDirection();
        } else if (this.size() > 1) {
            int reverseCtr = 0;
            while (true) {
                this.refreshRailTrackerIfChanged();
                for (MinecartMember<?> member : this) {
                    member.updateDirection();
                }
                if (reverseCtr++ == 2) break;
                double fforce = 0.0;
                for (MinecartMember<?> m : this) {
                    VectorAbstract vel = ((CommonMinecart)m.getEntity()).vel;
                    fforce += m.getRailTracker().getState().position().motDot(vel.getX(), vel.getY(), vel.getZ());
                }
                if (fforce >= 0.0) break;
                this.reverseDataStructures();
                MinecartGroup.notifyPhysicsChange();
            }
        }
    }

    public void reverse() {
        for (MinecartMember<?> mm : this) {
            mm.reverseDirection();
        }
        this.reverseDataStructures();
        MinecartGroup.notifyPhysicsChange();
        this.updateDirection();
    }

    private void reverseDataStructures() {
        Collections.reverse(this);
        this.getRailTracker().reverseRailData();
    }

    private void updateWheels() {
        for (MinecartMember<?> member : this) {
            member.getWheels().update();
        }
    }

    public double getAverageForce() {
        if (this.isEmpty()) {
            return 0.0;
        }
        if (this.size() == 1) {
            return ((MinecartMember)this.get(0)).getForce();
        }
        double force = 0.0;
        for (MinecartMember<?> m : this) {
            force += m.getForwardForce();
        }
        return force / (double)this.size();
    }

    public List<Material> getTypes() {
        ArrayList<Material> types = new ArrayList<Material>(this.size());
        for (MinecartMember<?> mm : this) {
            types.add(((CommonMinecart)mm.getEntity()).getCombinedItem());
        }
        return types;
    }

    public boolean hasPassenger() {
        for (MinecartMember<?> mm : this) {
            if (!((CommonMinecart)mm.getEntity()).hasPassenger()) continue;
            return true;
        }
        return false;
    }

    public boolean hasPlayerPassenger() {
        for (MinecartMember<?> mm : this) {
            CommonEntity entity = mm.getEntity();
            if (entity == null || !entity.hasPlayerPassenger()) continue;
            return true;
        }
        return false;
    }

    public boolean hasFuel() {
        for (MinecartMember<?> mm : this) {
            if (!(mm instanceof MinecartMemberFurnace) || !((CommonMinecartFurnace)((MinecartMemberFurnace)mm).getEntity()).hasFuel()) continue;
            return true;
        }
        return false;
    }

    public boolean hasItems() {
        for (MinecartMember<?> mm : this) {
            if (!(mm instanceof MinecartMemberChest) || !((MinecartMemberChest)mm).hasItems()) continue;
            return true;
        }
        return false;
    }

    public boolean hasItem(ItemParser item) {
        for (MinecartMember<?> mm : this) {
            if (!(mm instanceof MinecartMemberChest) || !((MinecartMemberChest)mm).hasItem(item)) continue;
            return true;
        }
        return false;
    }

    public boolean isMoving() {
        return !this.isEmpty() && this.head().isMoving();
    }

    public boolean isMovingOrWaiting() {
        return this.isMoving() || this.getActions().isWaitAction();
    }

    public boolean canUnload() {
        if (this.getProperties().isKeepingChunksLoaded() && (!TCConfig.keepChunksLoadedOnlyWhenMoving || this.isMovingOrWaiting())) {
            return false;
        }
        if (this.hasPlayerPassenger()) {
            return false;
        }
        return !this.isTeleportImmune();
    }

    public boolean isRemoved() {
        return !groups.contains((Object)this);
    }

    public Inventory getInventory() {
        Inventory[] source = (Inventory[])this.stream().map(CommonEntityController::getEntity).map(ExtendedEntity::getEntity).filter(e -> e instanceof InventoryHolder).map(e -> ((InventoryHolder)e).getInventory()).toArray(Inventory[]::new);
        return new MergedInventory(source);
    }

    public Inventory getPlayerInventory() {
        Inventory[] source = (Inventory[])this.stream().flatMap(m -> ((CommonMinecart)m.getEntity()).getPlayerPassengers().stream()).map(HumanEntity::getInventory).toArray(Inventory[]::new);
        return new MergedInventory(source);
    }

    @Deprecated
    public void keepChunksLoaded(boolean keepLoaded) {
        this.keepChunksLoaded(keepLoaded ? ChunkLoadOptions.Mode.FULL : ChunkLoadOptions.Mode.DISABLED);
    }

    public void keepChunksLoaded(ChunkLoadOptions.Mode mode) {
        for (ChunkArea.OwnedChunk chunk : this.chunkArea.getAll()) {
            chunk.keepLoaded(mode);
        }
    }

    public ChunkArea getChunkArea() {
        return this.chunkArea;
    }

    public boolean isInChunk(World world, long chunkLongCoord) {
        if (this.getWorld() != world) {
            return false;
        }
        if (this.chunkAreaValid) {
            return this.chunkArea.containsChunk(chunkLongCoord);
        }
        int center_chunkX = MathUtil.longHashMsw((long)chunkLongCoord);
        int center_chunkZ = MathUtil.longHashLsw((long)chunkLongCoord);
        LongHashSet.LongIterator chunkIter = this.loadChunksBuffer().longIterator();
        while (chunkIter.hasNext()) {
            long chunk = chunkIter.next();
            if (Math.abs(MathUtil.longHashMsw((long)chunk) - center_chunkX) > 2 || Math.abs(MathUtil.longHashLsw((long)chunk) - center_chunkZ) > 2) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onPropertiesChanged() {
        this.getSignTracker().update();
        for (MinecartMember<?> member : this.toArray()) {
            member.onPropertiesChanged();
        }
    }

    public int getTicksLived() {
        int ticksLived = 0;
        for (MinecartMember<?> member : this) {
            ticksLived = Math.max(ticksLived, ((CommonMinecart)member.getEntity()).getTicksLived());
        }
        return ticksLived;
    }

    public double getUpdateSpeedFactor() {
        return this.updateSpeedFactor;
    }

    public int getUpdateStepCount() {
        return this.updateStepCount;
    }

    public boolean isFirstUpdateStep() {
        return this.updateStepNr == 1;
    }

    public boolean isLastUpdateStep() {
        return this.updateStepNr == this.updateStepCount;
    }

    public void breakPhysics() {
        this.breakPhysics = true;
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        ArrayList<TrainStatus> info = new ArrayList<TrainStatus>(3);
        info.addAll(this.getActions().getStatusInfo());
        for (MinecartMember<?> member : this) {
            info.addAll(member.getActions().getStatusInfo());
        }
        info.addAll(this.obstacleTracker.getStatusInfo());
        for (MinecartMember<?> member : this) {
            if (!member.isDerailed()) continue;
            info.add(new TrainStatus.Derailed());
            break;
        }
        if (this.getProperties().getSpeedLimit() <= 1.0E-5) {
            info.add(new TrainStatus.WaitingZeroSpeedLimit());
        } else if (((CommonMinecart)this.head().getEntity()).getMaxSpeed() <= 1.0E-5) {
            info.add(new TrainStatus.NotMovingSpeedLimited());
        } else {
            double speed = this.head().getRealSpeedLimited();
            if (speed <= 1.0E-5) {
                info.add(new TrainStatus.NotMoving());
            } else {
                info.add(new TrainStatus.Moving(speed));
            }
        }
        if (this.getProperties().isKeepingChunksLoaded()) {
            info.add(new TrainStatus.KeepingChunksLoaded());
        }
        return info;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }

    public MinecartMember<?> getAt(IntVector3 position) {
        return this.getRailTracker().getMemberFromRails(position);
    }

    private boolean doConnectionCheck() {
        int i;
        for (i = 0; i < this.size() - 1; ++i) {
            if (!((MinecartMember)this.get(i)).getRailTracker().isTrainSplit()) continue;
            for (int j = i + 1; j < this.size(); ++j) {
                ((CommonMinecart)((MinecartMember)this.get((int)j)).getEntity()).vel.divide(this.updateSpeedFactor);
            }
            MinecartGroup gnew = this.split(i + 1);
            if (gnew != null) {
                int time = (int)MathUtil.clamp((double)(2.0 / gnew.head().getRealSpeed()), (double)20.0, (double)40.0);
                for (MinecartMember<?> mm1 : gnew) {
                    for (MinecartMember<?> mm2 : this) {
                        mm1.ignoreCollision(((CommonMinecart)mm2.getEntity()).getEntity(), time);
                    }
                }
            }
            return false;
        }
        for (i = 0; i < this.size() - 1; ++i) {
            MinecartMember m1 = (MinecartMember)this.get(i);
            MinecartMember m2 = (MinecartMember)this.get(i + 1);
            if (!m1.isDerailed() && !m2.isDerailed() || !(((CommonMinecart)m1.getEntity()).loc.distance((VectorAbstract)((CommonMinecart)m2.getEntity()).loc) >= m1.getMaximumDistance(m2))) continue;
            this.split(i + 1);
            return false;
        }
        return true;
    }

    private LongHashSet loadChunksBuffer() {
        chunksBuffer.clear();
        for (MinecartMember<?> mm : this) {
            chunksBuffer.add(((CommonMinecart)mm.getEntity()).loc.x.chunk(), ((CommonMinecart)mm.getEntity()).loc.z.chunk());
        }
        return chunksBuffer;
    }

    public void onGroupCreated() {
        this.onPropertiesChanged();
        if (this.getProperties().isKeepingChunksLoaded()) {
            this.updateChunkInformation(true, false);
        }
    }

    private void updateChunkInformation(boolean keepChunksLoaded, boolean isRemoving) {
        block8: {
            block7: {
                int radius;
                ChunkLoadOptions options;
                if (!keepChunksLoaded) {
                    options = ChunkLoadOptions.DEFAULT;
                    radius = 2;
                } else if (this.getProperties().isKeepingChunksLoaded()) {
                    options = this.getProperties().getChunkLoadOptions();
                    radius = Math.min(TCConfig.maxKeepChunksLoadedRadius, options.radius());
                } else {
                    options = ChunkLoadOptions.of(ChunkLoadOptions.Mode.MINIMAL, 0);
                    radius = 0;
                }
                this.chunkArea.refresh(this.getWorld(), radius, this.loadChunksBuffer());
                this.chunkAreaValid = true;
                if (!keepChunksLoaded) break block7;
                for (ChunkArea.OwnedChunk chunk : this.chunkArea.getAdded()) {
                    chunk.keepLoaded(options.mode());
                }
                for (ChunkArea.OwnedChunk chunk : this.chunkArea.getAll()) {
                    if (chunk.getDistance() > 1 || chunk.getPreviousDistance() <= 1) continue;
                    chunk.loadChunk();
                }
                break block8;
            }
            if (isRemoving) break block8;
            for (ChunkArea.OwnedChunk chunk : this.chunkArea.getAdded()) {
                if (chunk.isLoaded()) continue;
                this.unload();
                throw new GroupUnloadedException();
            }
        }
    }

    public void logCartInfo(String header) {
        StringBuilder msg = new StringBuilder(this.size() * 7 + 10);
        msg.append(header);
        for (MinecartMember<?> member : this) {
            msg.append(" [");
            msg.append(member.getDirection());
            msg.append(" - ").append(((CommonMinecart)member.getEntity()).vel);
            msg.append("]");
        }
        this.traincarts.log(Level.INFO, msg.toString());
    }

    public ObstacleTracker getObstacleTracker() {
        return this.obstacleTracker;
    }

    public List<ObstacleTracker.Obstacle> findObstaclesAhead(double distance, boolean trains, boolean railObstacles) {
        return this.obstacleTracker.findObstaclesAhead(distance, trains, railObstacles, 0.0);
    }

    public boolean isObstacleAhead(double distance, boolean trains, boolean railObstacles) {
        return !this.findObstaclesAhead(distance, trains, railObstacles).isEmpty();
    }

    public ObstacleTracker.ObstacleSpeedLimit findObstacleSpeedLimit(double distance) {
        return this.findObstacleSpeedLimit(distance, this.getProperties().getWaitDeceleration());
    }

    public ObstacleTracker.ObstacleSpeedLimit findObstacleSpeedLimit(double distance, double deceleration) {
        double waitDistance = this.getProperties().getWaitDistance();
        List<ObstacleTracker.Obstacle> obstacles = this.obstacleTracker.findObstaclesAhead(distance, waitDistance > 0.0, true, waitDistance);
        return ObstacleTracker.minimumSpeedLimit(obstacles, deceleration);
    }

    private void tickActions() {
        this.getActions().doTick();
    }

    protected void doPhysics(TrainCarts plugin) {
        boolean finishedRemoving;
        if (this.isUnloaded()) {
            groups.remove((Object)this);
            return;
        }
        for (int i = 0; i < this.size(); ++i) {
            MinecartMember member = (MinecartMember)super.get(i);
            if (member.getEntity() == null) {
                CartPropertiesStore.remove(member.getProperties().getUUID());
                this.onMemberRemoved(member);
                super.remove(i--);
                continue;
            }
            if (member.group == this) continue;
            this.onMemberRemoved(member);
            super.remove(i--);
        }
        block4: do {
            finishedRemoving = true;
            for (int i = 0; i < this.size(); ++i) {
                MinecartMember member = (MinecartMember)super.get(i);
                if (!((CommonMinecart)member.getEntity()).isRemoved()) continue;
                this.remove(i);
                finishedRemoving = false;
                continue block4;
            }
        } while (!finishedRemoving);
        if (super.isEmpty()) {
            this.remove();
            return;
        }
        if (this.canUnload()) {
            for (MinecartMember<?> m : this) {
                if (!m.isUnloaded()) continue;
                this.unload();
                return;
            }
        } else {
            for (MinecartMember<?> m : this) {
                m.setUnloaded(false);
            }
        }
        if (!plugin.getTrainUpdateController().isTicking()) {
            return;
        }
        try {
            double realtimeFactor;
            double totalforce = this.getAverageForce();
            double speedlimit = this.getProperties().getSpeedLimit();
            double d = realtimeFactor = this.getProperties().hasRealtimePhysics() ? plugin.getTrainUpdateController().getRealtimeFactor() : 1.0;
            if (realtimeFactor * totalforce > 0.4 && realtimeFactor * speedlimit > 0.4) {
                this.updateStepCount = (int)Math.ceil(realtimeFactor * speedlimit / 0.4);
                this.updateSpeedFactor = realtimeFactor / (double)this.updateStepCount;
            } else {
                this.updateStepCount = 1;
                this.updateSpeedFactor = realtimeFactor;
            }
            if (this.updateStepCount > 1) {
                for (MinecartMember<?> mm : this) {
                    ((CommonMinecart)mm.getEntity()).vel.multiply(this.updateSpeedFactor);
                }
            }
            for (int i = 1; i <= this.updateStepCount; ++i) {
                this.updateStepNr = i;
                while (!this.doPhysics_step()) {
                }
            }
            for (MinecartMember<?> mm : this) {
                ((CommonMinecart)mm.getEntity()).vel.divide(this.updateSpeedFactor);
                double newMaxSpeed = ((CommonMinecart)mm.getEntity()).getMaxSpeed() / this.updateSpeedFactor;
                newMaxSpeed = Math.min(newMaxSpeed, this.getProperties().getSpeedLimit());
                ((CommonMinecart)mm.getEntity()).setMaxSpeed(newMaxSpeed);
            }
            this.updateSpeedFactor = 1.0;
            for (MinecartMember<?> mm : this) {
                CommonEntity entity = mm.getEntity();
                if (!entity.isInLoadedChunk()) continue;
                int cx = entity.getChunkX();
                int cz = entity.getChunkZ();
                if (cx == entity.loc.x.chunk() && cz == entity.loc.z.chunk()) continue;
                LevelChunkHandle.fromBukkit((Chunk)entity.getWorld().getChunkAt(cx, cz)).markDirty();
            }
        }
        catch (GroupUnloadedException totalforce) {
        }
        catch (Throwable t) {
            TrainProperties p = this.getProperties();
            plugin.log(Level.SEVERE, "Failed to perform physics on train '" + p.getTrainName() + "' at " + p.getLocation() + ":");
            plugin.handle(t);
        }
    }

    private boolean doPhysics_step() throws GroupUnloadedException {
        this.breakPhysics = false;
        try {
            double limitedSpeed;
            double forwardMovingSpeed;
            if (this.isEmpty()) {
                this.remove();
                throw new GroupUnloadedException();
            }
            double speedLimitClamped = Math.min(this.getProperties().getSpeedLimit() * this.updateSpeedFactor, 0.4);
            for (MinecartMember<?> mm : this) {
                mm.checkMissing();
                ((CommonMinecart)mm.getEntity()).setMaxSpeed(speedLimitClamped);
            }
            for (MinecartMember<?> member : this) {
                member.getAttachments().fixNetworkController();
            }
            if (this.teleportImmunityTick > 0) {
                --this.teleportImmunityTick;
            }
            this.updateDirection();
            this.getSignTracker().refresh();
            for (MinecartMember<?> member : this) {
                member.checkMissing();
                if (!(member.hasBlockChanged() | member.forcedBlockUpdate.clear())) continue;
                MemberBlockChangeEvent.call(member, member.getLastBlock(), member.getBlock());
                member.checkMissing();
                member.onBlockChange(member.getLastBlock(), member.getBlock());
                this.getSignTracker().updatePosition();
                member.checkMissing();
            }
            this.getSignTracker().refresh();
            this.updateDirection();
            if (!this.doConnectionCheck()) {
                return true;
            }
            this.tickActions();
            this.updateDirection();
            for (MinecartMember<?> member : this) {
                member.onPhysicsStart();
            }
            for (MinecartMember<?> member : this) {
                member.onPhysicsPreMove();
            }
            if (this.isEmpty()) {
                return false;
            }
            if (this.getProperties().isSlowingDown(SlowdownMode.GRAVITY)) {
                double usf_sq = this.getProperties().getGravity() * this.getUpdateSpeedFactor() * this.getUpdateSpeedFactor();
                for (MinecartMember<?> member : this) {
                    if (member.isUnloaded() || member.isMovementControlled()) continue;
                    member.getRailLogic().onGravity(member, usf_sq);
                }
            }
            this.updateDirection();
            for (MinecartMember<?> member : this) {
                member.getRailTracker().getRailType().onPreMove(member);
            }
            this.updateDirection();
            if (this.size() > 1) {
                forwardMovingSpeed = this.getAverageForce();
                boolean performUpdate = true;
                for (int i = 0; i < this.size() - 1; ++i) {
                    if (!((MinecartMember)this.get(i)).getRailTracker().isTrainSplit()) continue;
                    performUpdate = false;
                    break;
                }
                if (performUpdate) {
                    for (MinecartMember<?> m : this) {
                        m.setForwardForce(forwardMovingSpeed);
                    }
                }
            } else {
                forwardMovingSpeed = this.head().getForce();
            }
            if (this.isFirstUpdateStep()) {
                this.obstacleTracker.update(forwardMovingSpeed / this.getUpdateSpeedFactor());
            }
            if ((limitedSpeed = this.obstacleTracker.getSpeedLimit()) == Double.MAX_VALUE) {
                limitedSpeed = this.getProperties().getSpeedLimit();
            }
            limitedSpeed = Math.min(0.4, this.updateSpeedFactor * limitedSpeed);
            for (MinecartMember<?> mm : this) {
                ((CommonMinecart)mm.getEntity()).setMaxSpeed(limitedSpeed);
            }
            for (MinecartMember<?> member : this) {
                member.calculateSpeedFactor();
            }
            for (MinecartMember<?> member : this) {
                member.onPhysicsPostMove();
                if (!this.breakPhysics) continue;
                return true;
            }
            if (this.isLastUpdateStep()) {
                MinecartGroup.notifyPhysicsChange();
            }
            this.updateDirection();
            if (!this.doConnectionCheck()) {
                return true;
            }
            this.updateChunkInformation(!this.canUnload(), false);
            this.updateWheels();
            if (!this.isEmpty() && this.getProperties().isKeepingChunksLoaded()) {
                double thres = TCConfig.unloadRunawayTrainDistance * TCConfig.unloadRunawayTrainDistance;
                for (MinecartMember<?> member : this) {
                    double distanceSqSinceDerailed;
                    Location derailedStartPos = member.getFirstKnownDerailedPosition();
                    if (derailedStartPos == null || !((distanceSqSinceDerailed = ((CommonMinecart)member.getEntity()).loc.distanceSquared(derailedStartPos)) > thres)) continue;
                    Location loc = ((CommonMinecart)member.getEntity()).getLocation();
                    this.traincarts.getLogger().log(Level.WARNING, "A cart of train " + this.getProperties().getTrainName() + " at world=" + loc.getWorld().getName() + " x=" + loc.getBlockX() + " y=" + loc.getBlockY() + " z=" + loc.getBlockZ() + " derailed and went moving/flying off into nowhere!");
                    this.traincarts.getLogger().log(Level.WARNING, "The train's keepChunksLoaded property has been  reset to false to prevent endless chunks being generated");
                    this.traincarts.getLogger().log(Level.WARNING, "The derailment likely occurred at x=" + derailedStartPos.getBlockX() + " y=" + derailedStartPos.getBlockY() + " z=" + derailedStartPos.getBlockZ());
                    this.getProperties().setKeepChunksLoaded(false);
                    break;
                }
            }
            return true;
        }
        catch (MemberMissingException ex) {
            return false;
        }
    }

    private static class FlippedMember {
        public final MinecartMember<?> member;
        public final boolean orientationInverted;
        public final double velocity;
        public double distanceRemaining;
        public RailState flippedState;
        public FlippedMember next;

        public FlippedMember(MinecartMember<?> member, double distanceFromBehind) {
            this.member = member;
            this.orientationInverted = member.isOrientationInverted();
            this.velocity = member.getForce();
            this.distanceRemaining = distanceFromBehind;
            this.flippedState = null;
            this.next = null;
        }

        public void apply() {
            Location position = this.flippedState.position().toLocation(this.flippedState.railBlock());
            Vector velocityVec = this.flippedState.motionVector().clone().multiply(this.velocity);
            Vector upVector = this.flippedState.position().getWheelOrientation().upVector();
            Vector forwardVector = this.flippedState.motionVector();
            if (!this.orientationInverted) {
                forwardVector.multiply(-1.0);
            }
            Quaternion orientation = Quaternion.fromLookDirection((Vector)forwardVector, (Vector)upVector);
            ((CommonMinecart)this.member.getEntity()).setPosition(position.getX(), position.getY(), position.getZ());
            ((CommonMinecart)this.member.getEntity()).setVelocity(velocityVec);
            this.member.setOrientation(orientation);
            this.member.getWheels().startTeleport();
        }
    }
}

