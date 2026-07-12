/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapClip;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMapItemDataPacketHandle;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class MapDisplayTile {
    public static final int RESOLUTION = 128;
    private final MapUUID uuid;
    public final int tileX;
    public final int tileY;
    public final IntVector2 tile;

    public MapDisplayTile(UUID mapDisplayUUID, int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tile = new IntVector2(tileX, tileY);
        this.uuid = new MapUUID(mapDisplayUUID, this.tileX, this.tileY);
    }

    public MapUUID getMapTileUUID() {
        return this.uuid;
    }

    public int getMapId() {
        return CommonPlugin.getInstance().getMapController().getMapId(this.uuid);
    }

    public void addTileUpdate(MapDisplay display, Player viewer, List<Update> updates, MapClip clip) {
        Update update = this.getTileUpdate(display, viewer, clip);
        if (update != null) {
            updates.add(update);
        }
    }

    public Update getTileUpdate(MapDisplay display, Player viewer, MapClip clip) {
        MapClip tileClip;
        int startX = this.tileX * 128;
        int startY = this.tileY * 128;
        if (startX >= display.getWidth() || startY >= display.getHeight()) {
            return null;
        }
        Update mapUpdate = new Update(this.tile, this.getMapId());
        boolean markersChanged = display.getMarkerManager().addMarkersToUpdate(viewer, mapUpdate);
        int stride = display.getWidth();
        int srcPos = startY * stride + startX;
        int dstPos = 0;
        byte[] liveBuffer = display.getLiveBuffer();
        if (liveBuffer == null) {
            return null;
        }
        MapClip mapClip = tileClip = clip == null ? null : clip.getArea(startX, startY, 128, 128);
        if (tileClip == null || tileClip.isEverythingDirty()) {
            byte[] pixels = new byte[16384];
            for (int y = 0; y < 128; ++y) {
                System.arraycopy(liveBuffer, srcPos, pixels, dstPos, 128);
                srcPos += stride;
                dstPos += 128;
            }
            mapUpdate.packet.data(0, 0, 128, 128, pixels);
            return mapUpdate;
        }
        if (tileClip.isDirty()) {
            int w = tileClip.getWidth();
            int h = tileClip.getHeight();
            byte[] pixels = new byte[w * h];
            srcPos += tileClip.getY() * stride;
            srcPos += tileClip.getX();
            for (int y = 0; y < h; ++y) {
                System.arraycopy(liveBuffer, srcPos, pixels, dstPos, w);
                srcPos += stride;
                dstPos += w;
            }
            mapUpdate.packet.data(tileClip.getX(), tileClip.getY(), w, h, pixels);
            return mapUpdate;
        }
        return markersChanged ? mapUpdate : null;
    }

    public static final class Update
    implements Cloneable {
        public final IntVector2 tile;
        public final ClientboundMapItemDataPacketHandle.Builder packet;

        public Update(IntVector2 tile, int mapId) {
            this.tile = tile;
            this.packet = ClientboundMapItemDataPacketHandle.build();
            this.packet.mapId(mapId);
        }

        private Update(IntVector2 tile, ClientboundMapItemDataPacketHandle.Builder packet) {
            this.tile = tile;
            this.packet = packet;
        }

        public Update clone() {
            return new Update(this.tile, this.packet.clone());
        }
    }
}

