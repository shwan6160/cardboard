/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerPortalEvent
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

class PortalHandler_1_9
extends PortalHandler {
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);

    @Override
    public void enable(CommonPlugin plugin) {
        plugin.register(new Listener(){

            @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
            public void onPortalEvent(PlayerPortalEvent event) {
                if (ServerPlayerHandle.fromBukkit(event.getPlayer()).isViewingCredits()) {
                    event.setCancelled(true);
                }
            }
        });
        plugin.register(new PacketListener(){

            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
            }

            @Override
            public void onPacketSend(PacketSendEvent event) {
                if (ServerPlayerHandle.fromBukkit(event.getPlayer()).isViewingCredits()) {
                    event.setCancelled(true);
                }
            }
        }, PacketType.OUT_WORLD_EVENT);
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }

    @Override
    public void forceInitialization() {
        this._pta.forceInitialization();
    }

    @Override
    public Block findNetherPortal(Block startBlock, int radius) {
        return this._pta.findNetherPortal(startBlock, radius);
    }

    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        int radius = ServerLevelHandle.fromBukkit(startBlock.getWorld()).getNetherPortalCreateRadius();
        if (this._pta.createNetherPortal(startBlock, radius)) {
            return this._pta.findNetherPortal(startBlock, radius);
        }
        return null;
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
    }

    @Override
    public Block findEndPlatform(World world) {
        return this._pta.findOrCreateEndPlatform(world, false);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        return this._pta.findOrCreateEndPlatform(world, true);
    }

    @Override
    public boolean isMainEndWorld(World world) {
        return this._pta.isMainEndWorld(world);
    }

    @Override
    public void showEndCredits(Player player) {
        ServerPlayerHandle ep = ServerPlayerHandle.fromBukkit(player);
        this._pta.showEndCredits(HandleConversion.toEntityHandle((Entity)player), ep.hasSeenCredits());
        ep.setHasSeenCredits(true);
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.core.BlockPos"), @Template.Import(value="net.minecraft.world.level.dimension.DimensionType"), @Template.Import(value="net.minecraft.server.level.ServerPlayer"), @Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.network.protocol.Packet"), @Template.Import(value="net.minecraft.network.protocol.game.ClientboundGameEventPacket")})
    @Template.InstanceType(value="net.minecraft.world.level.portal.PortalForcer")
    public static abstract class PortalTravelAgentHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {\n    ServerPlayer player = (ServerPlayer) entityPlayerRaw;\n#if version >= 1.10.2\n    player.worldChangeInvuln = true;\n#elseif version >= 1.9.4\n    #require net.minecraft.server.level.ServerPlayer protected boolean worldChangeInvuln:ck;\n    player#worldChangeInvuln = true;\n#elseif version >= 1.9\n    #require net.minecraft.server.level.ServerPlayer protected boolean worldChangeInvuln:cj;\n    player#worldChangeInvuln = true;\n#endif\n               player.world.kill((net.minecraft.world.entity.Entity) player);\n    if (!player.viewingCredits) {\n        player.viewingCredits = true;\n        player.playerConnection.sendPacket((Packet) new ClientboundGameEventPacket(4, seenCredits ? 0.0F : 1.0F));\n    }\n           }")
        public abstract void showEndCredits(Object var1, boolean var2);

        @Template.Generated(value="public static boolean isMainEndWorld(org.bukkit.World world) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();\n#if version >= 1.13.1\n    return world.dimension == DimensionType.THE_END;\n#else\n               return world.dimension == 1;\n#endif\n           }")
        public abstract boolean isMainEndWorld(World var1);

        @Template.Generated(value="public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock, int createRadius) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    BlockPos blockposition = new BlockPos(startBlock.getX(), startBlock.getY(), startBlock.getZ());\n    PortalForcer agent = new PortalForcer(world);\n    BlockPos result = agent.findPortal((double) startBlock.getX(),\n                                            (double) startBlock.getY(),\n                                            (double) startBlock.getZ(),\n                                            createRadius);\n    if (result == null) {\n        return null;\n    }\n               return startBlock.getWorld().getBlockAt(result.getX(), result.getY(), result.getZ());\n}")
        public abstract Block findNetherPortal(Block var1, int var2);

        @Template.Generated(value="public static boolean createNetherPortal(org.bukkit.block.Block startBlock, int createRadius) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    PortalForcer agent = new PortalForcer(world);\n    return agent.createPortal((double) startBlock.getX() + 0.5,\n                              (double) startBlock.getY(),\n                              (double) startBlock.getZ() + 0.5,\n                              createRadius);\n}")
        public abstract boolean createNetherPortal(Block var1, int var2);

        @Template.Generated(value="public static org.bukkit.block.Block findOrCreateEndPlatform(org.bukkit.World bworld, boolean create) {\n    #require net.minecraft.world.level.portal.PortalForcer private BlockPos findEndPortal(BlockPos portal);\n    #require net.minecraft.world.level.portal.PortalForcer private BlockPos createEndPortal(double x, double y, double z);\n\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();\n    PortalForcer agent = new PortalForcer(world);\n\n#if version >= 1.13\n    BlockPos platformPos = WorldProviderTheEnd.g;\n#else\n               World the_end_world = MinecraftServer.getServer().getWorldServer(1);\n    BlockPos platformPos = (the_end_world == null) ? null : the_end_world.worldProvider.h();\n    if (platformPos == null) {\n        platformPos = new BlockPos(100, 50, 0);\n    }\n           #endif\n\n    if (create) {\n        BlockPos position = agent#createEndPortal((double)platformPos.getX(),(double)platformPos.getY(),(double)platformPos.getZ());\n        return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());\n    } else {\n        BlockPos position = agent#findEndPortal(platformPos);\n        if (position == null) {\n            return null;\n        } else {\n            return bworld.getBlockAt(position.getX(), position.getY()-1, position.getZ());\n        }\n               }\n}")
        public abstract Block findOrCreateEndPlatform(World var1, boolean var2);
    }
}

