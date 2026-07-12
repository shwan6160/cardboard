/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Minecart
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartHandle;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CommonMinecart<T extends Minecart>
extends CommonEntity<T> {
    public final DataWatcher.EntityItem<Integer> metaShakingDirection = this.getDataItem(AbstractMinecartHandle.DATA_SHAKING_DIRECTION);
    public final DataWatcher.EntityItem<Float> metaShakingDamage = this.getDataItem(AbstractMinecartHandle.DATA_SHAKING_DAMAGE);
    public final DataWatcher.EntityItem<Integer> metaShakingFactor = this.getDataItem(AbstractMinecartHandle.DATA_SHAKING_FACTOR);
    public final DataWatcher.EntityItem<Integer> metaBlockOffset = this.getDataItem(AbstractMinecartHandle.DATA_BLOCK_OFFSET);

    public CommonMinecart(T base) {
        super(base);
    }

    public double getDamage() {
        return ((Minecart)this.entity).getDamage();
    }

    public Vector getDerailedVelocityMod() {
        return ((Minecart)this.entity).getDerailedVelocityMod();
    }

    public Vector getFlyingVelocityMod() {
        return ((Minecart)this.entity).getFlyingVelocityMod();
    }

    public double getMaxSpeed() {
        return ((Minecart)this.entity).getMaxSpeed();
    }

    public boolean isSlowWhenEmpty() {
        return ((Minecart)this.entity).isSlowWhenEmpty();
    }

    public void setSlowWhenEmpty(boolean arg0) {
        ((Minecart)this.entity).setSlowWhenEmpty(arg0);
    }

    public void setDamage(double damage) {
        ((Minecart)this.entity).setDamage(damage);
    }

    public void setDerailedVelocityMod(Vector arg0) {
        ((Minecart)this.entity).setDerailedVelocityMod(arg0);
    }

    public void setFlyingVelocityMod(Vector arg0) {
        ((Minecart)this.entity).setFlyingVelocityMod(arg0);
    }

    public void setMaxSpeed(double arg0) {
        ((Minecart)this.entity).setMaxSpeed(arg0);
    }

    public void setShakingDirection(int direction) {
        this.metaShakingDirection.set(direction);
    }

    public int getShakingDirection() {
        return this.metaShakingDirection.get();
    }

    public void setShakingFactor(int factor) {
        this.metaShakingFactor.set(factor);
    }

    public int getShakingFactor() {
        return this.metaShakingFactor.get();
    }

    public List<ItemStack> getBrokenDrops() {
        return Collections.emptyList();
    }

    public Material getCombinedItem() {
        return Material.MINECART;
    }

    public void setBlockOffset(int offsetPixels) {
        this.metaBlockOffset.set(offsetPixels);
    }

    public int getBlockOffset() {
        return this.metaBlockOffset.get();
    }

    @Deprecated
    public Material getBlockType() {
        return this.getBlock().getType();
    }

    @Deprecated
    public void setBlock(Material blockType, int blockData) {
        this.setBlock(BlockData.fromMaterialData(blockType, blockData));
    }

    public BlockData getBlock() {
        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            return this.getDataWatcher().get(AbstractMinecartHandle.DATA_CUSTOM_DISPLAY_BLOCK);
        }
        Integer value = this.getDataWatcher().get(AbstractMinecartHandle.DATA_BLOCK_TYPE);
        return value == null ? BlockData.AIR : BlockData.fromCombinedId(value);
    }

    public void setBlock(Material blockType) {
        this.setBlock(BlockData.fromMaterial(blockType));
    }

    public void setBlock(BlockData block) {
        DataWatcher meta = this.getDataWatcher();
        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            if (block.getType() == Material.AIR) {
                meta.set(AbstractMinecartHandle.DATA_CUSTOM_DISPLAY_BLOCK, null);
            } else {
                meta.set(AbstractMinecartHandle.DATA_CUSTOM_DISPLAY_BLOCK, block);
            }
        } else if (block.getType() == Material.AIR) {
            meta.set(AbstractMinecartHandle.DATA_BLOCK_TYPE, 0);
            meta.set(AbstractMinecartHandle.DATA_BLOCK_VISIBLE, false);
        } else {
            meta.set(AbstractMinecartHandle.DATA_BLOCK_TYPE, block.getCombinedId());
            meta.set(AbstractMinecartHandle.DATA_BLOCK_VISIBLE, true);
        }
    }

    @Override
    public boolean isVehicle() {
        return this.handle.isInstanceOf(MinecartHandle.T);
    }

    public void activate(Block activatorBlock, boolean activated) {
        AbstractMinecartHandle.T.activateMinecart.invoke(this.getHandle(), activatorBlock.getWorld(), activatorBlock.getX(), activatorBlock.getY(), activatorBlock.getZ(), activated);
    }
}

