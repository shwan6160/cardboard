package org.cardboardpowered.mixin.world.clock;

import net.minecraft.world.clock.ServerClockManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin {

    public void init(net.minecraft.server.MinecraftServer server, net.minecraft.server.level.ServerLevel level) {
        ((ServerClockManager) (Object) this).init(server);
    }
}
