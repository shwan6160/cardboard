package org.cardboardpowered.mixin.server.network;

import org.bukkit.craftbukkit.block.CraftSign;
import org.cardboardpowered.bridge.server.MinecraftServerBridge;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.cardboardpowered.bridge.world.level.block.entity.SignBlockEntityBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.cardboardpowered.util.MixinInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinInfo(events = {"SignChangeEvent"})
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 800)
public class ServerGamePacketListenerImplMixin_SignUpdateEvent {

    @Shadow 
    public ServerPlayer player;

    @SuppressWarnings("deprecation")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundSignUpdatePacket;getLines()[Ljava/lang/String;"), method = "handleSignUpdate", cancellable = true)
    public void fireSignUpdateEvent(ServerboundSignUpdatePacket packet, CallbackInfo ci) {
        try {
            String[] astring = packet.getLines();
    
            Player player = (Player) ((ServerPlayerBridge)this.player).getBukkitEntity();
            int x = packet.getPos().getX();
            int y = packet.getPos().getY();
            int z = packet.getPos().getZ();
            String[] lines = new String[4];
    
            for (int i = 0; i < astring.length; ++i)
                lines[i] = ChatFormatting.stripFormatting(ChatFormatting.stripFormatting(astring[i]));
            ((MinecraftServerBridge)CraftServer.server).cardboard_runOnMainThread(() -> {
                try {
                    org.bukkit.block.sign.Side side = packet.isFrontText() ? org.bukkit.block.sign.Side.FRONT : org.bukkit.block.sign.Side.BACK;
                    SignChangeEvent event = new SignChangeEvent((org.bukkit.craftbukkit.block.CraftBlock) player.getWorld().getBlockAt(x, y, z), player, lines, side);
                    CraftServer.INSTANCE.getPluginManager().callEvent(event);
            
                    if (!event.isCancelled()) {
                        BlockEntity tileentity = this.player.level().getBlockEntity(packet.getPos());
                        if (tileentity instanceof SignBlockEntity tileentitysign) {
                            net.minecraft.world.level.block.entity.SignText oldText = tileentitysign.getText(packet.isFrontText());
                            net.minecraft.network.chat.Component[] components = CraftSign.sanitizeLines(event.getLines());
                            net.minecraft.world.level.block.entity.SignText newText = new net.minecraft.world.level.block.entity.SignText(components, components, oldText.getColor(), oldText.hasGlowingText());
                            tileentitysign.setText(newText, packet.isFrontText());
                            tileentitysign.setChanged();
                            this.player.level().sendBlockUpdated(packet.getPos(), tileentitysign.getBlockState(), tileentitysign.getBlockState(), 3);
                        }
                     }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
