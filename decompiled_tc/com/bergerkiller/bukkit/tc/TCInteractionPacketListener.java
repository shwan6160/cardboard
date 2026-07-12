/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerActionPacketHandle$ActionHandle
 *  com.bergerkiller.mountiplex.reflection.FieldAccessor
 *  org.bukkit.World
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TCPacketListener;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerActionPacketHandle;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

class TCInteractionPacketListener
implements PacketListener {
    private final TCPacketListener mainPacketListener;
    public static final PacketType[] TYPES = new PacketType[]{PacketType.IN_USE_ITEM, PacketType.IN_USE_ITEM_ON, PacketType.IN_SWING, PacketType.IN_PLAYER_ACTION};

    TCInteractionPacketListener(TCPacketListener mainPacketListener) {
        this.mainPacketListener = mainPacketListener;
    }

    private void cancelBlockChanges(Player player, IntVector3 pos) {
        if (WorldUtil.isLoaded((World)player.getWorld(), (int)pos.x, (int)pos.y, (int)pos.z)) {
            CommonPacket bcPacket = PacketType.OUT_BLOCK_CHANGE.newInstance();
            bcPacket.write((FieldAccessor)PacketType.OUT_BLOCK_CHANGE.position, (Object)pos);
            bcPacket.write((FieldAccessor)PacketType.OUT_BLOCK_CHANGE.blockData, (Object)WorldUtil.getBlockData((World)player.getWorld(), (IntVector3)pos));
            PacketUtil.sendPacket((Player)player, (CommonPacket)bcPacket);
        }
    }

    public void onPacketSend(PacketSendEvent event) {
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        MinecartMember<?> member;
        HumanHand hand;
        String status;
        if (!TCConfig.optimizeInteraction) {
            return;
        }
        if (event.getPlayer().isSneaking()) {
            return;
        }
        if (event.getType() == PacketType.IN_USE_ITEM_ON) {
            this.mainPacketListener.suppressAttacksFor(event.getPlayer(), 250);
        }
        if (event.getType() == PacketType.IN_PLAYER_ACTION && !(status = ((ServerboundPlayerActionPacketHandle.ActionHandle)event.getPacket().read(PacketType.IN_PLAYER_ACTION.status)).toString()).equals("START_DESTROY_BLOCK")) {
            return;
        }
        boolean isAttackClick = false;
        if (event.getType() == PacketType.IN_PLAYER_ACTION) {
            isAttackClick = true;
        } else if (event.getType() == PacketType.IN_SWING && (hand = PacketType.IN_SWING.getHand(event.getPacket(), (HumanEntity)event.getPlayer())) == HumanHand.getOffHand((HumanEntity)event.getPlayer())) {
            if (this.mainPacketListener.isAttackSuppressed(event.getPlayer())) {
                event.setCancelled(true);
                return;
            }
            isAttackClick = true;
        }
        if (isAttackClick) {
            member = MinecartMemberStore.getFromHitTest(Util.getRealEyeLocation(event.getPlayer()));
            if (member != null) {
                event.setCancelled(true);
                TCPacketListener.fakeAttack(member, event.getPlayer());
                if (event.getType() == PacketType.IN_PLAYER_ACTION) {
                    IntVector3 pos = (IntVector3)event.getPacket().read(PacketType.IN_PLAYER_ACTION.position);
                    this.cancelBlockChanges(event.getPlayer(), pos);
                }
            }
            return;
        }
        if (event.getType() == PacketType.IN_USE_ITEM || event.getType() == PacketType.IN_USE_ITEM_ON) {
            member = MinecartMemberStore.getFromHitTest(event.getPlayer().getEyeLocation());
            if (member != null) {
                HumanHand hand2;
                if (event.getType() == PacketType.IN_USE_ITEM) {
                    hand2 = PacketType.IN_USE_ITEM.getHand(event.getPacket(), (HumanEntity)event.getPlayer());
                } else {
                    hand2 = PacketType.IN_USE_ITEM_ON.getHand(event.getPacket(), (HumanEntity)event.getPlayer());
                    IntVector3 pos = (IntVector3)event.getPacket().read(PacketType.IN_USE_ITEM_ON.position);
                    BlockFace dir = (BlockFace)event.getPacket().read(PacketType.IN_USE_ITEM_ON.direction);
                    this.cancelBlockChanges(event.getPlayer(), pos);
                    this.cancelBlockChanges(event.getPlayer(), pos.add(dir));
                }
                event.setCancelled(true);
                this.mainPacketListener.fakeInteraction(member, event.getPlayer(), hand2, ((CommonMinecart)member.getEntity()).getLocation().toVector());
            }
            return;
        }
    }
}

