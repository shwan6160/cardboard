/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet
 *  com.bergerkiller.bukkit.common.conversion.type.HandleConversion
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundAttackPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundInteractPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.TCSeatChangeListener;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundAttackPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundInteractPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

class TCPacketListener
implements PacketListener {
    public static final int ATTACK_SUPPRESS_DURATION = 250;
    public static final PacketType[] LISTENED_TYPES = new PacketType[]{PacketType.IN_STEER_VEHICLE, PacketType.IN_INTERACT, PacketType.IN_ATTACK, PacketType.IN_PLAYER_COMMAND};
    private final TrainCarts traincarts;
    private final Map<Player, Long> lastHitTime = new HashMap<Player, Long>();
    private final Consumer<PacketReceiveEvent> steerHandler = Common.hasCapability((String)"Common:PacketListener:SetPacket") ? this::handleSteerNew : this::handleSteerLegacy;

    public TCPacketListener(TrainCarts traincarts) {
        this.traincarts = traincarts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void suppressAttacksFor(Player player, int durationMillis) {
        Map<Player, Long> map = this.lastHitTime;
        synchronized (map) {
            if (this.lastHitTime.isEmpty()) {
                new HitTimeCleanTask((JavaPlugin)this.traincarts).start(1L, 1L);
            }
            this.lastHitTime.put(player, System.currentTimeMillis() + (long)durationMillis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isAttackSuppressed(Player player) {
        Map<Player, Long> map = this.lastHitTime;
        synchronized (map) {
            return this.lastHitTime.containsKey(player);
        }
    }

    public void onPacketSend(PacketSendEvent event) {
        Thread.dumpStack();
    }

    private void handleSteerLegacy(PacketReceiveEvent event) {
        CommonPacket packet = event.getPacket();
        if (((Boolean)packet.read(PacketType.IN_STEER_VEHICLE.unmount)).booleanValue()) {
            Player player = event.getPlayer();
            if (player.getVehicle() == null) {
                TCSeatChangeListener.markForUnmounting(this.traincarts, player);
            } else if (!this.traincarts.handlePlayerVehicleChange(player, null)) {
                packet.write(PacketType.IN_STEER_VEHICLE.unmount, (Object)false);
            }
        }
    }

    private void handleSteerNew(PacketReceiveEvent event) {
        ServerboundPlayerInputPacketHandle steerPacket = ServerboundPlayerInputPacketHandle.createHandle((Object)event.getPacket().getHandle());
        if (steerPacket.isUnmount()) {
            Player player = event.getPlayer();
            if (player.getVehicle() == null) {
                TCSeatChangeListener.markForUnmounting(this.traincarts, player);
            } else if (!this.traincarts.handlePlayerVehicleChange(player, null)) {
                event.setPacket((PacketHandle)ServerboundPlayerInputPacketHandle.createNew((boolean)steerPacket.isLeft(), (boolean)steerPacket.isRight(), (boolean)steerPacket.isForward(), (boolean)steerPacket.isBackward(), (boolean)steerPacket.isJump(), (boolean)false, (boolean)steerPacket.isSprint()));
            }
        }
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        String action;
        CommonPacket packet = event.getPacket();
        Player player = event.getPlayer();
        if (event.getType() == PacketType.IN_PLAYER_COMMAND && ((action = ((Enum)packet.read(PacketType.IN_PLAYER_COMMAND.action)).name()).equals("START_SNEAKING") || action.equals("PRESS_SHIFT_KEY"))) {
            if (player.getVehicle() == null) {
                TCSeatChangeListener.markForUnmounting(this.traincarts, player);
            } else if (!this.traincarts.handlePlayerVehicleChange(player, null)) {
                event.setCancelled(true);
            }
        }
        if (event.getType() == PacketType.IN_STEER_VEHICLE) {
            this.steerHandler.accept(event);
            return;
        }
        if (event.getType() == PacketType.IN_ATTACK) {
            ServerboundAttackPacketHandle packet_attack = ServerboundAttackPacketHandle.createHandle((Object)event.getPacket().getHandle());
            int entityId = packet_attack.getEntityId();
            if (WorldUtil.getEntityById((World)event.getPlayer().getWorld(), (int)entityId) != null) {
                return;
            }
            if (event.getPlayer().getGameMode().name().equals("SPECTATOR")) {
                return;
            }
            Location eyeLoc = event.getPlayer().getEyeLocation();
            try (ImplicitlySharedSet groups = MinecartGroupStore.getGroups().clone();){
                for (MinecartGroup group : groups) {
                    if (group.getWorld() != eyeLoc.getWorld()) continue;
                    for (MinecartMember<?> member : group) {
                        if (!member.getAttachments().isViewer(event.getPlayer()) || !member.getAttachments().isAttachment(entityId)) continue;
                        if (((CommonMinecart)member.getEntity()).loc.distanceSquared(eyeLoc) < 9.0) {
                            event.setPacket((PacketHandle)ServerboundAttackPacketHandle.createNew((int)((CommonMinecart)member.getEntity()).getEntityId()));
                            return;
                        }
                        TCPacketListener.fakeAttack(member, event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (event.getType() == PacketType.IN_INTERACT) {
            ServerboundInteractPacketHandle packet_use = ServerboundInteractPacketHandle.createHandle((Object)event.getPacket().getHandle());
            if (packet_use.isUsingSecondaryAction()) {
                if (player.getVehicle() == null) {
                    TCSeatChangeListener.markForUnmounting(this.traincarts, player);
                } else if (!this.traincarts.handlePlayerVehicleChange(player, null)) {
                    packet_use = ServerboundInteractPacketHandle.withUsingSecondaryAction((ServerboundInteractPacketHandle)packet_use, (boolean)false);
                    event.setPacket((PacketHandle)packet_use);
                }
            }
            int entityId = packet_use.getUsedEntityId();
            if (WorldUtil.getEntityById((World)event.getPlayer().getWorld(), (int)entityId) != null) {
                return;
            }
            if (event.getPlayer().getGameMode().name().equals("SPECTATOR")) {
                return;
            }
            Location eyeLoc = event.getPlayer().getEyeLocation();
            try (ImplicitlySharedSet groups = MinecartGroupStore.getGroups().clone();){
                for (MinecartGroup group : groups) {
                    if (group.getWorld() != eyeLoc.getWorld()) continue;
                    for (MinecartMember<?> member : group) {
                        if (!member.getAttachments().isViewer(event.getPlayer()) || !member.getAttachments().isAttachment(entityId)) continue;
                        if (!packet_use.hasInteractAtPosition()) {
                            event.setCancelled(true);
                            return;
                        }
                        if (((CommonMinecart)member.getEntity()).loc.distanceSquared(eyeLoc) < 9.0) {
                            this.suppressAttacksFor(event.getPlayer(), 250);
                            packet_use = ServerboundInteractPacketHandle.withUsedEntityId((ServerboundInteractPacketHandle)packet_use, (int)((CommonMinecart)member.getEntity()).getEntityId());
                            event.setPacket((PacketHandle)packet_use);
                            return;
                        }
                        HumanHand hand = packet_use.getHand((HumanEntity)event.getPlayer());
                        this.fakeInteraction(member, event.getPlayer(), hand, packet_use.getInteractAtPosition());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    public static void fakeAttack(final MinecartMember<?> member, final Player player) {
        if (!CommonUtil.isMainThread()) {
            CommonUtil.nextTick((Runnable)new Runnable(){

                @Override
                public void run() {
                    TCPacketListener.fakeAttack(member, player);
                }
            });
            return;
        }
        if (member == null || member.isUnloaded() || player == null || !player.isValid()) {
            return;
        }
        Object playerHandleRaw = HandleConversion.toEntityHandle((Entity)player);
        PlayerHandle.createHandle((Object)playerHandleRaw).attack(((CommonMinecart)member.getEntity()).getEntity());
    }

    public void fakeInteraction(final MinecartMember<?> member, final Player player, final HumanHand hand, final Vector atPosition) {
        this.suppressAttacksFor(player, 250);
        if (!CommonUtil.isMainThread()) {
            CommonUtil.nextTick((Runnable)new Runnable(){
                final /* synthetic */ TCPacketListener this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void run() {
                    this.this$0.fakeInteraction(member, player, hand, atPosition);
                }
            });
            return;
        }
        if (member == null || member.isUnloaded() || player == null || !player.isValid()) {
            return;
        }
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            PlayerInteractAtEntityEvent interactAtEvent;
            HumanHand mainHand = HumanHand.getMainHand((HumanEntity)player);
            EquipmentSlot slot = EquipmentSlot.HAND;
            if (hand != mainHand) {
                try {
                    slot = EquipmentSlot.OFF_HAND;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (((PlayerInteractAtEntityEvent)CommonUtil.callEvent((Event)(interactAtEvent = new PlayerInteractAtEntityEvent(player, ((CommonMinecart)member.getEntity()).getEntity(), atPosition, slot)))).isCancelled()) {
                return;
            }
        } else {
            Object interactEvent = atPosition != null ? new PlayerInteractAtEntityEvent(player, ((CommonMinecart)member.getEntity()).getEntity(), atPosition) : new PlayerInteractEntityEvent(player, ((CommonMinecart)member.getEntity()).getEntity());
            if (((PlayerInteractEntityEvent)CommonUtil.callEvent((Event)interactEvent)).isCancelled()) {
                return;
            }
        }
        member.onInteractBy((HumanEntity)player, hand, atPosition);
    }

    private final class HitTimeCleanTask
    extends Task {
        public HitTimeCleanTask(JavaPlugin plugin) {
            super(plugin);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Map map = TCPacketListener.this.lastHitTime;
            synchronized (map) {
                long timeout = System.currentTimeMillis();
                Iterator iter = TCPacketListener.this.lastHitTime.values().iterator();
                while (iter.hasNext()) {
                    if (timeout < (Long)iter.next()) continue;
                    iter.remove();
                }
                if (TCPacketListener.this.lastHitTime.isEmpty()) {
                    this.stop();
                }
            }
        }
    }
}

