/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Method;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class PortalHandler_1_14
extends PortalHandler {
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);
    private final Object _dummyEntityInstance;

    public PortalHandler_1_14() {
        ClassInterceptor interceptor = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getBukkitEntity")) {
                    return (inst, args) -> null;
                }
                return null;
            }
        };
        this._dummyEntityInstance = interceptor.createInstance(ServerPlayerHandle.T.getType());
    }

    @Override
    public void enable(CommonPlugin plugin) {
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
        return this._pta.findNetherPortal(startBlock);
    }

    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        if (this._pta.createNetherPortal(startBlock, this._dummyEntityInstance)) {
            return this._pta.findNetherPortal(startBlock);
        }
        return null;
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
    }

    @Override
    public Block findEndPlatform(World world) {
        return this._pta.findEndPlatform(world);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        return this._pta.createEndPlatform(world);
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
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.phys.Vec3"), @Template.Import(value="net.minecraft.core.BlockPos"), @Template.Import(value="net.minecraft.core.BlockPos$MutableBlockPos"), @Template.Import(value="net.minecraft.core.Direction"), @Template.Import(value="net.minecraft.server.level.ServerPlayer"), @Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.network.protocol.Packet"), @Template.Import(value="net.minecraft.network.protocol.game.ClientboundGameEventPacket"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.world.level.dimension.DimensionType")})
    @Template.InstanceType(value="net.minecraft.world.level.portal.PortalForcer")
    public static abstract class PortalTravelAgentHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {\n    ServerPlayer player = (ServerPlayer) entityPlayerRaw;\n    player.worldChangeInvuln = true;\n    player.decouple();\n    player.getWorldServer().removePlayer(player);\n    if (!player.viewingCredits) {\n        player.viewingCredits = true;\n        player.playerConnection.sendPacket((Packet) new ClientboundGameEventPacket(4, seenCredits ? 0.0F : 1.0F));\n    }\n           }")
        public abstract void showEndCredits(Object var1, boolean var2);

        @Template.Generated(value="public static boolean isMainEndWorld(org.bukkit.World world) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();\n    return world.getWorldProvider().getDimensionManager() == DimensionType.THE_END;\n}")
        public abstract boolean isMainEndWorld(World var1);

        @Template.Generated(value="public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    BlockPos blockposition = new BlockPos(startBlock.getX(), startBlock.getY(), startBlock.getZ());\n    PortalForcer agent = new PortalForcer(world);\n    net.minecraft.world.level.block.state.pattern.BlockPattern$Shape result = agent.a(blockposition, Vec3.ZERO, Direction.NORTH, 0.5, 1.0, true);\n    if (result == null) {\n        return null;\n    }\n               return startBlock.getWorld().getBlockAt(MathHelper.floor(result.a.x),\n                                            MathHelper.floor(result.a.y),\n                                            MathHelper.floor(result.a.z));\n}")
        public abstract Block findNetherPortal(Block var1);

        @Template.Generated(value="public static void createNetherPortal(org.bukkit.block.Block startBlock, Object dummyEntity) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    PortalForcer agent = new PortalForcer(world);\n    Entity dummy = (Entity) dummyEntity;\n    dummy.locX = (double) startBlock.getX()+0.5;\n    dummy.locY = (double) startBlock.getY();\n    dummy.locZ = (double) startBlock.getZ()+0.5;\n    return agent.a(dummy);\n}")
        public abstract boolean createNetherPortal(Block var1, Object var2);

        @Template.Generated(value="public static org.bukkit.block.Block findEndPlatform(org.bukkit.World bworld) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();\n    BlockPos platformPos = WorldProviderTheEnd.f;\n    int i = platformPos.getX();\n    int j = platformPos.getY() - 1;\n    int k = platformPos.getZ();\n    BlockPos$MutableBlockPos pos = new BlockPos$MutableBlockPos();\n    byte b0 = 1;\n    byte b1 = 0;\n    for (int l = -2; l <= 2; ++l) {\n        for (int i1 = -2; i1 <= 2; ++i1) {\n            for (int j1 = -1; j1 < 3; ++j1) {\n                int k1 = i + i1 * b0 + l * b1;\n                int l1 = j + j1;\n                int i2 = k + i1 * b1 - l * b0;\n                boolean flag = j1 < 0;\n                pos.d(k1, l1, i2);\n                if (world.getType(pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {\n                    return null;\n                }\n            }\n        }\n               }\n    return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());\n}")
        public abstract Block findEndPlatform(World var1);

        @Template.Generated(value="public static org.bukkit.block.Block createEndPlatform(org.bukkit.World bworld) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();\n    BlockPos platformPos = WorldProviderTheEnd.f;\n\n    // Code is embedded deep inside ServerPlayer.a(DimensionType, TeleportCause)\n    int i = platformPos.getX();\n    int j = platformPos.getY() - 1;\n    int k = platformPos.getZ();\n    boolean flag = true;\n    boolean flag1 = false;\n    org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(world);\n    BlockPos$MutableBlockPos blockposition = new BlockPos$MutableBlockPos();\n    for (int l = -2; l <= 2; ++l) {\n        for (int i1 = -2; i1 <= 2; ++i1) {\n            for (int j1 = -1; j1 < 3; ++j1) {\n                int k1 = i + i1 * 1 + l * 0;\n                int l1 = j + j1;\n                int i2 = k + i1 * 0 - l * 1;\n                boolean flag2 = j1 < 0;\n                blockposition.d(k1, l1, i2);\n                world.setTypeUpdate((BlockPos) blockposition, flag2 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData());\n            }\n        }\n               }\n\n    return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());\n}")
        public abstract Block createEndPlatform(World var1);
    }
}

