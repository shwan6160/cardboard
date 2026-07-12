/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 */
package com.bergerkiller.bukkit.tc.signactions.mutex.railslot;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import com.bergerkiller.bukkit.tc.signactions.mutex.railslot.MutexRailSlot;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MutexRailSlotMap {
    private static final Map<IntVector3, MutexRailSlot> INITIAL_RAILS = Collections.emptyMap();
    private final LinkedHashMap<IntVector3, MutexRailSlot> railsLive = new LinkedHashMap();
    private final ArrayList<MutexRailSlot> railsFull = new ArrayList();
    private Map<IntVector3, MutexRailSlot> rails = INITIAL_RAILS;
    private MutexRailSlot conflict = null;

    public List<MutexRailSlot> getLastPath() {
        ArrayList<MutexRailSlot> result = new ArrayList<MutexRailSlot>(this.railsLive.values());
        if (this.conflict != null) {
            result.add(this.conflict);
        }
        return result;
    }

    public void clearConflict(IntVector3 conflictRail) {
        MutexRailSlot prevConflict = this.conflict;
        this.rails = INITIAL_RAILS;
        this.railsFull.clear();
        this.conflict = (MutexRailSlot)this.railsLive.remove(conflictRail);
        if (this.conflict == null) {
            this.conflict = prevConflict;
        }
    }

    public void clearOldRails(int nowTicks) {
        Iterator<MutexRailSlot> iter = this.rails.values().iterator();
        while (iter.hasNext()) {
            MutexRailSlot slot = iter.next();
            if (slot.ticksLastProbed() >= nowTicks) continue;
            this.onSlotRemoved(slot);
            iter.remove();
        }
    }

    public void keepAlive(int nowTicks) {
        this.railsLive.values().forEach(slot -> slot.probe(nowTicks));
    }

    public boolean add(MutexZoneSlotType type, IntVector3 railBlock, int nowTicks) {
        Map<IntVector3, MutexRailSlot> currRails = this.rails;
        if (currRails == INITIAL_RAILS) {
            this.rails = currRails = this.railsLive;
            currRails.clear();
            this.conflict = null;
        }
        MutexRailSlot slot = currRails.computeIfAbsent(railBlock, MutexRailSlot::new);
        boolean added = slot.isNew();
        boolean wasFullLocking = slot.isFullLocking();
        slot.probe(type, nowTicks);
        if (!wasFullLocking && slot.isFullLocking()) {
            this.railsFull.add(slot);
        }
        return added;
    }

    public boolean remove(IntVector3 railBlock) {
        Map<IntVector3, MutexRailSlot> rails = this.rails;
        if (rails.isEmpty()) {
            return false;
        }
        MutexRailSlot slot = rails.remove(railBlock);
        if (slot == null) {
            return false;
        }
        this.onSlotRemoved(slot);
        return true;
    }

    public boolean isFullyLocked() {
        return !this.railsFull.isEmpty();
    }

    public boolean isFullyLockedVerify(MinecartGroup group, int nowTicks) {
        ArrayList<MutexRailSlot> railsFull = this.railsFull;
        if (!railsFull.isEmpty()) {
            Iterator<MutexRailSlot> iter = this.railsFull.iterator();
            while (iter.hasNext()) {
                MutexRailSlot slot = iter.next();
                if (nowTicks == slot.ticksLastProbed() || MutexRailSlotMap.isRailUsedByGroup(slot.rail(), group)) {
                    return true;
                }
                this.railsLive.remove(slot.rail());
                iter.remove();
            }
        }
        return false;
    }

    public boolean isSmartLocked(IntVector3 rail) {
        return this.rails.containsKey(rail);
    }

    public boolean isSmartLockedVerify(MinecartGroup group, int nowTicks, IntVector3 rail) {
        MutexRailSlot slot = this.rails.get(rail);
        if (slot == null) {
            return false;
        }
        if (nowTicks == slot.ticksLastProbed()) {
            return true;
        }
        if (group.isEmpty() || group.isUnloaded()) {
            return false;
        }
        if (MutexRailSlotMap.isRailUsedByGroup(rail, group)) {
            return true;
        }
        this.onSlotRemoved(slot);
        this.rails.remove(rail);
        return false;
    }

    public boolean verifyHasRailsUsedByGroup(MinecartGroup group) {
        Iterator<MutexRailSlot> iter = this.rails.values().iterator();
        while (iter.hasNext()) {
            MutexRailSlot slot = iter.next();
            if (MutexRailSlotMap.isRailUsedByGroup(slot.rail(), group)) {
                return true;
            }
            this.onSlotRemoved(slot);
            iter.remove();
        }
        return false;
    }

    private void onSlotRemoved(MutexRailSlot slot) {
        if (slot.isFullLocking()) {
            this.railsFull.remove(slot);
        }
    }

    private static boolean isRailUsedByGroup(IntVector3 rail, MinecartGroup group) {
        if (group.isEmpty() || group.isUnloaded()) {
            return false;
        }
        WorldRailLookup railLookup = group.head().railLookup();
        for (RailLookup.CachedRailPiece railPiece : railLookup.lookupCachedRailPieces(railLookup.getOfflineWorld().getBlockAt(rail))) {
            for (MinecartMember<?> member : railPiece.cachedMembers()) {
                if (member.isUnloaded() || ((CommonMinecart)member.getEntity()).isRemoved() || member.getGroup() != group) continue;
                return true;
            }
        }
        return false;
    }

    public void save(OfflineDataBlock root) throws IOException {
        root.addChild("rail-slots", stream -> {
            stream.writeBoolean(this.rails == INITIAL_RAILS);
            Util.writeVariableLengthInt(stream, this.railsLive.size());
            for (MutexRailSlot slot : this.railsLive.values()) {
                slot.writeTo(stream);
            }
            stream.writeBoolean(this.conflict != null);
            if (this.conflict != null) {
                this.conflict.writeTo(stream);
            }
        });
    }

    public void load(OfflineDataBlock root) throws IOException {
        try (DataInputStream stream = root.findChildOrThrow("rail-slots").readData();){
            boolean isSetToInitial = stream.readBoolean();
            int numRailSlots = Util.readVariableLengthInt(stream);
            this.railsLive.clear();
            this.railsFull.clear();
            MutexZoneSlotType[] types = MutexZoneSlotType.values();
            for (int num = 0; num < numRailSlots; ++num) {
                MutexRailSlot slot = MutexRailSlot.read(stream);
                this.railsLive.put(slot.rail(), slot);
                if (!slot.isFullLocking()) continue;
                this.railsFull.add(slot);
            }
            Map<IntVector3, MutexRailSlot> map = this.rails = isSetToInitial ? INITIAL_RAILS : this.railsLive;
            if (stream.readBoolean()) {
                this.conflict = MutexRailSlot.read(stream);
                MutexRailSlot existing = this.railsLive.get(this.conflict.rail());
                if (existing != null && existing.type() == this.conflict.type()) {
                    this.conflict = existing;
                }
            } else {
                this.conflict = null;
            }
        }
    }
}

