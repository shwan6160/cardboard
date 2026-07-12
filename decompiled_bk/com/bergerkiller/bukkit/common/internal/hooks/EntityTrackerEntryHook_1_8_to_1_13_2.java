/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.org.bukkit.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@ClassHook.HookPackage(value="net.minecraft.server")
@ClassHook.HookImport(value="net.minecraft.server.level.ServerPlayer")
public class EntityTrackerEntryHook_1_8_to_1_13_2
extends ClassHook<EntityTrackerEntryHook_1_8_to_1_13_2>
implements EntityTrackerEntryHook {
    private EntityNetworkController<?> controller;
    private ViewableLogic viewable;

    @Override
    public EntityNetworkController<?> getController() {
        return this.controller;
    }

    @Override
    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
        this.viewable = controller == null ? null : new ViewableLogic(controller);
    }

    @ClassHook.HookMethod(value="public void track(List<net.minecraft.world.entity.player.Player> list)")
    public void track(List<?> list) {
        EntityTrackerEntryStateHandle handle = EntityTrackerEntryStateHandle.createHandle(this.instance());
        if (handle.checkTrackNeeded()) {
            for (Object player : list) {
                this.updatePlayer(player);
            }
        }
        handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
        try {
            this.controller.onTick();
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize", t);
        }
        handle.setTickCounter(handle.getTickCounter() + 1);
    }

    @ClassHook.HookMethod(value="public void hideForAll:???()")
    public void hideForAll() {
        try {
            this.controller.makeHiddenForAll();
            Object entity = this.controller.getEntity();
            if (entity != null) {
                World world = ((ExtendedEntity)entity).getWorld();
                EntityTrackerEntryHandle eh = EntityTrackerEntryHandle.createHandle(this.instance());
                if (world != null && !WorldUtil.getTracker(world).containsEntry(eh)) {
                    this.controller.bind(null, null);
                }
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers", t);
        }
    }

    @ClassHook.HookMethod(value="public void clear(ServerPlayer entityplayer)")
    public void clear(Object entityplayer) {
        try {
            this.controller.removeViewer((Player)Conversion.toPlayer.convert(entityplayer));
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer", t);
        }
    }

    @ClassHook.HookMethod(value="public void removeViewer:???(ServerPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            this.controller.removeViewer((Player)Conversion.toPlayer.convert(entityplayer));
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer", t);
        }
    }

    @ClassHook.HookMethod(value="public void updatePlayer(ServerPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        if (entityplayer != ((ExtendedEntity)this.controller.getEntity()).getHandle()) {
            try {
                Player viewer = (Player)WrapperConversion.toEntity(entityplayer);
                if (this.viewable.isViewable(viewer)) {
                    this.controller.addViewer(viewer);
                } else {
                    this.controller.removeViewer(viewer);
                }
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer", t);
            }
        }
    }

    public static class ViewableLogic {
        private final EntityNetworkController<?> controller;

        public ViewableLogic(EntityNetworkController<?> controller) {
            this.controller = controller;
        }

        public void handleRespawnBlindness(Player viewer) {
            Object pendingEntity = this.controller.getEntity();
            if (pendingEntity == null || ((ExtendedEntity)pendingEntity).getWorld() != viewer.getWorld()) {
                return;
            }
            if (this.isViewable(viewer)) {
                this.controller.addViewer(viewer);
            }
        }

        public final boolean isViewable(Player viewer) {
            if (!CommonPlugin.getInstance().getPlayerMeta(viewer).respawnBlindnessCheck(this)) {
                return false;
            }
            return this.isViewable_self_or_passenger(viewer);
        }

        private boolean isViewable_self_or_passenger(Player viewer) {
            Object entity = this.controller.getEntity();
            if (!((Boolean)EntityHandle.T.isSeenBy.invoker.invoke(((ExtendedEntity)entity).getEntity(), viewer)).booleanValue()) {
                return false;
            }
            if (this.isViewable_self(viewer)) {
                return true;
            }
            for (Entity passenger : ((ExtendedEntity)entity).getPassengers()) {
                EntityNetworkController<CommonEntity<Entity>> network = CommonEntity.get(passenger).getNetworkController();
                if (network == null || !new ViewableLogic(network).isViewable_self_or_passenger(viewer)) continue;
                return true;
            }
            return false;
        }

        private boolean isViewable_self(Player viewer) {
            Object entity = this.controller.getEntity();
            for (Entity passenger : ((ExtendedEntity)entity).getPassengers()) {
                if (!viewer.equals((Object)passenger)) continue;
                return true;
            }
            int dx = MathUtil.floor(Math.abs(EntityUtil.getLocX((Entity)viewer) - this.controller.locSynched.getX()));
            int dz = MathUtil.floor(Math.abs(EntityUtil.getLocZ((Entity)viewer) - this.controller.locSynched.getZ()));
            int view = this.controller.getViewDistance();
            if (dx > view || dz > view) {
                return false;
            }
            if (!com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.T.isIgnoreChunkCheck.invoke(((ExtendedEntity)entity).getHandle()).booleanValue() && !PlayerUtil.isChunkVisible(viewer, ((ExtendedEntity)entity).getChunkX(), ((ExtendedEntity)entity).getChunkZ())) {
                return false;
            }
            return !(((ExtendedEntity)entity).getEntity() instanceof Player) || viewer.canSee((Player)((ExtendedEntity)entity).getEntity());
        }
    }
}

