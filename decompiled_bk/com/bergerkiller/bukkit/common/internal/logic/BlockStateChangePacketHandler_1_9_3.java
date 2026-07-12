/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacketHandle;
import java.util.Iterator;

class BlockStateChangePacketHandler_1_9_3
extends BlockStateChangePacketHandler {
    BlockStateChangePacketHandler_1_9_3() {
    }

    @Override
    public void enable() {
        this.register(PacketType.OUT_TILE_ENTITY_DATA, (player, commonPacket, listener) -> {
            ClientboundBlockEntityDataPacketHandle packet = ClientboundBlockEntityDataPacketHandle.createHandle(commonPacket.getHandle());
            BlockStateType tileType = packet.getType();
            if (tileType == null) {
                return true;
            }
            CommonTagCompound metadata = packet.getData();
            if (metadata != null) {
                BlockStateChange change = BlockStateChange.deferred(packet.getPosition(), tileType, LogicUtil.constantSupplier(metadata), () -> true);
                if (!listener.onBlockChange(player, change)) {
                    return false;
                }
            } else {
                DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(CommonTagCompound::new);
                BlockStateChange change = BlockStateChange.deferred(packet.getPosition(), tileType, metadataSupplier, metadataSupplier::isInitialized);
                if (!listener.onBlockChange(player, change)) {
                    return false;
                }
                if (metadataSupplier.isInitialized() && !metadataSupplier.get().isEmpty()) {
                    packet.setData(metadataSupplier.get());
                }
            }
            return true;
        });
        this.register(PacketType.OUT_MAP_CHUNK, (player, commonPacket, listener) -> {
            ClientboundLevelChunkWithLightPacketHandle packet = ClientboundLevelChunkWithLightPacketHandle.createHandle(commonPacket.getHandle());
            Iterator<BlockStateChange> iter = packet.getBlockStates().iterator();
            while (iter.hasNext()) {
                BlockStateChange change = iter.next();
                if (listener.onBlockChange(player, change)) continue;
                iter.remove();
            }
            return true;
        });
    }
}

