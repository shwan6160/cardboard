/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.material.MaterialData
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.proxies;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.proxies.ProxyBase;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class BlockStateProxy
extends ProxyBase<BlockState>
implements BlockState {
    public BlockStateProxy(BlockState base) {
        super(base);
    }

    public World getWorld() {
        return ((BlockState)this.base).getWorld();
    }

    public int getX() {
        return ((BlockState)this.base).getX();
    }

    public int getY() {
        return ((BlockState)this.base).getY();
    }

    public int getZ() {
        return ((BlockState)this.base).getZ();
    }

    public Chunk getChunk() {
        return ((BlockState)this.base).getChunk();
    }

    public void setData(MaterialData data) {
        ((BlockState)this.base).setData(data);
    }

    public MaterialData getData() {
        return ((BlockState)this.base).getData();
    }

    public void setType(Material type) {
        ((BlockState)this.base).setType(type);
    }

    @Deprecated
    public boolean setTypeId(int type) {
        ((BlockState)this.base).setType(CommonLegacyMaterials.getMaterialFromId(type));
        return true;
    }

    public Material getType() {
        return ((BlockState)this.base).getType();
    }

    @Deprecated
    public int getTypeId() {
        return CommonLegacyMaterials.getIdFromMaterial(this.getType());
    }

    public byte getLightLevel() {
        return ((BlockState)this.base).getLightLevel();
    }

    public Block getBlock() {
        return ((BlockState)this.base).getBlock();
    }

    public boolean update() {
        return ((BlockState)this.base).update();
    }

    public boolean update(boolean force) {
        return ((BlockState)this.base).update(force);
    }

    public byte getRawData() {
        return ((BlockState)this.base).getRawData();
    }

    public Location getLocation() {
        return ((BlockState)this.base).getLocation();
    }

    public Location getLocation(Location loc) {
        return ((BlockState)this.base).getLocation(loc);
    }

    public void setRawData(byte data) {
        ((BlockState)this.base).setRawData(data);
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        ((BlockState)this.base).setMetadata(metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return ((BlockState)this.base).getMetadata(metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return ((BlockState)this.base).hasMetadata(metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        ((BlockState)this.base).removeMetadata(metadataKey, owningPlugin);
    }

    public boolean update(boolean arg0, boolean arg1) {
        return ((BlockState)this.base).update(arg0, arg1);
    }

    public boolean isPlaced() {
        if (BlockStateHandle.T.isPlaced.isAvailable()) {
            return BlockStateHandle.T.isPlaced.invoke(this.base);
        }
        return true;
    }

    public BlockState copy() {
        return this;
    }

    public BlockState copy(Location location) {
        return null;
    }

    public BlockData getBlockData() {
        return ((BlockState)this.base).getBlockData();
    }

    public void setBlockData(BlockData arg0) {
        ((BlockState)this.base).setBlockData(arg0);
    }

    static {
        BlockStateProxy.validate(BlockStateProxy.class);
    }
}

