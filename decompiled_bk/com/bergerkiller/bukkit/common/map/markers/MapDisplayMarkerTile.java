/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.map.MapMarker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;

public class MapDisplayMarkerTile {
    private final IntVector2 coordinates;
    private final double offsetX;
    private final double offsetY;
    private final List<MapMarker> visibleMarkers = new ArrayList<MapMarker>();
    private final Set<Player> sentToPlayers = new HashSet<Player>();
    private boolean changed = false;

    public MapDisplayMarkerTile(IntVector2 coordinates) {
        this.coordinates = coordinates;
        this.offsetX = coordinates.x << 7;
        this.offsetY = coordinates.z << 7;
    }

    public IntVector2 getCoordinates() {
        return this.coordinates;
    }

    public boolean isEmpty() {
        return this.visibleMarkers.isEmpty();
    }

    public int getMarkerCount() {
        return this.visibleMarkers.size();
    }

    public MapMarker getMarker(int index) {
        return this.visibleMarkers.get(index);
    }

    public void setChanged(boolean new_changed) {
        this.changed = new_changed;
        this.sentToPlayers.clear();
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void clear() {
        if (!this.visibleMarkers.isEmpty()) {
            this.visibleMarkers.clear();
            this.changed = true;
        }
    }

    public void add(MapMarker marker) {
        this.visibleMarkers.add(marker);
        this.changed = true;
    }

    public void remove(MapMarker marker) {
        this.visibleMarkers.remove(marker);
        this.changed = true;
    }

    public boolean isSynchronized(Player viewer) {
        return this.sentToPlayers.contains(viewer);
    }

    public void setSynchronized(Player viewer) {
        if (this.changed) {
            this.sentToPlayers.add(viewer);
        }
    }

    public boolean isChangedFor(Player viewer) {
        return this.isChanged() && !this.sentToPlayers.contains(viewer);
    }

    public byte encodeX(double positionX) {
        return MapDisplayMarkerTile.encodeToByte(2.0 * (positionX - this.offsetX));
    }

    public byte encodeY(double positionY) {
        return MapDisplayMarkerTile.encodeToByte(2.0 * (positionY - this.offsetY));
    }

    public byte encodeRotation(double rotation) {
        return (byte)((int)((rotation + 180.0) / 22.5) & 0xF);
    }

    private static byte encodeToByte(double value) {
        if (value <= 0.0) {
            return -128;
        }
        if (value >= 256.0) {
            return 127;
        }
        return (byte)(value - 128.0);
    }
}

