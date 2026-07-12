/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import java.util.UUID;

public final class MapUUID {
    private final int tileX;
    private final int tileY;
    private final UUID uuid;

    public MapUUID(UUID uuid) {
        this(uuid, 0, 0);
    }

    public MapUUID(UUID uuid, int tileX, int tileY) {
        this.uuid = uuid;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public final UUID getUUID() {
        return this.uuid;
    }

    public final int getTileX() {
        return this.tileX;
    }

    public final int getTileY() {
        return this.tileY;
    }

    public boolean isStaticUUID() {
        return CommonMapUUIDStore.isStaticMapId(this.uuid);
    }

    public final int hashCode() {
        return (this.uuid.hashCode() << 6) + (this.tileX << 4) + (this.tileY << 2);
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MapUUID) {
            MapUUID other = (MapUUID)o;
            return this.uuid.equals(other.uuid) && this.tileX == other.tileX && this.tileY == other.tileY;
        }
        return false;
    }

    public final String toString() {
        return this.uuid.toString() + "{" + this.tileX + ", " + this.tileY + "}";
    }
}

