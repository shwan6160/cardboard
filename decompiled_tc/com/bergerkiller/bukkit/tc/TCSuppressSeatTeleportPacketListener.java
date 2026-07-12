/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.seat.MemberBeforeSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

class TCSuppressSeatTeleportPacketListener
implements Listener,
PacketListener {
    public static final boolean SUPPRESS_POST_ENTER_PLAYER_POSITION_PACKET = !Common.hasCapability((String)"Common:EntityController:PositionPassenger") && Common.evaluateMCVersion((String)">=", (String)"1.20");
    public static final PacketType[] LISTENED_TYPES = new PacketType[]{PacketType.OUT_POSITION};
    private final TrainCarts traincarts;
    private final WeakHashMap<Player, SeatMoment> seatEnterMoments = new WeakHashMap();

    public TCSuppressSeatTeleportPacketListener(TrainCarts traincarts) {
        this.traincarts = traincarts;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public synchronized void onMemberSeatExitEvent(MemberSeatExitEvent event) {
        SeatMoment moment;
        if (event.isPlayer() && (moment = this.seatEnterMoments.remove((Player)event.getEntity())) != null && moment.entityId != ((CommonMinecart)event.getMember().getEntity()).getEntityId()) {
            this.seatEnterMoments.put((Player)event.getEntity(), moment);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public synchronized void onMemberBeforeSeatEnterEvent(MemberBeforeSeatEnterEvent event) {
        if (event.isPlayer() && !event.getMember().getProperties().getModel().isDefault()) {
            this.seatEnterMoments.put((Player)event.getEntity(), new SeatMoment(event.getMember()));
        }
    }

    public void onPacketReceive(PacketReceiveEvent event) {
    }

    public synchronized void onPacketSend(PacketSendEvent event) {
        SeatMoment moment;
        if (event.getType() == PacketType.OUT_POSITION && (moment = this.seatEnterMoments.remove(event.getPlayer())) != null && CommonUtil.getServerTicks() < moment.expire) {
            event.setCancelled(true);
            if (CommonUtil.isMainThread()) {
                Util.resetPlayerAwaitingTeleport(event.getPlayer());
            } else {
                CommonUtil.nextTick(() -> Util.resetPlayerAwaitingTeleport(event.getPlayer()));
            }
        }
    }

    private static class SeatMoment {
        public final int expire = CommonUtil.getServerTicks() + 20;
        public final int entityId;

        public SeatMoment(MinecartMember<?> member) {
            this.entityId = ((CommonMinecart)member.getEntity()).getEntityId();
        }
    }
}

