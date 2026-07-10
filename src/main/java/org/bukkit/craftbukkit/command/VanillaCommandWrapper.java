package org.bukkit.craftbukkit.command;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.CommandSender;
import net.minecraft.commands.CommandSourceStack;
import org.cardboardpowered.impl.command.MinecraftCommandWrapper;

public class VanillaCommandWrapper extends BukkitCommand {
	
	public VanillaCommandWrapper(String name) {
		super(name);
	}

	public static CommandSourceStack getListener(CommandSender sender) {
		return MinecraftCommandWrapper.getCommandSource(sender);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		return false;
	}
}
