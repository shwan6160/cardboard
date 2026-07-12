/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.collections.EntityMap
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent
 *  com.bergerkiller.bukkit.common.events.EntityAddEvent
 *  com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.minecart.RideableMinecart
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityPortalEvent
 *  org.bukkit.event.entity.ItemSpawnEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryCreativeEvent
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleEntityCollisionEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Rails
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIController;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.debug.DebugTool;
import com.bergerkiller.bukkit.tc.editor.TCMapControl;
import com.bergerkiller.bukkit.tc.events.signactions.SignActionRegisterEvent;
import com.bergerkiller.bukkit.tc.events.signactions.SignActionUnregisterEvent;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class TCListener
implements Listener {
    private static final boolean DEBUG_DO_TRACKTEST = false;
    private static final boolean DEBUG_DO_INVISIBLE_TRACK = false;
    private static final boolean MUST_CHECK_PLAYER_TAKE = !Common.hasCapability((String)"Common:EntityController:isPlayerTakeable");
    private static final long SIGN_CLICK_INTERVAL = 500L;
    private static final long MAX_INTERACT_INTERVAL = 300L;
    public static boolean cancelNextDrops = false;
    public static MinecartMember<?> killedByMember = null;
    private final TrainCarts plugin;
    private EntityMap<Player, Long> lastHitTimes = new EntityMap();
    private EntityMap<Player, BlockFace> lastClickedDirection = new EntityMap();

    public TCListener(TrainCarts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MinecartMember<?> vehicle;
        if (MUST_CHECK_PLAYER_TAKE && (vehicle = MinecartMemberStore.getFromEntity(event.getPlayer().getVehicle())) != null && !vehicle.isPlayerTakeable()) {
            ((CommonMinecart)vehicle.getEntity()).removePassenger((Entity)event.getPlayer());
        }
        FakePlayerSpawner.onViewerQuit(event.getPlayer());
        this.plugin.getTeamProvider().reset(event.getPlayer());
        this.plugin.getAttachmentViewers().remove(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (cancelNextDrops) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        this.plugin.getOfflineGroups().unloadChunk(event.getChunk());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onChunkLoadEntities(ChunkLoadEntitiesEvent event) {
        this.plugin.getOfflineGroups().loadChunk(event.getChunk());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        this.plugin.getOfflineGroups().refresh(event.getWorld());
        Map<OfflineGroup, List<ForcedChunk>> chunks = this.plugin.getOfflineGroups().getForceLoadedChunks(event.getWorld());
        if (!chunks.isEmpty()) {
            this.plugin.log(Level.INFO, "Restoring trains and loading nearby chunks on world " + event.getWorld().getName() + "...");
            this.plugin.preloadChunks(chunks);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.plugin.getOfflineGroups().unloadWorld(event.getWorld());
        if (Bukkit.getPluginManager().isPluginEnabled("LightAPI")) {
            TCListener.disableLightAPIWorld(event.getWorld());
        }
        TCConfig.enabledWorlds.onWorldUnloaded(event.getWorld());
        TCConfig.disabledWorlds.onWorldUnloaded(event.getWorld());
    }

    private static void disableLightAPIWorld(World world) {
        LightAPIController.disableWorld(world);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.plugin.getTeamProvider().reset(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        String deathMessage;
        if (killedByMember != null && !(deathMessage = killedByMember.getGroup().getProperties().getKillMessage()).isEmpty()) {
            deathMessage = deathMessage.replaceAll("%0%", event.getEntity().getDisplayName());
            deathMessage = deathMessage.replaceAll("%1%", killedByMember.getGroup().getProperties().getDisplayName());
            event.setDeathMessage(deathMessage);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityAdd(EntityAddEvent event) {
        if (MinecartMemberStore.canConvertAutomatically(event.getEntity())) {
            MinecartMemberStore.convert(this.plugin, (Minecart)event.getEntity());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityRemoveFromServer(EntityRemoveFromServerEvent event) {
        if (event.getEntity() instanceof Minecart) {
            UUID entityUUID = event.getEntity().getUniqueId();
            if (EntityUtil.getEntity((World)event.getEntity().getWorld(), (UUID)entityUUID) != null) {
                return;
            }
            if (EntityHandle.fromBukkit((Entity)event.getEntity()).isDestroyed()) {
                this.plugin.getOfflineGroups().removeMember(entityUUID);
            } else {
                MinecartMember<?> member = MinecartMemberStore.getFromEntity(event.getEntity());
                if (member == null) {
                    return;
                }
                MinecartGroup group = member.getGroup();
                if (group == null) {
                    return;
                }
                if (group.canUnload()) {
                    this.plugin.log(Level.WARNING, "Train '" + group.getProperties().getTrainName() + "' forcibly unloaded!");
                } else {
                    this.plugin.log(Level.WARNING, "Train '" + group.getProperties().getTrainName() + "' had to be restored after unexpected unload");
                }
                group.unload();
                CommonUtil.nextTick((Runnable)new Runnable(){

                    @Override
                    public void run() {
                        TCListener.this.plugin.getOfflineGroups().refresh();
                    }
                });
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onVehicleDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.isCartDamageCancelled(event.getEntity(), event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (this.isCartDamageCancelled((Entity)event.getVehicle(), event.getAttacker())) {
            event.setCancelled(true);
        }
    }

    private boolean isCartDamageCancelled(Entity vehicle, Entity attacker) {
        boolean breakAny;
        MinecartMember<?> mm = MinecartMemberStore.getFromEntity(vehicle);
        if (mm == null) {
            return false;
        }
        if (attacker instanceof Projectile) {
            attacker = (Entity)((Projectile)attacker).getShooter();
        }
        boolean bl = breakAny = attacker instanceof Player && Permission.BREAK_MINECART_ANY.has((CommandSender)((Player)attacker));
        if (mm.getProperties().isInvincible() && !breakAny) {
            return true;
        }
        if (attacker instanceof Player) {
            Player p = (Player)attacker;
            if (!(breakAny || mm.getProperties().hasOwnership(p) && Permission.BREAK_MINECART_SELF.has((CommandSender)p))) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (TrainCarts.isWorldDisabled(event.getVehicle().getWorld())) {
            return;
        }
        try {
            MinecartMember<?> member = MinecartMemberStore.getFromEntity((Entity)event.getVehicle());
            if (member != null) {
                event.setCancelled(!member.onEntityCollision(event.getEntity()));
            }
        }
        catch (Throwable t) {
            this.plugin.handle(t);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (TrainCarts.isWorldDisabled(event.getPlayer().getWorld())) {
            return;
        }
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && DebugTool.onDebugInteract(this.plugin, event.getPlayer(), event.getClickedBlock(), event.getItem(), false)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack heldItem = HumanHand.getItemInMainHand((HumanEntity)event.getPlayer());
        if (TCMapControl.isTCMapItem(event.getItem())) {
            if (event.getClickedBlock() != null) {
                CommonItemStack.of((ItemStack)event.getItem()).updateCustomData(tag -> tag.putBlockLocation("selected", new BlockLocation(event.getClickedBlock())));
            }
            TCMapControl.updateMapItem(event.getPlayer(), true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            return;
        }
        try {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                clickedBlock = CommonEntity.get((Player)event.getPlayer()).getTargetBlock();
            }
            if (DebugTool.onDebugInteract(this.plugin, event.getPlayer(), clickedBlock, event.getItem(), true)) {
                event.setUseInteractedBlock(Event.Result.DENY);
                return;
            }
            if (clickedBlock == null) {
                return;
            }
            Material m = event.getItem() == null ? Material.AIR : event.getItem().getType();
            long lastHitTime = (Long)this.lastHitTimes.getOrDefault((Object)event.getPlayer(), (Object)Long.MIN_VALUE);
            long time = System.currentTimeMillis();
            long clickInterval = time - lastHitTime;
            this.lastHitTimes.put((Object)event.getPlayer(), (Object)time);
            this.handleRailPlacement(event, heldItem);
            if (!event.isCancelled() && !this.onRightClick(clickedBlock, event.getPlayer(), heldItem, clickInterval)) {
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
        catch (Throwable t) {
            this.plugin.handle(t);
        }
    }

    private void handleRailPlacement(PlayerInteractEvent event, ItemStack heldItem) {
        if (event.getClickedBlock() == null || heldItem == null) {
            return;
        }
        if (((Boolean)MaterialUtil.ISINTERACTABLE.get(event.getClickedBlock())).booleanValue() && !event.getPlayer().isSneaking()) {
            return;
        }
        Block placedBlock = event.getClickedBlock().getRelative(event.getBlockFace());
        if (!((Boolean)MaterialUtil.ISAIR.get(placedBlock)).booleanValue()) {
            return;
        }
        Material railType = heldItem.getType();
        if (MaterialUtil.ISRAILS.get(railType).booleanValue() && TCConfig.allowUpsideDownRails) {
            Block below = placedBlock.getRelative(BlockFace.DOWN);
            Block above = placedBlock.getRelative(BlockFace.UP);
            if ((((Boolean)MaterialUtil.ISAIR.get(below)).booleanValue() || ((Boolean)Util.ISVERTRAIL.get(below)).booleanValue()) && Util.isUpsideDownRailSupport(above)) {
                BlockPlaceEvent placeEvent = new BlockPlaceEvent(placedBlock, placedBlock.getState(), event.getClickedBlock(), heldItem.clone(), event.getPlayer(), true);
                BlockData railData = BlockData.fromMaterial((Material)railType);
                WorldUtil.setBlockDataFast((Block)placedBlock, (BlockData)railData);
                WorldUtil.queueBlockSend((Block)placedBlock);
                CommonUtil.callEvent((Event)placeEvent);
                if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
                    WorldUtil.setBlockDataFast((Block)placedBlock, (BlockData)BlockData.AIR);
                } else {
                    this.plugin.applyBlockPhysics(placedBlock, railData);
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        ItemStack oldItem = HumanHand.getItemInMainHand((HumanEntity)event.getPlayer());
                        if (oldItem == null || oldItem.getAmount() <= 1) {
                            oldItem = null;
                        } else {
                            oldItem = oldItem.clone();
                            oldItem.setAmount(oldItem.getAmount() - 1);
                        }
                        HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)oldItem);
                    }
                    WorldUtil.playSound((Location)event.getClickedBlock().getLocation(), (ResourceKey)railData.getPlaceSound(), (float)1.0f, (float)1.0f);
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean onRightClick(Block clickedBlock, Player player, ItemStack heldItem, long clickInterval) {
        BlockData type;
        RailType railType;
        if (clickedBlock != null && (((Boolean)MaterialUtil.ISMINECART.get(heldItem)).booleanValue() || ((Boolean)Util.ISTCRAIL.get(heldItem)).booleanValue()) && (railType = RailType.getType(clickedBlock, type = WorldUtil.getBlockData((Block)clickedBlock))) != RailType.NONE) {
            if (((Boolean)MaterialUtil.ISMINECART.get(heldItem)).booleanValue()) {
                return this.handleMinecartPlacement(player, clickedBlock);
            }
            if (type.isType(heldItem.getType()) && MaterialUtil.ISRAILS.get(type).booleanValue() && TCConfig.allowRailEditing && clickInterval >= 300L && BlockUtil.canBuildBlock((Block)clickedBlock, (BlockData)type)) {
                BlockFace direction = FaceUtil.getDirection((Vector)player.getLocation().getDirection(), (boolean)false);
                BlockFace lastDirection = (BlockFace)this.lastClickedDirection.getOrDefault((Object)player, (Object)direction);
                Rails rails = BlockUtil.getRails((Block)clickedBlock);
                if (BlockUtil.isSolid((Block)clickedBlock.getRelative(direction))) {
                    if (rails.isOnSlope()) {
                        if (rails.getDirection() == direction) {
                            rails.setDirection(direction, false);
                        } else {
                            rails.setDirection(direction, true);
                        }
                    } else {
                        rails.setDirection(direction, true);
                    }
                } else if (RailType.REGULAR.isRail(type)) {
                    Object[] faces = FaceUtil.getFaces((BlockFace)rails.getDirection());
                    if (!LogicUtil.contains((Object)direction.getOppositeFace(), (Object[])faces)) {
                        Object otherFace = faces[0] == lastDirection.getOppositeFace() ? faces[0] : faces[1];
                        rails.setDirection(FaceUtil.combine((BlockFace)otherFace, (BlockFace)direction.getOppositeFace()), false);
                    }
                } else {
                    rails.setDirection(direction, false);
                }
                TrainCarts.plugin.setBlockDataWithoutBreaking(clickedBlock, BlockData.fromMaterialData((MaterialData)rails));
                this.lastClickedDirection.put((Object)player, (Object)direction);
            }
        }
        return (Boolean)MaterialUtil.ISSIGN.get(clickedBlock) == false || clickInterval < 500L || !SignAction.handleClick(clickedBlock, player);
    }

    private boolean handleMinecartPlacement(Player player, Block clickedBlock) {
        if (!Permission.GENERAL_PLACE_MINECART.has((CommandSender)player)) {
            return false;
        }
        RailType clickedRailType = RailType.getType(clickedBlock);
        if (clickedRailType == RailType.NONE) {
            return true;
        }
        if (!TCConfig.allMinecartsAreTrainCarts && !Permission.GENERAL_PLACE_TRAINCART.has((CommandSender)player)) {
            return true;
        }
        BlockFace orientation = FaceUtil.vectorToBlockFace((Vector)player.getEyeLocation().getDirection().setY(0.0), (boolean)false);
        Location at = clickedRailType.getSpawnLocation(clickedBlock, orientation);
        if (MinecartMemberStore.getAt(at, null, 0.5) != null) {
            return false;
        }
        if (MinecartGroupStore.isPerWorldSpawnLimitReached(at, 1)) {
            Localization.SPAWN_MAX_PER_WORLD.message((CommandSender)player, new String[0]);
            return false;
        }
        MinecartMemberStore.spawnBy(this.plugin, at, player);
        return false;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        long time;
        long clickInterval;
        if (!(event.getRightClicked() instanceof Minecart)) {
            return;
        }
        Long lastHitTime = (Long)this.lastHitTimes.get((Object)event.getPlayer());
        if (lastHitTime != null && (clickInterval = (time = System.currentTimeMillis()) - lastHitTime) < 300L) {
            event.setCancelled(true);
            return;
        }
        if (event.getRightClicked() instanceof RideableMinecart) {
            event.setCancelled(!this.plugin.handlePlayerVehicleChange(event.getPlayer(), event.getRightClicked()));
            MinecartMember<?> newMinecart = MinecartMemberStore.getFromEntity(event.getRightClicked());
            if (!event.isCancelled() && newMinecart != null) {
                newMinecart.getAttachments().storeSeatHint(event.getPlayer());
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (((Boolean)MaterialUtil.ISRAILS.get(event.getBlock())).booleanValue()) {
            this.onRailsBreak(event.getBlock());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        RailType railType = RailType.getType(event.getBlockPlaced());
        if (railType != RailType.NONE) {
            final Block placed = event.getBlockPlaced();
            CommonUtil.nextTick((Runnable)new Runnable(){
                final /* synthetic */ TCListener this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void run() {
                    BlockData blockData = WorldUtil.getBlockData((Block)placed);
                    RailType railType = RailType.getType(placed, blockData);
                    if (railType != RailType.NONE) {
                        railType.onBlockPlaced(placed);
                        this.this$0.plugin.applyBlockPhysics(placed, blockData);
                    }
                }
            });
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        MinecartGroupStore.notifyPhysicsChange();
        Block block = event.getBlock();
        BlockData blockData = Util.getBlockDataOfPhysicsEvent(event);
        for (RailType type : RailType.values()) {
            if (!type.isHandlingPhysics() || !RailType.checkRailTypeIsAt(type, block, blockData)) continue;
            if (!type.isRailsSupported(block)) {
                this.onRailsBreak(block);
            }
            type.onBlockPhysics(event);
            RailLookup.CachedRailPiece cachedRailPiece = RailLookup.lookupCachedRailPieceIfCached(OfflineBlock.of((Block)block), type);
            if (cachedRailPiece.isNone()) continue;
            cachedRailPiece.forceCacheVerification();
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(event.getEntity().getVehicle());
        if (member != null && !member.canTakeDamage(event.getEntity(), event.getCause())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerCreativeSetSlot(InventoryCreativeEvent event) {
        CartAttachmentSeat seat;
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && (seat = this.plugin.getSeatAttachmentMap().get(event.getWhoClicked().getEntityId())) != null && seat.firstPerson.getLiveMode().isRealPlayerInvisible()) {
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityPortal(EntityPortalEvent event) {
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(event.getEntity());
        if (member == null) {
            return;
        }
        event.setCancelled(true);
        if (!TCConfig.allowNetherTeleport) {
            return;
        }
        Location loc = event.getTo();
        if (loc == null) {
            return;
        }
        Direction direction = Direction.fromFace(member.getDirectionFrom());
        final PortalDestination dest = PortalDestination.findDestinationAtNetherPortal(loc.getBlock(), direction);
        if (dest != null && dest.getRailsBlock() != null && dest.hasDirections()) {
            final MinecartGroup group = member.getGroup();
            CommonUtil.nextTick((Runnable)new Runnable(){
                final /* synthetic */ TCListener this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void run() {
                    group.teleport(dest.getRailsBlock(), dest.getDirections()[0]);
                }
            });
        }
    }

    public void onRailsBreak(Block railsBlock) {
        MinecartMember<?> mm = MinecartMemberStore.getAt(railsBlock);
        if (mm != null) {
            mm.getGroup().getSignTracker().updatePosition();
        }
        PathNode.remove(railsBlock);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onLateSignRegistered(SignActionRegisterEvent event) {
        this.plugin.redetectSignActions();
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onLateSignUnregistered(SignActionUnregisterEvent event) {
        this.plugin.redetectSignActions();
    }
}

