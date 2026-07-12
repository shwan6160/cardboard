package org.cardboardpowered.mixin.world.level.block.entity;

import java.util.List;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.cardboardpowered.mixin.world.SimpleContainerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends SimpleContainerMixin {

    // TODO Add FurnaceBurnEvent, FurnanceSmeltEvent, FurnaceExtractEvent
    // TODO

    @Shadow
    public int maxStack;

    @Shadow
    public List<HumanEntity> transaction;

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public void cardboard$setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public Location getLocation() {
        net.minecraft.world.level.block.entity.BlockEntity be = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
        if (be.getLevel() == null) return null;
        return new Location(((org.cardboardpowered.bridge.world.level.LevelBridge) be.getLevel()).cardboard$getWorld(), be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ());
    }

    @Override
    public InventoryHolder getOwner() {
        net.minecraft.world.level.block.entity.BlockEntity be = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
        if (be.getLevel() == null) return null;
        org.bukkit.block.Block block = ((org.cardboardpowered.bridge.world.level.LevelBridge) be.getLevel()).cardboard$getWorld().getBlockAt(be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ());
        if (block.getState() instanceof InventoryHolder) {
            return (InventoryHolder) block.getState();
        }
        return null;
    }

}