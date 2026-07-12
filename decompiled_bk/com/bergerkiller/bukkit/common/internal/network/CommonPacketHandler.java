/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.network.PacketHandlerHooked;
import com.bergerkiller.bukkit.common.internal.network.PacketHandlerRegistration;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommonPacketHandler
extends PacketHandlerHooked {
    private static final String[] incompatibilities = new String[]{"Spout"};

    @Override
    public String getName() {
        return "a PlayerConnection hook";
    }

    @Override
    public boolean onEnable() {
        if (!super.onEnable()) {
            return false;
        }
        for (String incompatibility : incompatibilities) {
            if (!CommonUtil.isPluginInDirectory(incompatibility)) continue;
            CommonPacketHandler.failPacketListener(incompatibility);
            return false;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CommonChannelListener.bind(player);
        }
        return true;
    }

    @Override
    public boolean onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CommonChannelListener.unbind(player);
        }
        return true;
    }

    @Override
    public void onPlayerJoin(Player player) {
        CommonChannelListener.bind(player);
    }

    private static void failPacketListener(String pluginName) {
        CommonPacketHandler.showFailureMessage("a plugin conflict, namely " + pluginName);
    }

    private static void showFailureMessage(String causeName) {
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by " + causeName);
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
    }

    @ChannelHandler.Sharable
    public static class CommonChannelListener
    extends ChannelDuplexHandler {
        private final PacketHandlerHooked handler = (PacketHandlerHooked)CommonPlugin.getInstance().getPacketHandler();
        private final Player player;

        public static void bind(Player player) {
            Object entityPlayer = HandleConversion.toEntityHandle((Entity)player);
            ServerGamePacketListenerImplHandle playerConnection = ServerPlayerHandle.T.playerConnection.get(entityPlayer);
            if (playerConnection == null) {
                return;
            }
            Object networkManager = playerConnection.getNetworkManager();
            Channel channel = ConnectionHandle.T.channel.get(networkManager);
            CommonChannelListener listener = new CommonChannelListener(player);
            try {
                channel.pipeline().addBefore("packet_handler", "bkcommonlib", (ChannelHandler)listener);
            }
            catch (NoSuchElementException ex) {
                channel.pipeline().addFirst("bkcommonlib", (ChannelHandler)listener);
            }
        }

        public static void unbind(Player player) {
            Object entityPlayer = HandleConversion.toEntityHandle((Entity)player);
            ServerGamePacketListenerImplHandle playerConnection = ServerPlayerHandle.T.playerConnection.get(entityPlayer);
            if (playerConnection == null) {
                return;
            }
            Object networkManager = playerConnection.getNetworkManager();
            Channel channel = ConnectionHandle.T.channel.get(networkManager);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove("bkcommonlib");
                return null;
            });
        }

        public CommonChannelListener(Player player) {
            this.player = player;
        }

        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            PacketHandlerRegistration.HandlerResult result = this.handler.handlePacketSend(this.player, msg, false);
            if (!result.isCancelled) {
                super.write(ctx, result.packet, promise);
            }
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            PacketHandlerRegistration.HandlerResult result = this.handler.handlePacketReceive(this.player, msg, false);
            if (!result.isCancelled) {
                super.channelRead(ctx, result.packet);
            }
        }
    }
}

