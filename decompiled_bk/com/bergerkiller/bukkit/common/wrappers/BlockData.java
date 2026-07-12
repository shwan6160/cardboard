/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.Attachable
 *  org.bukkit.material.Directional
 *  org.bukkit.material.MaterialData
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockDataRegistry;
import com.bergerkiller.bukkit.common.wrappers.BlockProperty;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

public abstract class BlockData
extends BlockDataRegistry {
    public abstract void loadBlock(Object var1);

    public abstract void loadBlockData(Object var1);

    @Deprecated
    public final void loadMaterialData(Material material, int data) {
        this.loadMaterialData(new MaterialData(material, (byte)data));
    }

    @Deprecated
    public abstract void loadMaterialData(MaterialData var1);

    public final Object getBlockRaw() {
        return Template.Handle.getRaw(this.getBlock());
    }

    public abstract BlockHandle getBlock();

    public abstract Object getData();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BlockData) {
            BlockData data = (BlockData)o;
            return data.getCombinedId() == this.getCombinedId();
        }
        return false;
    }

    public int hashCode() {
        return this.getCombinedId();
    }

    public String toString() {
        Object b = this.getData();
        return b == null ? "[null]" : b.toString();
    }

    public abstract String getBlockName();

    public abstract String serializeToString();

    public final BlockRenderOptions getDefaultRenderOptions() {
        return this.getRenderOptions(null, 0, 0, 0);
    }

    public final BlockRenderOptions getRenderOptions(Block block) {
        return this.getRenderOptions(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public abstract BlockRenderOptions getRenderOptions(World var1, int var2, int var3, int var4);

    @Deprecated
    public abstract int getRawData();

    @Deprecated
    public abstract int getCombinedId();

    @Deprecated
    public abstract int getCombinedId_1_8_8();

    public abstract Material getType();

    public abstract Material getLegacyType();

    public abstract boolean hasLegacyType();

    public abstract ItemStack createItem(int var1);

    public final CommonItemStack createCommonItem(int amount) {
        return CommonItemStack.of(this.createItem(amount));
    }

    public abstract BlockData setProperty(String var1, Object var2);

    public abstract BlockData setProperty(BlockProperty<?> var1, Object var2);

    public abstract <T> T getProperty(String var1, Class<T> var2);

    public abstract <T extends Comparable<?>> T getProperty(BlockProperty<T> var1);

    public abstract Collection<BlockProperty<?>> getProperties();

    public abstract BlockProperty<?> getPropertyKey(String var1);

    public final boolean isType(Material type) {
        if (MaterialUtil.isLegacyType(type)) {
            return this.getLegacyType() == type && this.hasLegacyType();
        }
        return this.getType() == type;
    }

    public final boolean isType(Material ... types) {
        for (Material type : types) {
            if (!this.isType(type)) continue;
            return true;
        }
        return false;
    }

    public final MaterialData newMaterialData() {
        return this.getMaterialData().clone();
    }

    public abstract MaterialData getMaterialData();

    public BlockFace getFacingDirection() {
        MaterialData data = this.getMaterialData();
        if (data instanceof Directional) {
            return ((Directional)data).getFacing();
        }
        return BlockFace.NORTH;
    }

    public BlockFace getAttachedFace() {
        MaterialData data = this.getMaterialData();
        if (data instanceof Attachable) {
            return ((Attachable)data).getAttachedFace();
        }
        return BlockFace.DOWN;
    }

    public final <T extends MaterialData> T newMaterialData(Class<T> type) {
        return (T)((MaterialData)LogicUtil.tryCast(this.newMaterialData(), type));
    }

    public abstract ResourceKey<SoundEffect> getStepSound();

    public abstract ResourceKey<SoundEffect> getPlaceSound();

    public abstract ResourceKey<SoundEffect> getBreakSound();

    public abstract int getOpacity(World var1, int var2, int var3, int var4);

    public abstract int getOpacity(Block var1);

    public abstract BlockFaceSet getOpaqueFaces(World var1, int var2, int var3, int var4);

    public abstract BlockFaceSet getOpaqueFaces(Block var1);

    @Deprecated
    public abstract int getEmission();

    public abstract int getEmission(World var1, int var2, int var3, int var4);

    public abstract int getEmission(Block var1);

    public abstract boolean isOccluding(Block var1);

    public abstract boolean isOccluding(World var1, int var2, int var3, int var4);

    public abstract boolean isSuffocating(Block var1);

    public abstract boolean isPowerSource();

    public final boolean isBuildable() {
        return this.isSolid();
    }

    public abstract boolean isSolid();

    public abstract float getDamageResilience();

    public abstract boolean canSupportTop(Block var1);

    public abstract boolean canSupportOnFace(Block var1, BlockFace var2);

    public abstract AABBHandle getBoundingBox(Block var1);

    public abstract AABBHandle getInteractableBox(Block var1);

    public final void dropNaturally(Block block, float yield) {
        this.dropNaturally(block, yield, 0);
    }

    public final void dropNaturally(Block block, float yield, int chance) {
        this.dropNaturally(block.getWorld(), block.getX(), block.getY(), block.getZ(), yield, 0);
    }

    public final void dropNaturally(World world, int x, int y, int z, float yield) {
        this.dropNaturally(world, x, y, z, yield, 0);
    }

    public abstract void dropNaturally(World var1, int var2, int var3, int var4, float var5, int var6);

    public final void destroy(Block block, float yield) {
        this.destroy(block.getWorld(), block.getX(), block.getY(), block.getZ(), yield);
    }

    public abstract void destroy(World var1, int var2, int var3, int var4, float var5);

    public final void stepOn(World world, int x, int y, int z, Entity entity) {
        this.stepOn(world, new IntVector3(x, y, z), entity);
    }

    public abstract void stepOn(World var1, IntVector3 var2, Entity var3);
}

