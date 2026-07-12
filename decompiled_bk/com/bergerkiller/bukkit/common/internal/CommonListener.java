/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.event.vehicle.VehicleEnterEvent
 *  org.bukkit.event.vehicle.VehicleExitEvent
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BlockStateMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.block.SignLineAccessor;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.block.SignSideLineAccessor;
import com.bergerkiller.bukkit.common.collections.ImmutableCachedSet;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.SignEditTextEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.hooks.AdvancementDataPlayerHook;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard;
import com.bergerkiller.bukkit.common.scoreboards.CommonTeam;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CommonListener
implements Listener {
    public static boolean BLOCK_PHYSICS_FIRED = false;
    private static final List<WeakReference<ImmutableCachedSet<Player>>> CACHED_IMMUTABLE_PLAYER_SETS = new ArrayList<WeakReference<ImmutableCachedSet<Player>>>();
    private static final Map<Player, Block> editedSignBlocks = new HashMap<Player, Block>();
    private static final FastField<PluginLoaderHandler> pluginLoaderHandlerField = new FastField();
    private static final Predicate<BlockPlaceEvent> CHECK_PLACING_BLOCK_STATE;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerImmutablePlayerSet(ImmutableCachedSet<Player> set) {
        List<WeakReference<ImmutableCachedSet<Player>>> list = CACHED_IMMUTABLE_PLAYER_SETS;
        synchronized (list) {
            CACHED_IMMUTABLE_PLAYER_SETS.add(new WeakReference<ImmutableCachedSet<Player>>(set));
        }
    }

    public static void storeEditedSign(Player player, Sign sign) {
        editedSignBlocks.put(player, sign.getBlock());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPluginEnable(PluginEnableEvent event) {
        if (!PluginLoaderHandler.isPluginFullyEnabled(event.getPlugin())) {
            return;
        }
        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin == event.getPlugin() || !plugin.isEnabled() || !(plugin instanceof PluginBase)) continue;
            PluginBase pb = (PluginBase)plugin;
            try {
                pluginLoaderHandlerField.get((Object)pb).onPluginLoaded(event.getPlugin());
                pb.updateDependency(event.getPlugin(), name, true);
            }
            catch (Throwable t) {
                pb.getLogger().log(Level.SEVERE, "Failed to handle updateDependency", t);
            }
        }
        CommonPlugin.flushSaveOperations(event.getPlugin());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPluginDisable(PluginDisableEvent event) {
        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin == event.getPlugin() || !plugin.isEnabled() || !(plugin instanceof PluginBase)) continue;
            PluginBase pb = (PluginBase)plugin;
            try {
                pb.updateDependency(event.getPlugin(), name, false);
            }
            catch (Throwable t) {
                pb.getLogger().log(Level.SEVERE, "Failed to handle updateDependency", t);
            }
        }
        CommonPlugin.flushSaveOperations(event.getPlugin());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onWorldInit(WorldInitEvent event) {
        CreaturePreSpawnHandler.INSTANCE.onWorldEnabled(event.getWorld());
        CommonUtil.nextTick(() -> CommonPlugin.getInstance().notifyWorldAdded(event.getWorld()));
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected void onWorldLoad(WorldLoadEvent event) {
        EntityAddRemoveHandler.INSTANCE.onWorldEnabled(event.getWorld());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected void onWorldUnload(WorldUnloadEvent event) {
        EntityAddRemoveHandler.INSTANCE.onWorldDisabled(event.getWorld());
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    protected void onVehicleEnter(VehicleEnterEvent event) {
        Entity realVehicle;
        if (EntityHandle.fromBukkit((Entity)event.getVehicle()).isDestroyed() && (realVehicle = EntityUtil.getEntity(event.getEntered().getWorld(), event.getVehicle().getUniqueId())) != null && realVehicle != event.getVehicle()) {
            event.setCancelled(true);
            ExtendedEntity<Entity> extRealVehicle = new ExtendedEntity<Entity>(realVehicle);
            extRealVehicle.addPassenger(event.getEntered());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        for (Entity vehicle = event.getPlayer().getVehicle(); vehicle != null; vehicle = vehicle.getVehicle()) {
            CommonEntity<Entity> vehicleCommonEntity = CommonEntity.get(vehicle);
            EntityController<CommonEntity<Entity>> controller = vehicleCommonEntity.getController();
            if (controller == null || controller instanceof DefaultEntityController || controller.isPlayerTakeable()) continue;
            CommonEntity.get(event.getPlayer().getVehicle()).removePassenger((Entity)event.getPlayer());
        }
        editedSignBlocks.remove(event.getPlayer());
        CommonScoreboard.removePlayer(event.getPlayer());
        CommonPlugin.getInstance().getVehicleMountManager().remove(event.getPlayer());
        this.removeFromCachedImmutablePlayerSets(event.getPlayer());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeFromCachedImmutablePlayerSets(Player player) {
        List<WeakReference<ImmutableCachedSet<Player>>> list = CACHED_IMMUTABLE_PLAYER_SETS;
        synchronized (list) {
            Iterator<WeakReference<ImmutableCachedSet<Player>>> iter = CACHED_IMMUTABLE_PLAYER_SETS.iterator();
            while (iter.hasNext()) {
                ImmutableCachedSet set = (ImmutableCachedSet)iter.next().get();
                if (set == null) {
                    iter.remove();
                    continue;
                }
                set.releaseFromCache(player);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CommonPlugin.getInstance().getPacketHandler().onPlayerJoin(player);
        AdvancementDataPlayerHook.hook(player);
        CommonTeam team = CommonScoreboard.get(player).getTeam();
        if (!team.shouldSendToAll()) {
            team.send(player);
        }
        for (CommonTeam ct : CommonScoreboard.getTeams()) {
            if (!ct.shouldSendToAll()) continue;
            ct.send(player);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        CommonPlugin.getInstance().getPlayerMeta(event.getPlayer()).initiateRespawnBlindness();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getFrom() != null && event.getTo() != null && event.getFrom().getWorld() != event.getTo().getWorld()) {
            CommonPlugin.getInstance().getPlayerMeta(event.getPlayer()).initiateRespawnBlindness();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onBlockPhysics(BlockPhysicsEvent event) {
        BLOCK_PHYSICS_FIRED = true;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        ItemStack item;
        Player p = event.getPlayer();
        MapDisplay display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInMainHand((HumanEntity)event.getPlayer()));
        if (display != null && (display.onItemDrop(p, item = event.getItemDrop().getItemStack()) || display.getRootWidget().onItemDrop(p, item))) {
            event.setCancelled(true);
            return;
        }
        display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInOffHand((HumanEntity)event.getPlayer()));
        if (display != null && (display.onItemDrop(p, item = event.getItemDrop().getItemStack()) || display.getRootWidget().onItemDrop(p, item))) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerInteractWithBlock(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        MapDisplay display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInMainHand((HumanEntity)event.getPlayer()));
        if (display != null) {
            display.onBlockInteract(event);
            display.getRootWidget().onBlockInteract(event);
        }
        if ((display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInOffHand((HumanEntity)event.getPlayer()))) != null) {
            display.onBlockInteract(event);
            display.getRootWidget().onBlockInteract(event);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getVehicle() == event.getRightClicked() && event.getRightClicked() instanceof Vehicle) {
            Vehicle vehicle = (Vehicle)event.getRightClicked();
            event.setCancelled(CommonUtil.callEvent(new VehicleExitEvent(vehicle, (LivingEntity)event.getPlayer())).isCancelled());
        }
    }

    private static Predicate<BlockPlaceEvent> initCheckPlacingBlockState() {
        if (Common.evaluateMCVersion("<", "1.9")) {
            try {
                Class.forName("org.bukkit.inventory.meta.BlockStateMeta");
            }
            catch (Throwable t) {
                return LogicUtil.alwaysFalsePredicate();
            }
        }
        return event -> {
            ItemMeta meta;
            ItemStack item = event.getItemInHand();
            if (item != null && (meta = item.getItemMeta()) instanceof BlockStateMeta) {
                return ((BlockStateMeta)meta).hasBlockState();
            }
            return false;
        };
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlaceHandleSignEvents(BlockPlaceEvent event) {
        if (!event.canBuild()) {
            return;
        }
        if (!CommonUtil.hasHandlers(SignEditTextEvent.getHandlerList())) {
            return;
        }
        if (!CHECK_PLACING_BLOCK_STATE.test(event)) {
            return;
        }
        if (!((Boolean)MaterialUtil.ISSIGN.get(event.getBlockPlaced())).booleanValue()) {
            return;
        }
        SignChangeTracker tracker = SignChangeTracker.track(event.getBlockPlaced());
        if (tracker.isRemoved()) {
            return;
        }
        SignSide[] sidesToFire = new SignSide[]{SignSide.FRONT};
        if (CommonCapabilities.HAS_SIGN_BACK_TEXT && !ChatText.isAllEmpty(tracker.getFormattedBackLines())) {
            sidesToFire = new SignSide[]{SignSide.FRONT, SignSide.BACK};
        }
        for (SignSide signSide : sidesToFire) {
            SignEditTextEvent editEvent = new SignEditTextEvent(event.getPlayer(), tracker.getBlock(), tracker.getSign(), SignEditTextEvent.EditReason.CTRL_PICK_PLACE, signSide, tracker.liveUpdatingAccessor());
            Bukkit.getPluginManager().callEvent((Event)editEvent);
            if (!editEvent.isCancelled()) continue;
            event.setBuild(false);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    protected void onSignChange(SignChangeEvent event) {
        if (!CommonUtil.hasHandlers(SignEditTextEvent.getHandlerList())) {
            return;
        }
        SignChangeTracker tracker = SignChangeTracker.track(event.getBlock());
        if (tracker.isRemoved()) {
            return;
        }
        SignEditTextEvent.EditReason editReason = SignEditTextEvent.EditReason.PLACE;
        Block editedSign = editedSignBlocks.remove(event.getPlayer());
        if (event.getBlock().equals((Object)editedSign)) {
            editReason = SignEditTextEvent.EditReason.EDIT;
        }
        if (!CommonCapabilities.HAS_SIGN_OPEN_EVENT && (!ChatText.isAllEmpty(tracker.getFormattedFrontLines()) || CommonCapabilities.HAS_SIGN_BACK_TEXT && !ChatText.isAllEmpty(tracker.getFormattedBackLines()))) {
            editReason = SignEditTextEvent.EditReason.EDIT;
        }
        SignSideLineAccessor editedLines = SignSideLineAccessor.ofChangeEvent(event);
        SignSideLineAccessor otherSideLines = tracker.liveUpdatingAccessor().accessLinesOfSide(editedLines.getSide().opposite());
        SignEditTextEvent editEvent = new SignEditTextEvent(event.getPlayer(), tracker.getBlock(), tracker.getSign(), editReason, editedLines.getSide(), editedLines.getSide().isFront() ? SignLineAccessor.compose(editedLines, otherSideLines) : SignLineAccessor.compose(otherSideLines, editedLines));
        Bukkit.getPluginManager().callEvent((Event)editEvent);
        if (editEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    static {
        try {
            pluginLoaderHandlerField.init(PluginBase.class.getDeclaredField("pluginLoaderHandler"));
        }
        catch (NoSuchFieldException e) {
            pluginLoaderHandlerField.initUnavailable("Field pluginLoaderHandler isn't there. This can't be!");
        }
        CHECK_PLACING_BLOCK_STATE = CommonListener.initCheckPlacingBlockState();
    }
}

