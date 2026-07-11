package org.cardboardpowered.mixin.commands;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.bridge.commands.CommandSourceBridge;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceBridge {

	// @Override
	// public CommandSender getBukkitSender(ServerCommandSource source);

	@Override
	default CommandSender getBukkitSender(CommandSourceStack source) {
		if (source.isPlayer()) {
			// Cardboard Note: Redirect ServerPlayerEntity$3 to ServerPlayerEntity
			return ( (CommandSourceBridge) source.getPlayer() ).getBukkitSender(source);
		}

		if (null != source.entity) {
			return ( (CommandSourceBridge) source.getEntity() ).getBukkitSender(source);
		}
		return org.bukkit.Bukkit.getConsoleSender();
	}

}