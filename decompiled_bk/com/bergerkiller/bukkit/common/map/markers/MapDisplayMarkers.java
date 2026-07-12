/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapMarker;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapDecorationHandle;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class MapDisplayMarkers {
    public static final UniqueHash RANDOM_NAME_SOURCE = new UniqueHash();
    private final Map<String, Entry> markersById = new HashMap<String, Entry>();
    private final Map<IntVector2, MapDisplayMarkerTile> markersByTile = new HashMap<IntVector2, MapDisplayMarkerTile>();

    public void clear() {
        this.markersById.clear();
        for (MapDisplayMarkerTile tile : this.markersByTile.values()) {
            tile.clear();
        }
    }

    public Collection<MapMarker> values() {
        return Collections.unmodifiableCollection(this.markersById.values().stream().map(e -> e.value).collect(Collectors.toList()));
    }

    List<MapDecorationHandle> serializeValuesAsMapIcons(MapDisplayMarkerTile tile) {
        return this.markersById.values().stream().filter(e -> e.tile == tile).map(e -> e.value).map(marker -> MapDecorationHandle.createNew(marker.getType(), tile.encodeX(marker.getPositionX()), tile.encodeY(marker.getPositionY()), tile.encodeRotation(marker.getRotation()), marker.getFormattedCaption())).collect(StreamUtil.toUnmodifiableList());
    }

    public MapMarker get(String id) {
        Entry e = this.markersById.get(id);
        return e == null ? null : e.value;
    }

    public MapMarker add(MapMarker marker) {
        MapDisplayMarkerTile tileForMarker = this.computeTileAtPosition(marker.getPositionX(), marker.getPositionY());
        Entry previousEntry = this.markersById.put(marker.getId(), new Entry(marker, tileForMarker));
        tileForMarker.add(marker);
        if (previousEntry != null) {
            previousEntry.setTile(null);
        }
        return marker;
    }

    public void updateVisible(MapMarker marker, boolean new_visible) {
        Entry entry;
        if (marker.isVisible() != new_visible && (entry = this.markersById.get(marker.getId())) != null) {
            entry.setTile(new_visible ? this.computeTileAtPosition(marker.getPositionX(), marker.getPositionY()) : null);
        }
    }

    public void update(MapMarker marker) {
        Entry entry = this.markersById.get(marker.getId());
        if (entry != null && entry.tile != null) {
            entry.tile.setChanged(true);
        }
    }

    public MapMarker remove(String id) {
        Entry removedEntry = this.markersById.remove(id);
        if (removedEntry != null) {
            removedEntry.setTile(null);
            return removedEntry.value;
        }
        return null;
    }

    public boolean remove(MapMarker marker) {
        Entry removedEntry = this.markersById.remove(marker.getId());
        if (removedEntry == null) {
            return false;
        }
        if (removedEntry.value != marker) {
            this.markersById.put(removedEntry.value.getId(), removedEntry);
            return false;
        }
        removedEntry.setTile(null);
        return true;
    }

    public void move(MapMarker marker, double new_x, double new_y) {
        if (!marker.isVisible()) {
            return;
        }
        Entry oldEntry = this.markersById.get(marker.getId());
        if (oldEntry == null) {
            return;
        }
        MapDisplayMarkerTile new_tile = this.computeTileAtPosition(new_x, new_y);
        if (oldEntry.tile == new_tile) {
            if (oldEntry.tile.encodeX(marker.getPositionX()) != oldEntry.tile.encodeX(new_x) || oldEntry.tile.encodeY(marker.getPositionY()) != oldEntry.tile.encodeY(new_y)) {
                oldEntry.tile.setChanged(true);
            }
        } else {
            oldEntry.setTile(new_tile);
        }
    }

    public void synchronize(MapSession session) {
        if (this.markersByTile.isEmpty()) {
            return;
        }
        for (MapDisplayTile displayedTile : session.tiles) {
            MapDisplayMarkerTile tile = this.markersByTile.get(displayedTile.tile);
            if (tile == null || !tile.isChanged()) continue;
            MapDisplayTile.Update mapUpdate = null;
            for (MapSession.Owner owner : session.onlineOwners) {
                if (tile.isSynchronized(owner.player)) continue;
                if (mapUpdate == null) {
                    mapUpdate = new MapDisplayTile.Update(displayedTile.tile, displayedTile.getMapId());
                    mapUpdate.packet.cursors_nms(this.serializeValuesAsMapIcons(tile));
                }
                PacketUtil.sendPacket(owner.player, mapUpdate.packet.create(), false);
            }
        }
        Iterator<MapDisplayMarkerTile> iter = this.markersByTile.values().iterator();
        while (iter.hasNext()) {
            MapDisplayMarkerTile tile = iter.next();
            if (tile.isEmpty()) {
                iter.remove();
                continue;
            }
            tile.setChanged(false);
        }
    }

    public void setMarkersSynchronized(Player viewer, MapDisplayTile.Update mapUpdate) {
        MapDisplayMarkerTile tile = this.markersByTile.get(mapUpdate.tile);
        if (tile != null) {
            tile.setSynchronized(viewer);
        }
    }

    public boolean addMarkersToUpdate(Player viewer, MapDisplayTile.Update mapUpdate) {
        MapDisplayMarkerTile tile = this.markersByTile.get(mapUpdate.tile);
        if (tile == null) {
            mapUpdate.packet.no_cursors();
            return false;
        }
        mapUpdate.packet.cursors_nms(this.serializeValuesAsMapIcons(tile));
        return tile.isChangedFor(viewer);
    }

    private MapDisplayMarkerTile computeTileAtPosition(double x, double y) {
        int tileX = MathUtil.floor(x) >> 7;
        int tileY = MathUtil.floor(y) >> 7;
        return this.markersByTile.computeIfAbsent(new IntVector2(tileX, tileY), MapDisplayMarkerTile::new);
    }

    public static final class Entry {
        public final MapMarker value;
        public MapDisplayMarkerTile tile;

        public Entry(MapMarker value, MapDisplayMarkerTile tile) {
            this.value = value;
            this.tile = tile;
        }

        public void setTile(MapDisplayMarkerTile new_tile) {
            if (this.tile != null) {
                this.tile.remove(this.value);
                this.tile = null;
            }
            if (new_tile != null) {
                this.tile = new_tile;
                new_tile.add(this.value);
            }
        }
    }
}

