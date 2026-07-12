/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map.binding;

import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MapDisplayInfo {
    private final UUID uuid;
    final ArrayList<ItemFrameInfo> itemFrames = new ArrayList();
    private final LinkedHashSet<Player> frameViewers = new LinkedHashSet();
    final FastTrackedUpdateSet.Tracker<MapDisplayInfo> hasFrameViewerChanges;
    final FastTrackedUpdateSet.Tracker<MapDisplayInfo> hasFrameResolutionChanges;
    private int desiredWidth;
    private int desiredHeight;
    private final ArrayList<MapSession> sessions = new ArrayList();
    private final HashMap<UUID, ViewStack> views = new HashMap();

    public MapDisplayInfo(CommonMapController controller, UUID uuid) {
        this.uuid = uuid;
        this.desiredWidth = 128;
        this.desiredHeight = 128;
        this.hasFrameViewerChanges = controller.mapsWithItemFrameViewerChanges.track(this);
        this.hasFrameResolutionChanges = controller.mapsWithItemFrameResolutionChanges.track(this);
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public Collection<Player> getViewers() {
        return this.frameViewers;
    }

    public List<ItemFrameInfo> getItemFrames() {
        return this.itemFrames;
    }

    public List<MapSession> getSessions() {
        return this.sessions;
    }

    public int getDesiredWidth() {
        return this.desiredWidth;
    }

    public int getDesiredHeight() {
        return this.desiredHeight;
    }

    public void removeTileIfMissing(int tileX, int tileY) {
        if (this.sessions.isEmpty() || tileX == 0 && tileY == 0) {
            return;
        }
        for (ItemFrameInfo frame : this.itemFrames) {
            if (frame.lastMapUUID == null || frame.lastMapUUID.getTileX() != tileX || frame.lastMapUUID.getTileY() != tileY) continue;
            return;
        }
        block1: for (MapSession session : this.sessions) {
            if (session.refreshResolutionRequested) continue;
            Iterator<MapDisplayTile> iter = session.tiles.iterator();
            while (iter.hasNext()) {
                MapDisplayTile tile = iter.next();
                if (tile.tileX != tileX || tile.tileY != tileY) continue;
                iter.remove();
                continue block1;
            }
        }
    }

    public void addTileIfMissing(int tileX, int tileY) {
        if (this.sessions.isEmpty() || tileX == 0 && tileY == 0) {
            return;
        }
        if (tileX << 7 >= this.desiredWidth) {
            return;
        }
        if (tileY << 7 >= this.desiredHeight) {
            return;
        }
        for (MapSession session : this.sessions) {
            if (session.refreshResolutionRequested || session.display.containsTile(tileX, tileY)) continue;
            MapDisplayTile newTile = new MapDisplayTile(this.uuid, tileX, tileY);
            session.tiles.add(newTile);
            for (MapSession.Owner owner : session.onlineOwners) {
                owner.sendDirtyTile(newTile);
            }
        }
    }

    public void onRemoved() {
        this.hasFrameResolutionChanges.untrack();
        this.hasFrameViewerChanges.untrack();
    }

    public void updateItemFrameViewers() {
        this.frameViewers.clear();
        for (ItemFrameInfo itemFrame : this.itemFrames) {
            itemFrame.viewers.forEach(this.frameViewers::add);
        }
    }

    public void updateItemFrameResolution() {
        for (int i = this.itemFrames.size() - 1; i >= 0; --i) {
            this.itemFrames.get(i).recalculateUUID();
        }
        this.refreshResolution();
    }

    public void refreshResolution() {
        int min_x = 0;
        int min_y = 0;
        int max_x = 0;
        int max_y = 0;
        boolean first = true;
        for (ItemFrameInfo itemFrame : this.itemFrames) {
            if (itemFrame.lastMapUUID == null) continue;
            int tx = itemFrame.lastMapUUID.getTileX() << 7;
            int ty = itemFrame.lastMapUUID.getTileY() << 7;
            if (first) {
                first = false;
                min_x = max_x = tx;
                min_y = max_y = ty;
                continue;
            }
            if (tx < min_x) {
                min_x = tx;
            }
            if (tx > max_x) {
                max_x = tx;
            }
            if (ty < min_y) {
                min_y = ty;
            }
            if (ty <= max_y) continue;
            max_y = ty;
        }
        int new_width = max_x - min_x + 128;
        int new_height = max_y - min_y + 128;
        if (new_width != this.desiredWidth || new_height != this.desiredHeight) {
            this.desiredWidth = new_width;
            this.desiredHeight = new_height;
            this.hasFrameResolutionChanges.set(true);
            for (MapSession session : this.sessions) {
                if (session.refreshResolutionRequested || session.display.getWidth() == this.desiredWidth && session.display.getHeight() == this.desiredHeight) continue;
                session.refreshResolutionRequested = true;
            }
        }
    }

    public void loadTiles(MapSession session, boolean initialize) {
        Object iter;
        LongHashSet tile_coords = new LongHashSet();
        for (ItemFrameInfo itemFrame : this.itemFrames) {
            MapUUID uuid = itemFrame.lastMapUUID;
            if (uuid == null) continue;
            tile_coords.add(uuid.getTileX(), uuid.getTileY());
        }
        tile_coords.add(0, 0);
        if (initialize) {
            session.tiles.clear();
        } else {
            iter = session.tiles.iterator();
            while (iter.hasNext()) {
                MapDisplayTile tile = (MapDisplayTile)iter.next();
                if (tile_coords.remove(tile.tileX, tile.tileY)) continue;
                iter.remove();
            }
        }
        iter = tile_coords.longIterator();
        while (((LongHashSet.LongIterator)iter).hasNext()) {
            long coord = ((LongHashSet.LongIterator)iter).next();
            MapDisplayTile newTile = new MapDisplayTile(this.uuid, MathUtil.longHashMsw(coord), MathUtil.longHashLsw(coord));
            session.tiles.add(newTile);
            if (initialize) continue;
            for (MapSession.Owner owner : session.onlineOwners) {
                owner.sendDirtyTile(newTile);
            }
        }
    }

    public boolean isMap(ItemStack item) {
        return this.uuid.equals(CommonMapUUIDStore.getMapUUID(item));
    }

    public ViewStack getOrCreateViewStack(Player player) {
        UUID playerUUID = player.getUniqueId();
        ViewStack stack = this.views.get(playerUUID);
        if (stack == null) {
            stack = new ViewStack();
            this.views.put(playerUUID, stack);
        }
        return stack;
    }

    public ViewStack getViewStackByPlayerUUID(UUID playerUUID) {
        return this.views.get(playerUUID);
    }

    public void setViewing(Player player, MapDisplay display, boolean viewing) {
        UUID playerUUID = player.getUniqueId();
        ViewStack stack = this.views.get(playerUUID);
        if (viewing) {
            if (stack == null) {
                stack = new ViewStack();
                stack.stack.add(display);
                this.views.put(playerUUID, stack);
            } else {
                stack.stack.remove(display);
                stack.stack.add(display);
            }
        } else if (stack != null) {
            stack.stack.remove(display);
        }
    }

    public MapDisplay getViewing(Player player) {
        ViewStack stack = this.views.get(player.getUniqueId());
        return stack == null || stack.stack.isEmpty() ? null : stack.stack.getLast();
    }

    public boolean isViewing(Player player, MapDisplay display) {
        return this.getViewing(player) == display;
    }

    public void addSession(MapSession session) {
        this.sessions.add(session);
    }

    public void removeSession(MapSession session) {
        this.sessions.remove(session);
    }

    public static class ViewStack {
        public final LinkedList<MapDisplay> stack = new LinkedList();

        public String toString() {
            String str = "ViewStack:";
            for (MapDisplay display : this.stack) {
                str = str + "\n  " + display.toString();
            }
            return str;
        }
    }
}

