package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperSulfurCubeContent(
    net.minecraft.world.item.component.SulfurCubeContent impl
) implements SulfurCubeContent, Handleable<net.minecraft.world.item.component.SulfurCubeContent> {

    @Override
    public net.minecraft.world.item.component.SulfurCubeContent getHandle() {
        return this.impl;
    }

    @Override
    public ItemStack absorbedItem() {
        if (this.impl.absorbedBlockItemStack() == null) {
            return null;
        }
        return CraftItemStack.asBukkitCopy(this.impl.absorbedBlockItemStack().create());
    }
}
