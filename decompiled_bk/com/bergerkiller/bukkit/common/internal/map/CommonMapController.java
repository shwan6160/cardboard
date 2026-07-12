/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.SetMultimap
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCreativeEvent
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerToggleFlightEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonMapReloadFile;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.internal.map.CreativeDraggedMapItem;
import com.bergerkiller.bukkit.common.internal.map.ImageFrameIdZeroApplier;
import com.bergerkiller.bukkit.common.internal.map.InteractiveBoardMapIDFilter;
import com.bergerkiller.bukkit.common.internal.map.ItemFrameCluster;
import com.bergerkiller.bukkit.common.internal.map.ItemFrameClusterKey;
import com.bergerkiller.bukkit.common.internal.map.ItemFrameUpdateList;
import com.bergerkiller.bukkit.common.internal.map.LookAtSearchResult;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayCreativeDraggedMapItemCleaner;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayFramedMapUpdater;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayHeldMapUpdater;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayInputUpdater;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayItemChangeListener;
import com.bergerkiller.bukkit.common.internal.map.MapDisplayItemMapIdUpdater;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMapItemDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ItemFrameHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.OutputTypeMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class CommonMapController
implements PacketListener,
Listener {
    private boolean isEnabled = false;
    private boolean isFrameDisplaysEnabled = true;
    private boolean isFrameTilingSupported = true;
    protected final Map<ItemFrameClusterKey, Set<ItemFrameHandle>> itemFrameEntities = new HashMap<ItemFrameClusterKey, Set<ItemFrameHandle>>();
    private final IntHashMap<MapUUID> mapUUIDById = new IntHashMap();
    private final HashMap<MapUUID, Integer> mapIdByUUID = new HashMap();
    private final List<StaticMapIdFilter> mapIdFilters = new ArrayList<StaticMapIdFilter>();
    protected final HashMap<UUID, MapDisplayInfo> maps = new HashMap();
    protected final ImplicitlySharedSet<MapDisplayInfo> mapsValues = new ImplicitlySharedSet();
    protected final HashMap<UUID, CreativeDraggedMapItem> creativeDraggedMapItems = new HashMap();
    private final OutputTypeMap<MapDisplay> displays = new OutputTypeMap();
    protected final HashMap<Player, MapPlayerInput> playerInputs = new HashMap();
    protected final Map<Integer, ItemFrameInfo> itemFrames = new HashMap<Integer, ItemFrameInfo>();
    public final FastTrackedUpdateSet<ItemFrameInfo> itemFramesThatNeedItemRefresh = new FastTrackedUpdateSet();
    public final FastTrackedUpdateSet<MapDisplayInfo> mapsWithItemFrameViewerChanges = new FastTrackedUpdateSet();
    public final FastTrackedUpdateSet<MapDisplayInfo> mapsWithItemFrameResolutionChanges = new FastTrackedUpdateSet();
    protected final ItemFrameUpdateList itemFrameUpdateList = new ItemFrameUpdateList();
    private final Set<Integer> itemFrameMetaMisses = new HashSet<Integer>();
    private final HashMap<World, Map<IntVector2, Set<IntVector2>>> itemFrameClusterDependencies = new HashMap();
    private SetMultimap<UUID, MapUUID> dirtyMapUUIDSet = HashMultimap.create((int)5, (int)100);
    private SetMultimap<UUID, MapUUID> dirtyMapUUIDSetTmp = HashMultimap.create((int)5, (int)100);
    private FindNeighboursCache findNeighboursCache = null;
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_X = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Y = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Z = new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST};
    protected final Map<World, Map<IntVector3, ItemFrameCluster>> itemFrameClustersByWorld = new IdentityHashMap<World, Map<IntVector3, ItemFrameCluster>>();
    protected boolean itemFrameClustersByWorldEnabled = false;
    protected int idGenerationCounter = 0;
    public static final PacketType[] PACKET_TYPES = new PacketType[]{PacketType.OUT_MAP, PacketType.IN_STEER_VEHICLE, PacketType.OUT_WINDOW_ITEMS, PacketType.OUT_WINDOW_SET_SLOT, PacketType.OUT_ENTITY_METADATA, PacketType.IN_SET_CREATIVE_SLOT, PacketType.IN_CLIENT_TICK_END};
    public static final MapDisplayInitializeFunction MAP_DISPLAY_INIT_FUNC = SafeField.get(MapDisplay.class, "INIT_FUNCTION", MapDisplayInitializeFunction.class);
    private Vector lastClickOffset = null;

    public <T extends MapDisplay> Collection<T> getDisplays(Class<T> type) {
        return this.displays.getAll(TypeDeclaration.fromClass(type));
    }

    public OutputTypeMap<MapDisplay> getDisplays() {
        return this.displays;
    }

    public Collection<MapDisplayInfo> getMaps() {
        return this.maps.values();
    }

    public Collection<ItemFrameInfo> getItemFrames() {
        return this.itemFrames.values();
    }

    public ItemFrameInfo getItemFrame(int entityId) {
        return this.itemFrames.get(entityId);
    }

    public synchronized void updateItemFrame(int entityId) {
        ItemFrameInfo info = this.getItemFrame(entityId);
        if (info != null) {
            this.itemFrameUpdateList.prioritize(info.updateEntry);
        }
    }

    public synchronized MapPlayerInput getPlayerInput(Player player) {
        return this.playerInputs.computeIfAbsent(player, MapPlayerInput::new);
    }

    public synchronized void resendMapData(Player player) {
        UUID playerUUID = player.getUniqueId();
        this.mapsValues.cloneAndForEach(display -> {
            if (display.getViewStackByPlayerUUID(playerUUID) != null) {
                for (MapSession session : display.getSessions()) {
                    for (MapSession.Owner owner : session.onlineOwners) {
                        if (owner.player != player) continue;
                        owner.clip.markEverythingDirty();
                    }
                }
            }
        });
    }

    public synchronized MapDisplayInfo getInfo(ItemFrame itemFrame) {
        ItemFrameInfo frameInfo = this.itemFrames.get(itemFrame.getEntityId());
        if (frameInfo != null) {
            if (frameInfo.lastMapUUID == null) {
                return null;
            }
            MapDisplayInfo info = this.maps.get(frameInfo.lastMapUUID.getUUID());
            if (info == null) {
                info = new MapDisplayInfo(this, frameInfo.lastMapUUID.getUUID());
                this.maps.put(frameInfo.lastMapUUID.getUUID(), info);
                this.mapsValues.add(info);
            }
            return info;
        }
        return this.getInfo(CommonMapController.getItemFrameItem(itemFrame));
    }

    public synchronized MapDisplayInfo getInfo(ItemStack mapItem) {
        UUID uuid = CommonMapUUIDStore.getMapUUID(mapItem);
        return uuid == null ? null : this.getInfo(uuid);
    }

    public synchronized MapDisplayInfo getInfo(UUID mapUUID) {
        if (mapUUID == null) {
            return null;
        }
        return this.maps.computeIfAbsent(mapUUID, uuid -> {
            MapDisplayInfo info = new MapDisplayInfo(this, (UUID)uuid);
            this.mapsValues.add(info);
            return info;
        });
    }

    public synchronized MapDisplayInfo getInfoIfExists(UUID mapUUID) {
        return this.maps.get(mapUUID);
    }

    public synchronized void updateMapItem(CommonItemStack oldItem, CommonItemStack newItem) {
        if (oldItem.isEmpty()) {
            throw new IllegalArgumentException("oldItem is empty");
        }
        oldItem.getHandle();
        newItem.getHandle();
        boolean unchanged = this.isItemUnchanged(oldItem, newItem);
        UUID oldMapUUID = oldItem.getHandle().map(ItemStackHandle::getMapDisplayUUID).orElse(null);
        if (oldMapUUID != null) {
            for (Player player : PlayerInstancePhase.getAlivePlayers()) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); ++i) {
                    UUID mapUUID = CommonMapUUIDStore.getMapUUID(inv.getItem(i));
                    if (!oldMapUUID.equals(mapUUID)) continue;
                    if (unchanged) {
                        PlayerUtil.setItemSilently(player, i, newItem.toBukkit());
                        continue;
                    }
                    inv.setItem(i, newItem.toBukkit());
                }
            }
            for (ItemFrameInfo itemFrameInfo : CommonPlugin.getInstance().getMapController().getItemFrames()) {
                if (itemFrameInfo.lastMapUUID == null || !oldMapUUID.equals(itemFrameInfo.lastMapUUID.getUUID())) continue;
                if (unchanged) {
                    DataWatcher data = EntityHandle.fromBukkit((Entity)itemFrameInfo.itemFrame).getDataWatcher();
                    DataWatcher.Item<ItemStack> dataItem = data.getItem(ItemFrameHandle.DATA_ITEM);
                    dataItem.setValue(newItem.toBukkit(), dataItem.isChanged());
                    continue;
                }
                itemFrameInfo.itemFrameHandle.setItem(newItem.toBukkit());
                this.itemFrameUpdateList.prioritize(itemFrameInfo.updateEntry);
            }
            MapDisplayInfo info = this.maps.get(oldMapUUID);
            if (info != null) {
                for (MapSession session : info.getSessions()) {
                    session.display.setMapItemSilently(newItem.toBukkit());
                }
            }
        }
    }

    private boolean isItemUnchanged(CommonItemStack item1, CommonItemStack item2) {
        return CommonMapController.trimExtraData(item1).equals(CommonMapController.trimExtraData(item2));
    }

    public void onEnable(CommonPlugin plugin, List<Task> startedTasks) {
        this.isFrameTilingSupported = plugin.isFrameTilingSupported();
        this.isFrameDisplaysEnabled = plugin.isFrameDisplaysEnabled();
        plugin.register(this);
        plugin.register(this, PACKET_TYPES);
        plugin.register(new MapDisplayItemChangeListener(this));
        startedTasks.add(new MapDisplayHeldMapUpdater(plugin, this).start(1L, 1L));
        startedTasks.add(new MapDisplayItemMapIdUpdater(plugin, this).start(1L, 1L));
        startedTasks.add(new MapDisplayInputUpdater(plugin, this).start(1L, 1L));
        startedTasks.add(new MapDisplayCreativeDraggedMapItemCleaner(plugin, this).start(100L, 60L));
        if (this.isFrameDisplaysEnabled) {
            startedTasks.add(new MapDisplayFramedMapUpdater(plugin, this).start(1L, 1L));
            startedTasks.add(new ByWorldItemFrameSetRefresher(plugin).start(2400L, 2400L));
        }
        this.mapsWithItemFrameResolutionChanges.setEnabled(this.isFrameDisplaysEnabled && this.isFrameTilingSupported);
        this.mapsWithItemFrameViewerChanges.setEnabled(this.isFrameDisplaysEnabled);
        this.itemFramesThatNeedItemRefresh.setEnabled(this.isFrameDisplaysEnabled);
        if (this.isFrameDisplaysEnabled) {
            for (World world : Bukkit.getWorlds()) {
                for (ItemFrameHandle itemFrame : this.initItemFrameSetOfWorld(world)) {
                    this.onAddItemFrame(itemFrame);
                }
            }
        }
        if (CommonUtil.getServerTicks() > 0) {
            this.getItemFrames().forEach(info -> {
                if (CommonMapUUIDStore.isMap(CommonMapController.getItemFrameItem(info.itemFrame))) {
                    info.sentMapInfoToPlayers = true;
                }
            });
        }
        CommonMapReloadFile.load(plugin, reloadFile -> {
            for (Integer staticId : reloadFile.staticReservedIds) {
                this.storeStaticMapId(staticId);
            }
            for (CommonMapReloadFile.DynamicMappedId dynamicMapId : reloadFile.dynamicMappedIds) {
                if (this.mapUUIDById.contains(dynamicMapId.id) || this.mapIdByUUID.containsKey(dynamicMapId.uuid)) continue;
                this.mapIdByUUID.put(dynamicMapId.uuid, dynamicMapId.id);
                this.mapUUIDById.put(dynamicMapId.id, dynamicMapId.uuid);
            }
            for (CommonMapReloadFile.ItemFrameDisplayUUID displayUUID : reloadFile.itemFrameDisplayUUIDs) {
                ItemFrameInfo itemFrame = this.itemFrames.get(displayUUID.entityId);
                if (itemFrame == null) continue;
                itemFrame.preReloadMapUUID = displayUUID.uuid;
            }
        });
        this.isEnabled = true;
    }

    public void onDisable(CommonPlugin plugin) {
        if (this.isEnabled) {
            this.isEnabled = false;
            CommonMapReloadFile.save(plugin, reloadFile -> {
                for (Map.Entry<MapUUID, Integer> entry : this.mapIdByUUID.entrySet()) {
                    MapUUID mapUUID = entry.getKey();
                    if (mapUUID.isStaticUUID()) {
                        reloadFile.staticReservedIds.add(entry.getValue());
                        continue;
                    }
                    reloadFile.addDynamicMapId(mapUUID, entry.getValue());
                }
                for (Map.Entry<Object, Object> entry : this.itemFrames.entrySet()) {
                    ItemFrameInfo info = (ItemFrameInfo)entry.getValue();
                    if (info.lastMapUUID == null) continue;
                    reloadFile.addItemFrameDisplayUUID((Integer)entry.getKey(), info.lastMapUUID);
                }
            });
            this.mapsValues.cloneAndForEach(map -> {
                for (MapSession session : new ArrayList<MapSession>(map.getSessions())) {
                    session.display.setRunning(false);
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
        if (enabled) {
            if (pluginName.equals("InteractiveBoard") && CommonUtil.getClass("com.interactiveboard.utility.MapChecker", false) != null) {
                try {
                    this.registerMapFilter(plugin, new InteractiveBoardMapIDFilter(plugin));
                }
                catch (Throwable t) {
                    Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to add InteractiveBoard support", t);
                }
            }
        } else {
            MapDisplay.stopDisplaysForPlugin(plugin);
            CommonMapController commonMapController = this;
            synchronized (commonMapController) {
                Iterator<StaticMapIdFilter> iter = this.mapIdFilters.iterator();
                while (iter.hasNext()) {
                    if (plugin != iter.next().owner) continue;
                    iter.remove();
                }
            }
        }
    }

    public synchronized void registerMapFilter(Plugin plugin, IntPredicate filter) {
        this.mapIdFilters.add(new StaticMapIdFilter(plugin, filter));
    }

    private synchronized boolean isMapIdFiltered(int mapId) {
        for (StaticMapIdFilter filter : this.mapIdFilters) {
            if (!filter.filter.test(mapId)) continue;
            return true;
        }
        return false;
    }

    public CommonItemStack handleItemSync(CommonItemStack item, int tileX, int tileY) {
        if (!item.isFilledMap()) {
            return null;
        }
        UUID mapUUID = item.getCustomData().getUUID("mapDisplay");
        if (mapUUID != null) {
            item = CommonMapController.trimExtraData(item);
            int id = this.getMapId(new MapUUID(mapUUID, tileX, tileY));
            item.setFilledMapId(id);
            return item;
        }
        int mapId = item.getFilledMapId();
        if (mapId != -1) {
            this.storeStaticMapId(mapId);
        }
        return null;
    }

    public synchronized int getMapId(MapUUID mapUUID) {
        Integer storedMapId = this.mapIdByUUID.get(mapUUID);
        if (storedMapId != null) {
            return storedMapId;
        }
        int mapId = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
        if (mapId != -1) {
            this.storeStaticMapId(mapId);
            return mapId;
        }
        return this.storeDynamicMapId(mapUUID);
    }

    private synchronized void storeStaticMapId(int mapId) {
        if (this.storeDynamicMapId(this.mapUUIDById.get(mapId)) != mapId) {
            MapUUID mapUUID = new MapUUID(CommonMapUUIDStore.getStaticMapUUID(mapId), 0, 0);
            this.mapUUIDById.put(mapId, mapUUID);
            this.mapIdByUUID.put(mapUUID, mapId);
        }
    }

    private synchronized int storeDynamicMapId(MapUUID mapUUID) {
        if (mapUUID == null) {
            return -1;
        }
        int staticMapid = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
        if (staticMapid != -1) {
            return staticMapid;
        }
        ImageFrameIdZeroApplier.apply();
        ++this.idGenerationCounter;
        int MAX_IDS = CommonCapabilities.MAP_ID_IN_NBT ? Integer.MAX_VALUE : Short.MAX_VALUE;
        for (int mapidValue = 0; mapidValue < MAX_IDS; ++mapidValue) {
            if (this.mapUUIDById.contains(mapidValue)) continue;
            if (this.isMapIdFiltered(mapidValue)) {
                this.storeStaticMapId(mapidValue);
                continue;
            }
            boolean idChanged = this.mapIdByUUID.containsKey(mapUUID);
            this.mapUUIDById.put(mapidValue, mapUUID);
            this.mapIdByUUID.put(mapUUID, mapidValue);
            if (idChanged) {
                this.dirtyMapUUIDSet.get((Object)mapUUID.getUUID()).add(mapUUID);
            }
            return mapidValue;
        }
        return -1;
    }

    @Override
    public synchronized void onPacketSend(PacketSendEvent event) {
        ItemStack oldItem;
        CommonItemStack newItem;
        if (event.getType() == PacketType.OUT_MAP) {
            int itemid = ClientboundMapItemDataPacketHandle.createHandle(event.getPacket().getHandle()).getMapId();
            this.storeStaticMapId(itemid);
        }
        if (event.getType() == PacketType.OUT_WINDOW_ITEMS) {
            List<ItemStack> items = event.getPacket().read(PacketType.OUT_WINDOW_ITEMS.items);
            ListIterator<ItemStack> iter = items.listIterator();
            while (iter.hasNext()) {
                CommonItemStack newItem2 = this.handleItemSync(CommonItemStack.of(iter.next()), 0, 0);
                if (newItem2 == null) continue;
                iter.set(newItem2.toBukkit());
            }
        }
        if (event.getType() == PacketType.OUT_WINDOW_SET_SLOT && (newItem = this.handleItemSync(CommonItemStack.of(oldItem = event.getPacket().read(PacketType.OUT_WINDOW_SET_SLOT.item)), 0, 0)) != null) {
            event.getPacket().write(PacketType.OUT_WINDOW_SET_SLOT.item, newItem.toBukkit());
        }
        if (this.isFrameDisplaysEnabled && event.getType() == PacketType.OUT_ENTITY_METADATA) {
            int entityId = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.entityId);
            ItemFrameInfo frameInfo = this.itemFrames.get(entityId);
            if (frameInfo == null) {
                if (CommonMapController.hasMapItemInMetadata(event.getPacket())) {
                    this.itemFrameMetaMisses.add(entityId);
                }
                return;
            }
            if (frameInfo.lastFrameItemUpdateNeeded || frameInfo.requiresFurtherLoading || frameInfo.lastMapUUID == null) {
                if (CommonMapController.hasMapItemInMetadata(event.getPacket())) {
                    frameInfo.sentMapInfoToPlayers = true;
                }
                return;
            }
            int staticMapId = CommonMapUUIDStore.getStaticMapId(frameInfo.lastMapUUID.getUUID());
            if (staticMapId != -1) {
                frameInfo.sentMapInfoToPlayers = true;
                this.storeStaticMapId(staticMapId);
                return;
            }
            int newMapId = this.getMapId(frameInfo.lastMapUUID);
            List<DataWatcher.PackedItem<Object>> items = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
            if (items != null) {
                ListIterator<DataWatcher.PackedItem<Object>> itemsIter = items.listIterator();
                while (itemsIter.hasNext()) {
                    int oldMapId;
                    DataWatcher.PackedItem<ItemStack> item;
                    DataWatcher.PackedItem<Object> itemRaw = itemsIter.next();
                    if (itemRaw == null || (item = itemRaw.translate(ItemFrameHandle.DATA_ITEM)) == null) continue;
                    ItemStack metaItem = item.value();
                    if (metaItem == null || (oldMapId = CommonMapUUIDStore.getItemMapId(metaItem)) == -1) break;
                    frameInfo.sentMapInfoToPlayers = true;
                    if (oldMapId == newMapId) break;
                    ItemStack newMapItem = ItemUtil.cloneItem(metaItem);
                    CommonMapUUIDStore.setItemMapId(newMapItem, newMapId);
                    item = item.cloneWithValue(newMapItem);
                    itemsIter.set(item);
                    break;
                }
            }
        }
    }

    private static boolean hasMapItemInMetadata(CommonPacket entityMetadataPacket) {
        List<DataWatcher.PackedItem<Object>> items = entityMetadataPacket.read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
        if (items != null) {
            for (DataWatcher.PackedItem<Object> dw_item : items) {
                DataWatcher.PackedItem<ItemStack> item;
                if (dw_item == null || (item = dw_item.translate(ItemFrameHandle.DATA_ITEM)) == null) continue;
                return CommonMapUUIDStore.isMap(item.value());
            }
        }
        return false;
    }

    @Override
    public synchronized void onPacketReceive(PacketReceiveEvent event) {
        ServerboundSetCreativeModeSlotPacketHandle packet;
        CommonItemStack item;
        UUID mapUUID;
        Player p;
        MapPlayerInput input;
        if (event.getType() == PacketType.IN_STEER_VEHICLE && (input = this.playerInputs.get(p = event.getPlayer())) != null) {
            ServerboundPlayerInputPacketHandle packet2 = ServerboundPlayerInputPacketHandle.createHandle(event.getPacket().getHandle());
            int dx = (int)(-Math.signum(packet2.getSideways()));
            int dy = (int)(-Math.signum(packet2.getForwards()));
            int dz = 0;
            if (packet2.isUnmount()) {
                --dz;
            }
            if (packet2.isJump()) {
                ++dz;
            }
            event.setCancelled(input.receiveInput(dx, dy, dz));
        }
        if (event.getType() == PacketType.IN_CLIENT_TICK_END && (input = this.playerInputs.get(p = event.getPlayer())) != null) {
            input.keepAliveInput();
        }
        if (event.getType() == PacketType.IN_SET_CREATIVE_SLOT && (mapUUID = CommonMapUUIDStore.getMapUUID(item = CommonItemStack.of((packet = ServerboundSetCreativeModeSlotPacketHandle.createHandle(event.getPacket().getHandle())).getItem()))) != null && CommonMapUUIDStore.getStaticMapId(mapUUID) == -1) {
            CommonItemStack originalMapItem = null;
            CreativeDraggedMapItem cachedItem = this.creativeDraggedMapItems.get(mapUUID);
            if (cachedItem != null) {
                cachedItem.life = 12000;
                originalMapItem = cachedItem.item;
            } else {
                for (ItemStack oldBukkitItem : event.getPlayer().getInventory()) {
                    CommonItemStack oldItem = CommonItemStack.of(oldBukkitItem);
                    if (!mapUUID.equals(CommonMapUUIDStore.getMapUUID(oldItem))) continue;
                    originalMapItem = oldItem.clone();
                    break;
                }
            }
            CommonItemStack newItem = item.clone();
            if (originalMapItem != null) {
                newItem.setCustomData(originalMapItem.getCustomData());
                newItem.setFilledMapId(originalMapItem.getFilledMapId());
            } else {
                newItem.setFilledMapId(0);
            }
            if (!item.equals(newItem)) {
                event.setPacket(ServerboundSetCreativeModeSlotPacketHandle.createNew(packet.getSlotIndex(), newItem.toBukkit()));
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    protected synchronized void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.mapsValues.cloneAndForEach(map -> {
            for (MapSession session : map.getSessions()) {
                session.updatePlayerOnline(player);
            }
        });
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player;
        boolean is_place;
        boolean bl = is_place = event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ONE;
        if (is_place && event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getWhoClicked() instanceof Player && CommonItemStack.of(event.getCursor()).isMapDisplay()) {
            player = (Player)event.getWhoClicked();
            int slot = event.getSlot();
            int rawSlot = event.getRawSlot();
            CommonUtil.nextTick(() -> {
                if (!player.isValid()) {
                    return;
                }
                ItemStack item = player.getInventory().getItem(slot);
                if (CommonItemStack.of(item).isMapDisplay()) {
                    PacketUtil.sendPacket(player, ClientboundContainerSetSlotPacketHandle.createNew(ServerPlayerHandle.fromBukkit(player).getCurrentWindowId(), rawSlot, item));
                }
            });
        }
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getSlotType() != InventoryType.SlotType.QUICKBAR && event.getWhoClicked() instanceof Player && CommonItemStack.of(event.getCurrentItem()).isMapDisplay()) {
            player = (Player)event.getWhoClicked();
            CommonUtil.nextTick(() -> {
                if (!player.isValid()) {
                    return;
                }
                player.updateInventory();
            });
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected synchronized void onPlayerQuit(PlayerQuitEvent event) {
        MapPlayerInput input = this.playerInputs.remove(event.getPlayer());
        if (input != null) {
            input.onDisconnected();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPlayerRespawn(PlayerRespawnEvent event) {
        this.resendMapData(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.resendMapData(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected synchronized void onEntityAdded(EntityAddEvent event) {
        if (this.isFrameDisplaysEnabled && event.getEntity() instanceof ItemFrame) {
            ItemFrameHandle frameHandle = ItemFrameHandle.createHandle(HandleConversion.toEntityHandle(event.getEntity()));
            this.getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).add(frameHandle);
            this.onAddItemFrame(frameHandle);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected synchronized void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame)event.getEntity();
            ItemFrameHandle frameHandle = ItemFrameHandle.fromBukkit(frame);
            this.getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).remove(frameHandle);
            ItemFrameInfo info = this.itemFrames.get(frame.getEntityId());
            if (info != null) {
                info.signalEntityRemoved();
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected synchronized void onChunkEntitiesLoaded(ChunkLoadEntitiesEvent event) {
        this.onChunkEntitiesLoaded(event.getChunk());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected synchronized void onWorldLoad(WorldLoadEvent event) {
        if (this.isFrameDisplaysEnabled) {
            for (ItemFrameHandle frame : this.initItemFrameSetOfWorld(event.getWorld())) {
                this.onAddItemFrame(frame);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected void onWorldUnload(WorldUnloadEvent event) {
        this.deinitItemFrameSetOfWorld(event.getWorld());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    protected synchronized void onInventoryCreativeSlot(InventoryCreativeEvent event) {
        CommonItemStack item;
        UUID mapUUID;
        if (event.getResult() != Event.Result.DENY && (mapUUID = CommonMapUUIDStore.getMapUUID(item = CommonItemStack.of(event.getCurrentItem()))) != null) {
            this.creativeDraggedMapItems.put(mapUUID, new CreativeDraggedMapItem(item.clone()));
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    protected void onFlightToggled(PlayerToggleFlightEvent event) {
        for (MapDisplayInfo info : this.mapsValues) {
            MapDisplay display = info.getViewing(event.getPlayer());
            if (display == null || !display.isControlling(event.getPlayer())) continue;
            event.setCancelled(true);
            break;
        }
    }

    private void onAddItemFrame(ItemFrameHandle frame) {
        int entityId = frame.getId();
        ItemFrameInfo frameInfo = this.itemFrames.get(entityId);
        if (frameInfo != null) {
            frameInfo.removed = false;
            if (frameInfo.sentMapInfoToPlayers) {
                frameInfo.needsItemRefresh.set(true);
            }
            return;
        }
        frameInfo = new ItemFrameInfo(this, frame);
        this.itemFrames.put(entityId, frameInfo);
        this.itemFrameUpdateList.add(frameInfo.updateEntry);
        if (this.itemFrameMetaMisses.remove(entityId)) {
            frameInfo.needsItemRefresh.set(true);
            frameInfo.sentMapInfoToPlayers = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onChunkEntitiesLoaded(Chunk chunk) {
        Set<IntVector2> dependingChunks;
        World world = chunk.getWorld();
        Map<IntVector2, Set<IntVector2>> dependencies = this.itemFrameClusterDependencies.get(world);
        if (dependencies == null || (dependingChunks = dependencies.remove(new IntVector2(chunk))) == null) {
            return;
        }
        boolean wasClustersByWorldCacheEnabled = this.itemFrameClustersByWorldEnabled;
        try {
            this.itemFrameClustersByWorldEnabled = true;
            for (IntVector2 depending : dependingChunks) {
                Chunk dependingChunk = WorldUtil.getChunk(world, depending.x, depending.z);
                if (dependingChunk == null || !WorldUtil.isChunkEntitiesLoaded(dependingChunk)) continue;
                for (Entity entity : ChunkUtil.getEntities(dependingChunk)) {
                    ItemFrameInfo frameInfo;
                    if (!(entity instanceof ItemFrame) || (frameInfo = this.itemFrames.get(entity.getEntityId())) == null) continue;
                    frameInfo.onChunkDependencyLoaded();
                }
            }
        }
        finally {
            this.itemFrameClustersByWorldEnabled = wasClustersByWorldCacheEnabled;
            if (!wasClustersByWorldCacheEnabled) {
                this.itemFrameClustersByWorld.clear();
            }
        }
    }

    public synchronized boolean checkClusterChunkDependency(World world, ItemFrameCluster.ChunkDependency dependency) {
        if (!this.isFrameTilingSupported) {
            return true;
        }
        if (WorldUtil.isChunkEntitiesLoaded(world, dependency.neighbour.x, dependency.neighbour.z)) {
            return true;
        }
        Map dependencies = this.itemFrameClusterDependencies.computeIfAbsent(world, unused -> new HashMap());
        Set dependingChunks = dependencies.computeIfAbsent(dependency.neighbour, unused -> new HashSet());
        dependingChunks.add(dependency.self);
        return false;
    }

    public synchronized void fillItemFrames(ItemFrame startItemFrame, ItemStack item) {
        if (!this.isFrameDisplaysEnabled) {
            throw new UnsupportedOperationException("Item frame map displays are disabled in BKCommonLib's configuration");
        }
        if (!this.isFrameTilingSupported) {
            throw new UnsupportedOperationException("Item frame map display tiling is disabled in BKCommonLib's configuration");
        }
        if (startItemFrame.isDead()) {
            throw new IllegalArgumentException("Input item frame was removed (dead)");
        }
        ItemFrameInfo info = this.getItemFrame(startItemFrame.getEntityId());
        if (info == null) {
            throw new IllegalStateException("Item frame had no metadata information for some reason");
        }
        ItemFrameCluster cluster = this.findCluster(info.itemFrameHandle, info.coordinates, true);
        for (ItemFrameInfo frame : this.findClusterItemFrames(cluster)) {
            frame.itemFrameHandle.setItem(item);
            frame.needsItemRefresh.set(true);
        }
    }

    protected SetMultimap<UUID, MapUUID> swapDirtyMapUUIDs() {
        SetMultimap<UUID, MapUUID> dirtyMaps = this.dirtyMapUUIDSet;
        this.dirtyMapUUIDSet = this.dirtyMapUUIDSetTmp;
        this.dirtyMapUUIDSetTmp = dirtyMaps;
        return dirtyMaps;
    }

    private LookAtSearchResult findLookingAt(Player player, ItemFrame itemFrame) {
        Location eye = player.getEyeLocation();
        return this.findLookingAt(player, itemFrame, eye.toVector(), eye.getDirection());
    }

    private LookAtSearchResult findLookingAt(Player player, ItemFrame itemFrame, Vector startPosition, Vector lookDirection) {
        MapDisplayInfo info = this.getInfo(itemFrame);
        if (info == null) {
            return null;
        }
        MapDisplayInfo.ViewStack stack = info.getViewStackByPlayerUUID(player.getUniqueId());
        if (stack == null || stack.stack.isEmpty()) {
            return null;
        }
        ItemFrameInfo frameInfo = this.itemFrames.get(itemFrame.getEntityId());
        if (frameInfo == null) {
            return null;
        }
        MapLookPosition position = frameInfo.findLookPosition(startPosition, lookDirection);
        double limit = 16.0;
        if (position == null || position.getEdgeDistance() > 0.125) {
            return null;
        }
        MapDisplay display = stack.stack.getLast();
        double new_x = position.getDoubleX();
        double new_y = position.getDoubleY();
        if (new_x < -16.0 || new_y < -16.0 || new_x > (double)display.getWidth() + 16.0 || new_y >= (double)display.getHeight() + 16.0) {
            return null;
        }
        if (new_x < 0.0 || new_y < 0.0 || new_x >= (double)display.getWidth() || new_y >= (double)display.getHeight()) {
            new_x = MathUtil.clamp(new_x, 0.0, (double)display.getWidth() - 1.0E-10);
            new_y = MathUtil.clamp(new_y, 0.0, (double)display.getHeight() - 1.0E-10);
            position = new MapLookPosition(position.getItemFrameInfo(), new_x, new_y, position.getDistance(), position.getEdgeDistance());
        }
        return new LookAtSearchResult(display, position);
    }

    private boolean dispatchClickAction(Player player, ItemFrame itemFrame, Vector startPosition, Vector lookDirection, MapAction action) {
        LookAtSearchResult lookAt = this.findLookingAt(player, itemFrame, startPosition, lookDirection);
        return lookAt != null && lookAt.click(player, action).isCancelled();
    }

    private boolean dispatchClickActionApprox(Player player, ItemFrame itemFrame, MapAction action) {
        Location eye = player.getEyeLocation();
        return this.dispatchClickAction(player, itemFrame, eye.toVector(), eye.getDirection(), action);
    }

    private boolean dispatchClickActionFromBlock(Player player, Block clickedBlock, BlockFace clickedFace, MapAction action) {
        if (!this.isFrameDisplaysEnabled) {
            return false;
        }
        Vector look = player.getEyeLocation().getDirection();
        double eps = 0.001;
        double x1 = (double)clickedBlock.getX() + 0.5 + (double)clickedFace.getModX() * 0.5;
        double y1 = (double)clickedBlock.getY() + 0.5 + (double)clickedFace.getModY() * 0.5;
        double z1 = (double)clickedBlock.getZ() + 0.5 + (double)clickedFace.getModZ() * 0.5;
        double x2 = x1;
        double y2 = y1;
        double z2 = z1;
        if (look.getX() < 0.0) {
            x2 += 1.001;
            x1 -= 0.001;
        } else {
            x2 -= 1.001;
            x1 += 0.001;
        }
        if (look.getY() < 0.0) {
            y2 += 1.001;
            y1 -= 0.001;
        } else {
            y2 -= 1.001;
            y1 += 0.001;
        }
        if (look.getZ() < 0.0) {
            z2 += 1.001;
            z1 -= 0.001;
        } else {
            z2 -= 1.001;
            z1 += 0.001;
        }
        LookAtSearchResult bestApprox = null;
        for (Entity e : WorldUtil.getEntities(clickedBlock.getWorld(), null, x1, y1, z1, x2, y2, z2)) {
            LookAtSearchResult result;
            if (!(e instanceof ItemFrame) || (result = this.findLookingAt(player, (ItemFrame)e)) == null) continue;
            if (result.lookPosition.isWithinBounds()) {
                return result.click(player, action).isCancelled();
            }
            if (bestApprox != null && !(bestApprox.lookPosition.getDistance() > result.lookPosition.getDistance())) continue;
            bestApprox = result;
        }
        return bestApprox != null && bestApprox.click(player, action).isCancelled();
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    protected void onEntityLeftClick(EntityDamageByEntityEvent event) {
        if (!(this.isFrameDisplaysEnabled && event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player)) {
            return;
        }
        if (this.dispatchClickActionApprox((Player)event.getDamager(), (ItemFrame)event.getEntity(), MapAction.LEFT_CLICK)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    protected void onEntityRightClickAt(PlayerInteractAtEntityEvent event) {
        if (!this.isFrameDisplaysEnabled || event.getRightClicked() instanceof ItemFrame) {
            this.lastClickOffset = event.getClickedPosition();
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    protected void onEntityRightClick(PlayerInteractEntityEvent event) {
        boolean cancelled;
        if (!this.isFrameDisplaysEnabled || !(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        if (event instanceof PlayerInteractAtEntityEvent) {
            this.lastClickOffset = ((PlayerInteractAtEntityEvent)event).getClickedPosition();
        }
        ItemFrame itemFrame = (ItemFrame)event.getRightClicked();
        if (this.lastClickOffset != null) {
            Location eye = event.getPlayer().getEyeLocation();
            Location pos = itemFrame.getLocation().add(this.lastClickOffset);
            Vector dir = eye.getDirection();
            this.lastClickOffset = null;
            double distance = eye.distance(pos);
            pos.subtract(dir.clone().multiply(distance));
            cancelled = this.dispatchClickAction(event.getPlayer(), itemFrame, pos.toVector(), dir, MapAction.RIGHT_CLICK);
        } else {
            cancelled = this.dispatchClickActionApprox(event.getPlayer(), itemFrame, MapAction.RIGHT_CLICK);
        }
        if (cancelled) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    protected void onBlockInteract(PlayerInteractEvent event) {
        MapAction action;
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            action = MapAction.LEFT_CLICK;
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            action = MapAction.RIGHT_CLICK;
        } else {
            return;
        }
        if (this.dispatchClickActionFromBlock(event.getPlayer(), event.getClickedBlock(), event.getBlockFace(), action)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    protected synchronized void cleanupUnusedUUIDs(Set<MapUUID> existingMapUUIDs) {
        HashSet<MapUUID> idsToRemove = new HashSet<MapUUID>(this.mapIdByUUID.keySet());
        idsToRemove.removeAll(existingMapUUIDs);
        for (MapUUID toRemove : idsToRemove) {
            Integer mapId;
            MapDisplayInfo displayInfo = this.maps.get(toRemove.getUUID());
            if (displayInfo != null) {
                if (!displayInfo.getSessions().isEmpty()) continue;
                MapDisplayInfo removed = this.maps.remove(toRemove.getUUID());
                if (removed != null) {
                    this.mapsValues.remove(removed);
                    removed.onRemoved();
                }
            }
            if ((mapId = this.mapIdByUUID.remove(toRemove)) != null) {
                this.mapUUIDById.remove(mapId);
            }
            this.dirtyMapUUIDSet.removeAll((Object)toRemove.getUUID());
        }
    }

    protected synchronized void handleMapShowEvent(MapShowEvent event) {
        Plugin plugin;
        Class<? extends MapDisplay> displayClass;
        MapDisplayInfo info = this.getInfo(event.getMapUUID());
        boolean hasDisplay = false;
        if (info != null) {
            for (MapSession session : info.getSessions()) {
                if (!session.display.isGlobal()) continue;
                session.display.addOwner(event.getPlayer());
                hasDisplay = true;
                break;
            }
        }
        MapDisplayProperties properties = MapDisplayProperties.of(event.getMapItem());
        if (!hasDisplay && !event.hasDisplay() && properties != null && (displayClass = properties.getMapDisplayClass()) != null && (plugin = properties.getPlugin()) instanceof JavaPlugin) {
            try {
                MapDisplay display = displayClass.newInstance();
                event.setDisplay((JavaPlugin)plugin, display);
            }
            catch (IllegalAccessException | InstantiationException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to initialize MapDisplay", e);
            }
        }
        CommonUtil.callEvent(event);
    }

    protected MapUUID getItemFrameMapUUID(ItemFrameHandle itemFrame) {
        if (itemFrame == null) {
            return null;
        }
        ItemFrameInfo info = this.itemFrames.get(itemFrame.getId());
        if (info == null) {
            return null;
        }
        info.updateItem();
        return info.lastMapUUID;
    }

    public List<ItemFrameInfo> findClusterItemFrames(ItemFrameCluster cluster) {
        if (!cluster.world.isLoaded()) {
            return Collections.emptyList();
        }
        ArrayList<ItemFrameInfo> result = new ArrayList<ItemFrameInfo>(cluster.coordinates.size());
        for (Entity entity : WorldUtil.getEntities(cluster.world.getLoadedWorld(), null, (double)cluster.min_coord.x + 0.01, (double)cluster.min_coord.y + 0.01, (double)cluster.min_coord.z + 0.01, (double)cluster.max_coord.x + 0.99, (double)cluster.max_coord.y + 0.99, (double)cluster.max_coord.z + 0.99)) {
            ItemFrameInfo itemFrame;
            if (!(entity instanceof ItemFrame) || (itemFrame = this.getItemFrame(entity.getEntityId())) == null || !cluster.coordinates.contains(itemFrame.coordinates)) continue;
            result.add(itemFrame);
        }
        return result;
    }

    public final synchronized ItemFrameCluster findCluster(ItemFrameHandle itemFrame, IntVector3 itemFramePosition) {
        return this.findCluster(itemFrame, itemFramePosition, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized ItemFrameCluster findCluster(ItemFrameHandle itemFrame, IntVector3 itemFramePosition, boolean includingEmpty) {
        FindNeighboursCache cache;
        Map<IntVector3, ItemFrameCluster> cachedClusters;
        UUID itemFrameMapUUID;
        Predicate<ItemFrameHandle> itemFrameFilter;
        if (includingEmpty && ItemUtil.isEmpty(itemFrame.getItem())) {
            itemFrameFilter = e -> ItemUtil.isEmpty(e.getItem());
        } else if (this.isFrameTilingSupported && (itemFrameMapUUID = itemFrame.getItemMapDisplayDynamicOnlyUUID()) != null) {
            itemFrameFilter = includingEmpty ? e -> itemFrameMapUUID.equals(e.getItemMapDisplayDynamicOnlyUUID()) || ItemUtil.isEmpty(e.getItem()) : e -> itemFrameMapUUID.equals(e.getItemMapDisplayDynamicOnlyUUID());
        } else {
            return new ItemFrameCluster(OfflineWorld.of(itemFrame.getBukkitWorld()), itemFrame.getFacing(), Collections.singleton(itemFramePosition), 0);
        }
        World world = itemFrame.getBukkitWorld();
        if (!includingEmpty && this.itemFrameClustersByWorldEnabled) {
            ItemFrameCluster fromCache;
            cachedClusters = this.itemFrameClustersByWorld.get(world);
            if (cachedClusters == null) {
                cachedClusters = new HashMap<IntVector3, ItemFrameCluster>();
                this.itemFrameClustersByWorld.put(world, cachedClusters);
            }
            if ((fromCache = cachedClusters.get(itemFramePosition)) != null) {
                return fromCache;
            }
        } else {
            cachedClusters = null;
        }
        if ((cache = this.findNeighboursCache) != null) {
            cache.reset();
            this.findNeighboursCache = null;
        } else {
            cache = new FindNeighboursCache();
        }
        try {
            ItemFrameClusterKey key = new ItemFrameClusterKey(world, itemFrame.getFacing(), itemFramePosition);
            for (ItemFrameHandle otherFrame : this.getItemFrameEntities(key)) {
                if (otherFrame.getId() == itemFrame.getId() || !itemFrameFilter.test(otherFrame)) continue;
                cache.put(otherFrame);
            }
            BlockFace[] neighbourAxis = FaceUtil.isAlongY(key.facing) ? NEIGHBOUR_AXIS_ALONG_Y : (FaceUtil.isAlongX(key.facing) ? NEIGHBOUR_AXIS_ALONG_X : NEIGHBOUR_AXIS_ALONG_Z);
            int[] rotation_counts = new int[4];
            int n = new FindNeighboursCache.Frame((ItemFrameHandle)itemFrame).rotation;
            rotation_counts[n] = rotation_counts[n] + 1;
            HashSet<IntVector3> result = new HashSet<IntVector3>(cache.cache.size());
            result.add(itemFramePosition);
            cache.pendingList.add(itemFramePosition);
            do {
                IntVector3 pending = cache.pendingList.poll();
                for (BlockFace side : neighbourAxis) {
                    IntVector3 sidePoint = pending.add(side);
                    FindNeighboursCache.Frame frame = cache.cache.remove(sidePoint);
                    if (frame == null) continue;
                    int n2 = frame.rotation;
                    rotation_counts[n2] = rotation_counts[n2] + 1;
                    cache.pendingList.add(sidePoint);
                    result.add(sidePoint);
                }
            } while (!cache.pendingList.isEmpty());
            int rotation_idx = 0;
            for (int i = 1; i < rotation_counts.length; ++i) {
                if (rotation_counts[i] <= rotation_counts[rotation_idx]) continue;
                rotation_idx = i;
            }
            ItemFrameCluster cluster = new ItemFrameCluster(OfflineWorld.of(key.world), key.facing, result, rotation_idx * 90);
            if (cachedClusters != null) {
                for (IntVector3 position : cluster.coordinates) {
                    cachedClusters.put(position, cluster);
                }
            }
            ItemFrameCluster itemFrameCluster = cluster;
            return itemFrameCluster;
        }
        finally {
            this.findNeighboursCache = cache;
        }
    }

    private final void deinitItemFrameListForWorldsNotIn(Collection<World> worlds) {
        Iterator<ItemFrameClusterKey> iter = this.itemFrameEntities.keySet().iterator();
        while (iter.hasNext()) {
            if (worlds.contains(iter.next().world)) continue;
            iter.remove();
        }
    }

    private final synchronized void deinitItemFrameSetOfWorld(World world) {
        Iterator<ItemFrameClusterKey> iter = this.itemFrameEntities.keySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().world != world) continue;
            iter.remove();
        }
    }

    private final List<ItemFrameHandle> initItemFrameSetOfWorld(World world) {
        ArrayList<ItemFrameHandle> itemFrames = new ArrayList<ItemFrameHandle>();
        for (Object entityHandle : (Iterable)((Template.Method)ServerLevelHandle.T.getEntities.raw).invoke(HandleConversion.toWorldHandle(world))) {
            if (!ItemFrameHandle.T.isAssignableFrom(entityHandle)) continue;
            ItemFrameHandle itemFrame = ItemFrameHandle.createHandle(entityHandle);
            this.getItemFrameEntities(new ItemFrameClusterKey(itemFrame)).add(itemFrame);
            itemFrames.add(itemFrame);
        }
        return itemFrames;
    }

    private final Set<ItemFrameHandle> getItemFrameEntities(ItemFrameClusterKey key) {
        Set<ItemFrameHandle> set = this.itemFrameEntities.get(key);
        if (set == null) {
            set = new HashSet<ItemFrameHandle>();
            this.itemFrameEntities.put(key, set);
        }
        return set;
    }

    public static ItemStack getItemFrameItem(ItemFrame itemFrame) {
        return ItemFrameHandle.fromBukkit(itemFrame).getItem();
    }

    public static void setItemFrameItem(ItemFrame itemFrame, ItemStack item) {
        ItemFrameHandle.fromBukkit(itemFrame).setItem(item);
    }

    public static CommonItemStack trimExtraData(CommonItemStack item) {
        if (item.isEmpty()) {
            return CommonItemStack.empty();
        }
        CommonTagCompound oldTag = item.getCustomData();
        if (oldTag.isEmpty()) {
            if (!item.isCraftItemStack()) {
                throw new IllegalArgumentException("Input item is no CraftItemStack");
            }
            return item;
        }
        item = item.clone();
        item.setCustomData(null);
        item.updateCustomData(newTag -> {
            String[] nbt_filter;
            for (String filter : nbt_filter = new String[]{"ench", "display", "RepairCost", "AttributeModifiers", "CanDestroy", "CanPlaceOn", "Unbreakable", "mapDisplayUUIDMost", "mapDisplayUUIDLeast", "mapDisplayPlugin", "mapDisplayClass"}) {
                if (!oldTag.containsKey(filter)) continue;
                newTag.put(filter, oldTag.get(filter));
            }
        });
        return item;
    }

    private static final class FindNeighboursCache {
        public final HashMap<IntVector3, Frame> cache = new HashMap();
        public final Queue<IntVector3> pendingList = new ArrayDeque<IntVector3>();

        private FindNeighboursCache() {
        }

        public void reset() {
            this.cache.clear();
            this.pendingList.clear();
        }

        public void put(ItemFrameHandle itemFrame) {
            this.cache.put(itemFrame.getBlockPosition(), new Frame(itemFrame));
        }

        public static final class Frame {
            public final int rotation;

            public Frame(ItemFrameHandle itemFrame) {
                this.rotation = itemFrame.getRotationOrdinal() & 3;
            }
        }
    }

    public class ByWorldItemFrameSetRefresher
    extends Task {
        public ByWorldItemFrameSetRefresher(JavaPlugin plugin) {
            super(plugin);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            List worlds = Bukkit.getWorlds();
            CommonMapController commonMapController = CommonMapController.this;
            synchronized (commonMapController) {
                CommonMapController.this.deinitItemFrameListForWorldsNotIn(worlds);
                ArrayList<ItemFrameHandle> itemFramesToAdd = new ArrayList<ItemFrameHandle>();
                for (World world : worlds) {
                    for (Object entityHandle : (Iterable)((Template.Method)ServerLevelHandle.T.getEntities.raw).invoke(HandleConversion.toWorldHandle(world))) {
                        Integer id;
                        if (!ItemFrameHandle.T.isAssignableFrom(entityHandle) || CommonMapController.this.itemFrames.containsKey(id = (Integer)EntityHandle.T.getId.invoker.invoke(entityHandle))) continue;
                        itemFramesToAdd.add(ItemFrameHandle.createHandle(entityHandle));
                    }
                }
                for (ItemFrameHandle frameHandle : itemFramesToAdd) {
                    CommonMapController.this.getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).add(frameHandle);
                    CommonMapController.this.onAddItemFrame(frameHandle);
                }
            }
        }
    }

    private static final class StaticMapIdFilter {
        public final IntPredicate filter;
        public final Plugin owner;

        public StaticMapIdFilter(Plugin owner, IntPredicate filter) {
            this.owner = owner;
            this.filter = filter;
        }
    }

    @FunctionalInterface
    public static interface MapDisplayInitializeFunction {
        public void initialize(MapDisplay var1, JavaPlugin var2, ItemStack var3);
    }
}

