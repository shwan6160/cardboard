package org.cardboardpowered.mixin.commands;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.cardboardpowered.bridge.commands.CommandSourceStackBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.bridge.commands.CommandSourceBridge;
import org.spongepowered.asm.mixin.Shadow;
import org.jetbrains.annotations.Nullable;
import org.cardboardpowered.bridge.world.level.LevelBridge;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements CommandSourceStackBridge, io.papermc.paper.command.brigadier.CommandSourceStack {
    @Shadow
    public CommandSource source;

    @Shadow
    public Vec3 getPosition() { return null; }

    @Shadow
    public ServerLevel getLevel() { return null; }

    @Shadow
    public Vec2 getRotation() { return null; }

    @Shadow
    public @Nullable Entity getEntity() { return null; }

    @Shadow
    public CommandSourceStack withPosition(Vec3 position) { return null; }

    @Shadow
    public CommandSourceStack withRotation(Vec2 rotation) { return null; }

    @Shadow
    public CommandSourceStack withEntity(Entity entity) { return null; }

    @Shadow
    public CommandSourceStack withLevel(ServerLevel level) { return null; }

    // CraftBukkit start
    public org.bukkit.command.CommandSender getBukkitSender() {
        return ((CommandSourceBridge)this.source).getBukkitSender((CommandSourceStack)(Object)this);
    }
    // CraftBukkit end

    @Override
    public Location getLocation() {
        Vec3 pos = getPosition();
        Vec2 rot = getRotation();
        return new Location(((LevelBridge) getLevel()).cardboard$getWorld(), pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    @Override
    public org.bukkit.command.CommandSender getSender() {
        return getBukkitSender();
    }

    @Override
    public org.bukkit.entity.Entity getExecutor() {
        Entity entity = getEntity();
        return entity == null ? null : entity.getBukkitEntity();
    }

    @Override
    public io.papermc.paper.command.brigadier.CommandSourceStack withLocation(Location location) {
        CommandSourceStack stack = (CommandSourceStack) (Object) this;
        if (location.getWorld() != null) {
            stack = stack.withLevel((ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) location.getWorld()).getHandle());
        }
        stack = stack.withPosition(new Vec3(location.getX(), location.getY(), location.getZ()));
        stack = stack.withRotation(new Vec2(location.getPitch(), location.getYaw()));
        return (io.papermc.paper.command.brigadier.CommandSourceStack) (Object) stack;
    }

    @Override
    public io.papermc.paper.command.brigadier.CommandSourceStack withExecutor(org.bukkit.entity.Entity executor) {
        CommandSourceStack stack = (CommandSourceStack) (Object) this;
        stack = stack.withEntity(executor == null ? null : ((org.bukkit.craftbukkit.entity.CraftEntity) executor).getHandle());
        return (io.papermc.paper.command.brigadier.CommandSourceStack) (Object) stack;
    }
}