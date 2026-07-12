/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUpdateSignHandle;
import java.util.concurrent.atomic.AtomicReference;

class BlockStateChangePacketHandler_1_8_to_1_9_2
extends BlockStateChangePacketHandler {
    private static final String[] LINE_META_KEYS = new String[]{"Text1", "Text2", "Text3", "Text4"};

    BlockStateChangePacketHandler_1_8_to_1_9_2() {
    }

    @Override
    public void enable() {
        this.register(PacketType.OUT_UPDATE_SIGN, (player, commonPacket, listener) -> {
            PacketPlayOutUpdateSignHandle packet = PacketPlayOutUpdateSignHandle.createHandle(commonPacket.getHandle());
            AtomicReference linesBeforeRef = new AtomicReference();
            DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(() -> {
                ChatText[] lines = packet.getLines();
                CommonTagCompound metadata = new CommonTagCompound();
                String[] linesBefore = new String[4];
                for (int n = 0; n < 4; ++n) {
                    linesBefore[n] = lines[n].getJson();
                    metadata.putValue(LINE_META_KEYS[n], linesBefore[n]);
                }
                linesBeforeRef.set(linesBefore);
                return metadata;
            });
            if (!listener.onBlockChange(player, BlockStateChange.deferred(packet.getPosition(), BlockStateType.SIGN, metadataSupplier, () -> true))) {
                return false;
            }
            if (metadataSupplier.isInitialized()) {
                CommonTagCompound metadata = metadataSupplier.get();
                String[] linesBefore = (String[])linesBeforeRef.get();
                ChatText[] newLines = (ChatText[])packet.getLines().clone();
                boolean changed = false;
                for (int n = 0; n < 4; ++n) {
                    String newLine = metadata.getValue(LINE_META_KEYS[n], "");
                    if (linesBefore[n].equals(newLine)) continue;
                    newLines[n] = ChatText.fromJson(newLine);
                    changed = true;
                }
                if (changed) {
                    packet.setLines(newLines);
                }
            }
            return true;
        });
    }
}

