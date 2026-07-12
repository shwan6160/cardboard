/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.incubator.vector.ByteVector
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.generator.BlockPopulator
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedHolder;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.collections.ObjectCache;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentList;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.type.DimensionResourceKeyConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_18_2_Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonClasses;
import com.bergerkiller.bukkit.common.internal.CommonForcedChunkManager;
import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.CommonNextTickExecutor;
import com.bergerkiller.bukkit.common.internal.CommonPlayerMeta;
import com.bergerkiller.bukkit.common.internal.CommonRegionChangeTracker;
import com.bergerkiller.bukkit.common.internal.CommonServerLogRecorder;
import com.bergerkiller.bukkit.common.internal.CommonSignOpenListenerBukkit;
import com.bergerkiller.bukkit.common.internal.CommonSignOpenListenerPaper;
import com.bergerkiller.bukkit.common.internal.CommonVehicleMountManager;
import com.bergerkiller.bukkit.common.internal.NextTickListener;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.internal.TimingsListener;
import com.bergerkiller.bukkit.common.internal.hooks.AdvancementDataPlayerHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.LookupEntityClassMap;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.internal.logic.BlockPhysicsEventDataAccessor;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PlayerGameInfoSupplier_ViaVersion;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandler;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerSelector;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.util.RGBColorToIntConversion;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryBaseImpl;
import com.bergerkiller.bukkit.common.softdependency.SoftDependency;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.incubator.vector.ByteVector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonPlugin
extends PluginBase {
    public static final TimingsRootListener TIMINGS = new TimingsRootListener();
    private static CommonPlugin instance;
    private final LibraryComponentList<CommonPlugin> components = LibraryComponentList.forPlugin(this);
    private EntityMap<Player, CommonPlayerMeta> playerMetadata;
    private CommonListener listener;
    private final ArrayList<SoftReference<EntityMap>> maps = new ArrayList();
    private final List<TimingsListener> timingsListeners = new ArrayList<TimingsListener>(1);
    private final List<Task> startedTasks = new ArrayList<Task>();
    private final ImplicitlySharedSet<Entity> entitiesRemovedFromServer = new ImplicitlySharedSet();
    private final HashMap<String, TypedValue> debugVariables = new HashMap();
    private final ThreadPoolExecutor fileIOWorker;
    private CommonEventFactory eventFactory;
    private boolean isServerStarted = false;
    private PacketHandler packetHandler = null;
    private boolean warnedAboutBrokenBundlePacket = false;
    private final PermissionHandlerSelector permissionHandlerSelector = new PermissionHandlerSelector(this);
    private CommonServerLogRecorder serverLogRecorder = new CommonServerLogRecorder(this);
    private CommonMapController mapController = null;
    private CommonForcedChunkManager forcedChunkManager = null;
    private CommonVehicleMountManager vehicleMountManager = null;
    private Function<Player, PlayerGameInfo> gameInfoSupplier = p -> PlayerGameInfo.SERVER;
    private boolean isFrameTilingSupported = true;
    private boolean isFrameDisplaysEnabled = true;
    private boolean isMapDisplaysEnabled = true;
    private boolean teleportPlayersToSeat = true;
    private boolean forceSynchronousSaving = false;
    private boolean isDebugCommandRegistered = false;
    private boolean cloudDisableBrigadier = false;
    private boolean enableProtocolLibPacketHandler = true;
    private Material fallbackItemModelType = null;

    public CommonPlugin() {
        if (!CommonBootstrap.verifyShadedAssets(this.getLogger())) {
            throw new IllegalStateException("BKCommonLib jar is corrupt! Please redownload.");
        }
        this.fileIOWorker = new ThreadPoolExecutor(3, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024), r -> {
            Thread t = new Thread(r, "BKCommonLib-IOWorker");
            t.setDaemon(true);
            return t;
        });
        this.fileIOWorker.allowCoreThreadTimeOut(true);
        MountiplexUtil.LOGGER = this.getLogger();
        instance = this;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static CommonPlugin getInstance() {
        if (instance == null) {
            throw new RuntimeException("BKCommonLib is not enabled - Plugin Instance can not be obtained! (disjointed Class state?)");
        }
        return instance;
    }

    public static CompletableFuture<Void> runIOTaskAsync(CheckedRunnable runnable) {
        CommonPlugin instance = CommonPlugin.instance;
        if (instance != null) {
            return CommonUtil.runCheckedAsync(runnable, instance.getFileIOExecutor());
        }
        return CommonUtil.runCheckedAsync(runnable);
    }

    public void registerMap(EntityMap map) {
        this.maps.add(new SoftReference<EntityMap>(map));
    }

    public boolean isServerStarted() {
        return this.isServerStarted;
    }

    public boolean isFrameTilingSupported() {
        return this.isFrameTilingSupported;
    }

    public boolean isFrameDisplaysEnabled() {
        return this.isFrameDisplaysEnabled;
    }

    public boolean isMapDisplaysEnabled() {
        return this.isMapDisplaysEnabled;
    }

    public boolean teleportPlayersToSeat() {
        return this.teleportPlayersToSeat;
    }

    public boolean forceSynchronousSaving() {
        return this.forceSynchronousSaving;
    }

    public boolean isCloudBrigadierDisabled() {
        return this.cloudDisableBrigadier;
    }

    public Material getFallbackItemModelType() {
        Material m = this.fallbackItemModelType;
        if (m == null) {
            this.fallbackItemModelType = m = CommonPlugin.getDefaultFallbackItemModelType();
        }
        return m;
    }

    public static Material getDefaultFallbackItemModelType() {
        return MaterialUtil.getFirst("IRON_NUGGET", "LEGACY_IRON_NUGGET", "LEGACY_GOLD_NUGGET");
    }

    public <T> TypedValue<T> getDebugVariable(String name, Class<T> type, T value) {
        this.registerDebugCommand();
        TypedValue<T> typed = this.debugVariables.get(name);
        if (typed == null || typed.type != type) {
            typed = new TypedValue<T>(type, value);
            this.debugVariables.put(name, typed);
        }
        return typed;
    }

    @Deprecated
    public void addNextTickListener(NextTickListener listener) {
        this.addTimingsListener(new NextTickListenerProxy(listener));
    }

    @Deprecated
    public void removeNextTickListener(NextTickListener listener) {
        Iterator<TimingsListener> iter = this.timingsListeners.iterator();
        while (iter.hasNext()) {
            TimingsListener t = iter.next();
            if (!(t instanceof NextTickListenerProxy) || ((NextTickListenerProxy)t).listener != listener) continue;
            iter.remove();
        }
        CommonPlugin.TIMINGS.setActive(!this.timingsListeners.isEmpty());
    }

    public void addTimingsListener(TimingsListener listener) {
        this.timingsListeners.add(listener);
        CommonPlugin.TIMINGS.setActive(true);
    }

    public void removeTimingsListener(TimingsListener listener) {
        this.timingsListeners.remove(listener);
        CommonPlugin.TIMINGS.setActive(!this.timingsListeners.isEmpty());
    }

    public void notifyAddedEarly(World world, Entity e) {
        this.entitiesRemovedFromServer.remove(e);
    }

    public void notifyAdded(World world, Entity e) {
        CommonUtil.callEvent(new EntityAddEvent(world, e));
    }

    public void notifyRemoved(World world, Entity e) {
        this.entitiesRemovedFromServer.add(e);
        CommonUtil.callEvent(new EntityRemoveEvent(world, e));
    }

    public void notifyRemovedFromServer(World world, Entity e, boolean removeFromChangeSet) {
        EntityController<?> controller;
        EntityHook hook;
        if (removeFromChangeSet) {
            this.entitiesRemovedFromServer.remove(e);
        }
        Iterator<SoftReference<EntityMap>> iter = this.maps.iterator();
        while (iter.hasNext()) {
            EntityMap map = iter.next().get();
            if (map == null) {
                iter.remove();
                continue;
            }
            map.remove(e);
        }
        if (CommonUtil.hasHandlers(EntityRemoveFromServerEvent.getHandlerList())) {
            CommonUtil.callEvent(new EntityRemoveFromServerEvent(e));
        }
        if ((hook = EntityHook.get(HandleConversion.toEntityHandle(e), EntityHook.class)) != null && hook.hasController() && (controller = hook.getController()).getEntity() != null) {
            ((CommonEntity)controller.getEntity()).setController(null);
        }
    }

    public void notifyWorldAdded(World world) {
        CreaturePreSpawnHandler.INSTANCE.onWorldEnabled(world);
        EntityAddRemoveHandler.INSTANCE.onWorldEnabled(world);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CommonPlayerMeta getPlayerMeta(Player player) {
        EntityMap<Player, CommonPlayerMeta> entityMap = this.playerMetadata;
        synchronized (entityMap) {
            CommonPlayerMeta meta = (CommonPlayerMeta)this.playerMetadata.get(player);
            if (meta == null) {
                meta = new CommonPlayerMeta(player);
                this.playerMetadata.put(player, meta);
            }
            return meta;
        }
    }

    public CommonMapController getMapController() {
        return this.mapController;
    }

    public PermissionHandler getPermissionHandler() {
        return this.permissionHandlerSelector.current();
    }

    public CommonEventFactory getEventFactory() {
        return this.eventFactory;
    }

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    public CommonForcedChunkManager getForcedChunkManager() {
        return this.forcedChunkManager;
    }

    public CommonVehicleMountManager getVehicleMountManager() {
        return this.vehicleMountManager;
    }

    public PlayerGameInfo getGameInfo(Player player) {
        return this.gameInfoSupplier.apply(player);
    }

    public CommonServerLogRecorder getServerLogRecorder() {
        return this.serverLogRecorder;
    }

    public Executor getFileIOExecutor() {
        return this.fileIOWorker;
    }

    private boolean updatePacketHandler() {
        try {
            Class handlerClass = CommonPacketHandler.class;
            if (this.enableProtocolLibPacketHandler && CommonUtil.isPluginEnabled("ProtocolLib")) {
                if (ProtocolLibPacketHandler.isBundlePacketWorking()) {
                    handlerClass = ProtocolLibPacketHandler.class;
                } else if (!this.warnedAboutBrokenBundlePacket) {
                    this.warnedAboutBrokenBundlePacket = true;
                    Logging.LOGGER_NETWORK.log(Level.WARNING, "ProtocolLib cannot be used because it does not support the Bundle packet yet");
                    Logging.LOGGER_NETWORK.log(Level.WARNING, "Please update ProtocolLib to a 1.19.4+ supporting version (build #620 or newer)");
                }
            }
            if (this.packetHandler != null && this.packetHandler.getClass() == handlerClass) {
                return true;
            }
            PacketHandler handler = (PacketHandler)handlerClass.newInstance();
            if (this.packetHandler != null) {
                this.packetHandler.transfer(handler);
                if (!this.packetHandler.onDisable()) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to disable the previous " + this.packetHandler.getName() + " packet handler!");
                    return false;
                }
            }
            this.packetHandler = handler;
            if (!this.packetHandler.onEnable()) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to enable the " + this.packetHandler.getName() + " packet handler!");
                return false;
            }
            Logging.LOGGER_NETWORK.log(Level.INFO, "Now using " + handler.getName() + " to provide Packet Listener and Monitor support");
            return true;
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to register a valid Packet Handler", t);
            return false;
        }
    }

    @Override
    protected void onCriticalStartupFailure(String reason) {
        try {
            if (CommonBootstrap.isCommonServerInitialized()) {
                this.log(Level.INFO, "Failed to initialize for server " + CommonBootstrap.initCommonServer().getServerDetails());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
        super.onCriticalStartupFailure(reason);
        this.serverLogRecorder.disable();
    }

    public void registerDebugCommand() {
        if (this.isDebugCommandRegistered) {
            return;
        }
        this.isDebugCommandRegistered = true;
        try {
            Constructor constr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constr.setAccessible(true);
            Command debugCommand = (Command)constr.newInstance(new Object[]{"debugvar", this});
            debugCommand.setDescription("Developer debugging commands for changing values at runtime. Not to be used in production.");
            debugCommand.setUsage("/debugvar [name] [value...]");
            debugCommand.setAliases(Arrays.asList("dvar"));
            debugCommand.setPermission("bkcommonlib.debug.variables");
            CommandMap commandMap = (CommandMap)SafeField.get(Bukkit.getPluginManager(), "commandMap", SimpleCommandMap.class);
            commandMap.register(this.getName(), debugCommand);
            if (Common.evaluateMCVersion(">=", "1.13")) {
                CommonUtil.nextTick(() -> {
                    try {
                        Method m = Bukkit.getServer().getClass().getMethod("syncCommands", new Class[0]);
                        m.invoke((Object)Bukkit.getServer(), new Object[0]);
                    }
                    catch (Throwable t) {
                        this.getLogger().log(Level.WARNING, "Failed to update brigadier", t);
                    }
                });
            }
        }
        catch (Throwable t) {
            this.getLogger().log(Level.WARNING, "Failed to register debug command", t);
        }
    }

    @Override
    public void permissions() {
    }

    @Override
    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
        if (!enabled) {
            this.packetHandler.removePacketListeners(plugin);
        }
        this.mapController.updateDependency(plugin, pluginName, enabled);
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }
        if (pluginName.equals("ViaVersion")) {
            if (enabled) {
                this.gameInfoSupplier = new PlayerGameInfoSupplier_ViaVersion();
                this.log(Level.INFO, "ViaVersion detected, will use it to detect player game versions");
            } else {
                this.gameInfoSupplier = p -> PlayerGameInfo.SERVER;
                this.log(Level.INFO, "ViaVersion was disabled, will no longer use it to detect player game versions");
            }
        }
    }

    @Override
    public int getMinimumLibVersion() {
        return 0;
    }

    public void onLoad() {
        try {
            if (!Common.IS_COMPATIBLE) {
                return;
            }
            if (Common.evaluateMCVersion("<=", "1.12.2")) {
                this.rewriteClass("com.bergerkiller.bukkit.common.proxies.BlockStateProxy", (plugin, name, classBytes) -> ASMUtil.removeClassMethods(classBytes, new HashSet<String>(Arrays.asList("getBlockData()Lorg/bukkit/block/data/BlockData;", "setBlockData(Lorg/bukkit/block/data/BlockData;)V"))));
            }
            this.gameInfoSupplier = p -> PlayerGameInfo.SERVER;
            CommonClasses.init();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "An error occurred while loading", t);
        }
    }

    @Override
    public void enable() {
        int version;
        if (!Common.IS_COMPATIBLE) {
            String verText = Common.TEMPLATE_RESOLVER.getDebugSupportedVersionsString();
            this.log(Level.SEVERE, "This version of BKCommonLib is not compatible with: " + Common.SERVER.getServerDetails());
            this.log(Level.SEVERE, "It could be that BKCommonLib has to be updated, as the current version is built for MC " + verText);
            this.log(Level.SEVERE, "Please look for a new updated BKCommonLib version that is compatible:");
            this.log(Level.SEVERE, "https://www.spigotmc.org/resources/bkcommonlib.39590/");
            this.log(Level.SEVERE, "Unstable development builds for MC " + Common.MC_VERSION + " may be found on our continuous integration server:");
            this.log(Level.SEVERE, "https://ci.mg-dev.eu/job/BKCommonLib/");
            this.onCriticalStartupFailure("BKCommonLib is not compatible with " + Common.SERVER.getServerDetails());
            return;
        }
        this.log(Level.INFO, "BKCommonLib is running on " + Common.SERVER.getServerDetails());
        try {
            CommonBootstrap.preloadCriticalComponents();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize some critical components", t);
        }
        FileConfiguration config = new FileConfiguration(this);
        config.load();
        config.setHeader("This is the main configuration file of BKCommonLib");
        config.addHeader("Normally you should not have to make changes to this file");
        config.addHeader("Unused components of the library can be disabled to improve performance");
        config.addHeader("By default all components and features are enabled");
        config.setHeader("enableMapDisplays", "\nWhether the Map Display engine is enabled, running in the background to refresh and render maps");
        config.addHeader("enableMapDisplays", "When enabled, the map item tracking may impose a slight overhead");
        config.addHeader("enableMapDisplays", "If no plugin is using map displays, then this can be safely disabled to improve performance");
        this.isMapDisplaysEnabled = config.get("enableMapDisplays", Boolean.valueOf(true));
        if (this.isMapDisplaysEnabled && CommonBootstrap.isHeadlessJDK()) {
            this.isMapDisplaysEnabled = false;
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "The Map Displays feature has been turned off because the server is incompatible");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Reason: The Java AWT runtime library is not available");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "This is usually because a headless JVM is used for the server");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Please install and configure a non-headless JVM to have Map Displays work");
        }
        config.setHeader("enableItemFrameDisplays", "\nWhether all item frames on the server are tracked to see if they display a map display.");
        config.addHeader("enableItemFrameDisplays", "This allows for map displays to be displayed on item frames and interacted with.");
        config.addHeader("enableItemFrameDisplays", "If 'enableItemFrameTiling' is also true, then this allows for multi-item frame displays.");
        config.addHeader("enableItemFrameDisplays", "Tracking the existence of all item frames on the server can pose an overhead, as");
        config.addHeader("enableItemFrameDisplays", "shown under the 'MapDisplayFramedMapUpdater' task. Turning this off can help performance.");
        config.addHeader("enableItemFrameDisplays", "If 'enableMapDisplays' is true then player-held maps will continue working fine.");
        this.isFrameDisplaysEnabled = config.get("enableItemFrameDisplays", Boolean.valueOf(true));
        config.setHeader("enableItemFrameTiling", "\nWhether multiple item frames next to each other can merge to show one large display");
        config.addHeader("enableItemFrameTiling", "This allows Map Displays to be displayed on multiple item frames at a larger resolution");
        config.addHeader("enableItemFrameTiling", "The tiling detection logic poses some overhead on the server, and if unused, can be disabled");
        this.isFrameTilingSupported = config.get("enableItemFrameTiling", Boolean.valueOf(true));
        config.setHeader("teleportPlayersToSeat", "\nWhether to teleport players to their supposed seat while they hold the sneak button");
        config.addHeader("teleportPlayersToSeat", "This is used on Minecraft 1.16 and later to make sure players stay near their seat,");
        config.addHeader("teleportPlayersToSeat", "when exiting the seat was cancelled.");
        this.teleportPlayersToSeat = config.get("teleportPlayersToSeat", Boolean.valueOf(true));
        config.setHeader("forceSynchronousSaving", "\nWhether to force saving to be done synchronously, rather than asynchronously");
        config.addHeader("forceSynchronousSaving", "If the Asynchronous File I/O in the JVM has a glitch in it, it might cause very large");
        config.addHeader("forceSynchronousSaving", "corrupt (.yml) files to be generated. On server restart this can cause a loss of data.");
        config.addHeader("forceSynchronousSaving", "Synchronous saving (such as YAML) may hurt server performance for large files,");
        config.addHeader("forceSynchronousSaving", "but will prevent these issues from happening.");
        this.forceSynchronousSaving = config.get("forceSynchronousSaving", Boolean.valueOf(false));
        config.setHeader("cloudDisableBrigadier", "\nWhether to disable using brigadier for all plugins that use BKCL's cloud command framework");
        config.addHeader("cloudDisableBrigadier", "This might fix problems that occur because of bugs in brigadier, or cloud's handler of it");
        this.cloudDisableBrigadier = config.get("cloudDisableBrigadier", Boolean.valueOf(false));
        config.setHeader("preloadTemplateClasses", "\nWhether to load and initialize ALL template classes when BKCommonLib first loads up.");
        config.addHeader("preloadTemplateClasses", "This reveals any at-runtime server incompatibility errors early on and eliminates any");
        config.addHeader("preloadTemplateClasses", "at-runtime lazy initialization lag. It does cause a lot of classes to be loaded into the");
        config.addHeader("preloadTemplateClasses", "JVM that may never get used, which wastes memory. Only enable this for debugging reasons!");
        config.addHeader("preloadTemplateClasses", "As loading is done on all CPU cores, this might improve boot performance on multi-core systems");
        boolean preloadTemplateClasses = config.get("preloadTemplateClasses", Boolean.valueOf(false));
        config.setHeader("trackForcedChunkCreationStack", "\nWhether to track the stack trace of where forced chunks are created");
        config.addHeader("trackForcedChunkCreationStack", "This is useful to detect ForcedChunk instances that are not closed by the developer.");
        config.addHeader("trackForcedChunkCreationStack", "Once a missed close is detected, tracking is automatically started anyway.");
        config.addHeader("trackForcedChunkCreationStack", "As such, this option is primarily useful to diagnose this problem at server startup");
        boolean trackForcedChunkCreationStack = config.get("trackForcedChunkCreationStack", Boolean.valueOf(false));
        config.setHeader("fallbackItemModelType", "\nMaterial type to use for custom-namespace item models in resource pack listing");
        config.addHeader("fallbackItemModelType", "Is used on Minecraft 1.21.2 and later with the item_model data component");
        this.fallbackItemModelType = config.get("fallbackItemModelType", CommonPlugin.getDefaultFallbackItemModelType());
        config.setHeader("enableProtocolLibPacketHandler", "\nWhether to use ProtocolLib for handling packets, if that plugin is installed");
        config.addHeader("enableProtocolLibPacketHandler", "Disabling this could cause bugs when multiple plugins mess with packets");
        this.enableProtocolLibPacketHandler = config.get("enableProtocolLibPacketHandler", Boolean.valueOf(true));
        config.save();
        if (preloadTemplateClasses) {
            CommonBootstrap.preloadTemplateClasses(null);
            BlockPhysicsEventDataAccessor.init();
        }
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }
        List<String> welcomeMessages = Arrays.asList("This library is written with stability in mind.", "No Bukkit moderators were harmed while compiling this piece of art.", "Have a problem Bukkit can't fix? Write a library!", "Bringing home the bacon since 2011!", "Completely virus-free and scanned by various Bukkit-dev-staff watching eyes.", "Hosts all the features that are impossible to include in a single Class", "CraftBukkit: redone, reworked, translated and interfaced.", "Having an error? *gasp* Don't forget to file a ticket on github!", "Package versioning is what brought BKCommonLib and CraftBukkit closer together!", "For all the haters out there: BKCommonLib at least tries!", "Want fries with that? We have hidden fries in the FoodUtil class.", "Not enough wrappers. Needs more wrappers. Moooreee...", "Reflection can open the way to everyone's heart, including CraftBukkit.", "Our love is not permitted by the overlords. We must flee...", "Now a plugin, a new server implementation tomorrow???", "Providing support for supporting the unsupportable.", "Every feature break in Bukkit makes my feature list longer.", "I...I forgot an exclamation mark...*rages internally*", "I am still winning the game. Are you?", "We did what our big brother couldn't", "If you need syntax help visit javadocs.a.b.v1_2_3.net", "v1_1_R1 1+1+1 = 3, Half life 3 confirmed?", "BKCommonLib > Minecraft.a.b().q.f * Achievement.OBFUSCATED.value", "BKCommonLib isn't a plugin, its a language based on english.", "Updating is like reinventing the wheel for BKCommonLib.", "Say thanks to our wonderful devs: Friwi, KamikazePlatypus and mg_1999", "Welcome to the modern era, welcome to callback hell", "Soon generating lambda expressions from thin air!", "We have a Discord!", "Years of Minecraft history carefully catalogued", "50% generated, 50% crafted by an artificial intelligence", "Supplier supplying suppliers for your lazy needs!", "Please wait while we get our code ready...", "60% of the time, it works all the time.", "I don't make mistakes. I just find ways not to code this plugin.", "Less complicated than the American election.", "Bless you SpottedLeaf <3", "Includes records and ways to change them!", "Working hard to not do too much", "Psst! I am backwards-compatible! Don't forget to update me regularly... :(");
        this.setEnableMessage(welcomeMessages.get(new Random().nextInt(welcomeMessages.size())));
        this.setDisableMessage(null);
        this.components.enable(this.serverLogRecorder);
        this.components.enable(new CommonRegionChangeTracker(this));
        this.components.enable("Region Flag Change Tracker", new LibraryComponent(){

            @Override
            public void enable() {
                RegionFlagRegistryBaseImpl.instance().enable((Plugin)CommonPlugin.this);
            }

            @Override
            public void disable() {
                RegionFlagRegistryBaseImpl.instance().disable();
            }
        });
        this.components.enableForVersions("Dimension to Holder conversion", "1.18.2", null, MC1_18_2_Conversion::initComponent);
        this.components.enableCreate(OfflineWorld::initializeComponent);
        this.components.enableForVersions("Dimension resource key tracker", "1.16", "1.16.1", DimensionResourceKeyConversion.Tracker::new);
        this.components.enableForVersions("Dimension resource key tracker", "1.19", null, DimensionResourceKeyConversion.Tracker::new);
        ChunkHandleTracker.INSTANCE.startTracking(this);
        BlockDataWrapperHook.init();
        CommonNextTickExecutor.INSTANCE.setExecutorTask(new CommonNextTickExecutor.ExecutorTask(this));
        try {
            LookupEntityClassMap.hook();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to hook LookupEntityClassMap", t);
        }
        if (this.isMapDisplaysEnabled) {
            MapColorPalette.getColor(0, 0, 0);
            if (RGBColorToIntConversion.ABGR.isUsingSIMD()) {
                this.getLogger().log(Level.INFO, "JDK17+ incubator vector maths are enabled (" + CommonPlugin.getPreferredSIMDBits() + " bits). Will use it for loading MapDisplay textures.");
            }
        }
        TagHandle.T.forceInitialization();
        EntityAddRemoveHandler.INSTANCE.onEnabled(this);
        this.vehicleMountManager = new CommonVehicleMountManager(this);
        this.vehicleMountManager.enable();
        this.forcedChunkManager = new CommonForcedChunkManager(this, trackForcedChunkCreationStack);
        this.forcedChunkManager.enable();
        SoftDependency.detectAll(this.permissionHandlerSelector);
        this.permissionHandlerSelector.detectPermOption();
        this.eventFactory = new CommonEventFactory();
        PortalHandler.INSTANCE.enable(this);
        this.playerMetadata = new EntityMap();
        this.listener = new CommonListener();
        this.register(this.listener);
        if (CommonCapabilities.HAS_SIGN_OPEN_EVENT_PAPER) {
            try {
                CommonSignOpenListenerPaper.register(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to listen for paper's open sign event", t);
            }
        } else if (CommonCapabilities.HAS_SIGN_OPEN_EVENT) {
            try {
                CommonSignOpenListenerBukkit.register(this);
            }
            catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, "Failed to listen for bukkit's open sign event", t);
            }
        }
        this.mapController = new CommonMapController();
        if (this.isMapDisplaysEnabled) {
            this.mapController.onEnable(this, this.startedTasks);
        }
        this.startedTasks.add(new MoveEventHandler(this).start(1L, 1L));
        this.startedTasks.add(new EntityRemovalHandler(this).start(1L, 1L));
        this.startedTasks.add(new CreaturePreSpawnEventHandlerDetectorTask(this).start(0L, 20L));
        this.startedTasks.add(new ObjectCacheCleanupTask(this).start(10L, 36000L));
        if (CommonCapabilities.HAS_ADVANCEMENTS) {
            this.startedTasks.add(new AdvancementProgressEventHandlerDetectorTask(this).start(0L, 1L));
            if (CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
                CommonPlugin.initAdvancementProgressHandler();
            }
        }
        if (!ServerPlayerHandle.T.getRemoveQueue.isAvailable()) {
            this.startedTasks.add(new EntityRemoveQueueSyncTask(this).start(1L, 1L));
        }
        CommonUtil.nextTick(() -> {
            this.isServerStarted = true;
        });
        for (World world : WorldUtil.getWorlds()) {
            this.notifyWorldAdded(world);
        }
        if (this.hasMetrics()) {
            // empty if block
        }
        Common.SERVER.enable(this);
        if (preloadTemplateClasses) {
            CommonClasses.initializeLogicClasses(this.getLogger());
        }
        if ((version = this.getVersionNumber()) != 20001) {
            this.log(Level.SEVERE, "Common.VERSION needs to be updated to contain '" + version + "'!");
        }
    }

    @Override
    public void disable() {
        Iterator<Task> entities = new ArrayList();
        for (World world : WorldUtil.getWorlds()) {
            CreaturePreSpawnHandler.INSTANCE.onWorldDisabled(world);
            for (Entity entity : WorldUtil.getEntities(world)) {
                entities.add((Task)entity);
            }
            Iterator<Object> iterator = entities.iterator();
            while (iterator.hasNext()) {
                Entity entity;
                entity = (Entity)iterator.next();
                CommonEntity.clearControllers(entity);
            }
            entities.clear();
        }
        this.components.disable();
        ChunkHandleTracker.INSTANCE.stopTracking();
        PortalHandler.INSTANCE.disable(this);
        this.mapController.onDisable(this);
        for (World world : Bukkit.getWorlds()) {
            EntityAddRemoveHandler.INSTANCE.onWorldDisabled(world);
        }
        EntityAddRemoveHandler.INSTANCE.onDisabled();
        HandlerList.unregisterAll((Listener)this.listener);
        PacketUtil.removePacketListener(this.mapController);
        this.vehicleMountManager.disable();
        this.vehicleMountManager = null;
        for (Task task : this.startedTasks) {
            task.stop();
        }
        this.startedTasks.clear();
        try {
            this.packetHandler.onDisable();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to properly disable the Packet Handler", t);
        }
        this.packetHandler = null;
        try {
            LookupEntityClassMap.unhook();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to unhook LookupEntityClassMap", t);
        }
        this.forcedChunkManager.disable(this);
        this.forcedChunkManager = null;
        CommonPlugin.flushSaveOperations(null);
        this.fileIOWorker.shutdown();
        Common.SERVER.disable(this);
        CommonNextTickExecutor.INSTANCE.setExecutorTask(null);
        try {
            BlockData.values().forEach(b -> BlockDataWrapperHook.INSTANCE.unhook(b.getData()));
            BlockDataWrapperHook.disableHook();
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Failed to disable the BlockData hook, some stuff might remain");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            AdvancementDataPlayerHook.unhook(player);
        }
        MountiplexUtil.unloadMountiplex();
        instance = null;
    }

    @Override
    public boolean command(CommandSender sender, String command, String[] args) {
        if (this.debugVariables.isEmpty()) {
            return false;
        }
        if (LogicUtil.contains(command, "debugvar", "dvar")) {
            MessageBuilder message = new MessageBuilder();
            if (args.length == 0) {
                message.green("This command allows you to tweak debug settings in plugins").newLine();
                message.green("All debug variables should be cleared in official builds").newLine();
                message.green("Available debug variables:").newLine();
                message.setSeparator(ChatColor.YELLOW, " \\ ").setIndent(4);
                for (String variable : this.debugVariables.keySet()) {
                    message.green(variable);
                }
            } else {
                ArrayList<String> variableNames = new ArrayList<String>(args.length);
                ArrayList<TypedValue> variables = new ArrayList<TypedValue>(args.length);
                ArrayList<String> values = new ArrayList<String>(args.length);
                for (String arg : args) {
                    TypedValue variable = this.debugVariables.get(arg);
                    if (variable != null) {
                        variables.add(variable);
                        variableNames.add(arg);
                        continue;
                    }
                    values.add(arg);
                }
                if (variables.size() != values.size()) {
                    if (variables.isEmpty()) {
                        message.red("No debug variable of name '").yellow(values.get(0)).red("'!");
                    } else {
                        for (int i = 0; i < variables.size(); ++i) {
                            if (i > 0) {
                                message.newLine();
                            }
                            message.green("Value of variable '").yellow(variableNames.get(i)).green("' ");
                            message.green("= ");
                            message.white(((TypedValue)variables.get(i)).toString());
                        }
                    }
                } else {
                    for (int i = 0; i < variables.size(); ++i) {
                        TypedValue value = (TypedValue)variables.get(i);
                        if (i > 0) {
                            message.newLine();
                        }
                        message.green("Value of variable '").yellow(variableNames.get(i)).green("' ");
                        message.green("set to ");
                        value.parseSet((String)values.get(i));
                        message.white(value.toString());
                    }
                }
            }
            message.send(sender);
            return true;
        }
        return false;
    }

    public static void flushSaveOperations(Plugin plugin) {
        Logger logger = plugin == null ? Logging.LOGGER_CONFIG : plugin.getLogger();
        File directory = plugin == null ? null : plugin.getDataFolder();
        for (File file : FileConfiguration.findSaveOperationsInDirectory(directory)) {
            if (FileConfiguration.flushSaveOperation(file, 500L)) continue;
            logger.log(Level.INFO, "Saving " + CommonPlugin.getPluginsRelativePath(plugin, file) + "...");
            FileConfiguration.flushSaveOperation(file);
        }
    }

    private static String getPluginsRelativePath(Plugin plugin, File file) {
        try {
            File pluginsDirectory = plugin == null ? CraftServerHandle.instance().getPluginsDirectory().getAbsoluteFile() : plugin.getDataFolder().getAbsoluteFile().getParentFile();
            Path pluginsParentPath = pluginsDirectory.getParentFile().toPath();
            Path filePath = file.getAbsoluteFile().toPath();
            return pluginsParentPath.relativize(filePath).toString();
        }
        catch (Throwable t) {
            return file.getAbsolutePath();
        }
    }

    private static int getPreferredSIMDBits() {
        try {
            return ByteVector.SPECIES_PREFERRED.length() * 8;
        }
        catch (Throwable throwable) {
            return -1;
        }
    }

    private static void initAdvancementProgressHandler() {
        AdvancementDataPlayerHook.getAdvancementsInitFailure().ifPresent(err -> CommonPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to hook advancement progress. Advancement disabling won't work! Error: " + err));
        for (Player player : Bukkit.getOnlinePlayers()) {
            AdvancementDataPlayerHook.hook(player);
        }
    }

    private static class NextTickListenerProxy
    implements TimingsListener {
        private final NextTickListener listener;

        public NextTickListenerProxy(NextTickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onNextTicked(Runnable runnable, long executionTime) {
            this.listener.onNextTicked(runnable, executionTime);
        }

        @Override
        public void onChunkLoad(Chunk chunk, long executionTime) {
        }

        @Override
        public void onChunkGenerate(Chunk chunk, long executionTime) {
        }

        @Override
        public void onChunkUnloading(World world, long executionTime) {
        }

        @Override
        public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {
        }
    }

    public static class TimingsRootListener
    implements TimingsListener {
        private boolean active = false;

        public final boolean isActive() {
            return this.active;
        }

        private final void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void onNextTicked(Runnable runnable, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onNextTicked(runnable, executionTime);
                    }
                }
                catch (Throwable t) {
                    Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkLoad(Chunk chunk, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkLoad(chunk, executionTime);
                    }
                }
                catch (Throwable t) {
                    Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkGenerate(Chunk chunk, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkGenerate(chunk, executionTime);
                    }
                }
                catch (Throwable t) {
                    Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkUnloading(World world, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkUnloading(world, executionTime);
                    }
                }
                catch (Throwable t) {
                    Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkPopulate(chunk, populator, executionTime);
                    }
                }
                catch (Throwable t) {
                    Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }
    }

    private static class MoveEventHandler
    extends Task {
        public MoveEventHandler(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            CommonPlugin.getInstance().getEventFactory().handleEntityMove();
        }
    }

    private static class EntityRemovalHandler
    extends Task {
        public EntityRemovalHandler(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            EntityAddRemoveHandler.INSTANCE.processEventsForAllWorlds();
            CommonPlugin plugin = CommonPlugin.getInstance();
            if (plugin.entitiesRemovedFromServer.isEmpty()) {
                return;
            }
            boolean clearRemovedSet = false;
            try (Object removedCopy = plugin.entitiesRemovedFromServer.clone();){
                Iterator iterator = ((ImplicitlySharedSet)removedCopy).iterator();
                while (iterator.hasNext()) {
                    Entity removedEntity = (Entity)iterator.next();
                    plugin.notifyRemovedFromServer(removedEntity.getWorld(), removedEntity, false);
                }
                if (plugin.entitiesRemovedFromServer.refEquals((ImplicitlySharedHolder<?>)removedCopy)) {
                    clearRemovedSet = true;
                } else {
                    plugin.entitiesRemovedFromServer.removeAll((Collection<?>)removedCopy);
                }
            }
            if (clearRemovedSet) {
                plugin.entitiesRemovedFromServer.clear();
            }
        }
    }

    private static class CreaturePreSpawnEventHandlerDetectorTask
    extends Task {
        private boolean _creaturePreSpawnEventHasHandlers = CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList());

        public CreaturePreSpawnEventHandlerDetectorTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            boolean hasHandlers = CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList());
            if (hasHandlers != this._creaturePreSpawnEventHasHandlers) {
                this._creaturePreSpawnEventHasHandlers = hasHandlers;
                if (hasHandlers) {
                    CreaturePreSpawnHandler.INSTANCE.onEventHasHandlers();
                }
            }
        }
    }

    private static class ObjectCacheCleanupTask
    extends Task {
        public ObjectCacheCleanupTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            ObjectCache.clearDefaultCaches();
        }
    }

    private static class AdvancementProgressEventHandlerDetectorTask
    extends Task {
        private boolean _advancementProgressEventHasHandlers = CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList());

        public AdvancementProgressEventHandlerDetectorTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            boolean hasHandlers = CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList());
            if (hasHandlers != this._advancementProgressEventHasHandlers) {
                this._advancementProgressEventHasHandlers = hasHandlers;
                if (hasHandlers) {
                    CommonPlugin.initAdvancementProgressHandler();
                }
            }
        }
    }

    private static class EntityRemoveQueueSyncTask
    extends Task {
        public EntityRemoveQueueSyncTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            if (CommonPlugin.hasInstance()) {
                for (CommonPlayerMeta meta : CommonPlugin.getInstance().playerMetadata.values()) {
                    meta.syncRemoveQueue();
                }
            }
        }
    }
}

