/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapClip;
import com.bergerkiller.bukkit.common.map.MapDisplayEvents;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapMarker;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetRoot;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MapDisplay
implements MapDisplayEvents {
    private final MapSession session = new MapSession(this);
    private int width;
    private int height;
    private final MapClip clip = new MapClip();
    private byte[] zbuffer = null;
    private byte[] livebuffer = null;
    private Layer layerStack;
    private boolean _isOnAttachedCalled = false;
    private boolean _updateWhenNotViewing = true;
    private boolean _receiveInputWhenHolding = false;
    private boolean _global = true;
    private boolean _playSoundToAllViewers = false;
    private float _masterVolume = 1.0f;
    private int updateTaskId = -1;
    private final CommonItemStack _oldItem = CommonItemStack.empty();
    private final CommonItemStack _item = CommonItemStack.empty();
    protected MapDisplayInfo info = null;
    protected JavaPlugin plugin = null;
    private final MapWidgetRoot widgets = new MapWidgetRoot(this);
    private final MapDisplayMarkers markers = new MapDisplayMarkers();
    private static final CommonMapController.MapDisplayInitializeFunction INIT_FUNCTION = MapDisplay::initialize;
    public final MapDisplayProperties properties = new MapDisplayProperties(){

        @Override
        public CommonItemStack getCommonMapItem() {
            return MapDisplay.this.getCommonMapItem();
        }

        @Override
        public String getPluginName() {
            JavaPlugin plugin = MapDisplay.this.getPlugin();
            return plugin != null ? plugin.getName() : super.getPluginName();
        }

        @Override
        public Plugin getPlugin() {
            JavaPlugin plugin = MapDisplay.this.getPlugin();
            return plugin != null ? plugin : super.getPlugin();
        }

        @Override
        public String getMapDisplayClassName() {
            return MapDisplay.this.getClass().getName();
        }

        @Override
        public Class<? extends MapDisplay> getMapDisplayClass() {
            return MapDisplay.this.getClass();
        }

        @Override
        public UUID getUniqueId() {
            MapDisplayInfo info = MapDisplay.this.getMapInfo();
            return info != null ? info.getUniqueId() : super.getUniqueId();
        }
    };

    private void initialize(JavaPlugin plugin, ItemStack mapItem) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo == null) {
            throw new IllegalArgumentException("Map Item is not of a valid map");
        }
        if (this.plugin != null) {
            if (this.plugin != plugin) {
                throw new IllegalArgumentException("This Map Display was already initialized for another plugin");
            }
            if (this.info != mapInfo) {
                throw new IllegalArgumentException("This Map Display was already initialized for a different map");
            }
        } else {
            this.plugin = plugin;
            this.info = mapInfo;
            this._item.setToCopyOf(mapItem);
            this._oldItem.setToCopyOf(this._item);
        }
        this.setRunning(true);
    }

    private void preRunInitialize() {
        this.info.loadTiles(this.session, true);
        this.markers.clear();
        this.width = this.info.getDesiredWidth();
        this.height = this.info.getDesiredHeight();
        this.clearLayers();
        this.widgets.setBounds(0, 0, this.width, this.height);
    }

    public List<MapDisplayTile> getDisplayTiles() {
        return this.session.tiles;
    }

    public boolean containsTile(int tileX, int tileY) {
        for (MapDisplayTile tile : this.session.tiles) {
            if (tile.tileX != tileX || tile.tileY != tileY) continue;
            return true;
        }
        return false;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final byte[] getLiveBuffer() {
        return this.livebuffer;
    }

    public boolean isInitialized() {
        return this.plugin != null;
    }

    public void setSessionMode(MapSessionMode mode) {
        this.session.mode = mode;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void addOwner(Player owner) {
        this.session.addOwner(owner);
        this.setRunning(true);
    }

    public void removeOwner(Player owner) {
        this.session.removeOwner(owner);
    }

    public MapDisplayInfo getMapInfo() {
        return this.info;
    }

    public boolean isRunning() {
        return this.updateTaskId != -1;
    }

    public boolean isGlobal() {
        return this._global;
    }

    public void setGlobal(boolean global) {
        this._global = global;
    }

    public void invalidate() {
        this.clip.markEverythingDirty();
    }

    public final void update() {
        if (this.info == null) {
            return;
        }
        if (!this._isOnAttachedCalled) {
            this._isOnAttachedCalled = true;
            this.callOnAttached();
        }
        if (!this.session.update()) {
            this.setRunning(false);
            return;
        }
        if (this.session.refreshResolutionRequested && this.session.hasViewers) {
            this.session.refreshResolutionRequested = false;
            if (this.getWidth() == this.info.getDesiredWidth() && this.getHeight() == this.info.getDesiredHeight()) {
                this.info.loadTiles(this.session, false);
            } else {
                this.handleStopRunning();
                this.handleStartRunning();
                this._isOnAttachedCalled = true;
                this.callOnAttached();
            }
        }
        if (this._receiveInputWhenHolding) {
            for (MapSession.Owner owner : this.session.onlineOwners) {
                owner.interceptInput = owner.controlling;
            }
        }
        if (this._updateWhenNotViewing || this.session.hasViewers) {
            this.onTick();
            this.widgets.performTickUpdates();
        }
        this.refreshMapItem();
        if (this.clip.isDirty()) {
            this.flushCanvasChanges();
        } else {
            if (this.session.hasNewViewers) {
                for (MapSession.Owner owner : this.session.onlineOwners) {
                    if (!owner.isNewViewer()) continue;
                    owner.updateMap(this.getUpdates(owner.clip, owner.player));
                }
            }
            for (MapSession.Owner owner : this.session.onlineOwners) {
                if (!owner.viewing || !owner.clip.isDirty()) continue;
                owner.updateMap(this.getUpdates(owner.clip, owner.player));
            }
        }
        this.markers.synchronize(this.session);
    }

    public final void flushCanvasChanges() {
        if (this.clip.isDirty()) {
            List<MapDisplayTile.Update> syncUpdates = null;
            for (MapSession.Owner owner : this.session.onlineOwners) {
                if (!owner.viewing) {
                    owner.clip.markDirty(this.clip);
                    continue;
                }
                if (owner.clip.isDirty()) {
                    owner.clip.markDirty(this.clip);
                    owner.updateMap(this.getUpdates(owner.clip, owner.player));
                    continue;
                }
                if (syncUpdates == null) {
                    syncUpdates = this.getUpdates(this.clip, owner.player);
                } else {
                    for (int i = 0; i < syncUpdates.size(); ++i) {
                        syncUpdates.set(i, syncUpdates.get(i).clone());
                    }
                }
                owner.updateMap(syncUpdates);
            }
            this.clip.clearDirty();
        }
    }

    private final void refreshMapItem() {
        if (!this._item.equals(this._oldItem)) {
            CommonPlugin.getInstance().getMapController().updateMapItem(this._oldItem, this._item);
        }
    }

    private final List<MapDisplayTile.Update> getUpdates(MapClip clip, Player viewer) {
        ArrayList<MapDisplayTile.Update> updates = new ArrayList<MapDisplayTile.Update>(this.session.tiles.size());
        for (MapDisplayTile tile : this.session.tiles) {
            tile.addTileUpdate(this, viewer, updates, clip);
        }
        return updates;
    }

    public final MapTexture loadTexture(String filename) {
        return MapTexture.loadPluginResource(this.plugin, filename);
    }

    public ItemStack getMapItem() {
        return this._item.toBukkit();
    }

    public CommonItemStack getCommonMapItem() {
        return this._item;
    }

    public void setMapItemSilently(ItemStack item) {
        this._item.setTo(item);
        this._oldItem.setToCopyOf(this._item);
        this.onMapItemChanged();
        MapDisplay.onMapChangedWidgets(this.widgets);
    }

    private static void onMapChangedWidgets(MapWidget widget) {
        widget.onMapItemChanged();
        for (MapWidget child : widget.getWidgets()) {
            MapDisplay.onMapChangedWidgets(child);
        }
    }

    public void setMapItem(ItemStack item) {
        this._item.setTo(item);
    }

    public MapPlayerInput getInput(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            return owner.input;
        }
        return null;
    }

    public List<Player> getOwners() {
        ArrayList<Player> owners = new ArrayList<Player>(this.session.onlineOwners.size());
        for (MapSession.Owner owner : this.session.onlineOwners) {
            owners.add(owner.player);
        }
        return owners;
    }

    public List<Player> getViewers() {
        ArrayList<Player> viewers = new ArrayList<Player>(this.session.onlineOwners.size());
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (!owner.viewing) continue;
            viewers.add(owner.player);
        }
        return viewers;
    }

    public boolean isViewing(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            return owner.viewing;
        }
        return false;
    }

    public boolean isHolding(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            return owner.holding;
        }
        return false;
    }

    public boolean isControlling(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            return owner.controlling;
        }
        return false;
    }

    public boolean hasViewers() {
        return this.session.hasViewers;
    }

    public void setUpdateWithoutViewers(boolean updateWhenNotViewing) {
        this._updateWhenNotViewing = updateWhenNotViewing;
    }

    public void setReceiveInputWhenHolding(boolean inputWhenHolding) {
        if (this._receiveInputWhenHolding != inputWhenHolding) {
            this._receiveInputWhenHolding = inputWhenHolding;
            if (!inputWhenHolding) {
                for (MapSession.Owner owner : this.session.onlineOwners) {
                    owner.interceptInput = false;
                    owner.input.handleDisplayUpdate(this, false);
                }
            }
        }
    }

    public boolean isReceivingInput(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            return owner.interceptInput;
        }
        throw new IllegalArgumentException("Player is not an owner of this display");
    }

    public void setReceiveInput(Player player, boolean interceptInput) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player != player) continue;
            owner.interceptInput = interceptInput;
            return;
        }
        throw new IllegalArgumentException("Player is not an owner of this display");
    }

    public MapLookPosition findLookPosition(Player viewer) {
        return this.findLookPosition(viewer.getEyeLocation());
    }

    public MapLookPosition findLookPosition(Location eyeLocation) {
        return this.findLookPosition(eyeLocation.getWorld(), eyeLocation.toVector(), eyeLocation.getDirection());
    }

    public MapLookPosition findLookPosition(World world, Vector startPosition, Vector lookDirection) {
        for (ItemFrameInfo itemFrame : this.info.getItemFrames()) {
            MapLookPosition position;
            if (itemFrame.getWorld() != world || (position = itemFrame.findLookPosition(startPosition, lookDirection)) == null || !position.isWithinBounds()) continue;
            return position;
        }
        return null;
    }

    public List<MapWidget> getWidgets() {
        return this.widgets.getWidgets();
    }

    public void clearWidgets() {
        this.widgets.clearWidgets();
    }

    public void addWidget(MapWidget widget) {
        this.widgets.addWidget(widget);
    }

    public boolean removeWidget(MapWidget widget) {
        return this.widgets.removeWidget(widget);
    }

    public MapWidget getRootWidget() {
        return this.widgets;
    }

    public MapWidget getActivatedWidget() {
        return this.widgets.getActivatedWidget();
    }

    public MapWidget getFocusedWidget() {
        return this.widgets.getFocusedWidget();
    }

    MapDisplayMarkers getMarkerManager() {
        return this.markers;
    }

    public Collection<MapMarker> getMarkers() {
        return this.markers.values();
    }

    public MapMarker createMarker() {
        String name;
        while (this.markers.get(name = MapDisplayMarkers.RANDOM_NAME_SOURCE.nextHex()) != null) {
        }
        return this.markers.add(new MapMarker(this.markers, name));
    }

    public MapMarker createMarker(String id) {
        return this.markers.add(new MapMarker(this.markers, id));
    }

    public MapMarker getMarker(String id) {
        return this.markers.get(id);
    }

    public MapMarker removeMarker(String id) {
        return this.markers.remove(id);
    }

    public void clearMarkers() {
        this.markers.clear();
    }

    public void setMasterVolume(float masterVolume) {
        this._masterVolume = masterVolume;
    }

    public float getMasterVolume() {
        return this._masterVolume;
    }

    public void setPlaySoundToEveryone(boolean everyone) {
        this._playSoundToAllViewers = everyone;
    }

    public void playSound(ResourceKey<SoundEffect> soundKey) {
        this.playSound(soundKey, 1.0f, 1.0f);
    }

    public void playSound(ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        for (Player viewer : this.getViewers()) {
            if (!this._playSoundToAllViewers && !this.isHolding(viewer)) continue;
            PlayerUtil.playSound(viewer, soundKey, this._masterVolume * volume, pitch);
        }
    }

    public void playSound(ResourceKey<SoundEffect> soundKey, String category, float volume, float pitch) {
        for (Player viewer : this.getViewers()) {
            if (!this._playSoundToAllViewers && !this.isHolding(viewer)) continue;
            PlayerUtil.playSound(viewer, soundKey, category, this._masterVolume * volume, pitch);
        }
    }

    public final void sendStatusChange(String name) {
        this.sendStatusChange(name, null);
    }

    public final void sendStatusChange(String name, Object argument) {
        MapStatusEvent event = new MapStatusEvent(name, argument);
        MapDisplay.sendStatusChanges(this.widgets, event);
        this.onStatusChanged(event);
    }

    private static void sendStatusChanges(MapWidget from, MapStatusEvent event) {
        from.onStatusChanged(event);
        for (MapWidget child : from.getWidgets()) {
            MapDisplay.sendStatusChanges(child, event);
        }
    }

    public void clearLayers() {
        this.zbuffer = new byte[this.width * this.height];
        this.livebuffer = new byte[this.width * this.height];
        this.layerStack = new Layer(this, this.width, this.height);
        this.clip.markEverythingDirty();
    }

    public final Layer getLayer() {
        return this.layerStack;
    }

    public final Layer getLayer(int z) {
        Layer currentLayer = this.layerStack;
        while (z < 0) {
            currentLayer = currentLayer.previous();
            ++z;
        }
        while (z > 0) {
            currentLayer = currentLayer.next();
            --z;
        }
        return currentLayer;
    }

    public final Layer getTopLayer() {
        Layer top = this.layerStack;
        while (top.next != null) {
            top = top.next;
        }
        return top;
    }

    public final Layer getBottomLayer() {
        Layer bottom = this.layerStack;
        while (bottom.previous != null) {
            bottom = bottom.previous;
        }
        return bottom;
    }

    public final void setRunning(boolean running) {
        if (running) {
            if (this.plugin != null && this.updateTaskId == -1) {
                this.updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, (Runnable)new UpdateTask(), 1L, 1L);
                CommonPlugin.getInstance().getMapController().getDisplays().add(this.getClass(), this);
                this.handleStartRunning();
            }
        } else if (this.updateTaskId != -1) {
            this.handleStopRunning();
            Bukkit.getScheduler().cancelTask(this.updateTaskId);
            this.updateTaskId = -1;
            CommonPlugin.getInstance().getMapController().getDisplays().remove(this.getClass(), this);
        }
    }

    public final void restartDisplay() {
        if (this.isRunning()) {
            this.handleStopRunning();
            this.handleStartRunning();
        }
    }

    private void handleStartRunning() {
        if (this.info != null) {
            this.preRunInitialize();
            this.info.addSession(this.session);
        }
        this.session.initOwners();
        this._isOnAttachedCalled = false;
    }

    private void handleStopRunning() {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            owner.input.handleDisplayUpdate(this, false);
        }
        if (this._isOnAttachedCalled) {
            this._isOnAttachedCalled = false;
            try {
                this.onDetached();
            }
            catch (Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, "An error occurred during MapDisplay onDetached", t);
            }
        }
        this.widgets.clearWidgets();
        this.widgets.handleDetach();
        this.refreshMapItem();
        if (this.info != null) {
            this.info.removeSession(this.session);
        }
    }

    private void callOnAttached() {
        try {
            this.onAttached();
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred during MapDisplay onAttached", t);
        }
    }

    public String toString() {
        if (this.plugin == null) {
            return "{UNREGISTERED " + this.getClass().getSimpleName() + "}";
        }
        return "{" + this.plugin.getName() + " " + this.getClass().getSimpleName() + "}";
    }

    @Override
    public void onAttached() {
    }

    @Override
    public void onDetached() {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onKey(MapKeyEvent event) {
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
    }

    @Override
    public void onKeyReleased(MapKeyEvent event) {
    }

    @Override
    public void onLeftClick(MapClickEvent event) {
    }

    @Override
    public void onRightClick(MapClickEvent event) {
    }

    @Override
    public void onMapItemChanged() {
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
    }

    @Override
    public boolean onItemDrop(Player player, ItemStack item) {
        return false;
    }

    @Override
    public void onBlockInteract(PlayerInteractEvent event) {
    }

    public static ItemStack createMapItem(Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(mapDisplayClass).getMapItem();
    }

    public static ItemStack createMapItem(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(plugin, mapDisplayClass).getMapItem();
    }

    public static CommonItemStack createMapCommonItem(Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(mapDisplayClass).getCommonMapItem();
    }

    public static CommonItemStack createMapCommonItem(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(plugin, mapDisplayClass).getCommonMapItem();
    }

    public static void updateMapItem(ItemStack oldItem, ItemStack newItem) {
        MapDisplay.updateMapItem(CommonItemStack.of(oldItem), CommonItemStack.of(newItem));
    }

    public static void updateMapItem(CommonItemStack oldItem, CommonItemStack newItem) {
        CommonPlugin.getInstance().getMapController().updateMapItem(oldItem, newItem);
    }

    public static MapDisplay getViewedDisplay(Player viewer, ItemStack item) {
        MapDisplayInfo info = CommonPlugin.getInstance().getMapController().getInfo(item);
        return info != null ? info.getViewing(viewer) : null;
    }

    public static void restartDisplays(ItemStack mapItem) {
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo != null) {
            for (MapSession session : new ArrayList<MapSession>(mapInfo.getSessions())) {
                session.display.restartDisplay();
            }
        }
    }

    public static void restartDisplays(Class<? extends MapDisplay> displayClass) {
        for (MapDisplay mapDisplay : new ArrayList<MapDisplay>(MapDisplay.getAllDisplays(displayClass))) {
            mapDisplay.restartDisplay();
        }
    }

    public static void stopDisplays(ItemStack mapItem) {
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo != null) {
            for (MapSession session : new ArrayList<MapSession>(mapInfo.getSessions())) {
                session.display.setRunning(false);
            }
        }
    }

    public static void stopDisplaysForPlugin(Plugin plugin) {
        for (MapDisplayInfo map : new ArrayList<MapDisplayInfo>(CommonPlugin.getInstance().getMapController().getMaps())) {
            for (MapSession session : new ArrayList<MapSession>(map.getSessions())) {
                if (session.display.getPlugin() != plugin) continue;
                session.display.setRunning(false);
            }
        }
    }

    public static <T extends MapDisplay> Collection<T> getAllDisplays(Class<T> displayClass) {
        return CommonPlugin.getInstance().getMapController().getDisplays(displayClass);
    }

    public static Collection<MapDisplay> getAllDisplays(ItemFrame itemFrame) {
        return MapDisplay.getAllDisplaysFromInfo(CommonPlugin.getInstance().getMapController().getInfo(itemFrame));
    }

    public static Collection<MapDisplay> getAllDisplays(ItemStack item) {
        return MapDisplay.getAllDisplaysFromInfo(CommonPlugin.getInstance().getMapController().getInfo(item));
    }

    public static Collection<MapDisplay> getAllDisplays(UUID mapUniqueId) {
        return MapDisplay.getAllDisplaysFromInfo(CommonPlugin.getInstance().getMapController().getInfoIfExists(mapUniqueId));
    }

    private static Collection<MapDisplay> getAllDisplaysFromInfo(MapDisplayInfo info) {
        if (info != null) {
            int numSessions = info.getSessions().size();
            if (numSessions > 1) {
                HashSet<MapDisplay> uniqueDisplays = new HashSet<MapDisplay>(numSessions);
                for (MapSession session : info.getSessions()) {
                    if (session.display == null) continue;
                    uniqueDisplays.add(session.display);
                }
                return uniqueDisplays;
            }
            if (numSessions == 1) {
                MapSession session = info.getSessions().get(0);
                if (session.display != null) {
                    return Collections.singleton(session.display);
                }
            }
        }
        return Collections.emptySet();
    }

    public static MapDisplay getHeldDisplay(Player viewer) {
        MapDisplay offDisplay;
        MapDisplay mainDisplay = MapDisplay.getViewedDisplay(viewer, HumanHand.getItemInMainHand((HumanEntity)viewer));
        if (mainDisplay != null) {
            return mainDisplay;
        }
        if (CommonCapabilities.PLAYER_OFF_HAND && (offDisplay = MapDisplay.getViewedDisplay(viewer, HumanHand.getItemInOffHand((HumanEntity)viewer))) != null) {
            return offDisplay;
        }
        return null;
    }

    public static <T extends MapDisplay> T getHeldDisplay(Player viewer, Class<T> displayClass) {
        MapDisplay offDisplay;
        MapDisplay mainDisplay = MapDisplay.getViewedDisplay(viewer, HumanHand.getItemInMainHand((HumanEntity)viewer));
        if (mainDisplay != null && displayClass.isAssignableFrom(mainDisplay.getClass())) {
            return (T)mainDisplay;
        }
        if (CommonCapabilities.PLAYER_OFF_HAND && (offDisplay = MapDisplay.getViewedDisplay(viewer, HumanHand.getItemInOffHand((HumanEntity)viewer))) != null && displayClass.isAssignableFrom(offDisplay.getClass())) {
            return (T)offDisplay;
        }
        return null;
    }

    public static void fillItemFrames(ItemFrame startItemFrame, ItemStack item) {
        CommonPlugin.getInstance().getMapController().fillItemFrames(startItemFrame, item);
    }

    public static void fillItemFrames(ItemFrame startItemFrame, Class<? extends MapDisplay> mapDisplayClass) {
        MapDisplayProperties.createNew(mapDisplayClass).fillItemFrames(startItemFrame);
    }

    public static class Layer
    extends MapCanvas {
        private Layer previous;
        private Layer next;
        private byte z_index;
        private final MapDisplay map;
        private final int width;
        private final int height;
        private final byte[] buffer;
        private final MapClip clip = new MapClip();

        private Layer(MapDisplay map, int width, int height) {
            this.width = width;
            this.height = height;
            this.buffer = new byte[this.width * this.height];
            this.map = map;
            this.z_index = 0;
        }

        @Override
        public final int getWidth() {
            return this.width;
        }

        @Override
        public final int getHeight() {
            return this.height;
        }

        @Override
        public final byte[] getBuffer() {
            return this.buffer;
        }

        public Layer next() {
            if (this.next == null) {
                this.next = new Layer(this.map, this.width, this.height);
                this.next.z_index = (byte)(this.z_index + 1);
                this.next.previous = this;
            }
            return this.next;
        }

        public Layer previous() {
            if (this.previous == null) {
                this.previous = new Layer(this.map, this.width, this.height);
                this.previous.next = this;
                Layer current = this;
                int i = 0;
                while (i < this.map.zbuffer.length) {
                    byte[] byArray = this.map.zbuffer;
                    int n = i++;
                    byArray[n] = (byte)(byArray[n] + 1);
                }
                do {
                    current.z_index = (byte)(current.z_index + 1);
                } while ((current = current.next) != null);
            }
            return this.previous;
        }

        @Override
        public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
            return super.writePixels(x, y, w, h, colorData);
        }

        @Override
        public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
            int idx;
            int dy;
            boolean is_entire_canvas;
            if (x >= this.getWidth() || y >= this.getHeight()) {
                return this;
            }
            boolean bl = is_entire_canvas = x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight();
            if (!is_entire_canvas) {
                if (x < 0) {
                    w += x;
                    x = 0;
                }
                if (y < 0) {
                    h += y;
                    y = 0;
                }
                if (x + w > this.getWidth()) {
                    w = this.getWidth() - x;
                }
                if (y + h > this.getHeight()) {
                    h = this.getHeight() - y;
                }
                if (w <= 0 || h <= 0) {
                    return this;
                }
            }
            if (is_entire_canvas) {
                Arrays.fill(this.buffer, color);
                if (color == 0) {
                    if (this.clip.isDirty()) {
                        this.map.clip.markDirty(this.clip);
                    }
                    this.clip.clearDirty();
                } else {
                    this.clip.markDirty(0, 0, this.getWidth(), this.getHeight());
                    this.map.clip.markDirty(0, 0, this.getWidth(), this.getHeight());
                }
            } else {
                for (dy = 0; dy < h; ++dy) {
                    idx = x + (y + dy) * this.getWidth();
                    Arrays.fill(this.buffer, idx, idx + w, color);
                }
                if (color == 0) {
                    if (this.clip.isFullyEnclosedBy(x, y, w, h)) {
                        this.clip.clearDirty();
                    }
                } else {
                    this.clip.markDirty(x, y, w, h);
                }
                this.map.clip.markDirty(x, y, w, h);
            }
            if (color == 0) {
                boolean hasFaultyZ;
                MapDisplay map = this.map;
                Layer layer = this;
                do {
                    hasFaultyZ = false;
                    if (layer.previous == null) {
                        for (int dy2 = 0; dy2 < h; ++dy2) {
                            int idx2;
                            int idx_end = idx2 + w;
                            for (idx2 = x + (y + dy2) * this.getWidth(); idx2 < idx_end; ++idx2) {
                                if (map.zbuffer[idx2] != layer.z_index) continue;
                                ((MapDisplay)map).livebuffer[idx2] = layer.buffer[idx2];
                            }
                        }
                    } else {
                        byte prev_z = layer.previous.z_index;
                        for (int dy3 = 0; dy3 < h; ++dy3) {
                            int index;
                            int index_end = index + w;
                            for (index = x + (y + dy3) * this.getWidth(); index < index_end; ++index) {
                                if (map.zbuffer[index] != layer.z_index) continue;
                                byte layer_color = layer.buffer[index];
                                if (layer_color != 0) {
                                    ((MapDisplay)map).livebuffer[index] = layer_color;
                                    continue;
                                }
                                ((MapDisplay)map).zbuffer[index] = prev_z;
                                hasFaultyZ = true;
                            }
                        }
                    }
                } while ((layer = layer.previous) != null && hasFaultyZ);
            } else if (is_entire_canvas) {
                for (int idx3 = 0; idx3 < this.map.zbuffer.length; ++idx3) {
                    byte curr_z = this.map.zbuffer[idx3];
                    if (curr_z == this.z_index) {
                        ((MapDisplay)this.map).livebuffer[idx3] = color;
                        continue;
                    }
                    if (curr_z >= this.z_index) continue;
                    ((MapDisplay)this.map).zbuffer[idx3] = this.z_index;
                    ((MapDisplay)this.map).livebuffer[idx3] = color;
                }
            } else {
                for (dy = 0; dy < h; ++dy) {
                    int idx_end = idx + w;
                    for (idx = x + (y + dy) * this.getWidth(); idx < idx_end; ++idx) {
                        byte curr_z = this.map.zbuffer[idx];
                        if (curr_z == this.z_index) {
                            ((MapDisplay)this.map).livebuffer[idx] = color;
                            continue;
                        }
                        if (curr_z >= this.z_index) continue;
                        ((MapDisplay)this.map).zbuffer[idx] = this.z_index;
                        ((MapDisplay)this.map).livebuffer[idx] = color;
                    }
                }
            }
            return this;
        }

        @Override
        public final void writePixel(int x, int y, byte color) {
            if (x < 0 || x >= this.getWidth()) {
                return;
            }
            int index = x + y * this.getWidth();
            if (index < 0 || index >= this.buffer.length) {
                return;
            }
            byte pixel_z = this.map.zbuffer[index];
            boolean is_transparency_change = this.buffer[index] == 0 != (color == 0);
            this.buffer[index] = color;
            if (is_transparency_change) {
                this.clip.markDirty(x, y);
                if (color == 0) {
                    if (pixel_z == this.z_index) {
                        Layer layer = this;
                        while (layer.buffer[index] == 0 && layer.previous != null) {
                            layer = layer.previous;
                        }
                        ((MapDisplay)this.map).zbuffer[index] = pixel_z = layer.z_index;
                        ((MapDisplay)this.map).livebuffer[index] = layer.buffer[index];
                        this.map.clip.markDirty(x, y);
                    }
                } else if (pixel_z <= this.z_index) {
                    ((MapDisplay)this.map).zbuffer[index] = this.z_index;
                    ((MapDisplay)this.map).livebuffer[index] = color;
                    this.map.clip.markDirty(x, y);
                }
            } else if (pixel_z == this.z_index) {
                ((MapDisplay)this.map).livebuffer[index] = color;
                this.map.clip.markDirty(x, y);
            }
        }

        @Override
        public final byte readPixel(int x, int y) {
            int index;
            if (x >= 0 && y < this.getHeight() && (index = x + y * this.getWidth()) >= 0 && index < this.buffer.length) {
                byte color;
                Layer layer = this;
                do {
                    color = layer.buffer[index];
                } while ((layer = layer.previous) != null && color == 0);
                return color;
            }
            return 0;
        }

        private final byte readBasePixel(int x, int y) {
            int index;
            if (x >= 0 && y < this.getHeight() && (index = x + y * this.getWidth()) >= 0 && index < this.buffer.length) {
                return this.buffer[index];
            }
            return 0;
        }

        private final byte[] readBasePixels(int x, int y, int w, int h, byte[] dst_buffer) {
            return super.readPixels(x, y, w, h, dst_buffer);
        }

        @Override
        public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
            this.readBasePixels(x, y, w, h, dst_buffer);
            if (this.getBlendMode() == MapBlendMode.NONE || this.getBlendMode() == MapBlendMode.OVERLAY) {
                return dst_buffer;
            }
            byte[] tmp_buffer = null;
            Layer layer = this;
            int remaining_pixels = dst_buffer.length;
            while (layer.previous != null && remaining_pixels > 0) {
                layer = layer.previous;
                if (!layer.clip.isDirty()) continue;
                if (remaining_pixels > 100) {
                    if (tmp_buffer == null) {
                        tmp_buffer = new byte[w * h];
                    }
                    layer.readBasePixels(x, y, w, h, tmp_buffer);
                    remaining_pixels = 0;
                    for (int i = 0; i < tmp_buffer.length; ++i) {
                        if (dst_buffer[i] != 0) continue;
                        byte color = tmp_buffer[i];
                        if (color == 0) {
                            ++remaining_pixels;
                            continue;
                        }
                        dst_buffer[i] = color;
                    }
                    continue;
                }
                int dx_end = x + w;
                int dx = x;
                int dy = y;
                remaining_pixels = 0;
                for (int i = 0; i < dst_buffer.length; ++i) {
                    if (dst_buffer[i] == 0) {
                        byte color = layer.readBasePixel(dx, dy);
                        if (color != 0) {
                            dst_buffer[i] = color;
                        } else {
                            ++remaining_pixels;
                        }
                    }
                    if (++dx != dx_end) continue;
                    dx = x;
                    ++dy;
                }
            }
            return dst_buffer;
        }

        public String toString() {
            return "{layer z=" + this.z_index + ",w=" + this.width + ",h=" + this.height + "} of " + this.map.toString();
        }
    }

    private final class UpdateTask
    implements Runnable {
        private UpdateTask() {
        }

        @Override
        public void run() {
            MapDisplay.this.update();
        }
    }
}

