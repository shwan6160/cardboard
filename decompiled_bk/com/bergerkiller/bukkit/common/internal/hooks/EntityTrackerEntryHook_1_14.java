/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.entity.Player;

@ClassHook.HookImport(value="net.minecraft.server.level.ServerPlayer")
@ClassHook.HookPackage(value="net.minecraft.server")
@ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class EntityTrackerEntryHook_1_14
extends ClassHook<EntityTrackerEntryHook_1_14>
implements EntityTrackerEntryHook {
    private EntityNetworkController<?> controller;
    private final StateHook stateHook = new StateHook();

    @Override
    public EntityNetworkController<?> getController() {
        return this.controller;
    }

    @Override
    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
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

    @ClassHook.HookMethod(value="public void removeViewer:???(ServerPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            this.controller.removeViewer((Player)Conversion.toPlayer.convert(entityplayer));
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer", t);
        }
    }

    @Override
    public <T> T hook(T object) {
        Object hookedTracker = super.hook(object);
        Object state = ((Template.Method)EntityTrackerEntryHandle.T.getState.raw).invoke(hookedTracker);
        state = this.stateHook.hook(state);
        ((Template.Method)EntityTrackerEntryHandle.T.setState.raw).invoke(hookedTracker, state);
        if (EntityTrackerEntryStateHandle.T.broadcastMethod.isAvailable()) {
            EntityTrackerEntryStateHandle.T.broadcastMethod.set(state, packet -> ((Template.Method)EntityTrackerEntryHandle.T.broadcastRawPacket.raw).invoke(hookedTracker, packet));
        }
        return hookedTracker;
    }

    @ClassHook.HookImport(value="net.minecraft.server.level.ServerPlayer")
    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    public class StateHook
    extends ClassHook<StateHook> {
        @ClassHook.HookMethod(value="public void onTick:???()")
        public void onTick() {
            try {
                EntityTrackerEntryStateHandle handle = EntityTrackerEntryHook_1_14.this.controller.getStateHandle();
                handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
                EntityTrackerEntryHook_1_14.this.controller.onTick();
                handle.setTickCounter(handle.getTickCounter() + 1);
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize", t);
            }
        }

        @ClassHook.HookMethodCondition(value="version < 1.18")
        @ClassHook.HookMethod(value="public void removePairing:a(ServerPlayer entityPlayer)")
        public void removePairing_pre_1_18(Object entityplayer) {
            this.removePairing(entityplayer);
        }

        @ClassHook.HookMethodCondition(value="version < 1.18")
        @ClassHook.HookMethod(value="public void addPairing:b(ServerPlayer entityPlayer)")
        public void addPairing_pre_1_18(Object entityplayer) {
            this.addPairing(entityplayer);
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18 && exists net.minecraft.server.level.EntityTrackerEntryState public boolean removePairing(net.minecraft.server.level.ServerPlayer viewer);")
        @ClassHook.HookMethod(value="public boolean removePairing(ServerPlayer entityPlayer)")
        public boolean removePairing_universe_spigot(Object entityplayer) {
            this.removePairing(entityplayer);
            return true;
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18 && exists net.minecraft.server.level.EntityTrackerEntryState public boolean addPairing(net.minecraft.server.level.ServerPlayer viewer);")
        @ClassHook.HookMethod(value="public boolean addPairing(ServerPlayer entityPlayer)")
        public boolean addPairing_universe_spigot(Object entityplayer) {
            this.addPairing(entityplayer);
            return true;
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18 && !exists net.minecraft.server.level.EntityTrackerEntryState public boolean removePairing(net.minecraft.server.level.ServerPlayer viewer);")
        @ClassHook.HookMethod(value="public void removePairing(ServerPlayer entityPlayer)")
        public void removePairing(Object entityplayer) {
            try {
                Player viewer = (Player)WrapperConversion.toEntity(entityplayer);
                EntityTrackerEntryHook_1_14.this.controller.makeHidden(viewer);
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to despawn controller entity for a viewer", t);
            }
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18 && !exists net.minecraft.server.level.EntityTrackerEntryState public boolean addPairing(net.minecraft.server.level.ServerPlayer viewer);")
        @ClassHook.HookMethod(value="public void addPairing(ServerPlayer entityPlayer)")
        public void addPairing(Object entityplayer) {
            try {
                Player viewer = (Player)WrapperConversion.toEntity(entityplayer);
                EntityTrackerEntryHook_1_14.this.controller.makeVisible(viewer);
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to spawn controller entity for a viewer", t);
            }
        }
    }
}

