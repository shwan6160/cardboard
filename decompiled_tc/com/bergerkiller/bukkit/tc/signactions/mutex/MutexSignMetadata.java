/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.Location
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import java.util.UUID;
import org.bukkit.Location;

public class MutexSignMetadata {
    public final MutexZoneSlotType type;
    public final String name;
    public final IntVector3 start;
    public final IntVector3 end;
    public final String statement;

    public MutexSignMetadata(MutexZoneSlotType type, String name, IntVector3 start, IntVector3 end, String statement) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        this.type = type;
        this.name = name;
        this.start = start;
        this.end = end;
        this.statement = statement;
    }

    public static MutexSignMetadata fromSign(SignActionEvent info) {
        int dx = 1;
        int dy = 2;
        int dz = 1;
        String coords = info.getLine(1);
        int firstSpace = coords.indexOf(32);
        if (firstSpace != -1 && !(coords = coords.substring(firstSpace).trim()).isEmpty()) {
            String[] parts = coords.split("/");
            if (parts.length >= 3) {
                dx = ParseUtil.parseInt((String)parts[0], (int)dx);
                dy = ParseUtil.parseInt((String)parts[1], (int)dy);
                dz = ParseUtil.parseInt((String)parts[2], (int)dz);
            } else if (parts.length >= 2) {
                dx = dz = ParseUtil.parseInt((String)parts[0], (int)dx);
                dy = ParseUtil.parseInt((String)parts[1], (int)dy);
            } else if (parts.length >= 1) {
                dy = dz = ParseUtil.parseInt((String)parts[0], (int)dx);
                dx = dz;
            }
        }
        UUID worldUUID = info.getWorld().getUID();
        String name = info.getLine(2).trim();
        if (!name.isEmpty()) {
            name = worldUUID.toString() + "_" + name;
        }
        String statement = info.getLine(3).trim();
        IntVector3 block = MutexSignMetadata.getPosition(info);
        IntVector3 start = block.subtract(dx, dy, dz);
        IntVector3 end = block.add(dx, dy, dz);
        MutexZoneSlotType type = info.isType("smartmutex", "smutex") ? MutexZoneSlotType.SMART : MutexZoneSlotType.NORMAL;
        return new MutexSignMetadata(type, name, start, end, statement);
    }

    private static IntVector3 getPosition(SignActionEvent info) {
        Location middlePos = info.getCenterLocation();
        if (middlePos != null) {
            return new IntVector3(middlePos);
        }
        return new IntVector3(info.getBlock());
    }
}

