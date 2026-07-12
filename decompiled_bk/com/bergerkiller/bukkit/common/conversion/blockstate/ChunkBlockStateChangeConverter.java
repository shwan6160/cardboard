/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.conversion.blockstate;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelChunkPacketDataHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.List;

public final class ChunkBlockStateChangeConverter
extends DuplexConverter<Object, BlockStateChange> {
    private static final TypeDeclaration INPUT_TYPE = TypeDeclaration.fromClass(ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.T.getType());
    private static final TypeDeclaration OUTPUT_TYPE = TypeDeclaration.fromClass(BlockStateChange.class);
    private final int chunkX;
    private final int chunkZ;

    public static List<BlockStateChange> convertList(List<?> list, int chunkX, int chunkZ) {
        return new ConvertingList<BlockStateChange>(list, (DuplexConverter<?, BlockStateChange>)new ChunkBlockStateChangeConverter(chunkX, chunkZ));
    }

    public ChunkBlockStateChangeConverter(int chunkX, int chunkZ) {
        super(INPUT_TYPE, OUTPUT_TYPE);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public BlockStateChange convertInput(Object value) {
        ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle handle = ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.createHandle(value);
        IntVector3 position = handle.getPosition(this.chunkX, this.chunkZ);
        BlockStateType type = handle.getType();
        CommonTagCompound initialMetadata = handle.getTag();
        if (initialMetadata != null) {
            return BlockStateChange.deferred(position, type, LogicUtil.constantSupplier(initialMetadata), () -> true);
        }
        DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(() -> {
            CommonTagCompound metadata = new CommonTagCompound();
            handle.setTag(metadata);
            return metadata;
        });
        return BlockStateChange.deferred(position, type, metadataSupplier, metadataSupplier::isInitialized);
    }

    @Override
    public Object convertOutput(BlockStateChange value) {
        return ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.encodeRaw(value.getPosition(), value.getType(), value.hasMetadata() ? value.getMetadata() : null);
    }
}

