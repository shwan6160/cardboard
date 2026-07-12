package org.cardboardpowered.mixin.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.cardboardpowered.mixin.world.SimpleContainerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.cardboardpowered.bridge.world.level.LevelBridge;

@Mixin(PlayerEnderChestContainer.class)
public abstract class PlayerEnderChestContainerMixin extends SimpleContainerMixin {

	@Unique
	private Player cardboard$owner;

	@Inject(method = "stillValid", at = @At("HEAD"))
	private void onStillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
		this.cardboard$owner = player;
	}
	
    @Shadow private EnderChestBlockEntity activeChest;

    public InventoryHolder getBukkitOwner() {
        return this.cardboard$owner != null ? (InventoryHolder) ((org.cardboardpowered.bridge.world.entity.EntityBridge) this.cardboard$owner).getBukkitEntity() : null;
    }

    @Override
    public Location getLocation() {
        return new Location(((LevelBridge)this.activeChest.getLevel()).cardboard$getWorld(), this.activeChest.getBlockPos().getX(), this.activeChest.getBlockPos().getY(), this.activeChest.getBlockPos().getZ());
    }

}