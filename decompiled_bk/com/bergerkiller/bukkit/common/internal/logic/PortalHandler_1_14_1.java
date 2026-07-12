/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.PortalCreateEvent
 *  org.bukkit.event.world.PortalCreateEvent$CreateReason
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Method;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

class PortalHandler_1_14_1
extends PortalHandler
implements Listener {
    private static final Material OBSIDIAN_TYPE = MaterialUtil.getFirst("OBSIDIAN", "LEGACY_OBSIDIAN");
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);
    private boolean _anticipatingNetherPairCreateEvent = false;
    private Block _createdNetherPortalBlock;
    private boolean _endPlatformCreated = false;
    private Entity _bukkitEntityReturnedByDummy;
    private final Object _dummyEntityInstance;

    public PortalHandler_1_14_1() {
        ClassInterceptor interceptor = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getBukkitEntity")) {
                    return (inst, args) -> PortalHandler_1_14_1.this._bukkitEntityReturnedByDummy;
                }
                return null;
            }
        };
        this._dummyEntityInstance = interceptor.createInstance(ServerPlayerHandle.T.getType());
    }

    @Override
    public void enable(CommonPlugin plugin) {
        plugin.register(this);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        try {
            this._anticipatingNetherPairCreateEvent = true;
            this._createdNetherPortalBlock = null;
            this._bukkitEntityReturnedByDummy = initiator;
            this._pta.createNetherPortal(startBlock, orientation, initiator == null ? null : HandleConversion.toEntityHandle(initiator), this._dummyEntityInstance, ServerLevelHandle.fromBukkit(startBlock.getWorld()).getNetherPortalCreateRadius());
            Block block = this._createdNetherPortalBlock;
            return block;
        }
        finally {
            this._anticipatingNetherPairCreateEvent = false;
        }
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
        if (((Boolean)MaterialUtil.ISNETHERPORTAL.get(netherPortalBlock)).booleanValue()) {
            this._pta.storeNetherPortal(netherPortalBlock);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPortalCreated(PortalCreateEvent event) {
        if (event.getReason() == PortalCreateEvent.CreateReason.NETHER_PAIR) {
            if (!this._anticipatingNetherPairCreateEvent) {
                this._createdNetherPortalBlock = null;
                return;
            }
            for (BlockState state : event.getBlocks()) {
                if (!MaterialUtil.ISNETHERPORTAL.get(state.getType()).booleanValue()) continue;
                Block block = state.getBlock();
                this._pta.storeNetherPortal(block);
                if (this._createdNetherPortalBlock != null && state.getY() >= block.getY()) continue;
                this._createdNetherPortalBlock = block;
            }
            this._anticipatingNetherPairCreateEvent = false;
        } else if (event.getReason() == PortalCreateEvent.CreateReason.END_PLATFORM) {
            this._endPlatformCreated = true;
        }
    }

    @Override
    public Block findEndPlatform(World world) {
        return this._pta.findEndPlatform(world);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        this._endPlatformCreated = false;
        Block block = this._pta.createEndPlatform(world, HandleConversion.toEntityHandle(initiator));
        if (initiator == null && !this._endPlatformCreated) {
            this._endPlatformCreated = block.getType() == OBSIDIAN_TYPE;
        }
        return this._endPlatformCreated ? block : null;
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
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.phys.Vec3"), @Template.Import(value="net.minecraft.core.BlockPos"), @Template.Import(value="net.minecraft.core.BlockPos$MutableBlockPos"), @Template.Import(value="net.minecraft.core.Direction"), @Template.Import(value="net.minecraft.core.Direction$Axis"), @Template.Import(value="net.minecraft.core.registries.BuiltInRegistries"), @Template.Import(value="net.minecraft.server.level.ServerPlayer"), @Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.network.protocol.Packet"), @Template.Import(value="net.minecraft.network.protocol.game.ClientboundGameEventPacket"), @Template.Import(value="net.minecraft.world.entity.ai.village.poi.PoiManager"), @Template.Import(value="net.minecraft.world.entity.ai.village.poi.PoiType"), @Template.Import(value="net.minecraft.world.entity.ai.village.poi.PoiTypes"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.world.entity.Entity$RemovalReason"), @Template.Import(value="net.minecraft.world.level.block.Blocks"), @Template.Import(value="net.minecraft.world.level.dimension.DimensionType"), @Template.Import(value="net.minecraft.world.level.Level"), @Template.Import(value="net.minecraft.world.level.border.WorldBorder")})
    @Template.InstanceType(value="net.minecraft.world.level.portal.PortalForcer")
    public static abstract class PortalTravelAgentHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {\n    ServerPlayer player = (ServerPlayer) entityPlayerRaw;\n\n    // Using require because field is private on forge\n#if version >= 1.17\n    #require ServerPlayer private boolean isChangingDimension;\n#else\n               #require ServerPlayer private boolean isChangingDimension:worldChangeInvuln;\n#endif\n               player#isChangingDimension = true;\n\n#if version >= 1.20\n    ServerLevel world = (ServerLevel) player.level();\n#elseif version >= 1.18\n    ServerLevel world = player.getLevel();\n#else\n               ServerLevel world = player.getWorldServer();\n#endif\n\n           #if version >= 1.18\n    player.unRide();\n    world.removePlayerImmediately(player, Entity$RemovalReason.CHANGED_DIMENSION);\n    if (!player.wonGame) {\n        player.wonGame = true;\n        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, seenCredits ? 0.0F : 1.0F));\n    }\n           #elseif version >= 1.17\n    player.decouple();\n    world.a(player, Entity$RemovalReason.CHANGED_DIMENSION);\n    if (!player.wonGame) {\n        player.wonGame = true;\n        player.connection.sendPacket((Packet) new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, seenCredits ? 0.0F : 1.0F));\n    }\n           #else\n    player.decouple();\n    world.removePlayer(player);\n    if (!player.viewingCredits) {\n        player.viewingCredits = true;\n  #if version >= 1.16\n        player.playerConnection.sendPacket((Packet) new ClientboundGameEventPacket(ClientboundGameEventPacket.e, seenCredits ? 0.0F : 1.0F));\n  #else\n                   player.playerConnection.sendPacket((Packet) new ClientboundGameEventPacket(4, seenCredits ? 0.0F : 1.0F));\n  #endif\n               }\n#endif\n           }")
        public abstract void showEndCredits(Object var1, boolean var2);

        @Template.Generated(value="public static boolean isMainEndWorld(org.bukkit.World world) {\n    Level world = (Level) ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();\n#if version >= 1.18\n    return world.dimension() == Level.END;\n#elseif version >= 1.17\n    return world.getDimensionKey() == Level.END;\n#elseif version >= 1.16\n    return world.getDimensionKey() == Level.THE_END;\n#else\n               return world.getWorldProvider().getDimensionManager().getType() == DimensionType.THE_END;\n#endif\n           }")
        public abstract boolean isMainEndWorld(World var1);

        @Template.Generated(value="public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock, int radius) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    BlockPos blockposition = new BlockPos(startBlock.getX(), startBlock.getY(), startBlock.getZ());\n    PortalForcer agent = new PortalForcer(world);\n#if version >= 1.21\n  #if forge && !exists net.minecraft.world.level.portal.PortalForcer public java.util.Optional<net.minecraft.core.BlockPos> findClosestPortalPosition(net.minecraft.core.BlockPos blockposition, net.minecraft.world.level.border.WorldBorder worldborder, int radius);\n    // We must use the bool method, the craftbukkit-added radius version doesn't exist\n    boolean isSmallRadius = (radius <= 16);\n    java.util.Optional opt_result = agent.findClosestPortalPosition(blockposition, isSmallRadius, world.getWorldBorder());\n  #else\n               java.util.Optional opt_result = agent.findClosestPortalPosition(blockposition, world.getWorldBorder(), radius);\n  #endif\n               if (!opt_result.isPresent()) {\n        return null;\n    }\n               net.minecraft.core.BlockPos position = (net.minecraft.core.BlockPos) opt_result.get();\n    return startBlock.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());\n#elseif version >= 1.16.2\n  #if version >= 1.18\n    java.util.Optional opt_result = agent.findPortalAround(blockposition, world.getWorldBorder(), radius);\n  #else\n               java.util.Optional opt_result = agent.findPortal(blockposition, radius);\n  #endif\n               if (!opt_result.isPresent()) {\n        return null;\n    }\n               net.minecraft.BlockUtil$FoundRectangle result = (net.minecraft.BlockUtil$FoundRectangle) opt_result.get();\n  #if version >= 1.17\n    return startBlock.getWorld().getBlockAt(result.minCorner.getX(), result.minCorner.getY(), result.minCorner.getZ());\n  #else\n               return startBlock.getWorld().getBlockAt(result.origin.getX(), result.origin.getY(), result.origin.getZ());\n  #endif\n           #else\n  #if version >= 1.15.2\n    net.minecraft.world.level.block.state.pattern.BlockPattern$Shape result = agent.findPortal(blockposition, Vec3.ZERO, Direction.NORTH, 0.5, 1.0, true, radius);\n  #else\n               net.minecraft.world.level.block.state.pattern.BlockPattern$Shape result = agent.a(blockposition, Vec3.ZERO, Direction.NORTH, 0.5, 1.0, true);\n  #endif\n               if (result == null) {\n        return null;\n    }\n             #if version >= 1.14.2\n    return startBlock.getWorld().getBlockAt(MathHelper.floor(result.position.x),\n                                            MathHelper.floor(result.position.y),\n                                            MathHelper.floor(result.position.z));\n  #else\n               return startBlock.getWorld().getBlockAt(MathHelper.floor(result.a.x),\n                                            MathHelper.floor(result.a.y),\n                                            MathHelper.floor(result.a.z));\n  #endif\n           #endif\n}")
        public abstract Block findNetherPortal(Block var1, int var2);

        @Template.Generated(value="public static void createNetherPortal(org.bukkit.block.Block startBlock, org.bukkit.block.BlockFace orientation, Object entityInitiator, Object dummyEntity, int createRadius) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    BlockPos blockposition = new BlockPos(startBlock.getX(), startBlock.getY(), startBlock.getZ());\n    PortalForcer agent = new PortalForcer(world);\n    Entity initiator = (Entity) entityInitiator;\n\n#if version >= 1.16.2\n    // Orientation of the portal, if set, use random if invalid/null/self\n    Direction$Axis axis;\n    if (orientation == org.bukkit.block.BlockFace.NORTH || orientation == org.bukkit.block.BlockFace.SOUTH) {\n        axis = Direction$Axis.X;\n    } else if (orientation == org.bukkit.block.BlockFace.EAST || orientation == org.bukkit.block.BlockFace.WEST) {\n        axis = Direction$Axis.Z;\n    } else {\n        axis = (Math.random() < 0.5) ? Direction$Axis.Z : Direction$Axis.X;\n    }\n               #require PortalForcer public java.util.Optional<net.minecraft.BlockUtil.FoundRectangle> createPortal();\n  #if forge && !exists net.minecraft.world.level.portal.PortalForcer public java.util.Optional<net.minecraft.BlockUtil.Rectangle> createPortal(net.minecraft.core.BlockPos blockposition, net.minecraft.core.Direction.EnumAxis enumdirection_enumaxis, net.minecraft.world.entity.Entity entity, int createRadius);\n    agent.createPortal(blockposition, axis);\n  #else\n               agent.createPortal(blockposition, axis, initiator, createRadius);\n  #endif\n           #elseif version >= 1.16\n    // Use default entity if null, set it up properly\n    if (initiator == null) {\n        initiator = (Entity) dummyEntity;\n        #require net.minecraft.world.entity.Entity private Vec3 loc;\n        #require net.minecraft.world.entity.Entity private BlockPos locBlock;\n        initiator#loc = new Vec3((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);\n        initiator#locBlock = blockposition;\n    }\n               agent.createPortal(initiator, blockposition, createRadius);\n#elseif version >= 1.15.2\n    // Use default entity if null, set it up properly\n    if (initiator == null) {\n        initiator = (Entity) dummyEntity;\n        initiator.setPositionRaw((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);\n    }\n               agent.createPortal(initiator, blockposition, createRadius);\n#elseif version >= 1.15\n    // No blockposition/radius args, use initiator entity at all times to pass coordinates\n    initiator = (Entity) dummyEntity;\n    initiator.setPositionRaw((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);\n    agent.a(initiator);\n#else\n               // No blockposition/radius args, use initiator entity at all times to pass coordinates\n    // setPositionRaw doesn't exist yet, assign locX/Y/Z directly\n    initiator = (Entity) dummyEntity;\n    initiator.locX = (double) startBlock.getX()+0.5;\n    initiator.locY = (double) startBlock.getY();\n    initiator.locZ = (double) startBlock.getZ()+0.5;\n    agent.a(initiator);\n#endif\n           }")
        public abstract void createNetherPortal(Block var1, BlockFace var2, Object var3, Object var4, int var5);

        @Template.Generated(value="public static void storeNetherPortal(org.bukkit.block.Block startBlock) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();\n    BlockPos blockposition = new BlockPos(startBlock.getX(), startBlock.getY(), startBlock.getZ());\n#if version >= 1.21.2\n    PoiManager villageplace = world.getPoiManager();\n    java.util.Optional typeHolderOpt = BuiltInRegistries.POINT_OF_INTEREST_TYPE.get(PoiTypes.NETHER_PORTAL);\n    if (typeHolderOpt.isPresent()) {\n        villageplace.add(blockposition, (net.minecraft.core.Holder) typeHolderOpt.get());\n    }\n           #elseif version >= 1.19\n    PoiManager villageplace = world.getPoiManager();\n    java.util.Optional typeHolderOpt = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(PoiTypes.NETHER_PORTAL);\n    if (typeHolderOpt.isPresent()) {\n        villageplace.add(blockposition, (net.minecraft.core.Holder) typeHolderOpt.get());\n    }\n           #elseif version >= 1.18\n    PoiManager villageplace = world.getPoiManager();\n    villageplace.add(blockposition, PoiType.NETHER_PORTAL);\n#elseif version >= 1.17\n    PoiManager villageplace = world.A();\n    villageplace.a(blockposition, PoiType.NETHER_PORTAL);\n#elseif version >= 1.16.2\n    PoiManager villageplace = world.y();\n    villageplace.a(blockposition, PoiType.v);\n#elseif version >= 1.16\n    PoiManager villageplace = world.x();\n    villageplace.a(blockposition, PoiType.v);\n#elseif version >= 1.15\n    PoiManager villageplace = world.B();\n    villageplace.a(blockposition, PoiType.u);\n#endif\n           }")
        public abstract void storeNetherPortal(Block var1);

        @Template.Generated(value="public static org.bukkit.block.Block findEndPlatform(org.bukkit.World bworld) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();\n#if version >= 1.17\n    BlockPos platformPos = ServerLevel.END_SPAWN_POINT;\n#elseif version >= 1.16\n    BlockPos platformPos = ServerLevel.a;\n#else\n               BlockPos platformPos = WorldProviderTheEnd.f;\n#endif\n               int i = platformPos.getX();\n    int j = platformPos.getY() - 1;\n    int k = platformPos.getZ();\n    BlockPos$MutableBlockPos pos = new BlockPos$MutableBlockPos();\n    byte b0 = 1;\n    byte b1 = 0;\n    for (int l = -2; l <= 2; ++l) {\n        for (int i1 = -2; i1 <= 2; ++i1) {\n            for (int j1 = -1; j1 < 3; ++j1) {\n                int k1 = i + i1 * b0 + l * b1;\n                int l1 = j + j1;\n                int i2 = k + i1 * b1 - l * b0;\n                boolean flag = j1 < 0;\n#if version >= 1.18\n                pos.set(k1, l1, i2);\n                if (world.getBlockState((BlockPos) pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {\n#else\n                           pos.d(k1, l1, i2);\n                if (world.getType((BlockPos) pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {\n#endif\n                               return null;\n                }\n            }\n        }\n               }\n    return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());\n}")
        public abstract Block findEndPlatform(World var1);

        @Template.Generated(value="public static org.bukkit.block.Block createEndPlatform(org.bukkit.World bworld, Object entityInitiatorRaw) {\n    ServerLevel world = (ServerLevel) ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();\n    Entity entityInitiator = (Entity) entityInitiatorRaw;\n\n#if version >= 1.17\n    BlockPos platformPos = ServerLevel.END_SPAWN_POINT;\n#elseif version >= 1.16\n    BlockPos platformPos = ServerLevel.a;\n#endif\n\n           #if version >= 1.21\n    BlockPos fixedPos = new BlockPos(\n        platformPos.getX(),\n        platformPos.getY() - 1,\n        platformPos.getZ());\n    net.minecraft.world.level.levelgen.feature.EndPlatformFeature.createEndPlatform(world, fixedPos, true, entityInitiator);\n\n#elseif version >= 1.18\n  #if exists net.minecraft.server.level.ServerLevel public static void makeObsidianPlatform(net.minecraft.server.level.ServerLevel worldserver, net.minecraft.world.entity.Entity entity);\n    ServerLevel.makeObsidianPlatform(world, entityInitiator);\n  #else\n               // No-entity arg version I guess? Meh.\n    ServerLevel.makeObsidianPlatform(world);\n  #endif\n           #elseif !exists net.minecraft.server.level.ServerLevel public static void a(ServerLevel world, net.minecraft.world.entity.Entity entity) && exists net.minecraft.server.level.ServerLevel public static void a(ServerLevel world)\n    // Forge lacks an Entity parameter\n    ServerLevel.a(world);\n#elseif version >= 1.16\n    ServerLevel.a(world, entityInitiator);\n#else\n               // Custom code for MC 1.15.2 and before\n    BlockPos platformPos = WorldProviderTheEnd.f;\n\n    // Code is embedded deep inside ServerPlayer.a(DimensionType, TeleportCause)\n    int i = platformPos.getX();\n    int j = platformPos.getY() - 1;\n    int k = platformPos.getZ();\n    boolean flag = true;\n    boolean flag1 = false;\n    org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(world);\n    BlockPos$MutableBlockPos blockposition = new BlockPos$MutableBlockPos();\n    for (int l = -2; l <= 2; ++l) {\n        for (int i1 = -2; i1 <= 2; ++i1) {\n            for (int j1 = -1; j1 < 3; ++j1) {\n                int k1 = i + i1 * 1 + l * 0;\n                int l1 = j + j1;\n                int i2 = k + i1 * 0 - l * 1;\n                boolean flag2 = j1 < 0;\n                blockposition.d(k1, l1, i2);\n                blockList.setTypeAndData((BlockPos) blockposition, flag2 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData(), 3);\n            }\n        }\n               }\n\n    // Event handling\n    java.util.List blocks = blockList.getList(); // List of BlockState!\n    org.bukkit.event.world.PortalCreateEvent portalEvent;\n  #if version >= 1.14.4 || exists org.bukkit.event.world.PortalCreateEvent public PortalCreateEvent(java.util.List blocks, org.bukkit.World world, org.bukkit.entity.Entity entity, PortalCreateEvent.CreateReason reason);\n    org.bukkit.entity.Entity binitiator = (entityInitiator == null) ? null : entityInitiator.getBukkitEntity();\n    portalEvent = new org.bukkit.event.world.PortalCreateEvent(blocks, bworld, binitiator, org.bukkit.event.world.PortalCreateEvent$CreateReason.END_PLATFORM);\n  #else\n               portalEvent = new org.bukkit.event.world.PortalCreateEvent(blocks, bworld, org.bukkit.event.world.PortalCreateEvent$CreateReason.END_PLATFORM);\n  #endif\n               world.getServer().getPluginManager().callEvent(portalEvent);\n    if (!portalEvent.isCancelled()) {\n        blockList.updateList();\n    }\n           #endif\n    return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());\n}")
        public abstract Block createEndPlatform(World var1, Object var2);
    }
}

