/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.CheckedDeferredSupplier;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.controller.ExternalEntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.entity.type.CommonItem;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkHolderHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.entity.CraftEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CommonEntity<T extends Entity>
extends ExtendedEntity<T> {
    private static final CheckedDeferredSupplier<Void> CONTROLLER_INITIALIZER = CheckedDeferredSupplier.of(() -> {
        try {
            CommonEntityType.PLAYER.getClass();
            EntityHandle.T.forceInitialization();
            ChunkHolderHandle.T.forceInitialization();
            ChunkMapHandle.T.forceInitialization();
            EntityMoveHandler.assertInitialized();
            DimensionType.OVERWORLD.getClass();
            ServerPlayerHandle.T.createHandle(null, true);
            ServerLevelHandle.T.createHandle(null, true);
            ChunkMapHandle.T.createHandle(null, true);
            ServerGamePacketListenerImplHandle.T.forceInitialization();
            ConnectionHandle.T.forceInitialization();
            EntityTrackerHandle.T.forceInitialization();
            EntityTrackerEntryHandle.T.forceInitialization();
            EntityTrackerEntryStateHandle.T.forceInitialization();
            return null;
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize some entity controller logic", t);
            throw t;
        }
    });

    public static void forceControllerInitialization() {
        try {
            CONTROLLER_INITIALIZER.get();
        }
        catch (Throwable t) {
            throw new UnsupportedOperationException("Failed to initialize", t);
        }
    }

    public CommonEntity(T entity) {
        super(entity);
    }

    public EntityNetworkController<CommonEntity<T>> getNetworkController() {
        EntityNetworkController result;
        if (this.handle.getWorld() == null) {
            return null;
        }
        Object entityTrackerEntry = Template.Handle.getRaw(WorldUtil.getTrackerEntry(this.entity));
        if (entityTrackerEntry == null) {
            return null;
        }
        EntityTrackerEntryHook hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(entityTrackerEntry);
        if (hook != null) {
            return (EntityNetworkController)LogicUtil.unsafeCast(hook.getController());
        }
        if (EntityTrackerEntryHandle.T.isType(entityTrackerEntry)) {
            result = new DefaultEntityNetworkController();
            result.bind(this, entityTrackerEntry);
        } else {
            result = new ExternalEntityNetworkController();
            result.bind(this, entityTrackerEntry);
        }
        return result;
    }

    public void setNetworkController(EntityNetworkController controller) {
        EntityTrackerEntryHandle newEntry;
        if (this.getWorld() == null) {
            throw new RuntimeException("Can not set the network controller when no world is known! (need to spawn it?)");
        }
        EntityTracker tracker = WorldUtil.getTracker(this.getWorld());
        EntityTrackerEntryHandle storedEntry = tracker.getEntry(this.entity);
        if (controller == null) {
            tracker.stopTracking(this.entity);
            return;
        }
        EntityNetworkController<?> oldController = null;
        EntityTrackerEntryHook hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(Template.Handle.getRaw(storedEntry));
        if (hook != null && (oldController = hook.getController()) == controller) {
            return;
        }
        List<Object> previousViewers = storedEntry != null ? new ArrayList<Player>(storedEntry.getViewers()) : Collections.emptyList();
        for (Player previousViewer : previousViewers) {
            storedEntry.removeViewer(previousViewer);
        }
        for (Player previousViewer : previousViewers) {
            if (!PlayerUtil.getEntityRemoveQueue(previousViewer).remove(this.entity.getEntityId())) continue;
            CommonPacket destroyPacket = PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(this.entity.getEntityId());
            PacketUtil.sendPacket(previousViewer, destroyPacket);
        }
        if (oldController != null) {
            oldController.bind(null, storedEntry.getRaw());
        }
        if (EntityHandle.T.setLegacyTrackingEntity.isAvailable()) {
            if (controller instanceof DefaultEntityNetworkController) {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(this.getHandle(), Boolean.FALSE);
            } else {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(this.getHandle(), Boolean.TRUE);
            }
        }
        if (controller instanceof DefaultEntityNetworkController) {
            if (EntityTrackerEntryHandle.T.isHandleType(storedEntry)) {
                newEntry = storedEntry;
            } else {
                newEntry = EntityTypingHandler.INSTANCE.createEntityTrackerEntry(tracker, this.entity);
                if (storedEntry != null) {
                    CommonEntity.copyEntityTrackerEntry(storedEntry, newEntry);
                }
            }
        } else if (controller instanceof ExternalEntityNetworkController) {
            newEntry = EntityTrackerEntryHandle.createHandle(controller.getHandle());
            if (storedEntry != null && newEntry != null) {
                CommonEntity.copyEntityTrackerEntry(storedEntry, newEntry);
            }
        } else if (hook != null) {
            newEntry = storedEntry;
            newEntry.clearViewers();
        } else {
            EntityTrackerEntryHandle oldEntry = storedEntry;
            if (oldEntry == null) {
                oldEntry = EntityTypingHandler.INSTANCE.createEntityTrackerEntry(tracker, this.entity);
            }
            newEntry = EntityTrackerEntryHandle.createHandle(EntityTypingHandler.INSTANCE.hookEntityTrackerEntry(oldEntry.getRaw()));
        }
        controller.bind(this, newEntry.getRaw());
        if (Template.Handle.getRaw(storedEntry) != Template.Handle.getRaw(newEntry)) {
            tracker.setEntry(this.entity, newEntry);
        }
        if (storedEntry != null) {
            for (Player previousViewer : previousViewers) {
                newEntry.updatePlayer(previousViewer);
            }
        } else {
            newEntry.updateViewers();
        }
    }

    private static void copyEntityTrackerEntry(EntityTrackerEntryHandle from, EntityTrackerEntryHandle to) {
        if (EntityTrackerEntryHandle.T.getType() == EntityTrackerEntryStateHandle.T.getType()) {
            EntityTrackerEntryHandle.T.copyHandle(from, to);
        } else {
            EntityTrackerEntryStateHandle to_state = to.getState();
            EntityTrackerEntryHandle.T.copyHandle(from, to);
            EntityTrackerEntryHandle.T.setState.invoke(to.getRaw(), to_state);
            if (EntityTrackerEntryStateHandle.T.broadcastMethod.isAvailable()) {
                Consumer to_state_broadcastMethod = EntityTrackerEntryStateHandle.T.broadcastMethod.get(to_state.getRaw());
                EntityTrackerEntryStateHandle.T.copyHandle(from.getState(), to_state);
                EntityTrackerEntryStateHandle.T.broadcastMethod.set(to_state.getRaw(), to_state_broadcastMethod);
            } else {
                EntityTrackerEntryStateHandle.T.copyHandle(from.getState(), to_state);
            }
        }
    }

    public <C extends EntityController<?>> C getController(Class<? extends C> controllerType) {
        EntityHook hook = EntityHook.get(this.getHandle(), EntityHook.class);
        if (hook == null || !hook.hasController()) {
            return null;
        }
        EntityController<?> controller = hook.getController();
        if (controllerType.isAssignableFrom(controller.getClass())) {
            return (C)controller;
        }
        return null;
    }

    public EntityController<CommonEntity<T>> getController() {
        EntityController controller;
        EntityHook hook = EntityHook.get(this.getHandle(), EntityHook.class);
        if (hook == null) {
            controller = new DefaultEntityController();
            controller.bind(this, false);
        } else if (hook.hasController()) {
            controller = hook.getController();
        } else {
            controller = new DefaultEntityController();
            controller.bind(this, false);
        }
        return controller;
    }

    public void setController(EntityController controller) {
        EntityController<CommonEntity<T>> old_controller;
        this.prepareHook();
        if (controller == null) {
            controller = new DefaultEntityController();
        }
        if (this.isHooked() && (old_controller = this.getController()) != null) {
            old_controller.bind(null, true);
        }
        controller.bind(this, true);
    }

    public boolean hasControllerSupport() {
        if (this.isHooked()) {
            return true;
        }
        Object handle = this.getHandle();
        CommonEntityType type = CommonEntityType.byNMSEntity(handle);
        return handle != null && type.nmsType.isValid() && type.nmsType.isType(handle);
    }

    protected boolean isHooked() {
        return EntityHook.get(this.getHandle(), EntityHook.class) != null;
    }

    protected void prepareHook() {
        if (this.isHooked()) {
            return;
        }
        Object oldInstance = this.getHandle();
        String oldInstanceName = oldInstance.getClass().getName();
        CommonEntityType type = CommonEntityType.byEntity(this.entity);
        if (!type.nmsType.isType(oldInstance)) {
            throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
        }
        try {
            this.replaceEntity(EntityHandle.createHandle(type.createNMSHookFromEntity(this)));
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to prepare entity controller hook", t);
            throw new RuntimeException("Failed to set controller:", t);
        }
    }

    private void replaceEntity(EntityHandle newInstance) {
        Inventory inv;
        EntityHandle oldInstance = this.handle;
        if (oldInstance.getRaw() == newInstance.getRaw()) {
            throw new RuntimeException("Can not replace an entity with itself!");
        }
        CraftEntityHandle craftEntity = CraftEntityHandle.createHandle(this.entity);
        craftEntity.setHandle(newInstance);
        if (this.entity instanceof InventoryHolder && CraftInventoryHandle.T.isAssignableFrom(inv = ((InventoryHolder)this.entity).getInventory())) {
            CraftInventoryHandle cInv = CraftInventoryHandle.createHandle(inv);
            if (ContainerHandle.T.isAssignableFrom(newInstance.getRaw())) {
                ContainerHandle iinvHandle = ContainerHandle.createHandle(newInstance.getRaw());
                cInv.setHandleField(iinvHandle);
            }
        }
        oldInstance.setBukkitEntityField(CraftEntityHandle.createCraftEntity(Bukkit.getServer(), oldInstance));
        this.handle = newInstance;
        EntityHandle vehicle = newInstance.getVehicle();
        if (vehicle != null) {
            ArrayList<EntityHandle> passengers = new ArrayList<EntityHandle>(vehicle.getPassengers());
            CommonEntity.replaceInList(passengers, newInstance);
            vehicle.setPassengers((List<EntityHandle>)passengers);
        }
        for (EntityHandle passenger : newInstance.getPassengers()) {
            if (oldInstance.getRaw() != passenger.getVehicle().getRaw()) continue;
            passenger.setVehicle(newInstance);
        }
        newInstance.assignEntityReference();
        EntityAddRemoveHandler.INSTANCE.replace(oldInstance, newInstance);
        oldInstance.setRemovedPassive();
        newInstance.setValid(true);
        CommonUtil.nextTick(() -> EntityAddRemoveHandler.INSTANCE.replace(oldInstance, newInstance));
        if (this.isHooked()) {
            DefaultEntityController controller = new DefaultEntityController();
            controller.bind(this, true);
        }
    }

    private static boolean replaceInList(List list, EntityHandle entity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            int obj_id;
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle)obj;
                if (obj_e.getIdField() != entity.getIdField()) continue;
                iter.set(entity);
                continue;
            }
            if (!EntityHandle.T.isAssignableFrom(obj) || (obj_id = EntityHandle.T.idField.getInteger(obj)) != entity.getIdField()) continue;
            iter.set(entity.getRaw());
        }
        return false;
    }

    public void doPostTick() {
        EntityAddRemoveHandler.INSTANCE.moveToChunk(this.handle);
        if (this.isInLoadedChunk()) {
            ArrayList<Entity> updatedPassengers = null;
            for (Entity passenger : this.getPassengers()) {
                if (!passenger.isDead() && passenger.getVehicle() == this.entity) {
                    if (updatedPassengers != null) {
                        updatedPassengers.add(passenger);
                    }
                    EntityHandle.fromBukkit(passenger).onTick();
                    CommonEntity.get(passenger).doPostTick();
                    continue;
                }
                if (updatedPassengers != null) continue;
                updatedPassengers = new ArrayList<Entity>();
                for (Entity passedPassenger : this.getPassengers()) {
                    if (passedPassenger == passenger) break;
                    updatedPassengers.add(passedPassenger);
                }
            }
            if (updatedPassengers != null) {
                this.setPassengersSilent(updatedPassengers);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        boolean succ;
        ExtendedEntity<Entity> extVeh;
        if (this.isRemoved()) {
            return false;
        }
        Location oldLocation = this.entity.getLocation();
        List<Entity> passengers = this.getPassengers();
        LevelHandle newworld = LevelHandle.fromBukkit(location.getWorld());
        boolean isWorldChange = !this.handle.getWorld().equals(newworld);
        EntityNetworkController<CommonEntity<T>> oldNetworkController = this.getNetworkController();
        boolean hasNetworkController = !(oldNetworkController instanceof DefaultEntityNetworkController);
        WorldUtil.loadChunks(location, 3);
        if (this.isInsideVehicle() && !(extVeh = new ExtendedEntity<Entity>(this.getVehicle())).removePassenger(this.entity)) {
            return false;
        }
        if (this.hasPassenger()) {
            this.setPassengersSilent(Collections.emptyList());
        }
        if (this.entity instanceof Player) {
            succ = this.entity.teleport(location, cause);
        } else if (!isWorldChange) {
            EntityTracker tracker = WorldUtil.getTracker(this.getWorld());
            tracker.stopTracking(this.entity);
            for (Player bukkitPlayer : WorldUtil.getPlayers(this.getWorld())) {
                CommonPlayer player = CommonEntity.get(bukkitPlayer);
                if (player == null) continue;
                player.flushEntityRemoveQueue();
            }
            succ = this.entity.teleport(location, cause);
            if (!hasNetworkController && !isWorldChange) {
                tracker.startTracking(this.entity);
            }
        } else {
            this.handle.getWorldServer().removeEntityWithoutDeath(this.handle);
            this.handle.setDestroyed(false);
            this.handle.setWorld(newworld);
            this.handle.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            if (hasNetworkController) {
                Object nmsWorldHandle = HandleConversion.toWorldHandle(location.getWorld());
                EntityTrackerHook hook = CommonEntity.hookWorldEntityTracker(nmsWorldHandle);
                hook.ignoredEntities.add(this.handle.getRaw());
                try {
                    EntityUtil.addEntity(this.entity);
                }
                finally {
                    hook.ignoredEntities.remove(this.handle.getRaw());
                    CommonEntity.unhookWorldEntityTracker(nmsWorldHandle, hook);
                }
            } else {
                EntityUtil.addEntity(this.entity);
            }
            succ = true;
        }
        if (succ && !passengers.isEmpty()) {
            ArrayList<Entity> teleportedPassengers = new ArrayList<Entity>();
            this.handle.setIgnoreChunkCheck(true);
            float yawChange = location.getYaw() - oldLocation.getYaw();
            float pitchChange = location.getPitch() - oldLocation.getPitch();
            for (Entity passenger : passengers) {
                Location passengerOld = passenger.getLocation();
                Location passengerLoc = location.clone();
                passengerLoc.setYaw(passengerOld.getYaw() + yawChange);
                passengerLoc.setPitch(passengerOld.getPitch() + pitchChange);
                if (!CommonEntity.get(passenger).teleport(passengerLoc, cause)) continue;
                teleportedPassengers.add(passenger);
            }
            this.handle.setIgnoreChunkCheck(false);
            if (!teleportedPassengers.isEmpty()) {
                if (hasNetworkController) {
                    ArrayList<EntityHandle> passengerHandles = new ArrayList<EntityHandle>(teleportedPassengers.size());
                    for (Entity passenger : teleportedPassengers) {
                        EntityHandle phandle = EntityHandle.fromBukkit(passenger);
                        passengerHandles.add(phandle);
                        phandle.setVehicle(this.handle);
                    }
                    this.handle.setPassengers(passengerHandles);
                } else {
                    this.setPassengersSilent(teleportedPassengers);
                }
            }
        }
        if (hasNetworkController) {
            this.setNetworkController(oldNetworkController);
        }
        return succ;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final CommonEntity spawn(EntityType entityType, Location location, EntityController controller, EntityNetworkController networkController) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }
        CommonEntity entity = type.createNMSHookEntity(location);
        controller.bind(entity, false);
        if (networkController != null && !(networkController instanceof DefaultEntityNetworkController)) {
            if (EntityHandle.T.setLegacyTrackingEntity.isAvailable()) {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(entity.getHandle(), Boolean.TRUE);
            }
            Object nmsWorldHandle = HandleConversion.toWorldHandle(location.getWorld());
            EntityTrackerHook hook = CommonEntity.hookWorldEntityTracker(nmsWorldHandle);
            hook.ignoredEntities.add(entity.getHandle());
            try {
                EntityUtil.addEntity(entity.getEntity());
                entity.getController().onAttached();
                entity.setNetworkController(networkController);
            }
            finally {
                hook.ignoredEntities.remove(entity.getHandle());
                CommonEntity.unhookWorldEntityTracker(nmsWorldHandle, hook);
            }
        } else {
            EntityUtil.addEntity(entity.getEntity());
            entity.getController().onAttached();
        }
        return entity;
    }

    public static final CommonEntity create(EntityType entityType, Location location) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }
        return type.createNMSHookEntity(location);
    }

    public static final CommonEntity createNull(EntityType entityType) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }
        return type.createCommonEntityNull();
    }

    public static CommonItem get(Item item) {
        return CommonEntity.get(item, CommonItem.class);
    }

    public static CommonPlayer get(Player player) {
        return CommonEntity.get(player, CommonPlayer.class);
    }

    public static <T extends LivingEntity, C extends CommonLivingEntity<? extends T>> C get(T livingEntity) {
        return (C)CommonEntity.get(livingEntity, CommonLivingEntity.class);
    }

    public static <T extends Entity, C extends CommonEntity<? extends T>> C get(T entity, Class<C> type) {
        return (C)((CommonEntity)LogicUtil.tryCast(CommonEntity.get(entity), type));
    }

    public static <T extends Entity> CommonEntity<T> get(T entity) {
        if (entity == null) {
            return null;
        }
        Object handle = HandleConversion.toEntityHandle(entity);
        if (handle == null) {
            return null;
        }
        EntityHook hook = EntityHook.get(handle, EntityHook.class);
        if (hook != null && hook.hasController()) {
            return hook.getController().getEntity();
        }
        return CommonEntityType.byNMSEntity(handle).createCommonEntity(entity);
    }

    public static void clearControllers(Entity entity) {
        CommonEntity.clearControllers(entity, true);
    }

    public static void clearControllers(Entity entity, boolean isShutdownClear) {
        CommonEntityController controller;
        CommonEntity<Entity> commonEntity = CommonEntity.get(entity);
        Object oldInstance = commonEntity.getHandle();
        EntityHook oldHook = EntityHook.get(oldInstance, EntityHook.class);
        if (isShutdownClear && (controller = commonEntity.getController()) != null && !(controller instanceof DefaultEntityController) && !((EntityController)controller).isPlayerTakeable()) {
            for (Player player : commonEntity.getPlayerPassengers()) {
                commonEntity.removePassenger((Entity)player);
            }
        }
        if ((controller = commonEntity.getNetworkController()) != null && !(controller instanceof DefaultEntityNetworkController)) {
            commonEntity.setNetworkController(new DefaultEntityNetworkController());
        }
        if (oldHook != null) {
            try {
                controller = oldHook.getController();
                if (controller != null) {
                    controller.onDetached();
                }
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to handle controller detachment", t);
            }
            try {
                Object newInstance = EntityHook.unhook(oldInstance);
                super.replaceEntity(EntityHandle.createHandle(newInstance));
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to unhook Common Entity Controller", t);
            }
        }
    }

    private static EntityTrackerHook hookWorldEntityTracker(Object nmsWorldHandle) {
        Object nmsEntityTrackerHandle = ServerLevelHandle.T.getEntityTrackerHandle.invoke(nmsWorldHandle);
        EntityTrackerHook hook = EntityTrackerHook.get(nmsEntityTrackerHandle, EntityTrackerHook.class);
        if (hook == null) {
            hook = new EntityTrackerHook(nmsEntityTrackerHandle);
            ServerLevelHandle.T.setEntityTrackerHandle.invoke(nmsWorldHandle, hook.hook(nmsEntityTrackerHandle));
        }
        return hook;
    }

    private static void unhookWorldEntityTracker(Object nmsWorldHandle, EntityTrackerHook hook) {
        if (hook.ignoredEntities.isEmpty()) {
            ServerLevelHandle.T.setEntityTrackerHandle.invoke(nmsWorldHandle, hook.original);
            if (EntityTrackerHandle.T.setVisibleChunksToUpdatingChunks.isAvailable()) {
                EntityTrackerHandle.T.setVisibleChunksToUpdatingChunks.invoke(hook.hookedInstance());
            }
        }
    }
}

