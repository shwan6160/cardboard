/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.PluginBase
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.component.LibraryComponentList
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 *  com.bergerkiller.bukkit.common.controller.DefaultEntityController
 *  com.bergerkiller.bukkit.common.controller.EntityController
 *  com.bergerkiller.bukkit.common.conversion.type.HandleConversion
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.metrics.Metrics
 *  com.bergerkiller.bukkit.common.metrics.Metrics$CustomChart
 *  com.bergerkiller.bukkit.common.metrics.Metrics$DrilldownPie
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.ChunkUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.sl.API.Variables
 *  com.bergerkiller.bukkit.sl.API.events.SignVariablesDetectEvent
 *  com.bergerkiller.generated.net.minecraft.world.item.ItemHandle
 *  com.bergerkiller.mountiplex.conversion.Conversion
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentList;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.metrics.Metrics;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.sl.API.events.SignVariablesDetectEvent;
import com.bergerkiller.bukkit.tc.ArrivalSigns;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TCInteractionPacketListener;
import com.bergerkiller.bukkit.tc.TCListener;
import com.bergerkiller.bukkit.tc.TCPacketListener;
import com.bergerkiller.bukkit.tc.TCSeatChangeListener;
import com.bergerkiller.bukkit.tc.TCSuppressSeatTeleportPacketListener;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModelStore;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentLight;
import com.bergerkiller.bukkit.tc.attachments.control.GlowColorTeamProvider;
import com.bergerkiller.bukkit.tc.attachments.control.SeatAttachmentMap;
import com.bergerkiller.bukkit.tc.attachments.control.TeamProvider;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.schematic.WorldEditSchematicLoader;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListing;
import com.bergerkiller.bukkit.tc.chest.TrainChestListener;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandlerRegistry;
import com.bergerkiller.bukkit.tc.commands.selector.TCSelectorHandlerRegistry;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberNetwork;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.global.ActionSignHighlighter;
import com.bergerkiller.bukkit.tc.controller.global.EffectLoopPlayerController;
import com.bergerkiller.bukkit.tc.controller.global.SignController;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayerStore;
import com.bergerkiller.bukkit.tc.controller.global.TrainUpdateController;
import com.bergerkiller.bukkit.tc.controller.player.TrainCartsAttachmentViewerMap;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizer;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerPacketListener;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.SmoothCoastersAPI;
import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHider;
import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHiderDependency;
import com.bergerkiller.bukkit.tc.dep.softdependency.SoftDependency;
import com.bergerkiller.bukkit.tc.dep.softdependency.SoftServiceDependency;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.itemanimation.ItemAnimation;
import com.bergerkiller.bukkit.tc.locator.TrainLocator;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupManager;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import com.bergerkiller.bukkit.tc.pathfinding.RouteManager;
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import com.bergerkiller.bukkit.tc.portals.TCPortalManager;
import com.bergerkiller.bukkit.tc.properties.SavedTrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.registry.TCPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.category.PaperPlayerViewDistanceProperty;
import com.bergerkiller.bukkit.tc.properties.standard.category.PaperTrackingRangeProperty;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.TrackedSignLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionDetector;
import com.bergerkiller.bukkit.tc.signactions.SignActionSpawn;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSignManager;
import com.bergerkiller.bukkit.tc.statements.Statement;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import com.bergerkiller.bukkit.tc.utils.BlockPhysicsEventDataAccessor;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TrainCarts
extends PluginBase {
    public static TrainCarts plugin;
    private final LibraryComponentList<TrainCarts> optionalComponents = LibraryComponentList.forPlugin((Plugin)this);
    private final LibraryComponentList<TrainCarts> criticalComponents = LibraryComponentList.forPlugin((Plugin)this);
    private final Task autosaveTask = new AutosaveTask(this);
    private Task cacheCleanupTask;
    private Task mutexZoneUpdateTask;
    private final List<ChunkPreloadTask> chunkPreloadTasks = new ArrayList<ChunkPreloadTask>();
    private TCPropertyRegistry propertyRegistry;
    private TCListener listener;
    private TCPacketListener packetListener;
    private TCSuppressSeatTeleportPacketListener suppressSeatTeleportPacketListener;
    private TCInteractionPacketListener interactionPacketListener;
    private FileConfiguration config;
    private final SpawnSignManager spawnSignManager = new SpawnSignManager(this);
    private SavedAttachmentModelStore savedAttachmentModels;
    private SavedTrainPropertiesStore savedTrainsStore;
    private SeatAttachmentMap seatAttachmentMap;
    private final TeamProvider teamProvider = new TeamProvider(this);
    private PathProvider pathProvider;
    private RouteManager routeManager;
    private final TrainLocator trainLocator = new TrainLocator(this);
    private TrainUpdateController trainUpdateController = new TrainUpdateController(this);
    private final TCSelectorHandlerRegistry selectorHandlerRegistry = new TCSelectorHandlerRegistry(this);
    private final OfflineGroupManager offlineGroupManager = new OfflineGroupManager(this);
    private final OfflineSignStore offlineSignStore = new OfflineSignStore(this);
    private final ActionRegistry actionRegistry = new ActionRegistry(this);
    private final TrackedSignLookup trackedSignLookup = new TrackedSignLookup(this);
    private final SignController signController = new SignController(this);
    private final TrainCartsAttachmentViewerMap attachmentViewerMap = new TrainCartsAttachmentViewerMap(this);
    private ResourcePackModelListing modelListing = new ResourcePackModelListing();
    private ActionSignHighlighter actionSignHighlighter = null;
    private final WorldEditSchematicLoader worldEditSchematicLoader = new WorldEditSchematicLoader(this);
    private final TrainCartsPlayerStore playerStore = new TrainCartsPlayerStore(this);
    private final EffectLoopPlayerController effectLoopPlayerController = new EffectLoopPlayerController(this);
    private final PlayerClientSynchronizer.Provider playerClientSynchronizerProvider = PlayerClientSynchronizer.Provider.create(this);
    private final PlayerPacketListener.Provider playerPacketListenerProvider = PlayerPacketListener.Provider.create(this);
    private SmoothCoastersAPI smoothCoastersAPI;
    private Commands commands;
    private final SoftDependency<TabNameTagHider> tabNameTagHider = new TabNameTagHiderDependency((Plugin)this){

        @Override
        protected void onEnable() {
            TrainCarts.this.getLogger().info("Neznamy TAB plugin detected! Seats with nametag hidden will also hide TAB nametags.");
        }
    };
    private final SoftDependency<Plugin> signLink = new SoftDependency<Plugin>((Plugin)this, "SignLink"){
        private Task signtask;
        private Listener variableSuppressionListener;
        {
            this.variableSuppressionListener = null;
        }

        @Override
        protected Plugin initialize(Plugin plugin) {
            return plugin;
        }

        @Override
        protected void onEnable() {
            TrainCarts.this.log(Level.INFO, "SignLink detected, support for arrival signs added!");
            Task.stop((Task)this.signtask);
            this.signtask = new Task((JavaPlugin)TrainCarts.this){

                public void run() {
                    ArrivalSigns.updateAll();
                }
            };
            this.signtask.start(0L, 10L);
            boolean hasEvent = false;
            try {
                Class.forName("com.bergerkiller.bukkit.sl.API.events.SignVariablesDetectEvent");
                hasEvent = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (hasEvent) {
                this.variableSuppressionListener = this.createVariableSuppressionListener();
                TrainCarts.this.register(this.variableSuppressionListener);
            }
        }

        private Listener createVariableSuppressionListener() {
            return new Listener(){

                @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
                public void onSignVariablesDetected(SignVariablesDetectEvent event) {
                    if (SignActionHeader.parse(event.getLine(0)).isValid()) {
                        event.setCancelled(true);
                    }
                }
            };
        }

        @Override
        protected void onDisable() {
            Task.stop((Task)this.signtask);
            this.signtask = null;
            if (this.variableSuppressionListener != null) {
                CommonUtil.unregisterListener((Listener)this.variableSuppressionListener);
                this.variableSuppressionListener = null;
            }
        }
    };
    private final SoftDependency<Plugin> lightAPI = SoftDependency.build((Plugin)this, "LightAPI").withInitializer(p -> p).whenEnable(p -> {
        this.log(Level.INFO, "LightAPI detected, the Light attachment is now available");
        AttachmentTypeRegistry.instance().register(CartAttachmentLight.TYPE);
    }).whenDisable(p -> AttachmentTypeRegistry.instance().unregister(CartAttachmentLight.TYPE)).create();
    private final SoftDependency<PortalProvider> myWorldsPortalProvider = SoftDependency.build((Plugin)this, "My_Worlds").withInitializer(p -> {
        try {
            Class<?> cls = Class.forName("com.bergerkiller.bukkit.tc.portals.plugins.MyWorldsPortalsProvider");
            return (PortalProvider)PortalProvider.class.cast(cls.getConstructor(TrainCarts.class, Plugin.class).newInstance(new Object[]{this, p}));
        }
        catch (Throwable t) {
            this.getLogger().log(Level.WARNING, "My_Worlds portal integration not available", t);
            return null;
        }
    }).whenEnable(s -> {
        if (s.get() != null) {
            TCPortalManager.addPortalSupport(s.name(), (PortalProvider)s.get());
        }
    }).whenDisable(s -> {
        if (s.get() != null) {
            TCPortalManager.removePortalSupport(s.name());
        }
    }).create();
    private final SoftDependency<PortalProvider> multiversePortalProvider = SoftDependency.build((Plugin)this, "Multiverse-Portals").withInitializer(p -> {
        try {
            Class<?> cls = p.getClass().getName().equals("com.onarandombox.MultiversePortals.MultiversePortals") ? Class.forName("com.bergerkiller.bukkit.tc.portals.plugins.MultiversePortalsLegacyProvider") : Class.forName("com.bergerkiller.bukkit.tc.portals.plugins.MultiversePortalsProvider");
            return (PortalProvider)PortalProvider.class.cast(cls.getConstructor(TrainCarts.class, Plugin.class).newInstance(new Object[]{this, p}));
        }
        catch (Throwable t) {
            this.getLogger().log(Level.WARNING, "Multiverse-Portals integration not available", t);
            return null;
        }
    }).whenEnable(s -> {
        if (s.get() != null) {
            TCPortalManager.addPortalSupport(s.name(), (PortalProvider)s.get());
        }
    }).whenDisable(s -> {
        if (s.get() != null) {
            TCPortalManager.removePortalSupport(s.name());
        }
    }).create();
    private final SoftServiceDependency<Economy> vaultEconomy = new SoftServiceDependency<Economy>((Plugin)this, "net.milkbowl.vault.economy.Economy"){

        @Override
        protected Economy initialize(Object service) throws Error, Exception {
            return (Economy)Economy.class.cast(service);
        }

        @Override
        protected void onEnable() {
            TrainCarts.this.log(Level.INFO, "Support for Economy plugin '" + this.getServicePlugin().getName() + "' enabled");
        }
    };

    public IPropertyRegistry getPropertyRegistry() {
        return this.propertyRegistry;
    }

    public GlowColorTeamProvider getGlowColorTeamProvider() {
        return this.teamProvider.glowColors();
    }

    public TeamProvider getTeamProvider() {
        return this.teamProvider;
    }

    public SeatAttachmentMap getSeatAttachmentMap() {
        return this.seatAttachmentMap;
    }

    public SpawnSignManager getSpawnSignManager() {
        return this.spawnSignManager;
    }

    public SavedAttachmentModelStore getSavedAttachmentModels() {
        return this.savedAttachmentModels;
    }

    public SavedTrainPropertiesStore getSavedTrains() {
        return this.savedTrainsStore;
    }

    public PathProvider getPathProvider() {
        return this.pathProvider;
    }

    public RouteManager getRouteManager() {
        return this.routeManager;
    }

    public SelectorHandlerRegistry getSelectorHandlerRegistry() {
        return this.selectorHandlerRegistry;
    }

    public TrainLocator getTrainLocator() {
        return this.trainLocator;
    }

    public TrainUpdateController getTrainUpdateController() {
        return this.trainUpdateController;
    }

    public OfflineGroupManager getOfflineGroups() {
        return this.offlineGroupManager;
    }

    public OfflineSignStore getOfflineSigns() {
        return this.offlineSignStore;
    }

    public TrackedSignLookup getTrackedSignLookup() {
        return this.trackedSignLookup;
    }

    public ActionRegistry getActionRegistry() {
        return this.actionRegistry;
    }

    public SignController getSignController() {
        return this.signController;
    }

    public TrainCartsAttachmentViewerMap getAttachmentViewers() {
        return this.attachmentViewerMap;
    }

    public AttachmentViewer getAttachmentViewer(Player player) {
        return this.attachmentViewerMap.getViewer(player);
    }

    public TabNameTagHider.TabPlayerNameTagHider getTabNameHider(Player player) {
        return this.tabNameTagHider.get().get(player);
    }

    public ResourcePackModelListing getModelListing() {
        ResourcePackModelListing listing = this.modelListing;
        if (listing.loadedResourcePack() != TCConfig.resourcePack) {
            listing = new ResourcePackModelListing((Plugin)this);
            listing.load(TCConfig.resourcePack);
            this.modelListing = listing;
        }
        return listing;
    }

    public WorldEditSchematicLoader getWorldEditSchematicLoader() {
        return this.worldEditSchematicLoader;
    }

    public TrainCartsPlayerStore getPlayerStore() {
        return this.playerStore;
    }

    public TrainCartsPlayer getPlayer(UUID playerUUID) {
        return this.playerStore.get(playerUUID);
    }

    public TrainCartsPlayer getPlayer(Player player) {
        return this.playerStore.get(player);
    }

    public EffectLoopPlayerController getEffectLoopPlayerController() {
        return this.effectLoopPlayerController;
    }

    public EffectLoop.Player createEffectLoopPlayer() {
        return this.effectLoopPlayerController.createPlayer();
    }

    public EffectLoop.Player createEffectLoopPlayer(int limit) {
        return this.effectLoopPlayerController.createPlayer(limit);
    }

    public PlayerClientSynchronizer.Provider getPlayerClientSynchronizerProvider() {
        return this.playerClientSynchronizerProvider;
    }

    public PlayerPacketListener.Provider getPlayerPacketListenerProvider() {
        return this.playerPacketListenerProvider;
    }

    public Economy getEconomy() {
        return this.vaultEconomy.get();
    }

    public SmoothCoastersAPI getSmoothCoastersAPI() {
        return this.smoothCoastersAPI;
    }

    public boolean isSignLinkEnabled() {
        return this.signLink.isEnabled();
    }

    public static boolean canBreak(Material type) {
        return TCConfig.allowedBlockBreakTypes.contains(type);
    }

    public static String getCurrencyText(double value) {
        Economy econ = TrainCarts.plugin.vaultEconomy.get();
        if (econ != null) {
            return econ.format(value);
        }
        return TCConfig.currencyFormat.replace("%value%", Double.toString(value));
    }

    public static String getMessage(String text) {
        return StringUtil.ampToColor((String)TCConfig.messageShortcuts.replace(text));
    }

    public static void sendMessage(Player player, String text) {
        if (plugin.isSignLinkEnabled()) {
            int startindex;
            int endindex = 0;
            while ((startindex = text.indexOf(37, endindex)) != -1 && (endindex = text.indexOf(37, startindex + 1)) != -1) {
                String varname = text.substring(startindex + 1, endindex);
                String value = varname.isEmpty() ? "%" : Variables.get((String)varname).get(player.getName());
                text = text.substring(0, startindex) + value + text.substring(endindex + 1);
                endindex = startindex + value.length();
            }
        }
        player.sendMessage(text);
    }

    public static boolean isWorldDisabled(BlockEvent event) {
        return TrainCarts.isWorldDisabled(event.getBlock().getWorld());
    }

    public static boolean isWorldDisabled(Block worldContainer) {
        return TrainCarts.isWorldDisabled(worldContainer.getWorld());
    }

    public static boolean isWorldDisabled(World world) {
        if (!TCConfig.enabledWorlds.isEmpty()) {
            return !TCConfig.enabledWorlds.contains(world);
        }
        return TCConfig.disabledWorlds.contains(world);
    }

    public static boolean isWorldDisabled(String worldname) {
        if (!TCConfig.enabledWorlds.isEmpty()) {
            return !TCConfig.enabledWorlds.contains(worldname);
        }
        return TCConfig.disabledWorlds.contains(worldname);
    }

    public boolean handlePlayerVehicleChange(Player player, Entity newVehicle) {
        try {
            MinecartMember<?> newMinecart = MinecartMemberStore.getFromEntity(newVehicle);
            MinecartMember<?> entered = MinecartMemberStore.getFromEntity(player.getVehicle());
            if (entered != null && !entered.getProperties().getPlayersExit()) {
                return false;
            }
            if (newMinecart != null && !newMinecart.getProperties().getPlayersEnter()) {
                return false;
            }
        }
        catch (Throwable t) {
            this.handle(t);
        }
        return true;
    }

    public void saveShortcuts() {
        TCConfig.messageShortcuts.save(this.config.getNode("messageShortcuts"));
        this.config.save();
    }

    public ItemParser[] getParsers(String key, int amount) {
        ItemParser[] rval;
        block4: {
            block3: {
                rval = TCConfig.parsers.get(key.toLowerCase(Locale.ENGLISH));
                if (rval == null) {
                    return new ItemParser[]{ItemParser.parse((String)key, (String)(amount == -1 ? null : Integer.toString(amount)))};
                }
                rval = (ItemParser[])rval.clone();
                if (amount != -1) break block3;
                for (int i = 0; i < rval.length; ++i) {
                    rval[i] = rval[i].setAmount(-1);
                }
                break block4;
            }
            if (amount <= 1) break block4;
            for (int i = 0; i < rval.length; ++i) {
                rval[i] = rval[i].multiplyAmount(amount);
            }
        }
        return rval;
    }

    public void putParsers(String key, ItemParser[] parsers) {
        TCConfig.putParsers(key, parsers);
    }

    protected void preloadChunks(Map<OfflineGroup, List<ForcedChunk>> chunks) {
        chunks.values().stream().flatMap(list -> list.stream()).forEachOrdered(chunk -> {
            try {
                RailLookup.forWorld(chunk.getWorld());
                chunk.getChunk();
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to load chunk " + chunk.getWorld().getName() + " [" + chunk.getX() + ", " + chunk.getZ() + "]", t);
            }
        });
        ChunkPreloadTask preloadTask = new ChunkPreloadTask((JavaPlugin)this, chunks);
        preloadTask.startPreloading();
        this.chunkPreloadTasks.add(preloadTask);
    }

    private void loadConfig(boolean isEnabling) {
        this.config = new FileConfiguration((JavaPlugin)this);
        this.config.load();
        TCConfig.load(this, this.config);
        this.config.save();
        this.autosaveTask.stop().start((long)TCConfig.autoSaveInterval, (long)TCConfig.autoSaveInterval);
        this.modelListing = new ResourcePackModelListing((Plugin)this);
        this.modelListing.load(TCConfig.resourcePack);
        if (!isEnabling) {
            this.signController.updateEnabled();
            this.actionSignHighlighter.updateEnabled();
        }
    }

    public void loadConfig() {
        this.loadConfig(false);
    }

    public int getMinimumLibVersion() {
        return 20000;
    }

    public void onLoad() {
        this.commands = new Commands();
        this.propertyRegistry = new TCPropertyRegistry(this, this.commands.getHandler());
        this.propertyRegistry.registerAll(StandardProperties.class);
        if (Util.hasPaperViewDistanceSupport()) {
            try {
                this.propertyRegistry.register(PaperPlayerViewDistanceProperty.INSTANCE);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to register paper player view distance property", t);
            }
        }
        if (Util.hasPaperCustomTrackingRangeSupport()) {
            try {
                this.propertyRegistry.register(PaperTrackingRangeProperty.INSTANCE);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to register paper tracking range property", t);
            }
        }
        CartAttachment.registerDefaultAttachments();
        RailType.values();
        DetectorRegion.init(this);
        SignAction.init();
        this.offlineSignStore.load();
        MutexZoneCache.init(this);
        this.spawnSignManager.load();
        SignActionDetector.INSTANCE.enable(this);
        this.pathProvider = new PathProvider(this, this.getDataFolder() + File.separator + "destinations.dat");
        plugin = this;
    }

    public void enable() {
        plugin = this;
        CommonEntity.forceControllerInitialization();
        Conversion.registerConverters(MinecartMemberStore.class);
        this.commands.enable(this);
        this.criticalComponents.enable((LibraryComponent)this.propertyRegistry);
        this.criticalComponents.enable((LibraryComponent)this.selectorHandlerRegistry);
        this.criticalComponents.enable((LibraryComponent)this.effectLoopPlayerController);
        this.loadConfig(true);
        SoftDependency.detectAll((Object)this);
        if (TCConfig.maxMinecartStackSize != 1) {
            for (Material material : MaterialsByName.getAllMaterials()) {
                if (!MaterialUtil.ISMINECART.get(material).booleanValue()) continue;
                ItemHandle.createHandle((Object)HandleConversion.toItemHandle((Material)material)).setMaxStackSize(TCConfig.maxMinecartStackSize);
            }
        }
        this.criticalComponents.enable((LibraryComponent)this.worldEditSchematicLoader);
        this.optionalComponents.enable((LibraryComponent)this.signController);
        this.criticalComponents.enable((LibraryComponent)this.offlineSignStore);
        this.criticalComponents.enable((LibraryComponent)this.teamProvider);
        this.criticalComponents.enable((LibraryComponent)this.trainLocator);
        this.routeManager = (RouteManager)this.optionalComponents.enable((LibraryComponent)new RouteManager(this.getDataFolder() + File.separator + "routes.yml"));
        this.smoothCoastersAPI = new SmoothCoastersAPI((Plugin)this);
        this.seatAttachmentMap = new SeatAttachmentMap();
        this.register(this.seatAttachmentMap, SeatAttachmentMap.LISTENED_TYPES);
        Statement.init();
        this.optionalComponents.enable((LibraryComponent)this.pathProvider);
        this.optionalComponents.enable((LibraryComponent)this.trainUpdateController);
        TrainProperties.load(this);
        TicketStore.load(this);
        this.savedAttachmentModels = SavedAttachmentModelStore.create(this, "SavedModels.yml", "savedModelModules");
        this.savedTrainsStore = SavedTrainPropertiesStore.create(this, "SavedTrainProperties.yml", "savedTrainModules");
        this.offlineGroupManager.load();
        MinecartMemberStore.convertAllAutomatically(this);
        ArrivalSigns.init(this.getDataFolder() + File.separator + "arrivaltimes.txt");
        this.cacheCleanupTask = new CacheCleanupTask((JavaPlugin)this).start(1L, 1L);
        RailLookup.forceRecalculation();
        this.mutexZoneUpdateTask = new MutexZoneUpdateTask((JavaPlugin)this).start(1L, 1L);
        this.spawnSignManager.enable();
        CommonUtil.nextTick((Runnable)new Runnable(){

            @Override
            public void run() {
                for (World world : WorldUtil.getWorlds()) {
                    OfflineGroupManager.removeBuggedMinecarts(world);
                }
            }
        });
        this.packetListener = new TCPacketListener(this);
        this.register(this.packetListener, TCPacketListener.LISTENED_TYPES);
        this.interactionPacketListener = new TCInteractionPacketListener(this.packetListener);
        this.register(this.interactionPacketListener, TCInteractionPacketListener.TYPES);
        this.listener = new TCListener(this);
        this.register(this.listener);
        this.register(new TCSeatChangeListener());
        this.register(new TrainChestListener(this));
        this.optionalComponents.enable((LibraryComponent)this.playerClientSynchronizerProvider);
        this.optionalComponents.enable((LibraryComponent)this.playerPacketListenerProvider);
        if (CommonCapabilities.HAS_DISPLAY_ENTITY && Common.hasCapability((String)"Common:Block:RayTraceUtilImprovements") && Common.hasCapability((String)"Common:BlockData:GetInteractableBox")) {
            this.actionSignHighlighter = (ActionSignHighlighter)this.optionalComponents.enable((LibraryComponent)new ActionSignHighlighter(this));
        }
        if (TCSuppressSeatTeleportPacketListener.SUPPRESS_POST_ENTER_PLAYER_POSITION_PACKET) {
            this.suppressSeatTeleportPacketListener = new TCSuppressSeatTeleportPacketListener(this);
            this.register(this.suppressSeatTeleportPacketListener, TCSuppressSeatTeleportPacketListener.LISTENED_TYPES);
            this.register(this.suppressSeatTeleportPacketListener);
        }
        this.log(Level.INFO, "Restoring trains and loading nearby chunks...");
        this.offlineGroupManager.refresh();
        this.preloadChunks(this.offlineGroupManager.getForceLoadedChunks());
        DetectorRegion.detectAllMinecarts();
        if (Util.hasPaperViewDistanceSupport()) {
            try {
                PaperPlayerViewDistanceProperty.INSTANCE.enable(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to enable paper player view distance property", t);
                this.propertyRegistry.unregister(PaperPlayerViewDistanceProperty.INSTANCE);
            }
        }
        if (Util.hasPaperCustomTrackingRangeSupport()) {
            try {
                PaperTrackingRangeProperty.INSTANCE.enable(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to enable paper tracking range property", t);
                this.propertyRegistry.unregister(PaperTrackingRangeProperty.INSTANCE);
            }
        }
        if (TCConfig.destroyAllOnShutdown) {
            this.offlineGroupManager.destroyAllAsync(false).thenAccept(count -> this.getLogger().info("[DestroyOnShutdown] Destroyed " + count + " trains"));
        }
        if (this.hasMetrics()) {
            Metrics metrics = this.getMetrics();
            metrics.addCustomChart((Metrics.CustomChart)new Metrics.DrilldownPie("smoothCoastersInstalled", () -> {
                HashMap<String, Integer> versions = new HashMap<String, Integer>();
                int disabled = 0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (this.smoothCoastersAPI.isEnabled(player)) {
                        String version = this.smoothCoastersAPI.getModVersion(player);
                        if (version == null) {
                            version = "unknown";
                        }
                        versions.merge(version, 1, Integer::sum);
                        continue;
                    }
                    ++disabled;
                }
                HashMap<String, Map<String, Integer>> categories = new HashMap<String, Map<String, Integer>>();
                categories.put("installed", versions);
                categories.put("not installed", Collections.singletonMap("not installed", disabled));
                return categories;
            }));
        }
        this.trainUpdateController.startUpdatingAttachments();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disable() {
        if (TCConfig.destroyAllOnShutdown) {
            try (ImplicitlySharedSet groups = MinecartGroupStore.getGroups().clone();){
                for (MinecartGroup group : groups) {
                    group.destroy();
                }
                this.getLogger().info("[DestroyOnShutdown] Destroyed " + groups.size() + " trains");
            }
        }
        if (Util.hasPaperViewDistanceSupport()) {
            try {
                PaperPlayerViewDistanceProperty.INSTANCE.disable(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to disable paper player view distance property", t);
            }
        }
        if (Util.hasPaperCustomTrackingRangeSupport()) {
            try {
                PaperTrackingRangeProperty.INSTANCE.disable(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to disable paper tracking range property", t);
            }
        }
        try {
            ResourcePackModelListing.closeAllDialogs();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to shut down all open resource pack model dialogs");
        }
        this.unregister(this.packetListener);
        this.unregister(this.interactionPacketListener);
        this.smoothCoastersAPI.unregister();
        this.listener = null;
        this.packetListener = null;
        this.interactionPacketListener = null;
        this.smoothCoastersAPI = null;
        FakePlayerSpawner.runAndClearCleanupTasks();
        Task.stop((Task)this.autosaveTask);
        Task.stop((Task)this.cacheCleanupTask);
        Task.stop((Task)this.mutexZoneUpdateTask);
        for (ChunkPreloadTask preloadTask : this.chunkPreloadTasks) {
            preloadTask.abortPreloading();
        }
        if (TCConfig.maxMinecartStackSize != 1) {
            for (Material material : MaterialsByName.getAllMaterials()) {
                if (!MaterialUtil.ISMINECART.get(material).booleanValue()) continue;
                ItemHandle.createHandle((Object)HandleConversion.toItemHandle((Material)material)).setMaxStackSize(1);
            }
        }
        MinecartGroupStore.doPostMoveLogic();
        if (!Common.hasCapability((String)"Common:EntityController:isPlayerTakeable")) {
            for (World world : WorldUtil.getWorlds()) {
                for (Chunk chunk : WorldUtil.getChunks((World)world)) {
                    for (Entity entity : ChunkUtil.getEntities((Chunk)chunk)) {
                        MinecartMember member;
                        CommonEntity commonEntity;
                        if (!(entity instanceof Minecart) || !(commonEntity = CommonEntity.get((Entity)entity)).hasPlayerPassenger() || (member = (MinecartMember)commonEntity.getController(MinecartMember.class)) == null || member.isPlayerTakeable()) continue;
                        commonEntity.eject();
                    }
                }
            }
        }
        ArrayList<ForcedChunk> allForcedChunks = new ArrayList<ForcedChunk>();
        try {
            for (MinecartGroup mg : MinecartGroup.getGroups().cloneAsIterable()) {
                mg.getChunkArea().getForcedChunks(allForcedChunks);
                mg.unload();
            }
            for (World world : WorldUtil.getWorlds()) {
                for (Chunk chunk : WorldUtil.getChunks((World)world)) {
                    for (Entity entity : ChunkUtil.getEntities((Chunk)chunk)) {
                        CommonEntity commonEntity;
                        if (entity.isDead()) continue;
                        MinecartGroup group = MinecartGroup.get(entity);
                        if (group != null) {
                            group.unload();
                        }
                        if (!(entity instanceof Minecart) || (commonEntity = CommonEntity.get((Entity)entity)).getController(MinecartMember.class) == null) continue;
                        commonEntity.setController((EntityController)new DefaultEntityController());
                    }
                }
            }
        }
        finally {
            for (ForcedChunk forcedChunk : allForcedChunks) {
                forcedChunk.close();
            }
            allForcedChunks.clear();
        }
        this.save(SaveMode.SHUTDOWN);
        ArrivalSigns.deinit();
        SignActionSpawn.deinit();
        Statement.deinit();
        SignAction.deinit();
        ItemAnimation.deinit();
        this.offlineGroupManager.deinit();
        RailLookup.clear();
        this.optionalComponents.disable();
        this.pathProvider = null;
        this.undoAllTCControllers();
        AttachmentTypeRegistry.instance().unregisterAll();
        MutexZoneCache.deinit(this);
        this.spawnSignManager.disable();
        SignActionDetector.INSTANCE.disable(this);
        this.criticalComponents.disable();
    }

    private void undoAllTCControllers() {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (World world : WorldUtil.getWorlds()) {
            for (Entity entity : WorldUtil.getEntities((World)world)) {
                CommonEntity ce = CommonEntity.get((Entity)entity);
                if (ce.getController(MinecartMember.class) == null && !(ce.getNetworkController() instanceof MinecartMemberNetwork)) continue;
                entities.add(entity);
            }
        }
        entities.forEach(CommonEntity::clearControllers);
    }

    public void redetectSignActions() {
        this.getSignController().redetectSignActions();
        RailLookup.redetectSignActions();
        for (MinecartGroup group : MinecartGroupStore.getGroups()) {
            group.getSignTracker().updatePosition();
        }
    }

    public void save(SaveMode saveMode) {
        boolean autosave = saveMode.isAutoSave();
        TrainProperties.save(autosave);
        this.savedAttachmentModels.save(autosave);
        this.savedTrainsStore.save(autosave);
        TicketStore.save(this, autosave);
        this.pathProvider.save(autosave, this.getDataFolder() + File.separator + "destinations.dat");
        if (!autosave) {
            ArrivalSigns.save(this.getDataFolder() + File.separator + "arrivaltimes.txt");
        }
        DetectorRegion.save(this, autosave);
        this.routeManager.save(autosave);
        this.offlineGroupManager.save(saveMode);
    }

    public void setBlockDataWithoutBreaking(Block block, BlockData blockData) {
        if (Common.evaluateMCVersion((String)">=", (String)"1.19")) {
            WorldUtil.setBlockDataFast((Block)block, (BlockData)blockData);
            WorldUtil.queueBlockSend((Block)block);
            this.applyBlockPhysics(block, blockData);
        } else {
            WorldUtil.setBlockData((Block)block, (BlockData)blockData);
        }
    }

    public void applyBlockPhysics(Block block, BlockData blockData) {
        if (Common.evaluateMCVersion((String)">=", (String)"1.19")) {
            this.listener.onBlockPhysics(BlockPhysicsEventDataAccessor.INSTANCE.createEvent(block, blockData));
            for (BlockFace face : FaceUtil.BLOCK_SIDES) {
                this.listener.onBlockPhysics(BlockPhysicsEventDataAccessor.INSTANCE.createEvent(block.getRelative(face), blockData));
            }
        } else {
            BlockUtil.applyPhysics((Block)block, (Material)blockData.getType());
        }
    }

    public boolean command(CommandSender sender, String cmd, String[] args) {
        return false;
    }

    public void localization() {
        this.loadLocales(Localization.class);
    }

    public void permissions() {
        this.loadPermissions(Permission.class);
    }

    private static class AutosaveTask
    extends Task {
        public AutosaveTask(TrainCarts plugin) {
            super((JavaPlugin)plugin);
        }

        public void run() {
            ((TrainCarts)this.getPlugin()).save(SaveMode.AUTOSAVE);
        }
    }

    private static class ChunkPreloadTask
    extends Task {
        private final Map<OfflineGroup, List<ForcedChunk>> chunks;
        private final List<FinishedChunks> finished = new ArrayList<FinishedChunks>();
        private int deadline;

        public ChunkPreloadTask(JavaPlugin plugin, Map<OfflineGroup, List<ForcedChunk>> chunks) {
            super(plugin);
            this.chunks = chunks;
        }

        public void startPreloading() {
            this.start(5L, 5L);
            this.deadline = CommonUtil.getServerTicks() + 12000;
        }

        public void abortPreloading() {
            this.stop();
            this.finished.forEach(FinishedChunks::close);
            this.finished.clear();
            this.chunks.values().forEach(chunks -> chunks.forEach(ForcedChunk::close));
            this.chunks.clear();
        }

        public void run() {
            if (this.finished.isEmpty() && this.chunks.isEmpty()) {
                ((TrainCarts)this.getPlugin()).chunkPreloadTasks.remove((Object)this);
                this.stop();
                return;
            }
            int ticks = CommonUtil.getServerTicks();
            if (!this.chunks.isEmpty() && ticks > this.deadline) {
                List trainNames = this.chunks.keySet().stream().map(g -> g.name).collect(Collectors.toList());
                this.getPlugin().getLogger().log(Level.SEVERE, "Failed to restore " + trainNames.size() + " keep-chunks-loaded trains in time!");
                if (trainNames.size() < 10) {
                    this.getPlugin().getLogger().log(Level.SEVERE, "Trains: " + StringUtil.combineNames(trainNames));
                }
                this.abortPreloading();
                return;
            }
            Iterator<Object> iter = this.finished.iterator();
            while (iter.hasNext()) {
                FinishedChunks chunks = iter.next();
                if (ticks <= chunks.deadline) continue;
                chunks.close();
                iter.remove();
            }
            iter = this.chunks.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                if (!((OfflineGroup)entry.getKey()).isLoadedAsGroup()) continue;
                this.finished.add(new FinishedChunks((List)entry.getValue()));
                iter.remove();
            }
        }

        private static class FinishedChunks
        implements AutoCloseable {
            public final List<ForcedChunk> chunks;
            public final int deadline;

            public FinishedChunks(List<ForcedChunk> chunks) {
                this.chunks = chunks;
                this.deadline = CommonUtil.getServerTicks() + 10;
            }

            @Override
            public void close() {
                this.chunks.forEach(ForcedChunk::close);
                this.chunks.clear();
            }
        }
    }

    private static class CacheCleanupTask
    extends Task {
        public CacheCleanupTask(JavaPlugin plugin) {
            super(plugin);
        }

        public void run() {
            RailLookup.update();
        }
    }

    private static class MutexZoneUpdateTask
    extends Task {
        public MutexZoneUpdateTask(JavaPlugin plugin) {
            super(plugin);
        }

        public void run() {
            MutexZoneCache.refreshAll();
        }
    }

    public static enum SaveMode {
        AUTOSAVE,
        COMMAND,
        SHUTDOWN;


        public boolean isAutoSave() {
            return this == AUTOSAVE;
        }
    }

    public static interface Provider {
        public TrainCarts getTrainCarts();
    }
}

