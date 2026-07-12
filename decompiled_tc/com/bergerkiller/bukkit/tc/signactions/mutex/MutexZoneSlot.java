/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.events.MutexZoneConflictEvent;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import com.bergerkiller.bukkit.tc.signactions.mutex.railslot.MutexRailSlot;
import com.bergerkiller.bukkit.tc.signactions.mutex.railslot.MutexRailSlotMap;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.block.Block;

public class MutexZoneSlot {
    private static final int TICK_DELAY_CLEAR_AUTOMATIC = 6;
    private final String name;
    private final List<EnteredGroup> entered = new ArrayList<EnteredGroup>(2);
    private List<MutexZone> zones;
    private List<String> statements;
    private int tickLastHardEntered = 0;

    protected MutexZoneSlot(String name) {
        this.name = name;
        this.zones = Collections.emptyList();
        this.statements = Collections.emptyList();
    }

    public String getName() {
        return this.name;
    }

    public String getNameWithoutWorldUUID() {
        String uuid_str;
        if (!this.zones.isEmpty() && this.name.startsWith(uuid_str = this.zones.get((int)0).signBlock.getWorldUUID().toString() + "_")) {
            return this.name.substring(uuid_str.length());
        }
        return this.name;
    }

    public boolean isAnonymous() {
        return this.name.isEmpty();
    }

    protected MutexZoneSlot addZone(MutexZone zone) {
        if (this.zones.isEmpty()) {
            this.zones = Collections.singletonList(zone);
        } else {
            this.zones = new ArrayList<MutexZone>(this.zones);
            this.zones.add(zone);
        }
        this.refreshStatements();
        return this;
    }

    public void removeZone(MutexZone zone) {
        if (this.zones.size() == 1 && this.zones.get(0) == zone) {
            this.zones = Collections.emptyList();
            this.statements = Collections.emptyList();
        } else if (this.zones.size() > 1) {
            this.zones.remove(zone);
            this.refreshStatements();
        }
    }

    public List<MutexZone> getZones() {
        return this.zones;
    }

    public boolean hasZones() {
        return !this.zones.isEmpty();
    }

    public List<EnteredGroup> getEnteredGroups() {
        return this.entered;
    }

    public List<String> getStatements() {
        return this.statements;
    }

    private void refreshStatements() {
        this.statements = this.zones.stream().sorted((z0, z1) -> z0.signBlock.getPosition().compareTo(z1.signBlock.getPosition())).map(z -> z.statement).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public void onTick() {
        if (!this.entered.isEmpty()) {
            ListIterator<EnteredGroup> iter = this.entered.listIterator();
            boolean hasHardEnteredGroup = false;
            boolean trainsHaveLeft = false;
            while (iter.hasNext()) {
                EnteredGroup enteredGroup = iter.next();
                if (!enteredGroup.refresh(this, newGroup -> {
                    iter.set((EnteredGroup)newGroup);
                    this.swapEnteredGroup(enteredGroup, (EnteredGroup)newGroup);
                })) {
                    iter.remove();
                    this.swapDeactivatedEnteredGroups(enteredGroup, null);
                    trainsHaveLeft = true;
                    continue;
                }
                if (!enteredGroup.hardEnter) continue;
                hasHardEnteredGroup = true;
            }
            if (trainsHaveLeft && !hasHardEnteredGroup) {
                this.setLevers(false);
            }
        }
    }

    public void unload(MinecartGroup group) {
        ListIterator<EnteredGroup> iter = this.entered.listIterator();
        while (iter.hasNext()) {
            EnteredGroup entered = iter.next();
            if (!entered.isGroup(group)) continue;
            UnloadedEnteredGroup unloaded = entered.unload();
            if (entered == unloaded) break;
            iter.set(unloaded);
            this.swapDeactivatedEnteredGroups(entered, unloaded);
            break;
        }
    }

    public LoadedEnteredGroup findEntered(MinecartGroup group) {
        for (EnteredGroup entered : this.entered) {
            if (!(entered instanceof LoadedEnteredGroup) || !entered.isGroup(group)) continue;
            return (LoadedEnteredGroup)entered;
        }
        return null;
    }

    public LoadedEnteredGroup track(MinecartGroup group, double distanceToMutex) {
        int nowTicks = group.getObstacleTracker().getTickCounter();
        List<String> statements = this.getStatements();
        if (!statements.isEmpty()) {
            Block signBlock;
            SignActionEvent signEvent = null;
            if (this.zones.size() == 1 && (signBlock = this.zones.get(0).getSignBlock()) != null && ((Boolean)MaterialUtil.ISSIGN.get(signBlock)).booleanValue()) {
                RailLookup.TrackedSign trackedSign = RailLookup.TrackedSign.forRealSign(signBlock, this.zones.get(0).isSignFrontText(), null);
                signEvent = new SignActionEvent(trackedSign, group);
                signEvent.setAction(SignActionType.GROUP_ENTER);
            }
            if (!Statement.hasMultiple(group, this.getStatements(), signEvent)) {
                boolean wasGroupHardEntered = false;
                boolean hasHardEnteredGroup = false;
                Iterator<EnteredGroup> iter = this.entered.iterator();
                while (iter.hasNext()) {
                    EnteredGroup enteredGroup = iter.next();
                    if (enteredGroup.isGroup(group)) {
                        iter.remove();
                        this.swapDeactivatedEnteredGroups(enteredGroup, null);
                        wasGroupHardEntered = enteredGroup.hardEnter;
                        continue;
                    }
                    if (!enteredGroup.hardEnter) continue;
                    hasHardEnteredGroup = true;
                }
                if (wasGroupHardEntered && !hasHardEnteredGroup) {
                    this.setLevers(false);
                }
                return new IgnoredEnteredGroup(this, group, distanceToMutex, nowTicks);
            }
        }
        for (EnteredGroup enteredGroup : this.entered) {
            if (!enteredGroup.isGroup(group)) continue;
            LoadedEnteredGroup loadedEnteredGroup = enteredGroup.load(this, group);
            if (enteredGroup != loadedEnteredGroup) {
                this.swapEnteredGroup(enteredGroup, loadedEnteredGroup);
            }
            loadedEnteredGroup.deactivateByOtherGroups();
            loadedEnteredGroup.probeTick = nowTicks;
            if (loadedEnteredGroup.active) {
                loadedEnteredGroup.distanceToMutex = Math.min(loadedEnteredGroup.distanceToMutex, distanceToMutex);
            } else {
                loadedEnteredGroup.active = true;
                loadedEnteredGroup.distanceToMutex = distanceToMutex;
            }
            return loadedEnteredGroup;
        }
        LoadedEnteredGroup enteredGroup = new LoadedEnteredGroup(this, group, distanceToMutex, nowTicks, nowTicks);
        this.entered.add(enteredGroup);
        return enteredGroup;
    }

    private void setLevers(boolean down) {
        for (MutexZone zone : this.zones) {
            zone.setLevers(down);
        }
    }

    public List<MinecartGroup> getCurrentGroups() {
        if (this.entered.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<MinecartGroup> result = new ArrayList<MinecartGroup>(this.entered.size());
        for (EnteredGroup enteredGroup : this.entered) {
            if (!enteredGroup.active || !enteredGroup.hardEnter || !(enteredGroup instanceof LoadedEnteredGroup)) continue;
            result.add(((LoadedEnteredGroup)enteredGroup).group);
        }
        return result;
    }

    public List<MinecartGroup> getProspectiveGroups() {
        if (this.entered.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<MinecartGroup> result = new ArrayList<MinecartGroup>(this.entered.size());
        for (EnteredGroup enteredGroup : this.entered) {
            if (!enteredGroup.active || !(enteredGroup instanceof LoadedEnteredGroup)) continue;
            result.add(((LoadedEnteredGroup)enteredGroup).group);
        }
        return result;
    }

    private void swapEnteredGroup(EnteredGroup toReplace, EnteredGroup replacement) {
        MutexZoneSlot.swapEnteredGroup(this.entered, toReplace, replacement);
        this.swapDeactivatedEnteredGroups(toReplace, replacement);
    }

    private void swapDeactivatedEnteredGroups(EnteredGroup toReplace, EnteredGroup replacement) {
        for (EnteredGroup group : this.entered) {
            MutexZoneSlot.swapEnteredGroup(group.groupsDeactivatingMe, toReplace, replacement);
            MutexZoneSlot.swapEnteredGroup(group.otherGroupsToDeactivate, toReplace, replacement);
        }
    }

    private static void swapEnteredGroup(List<EnteredGroup> groups, EnteredGroup toReplace, EnteredGroup replacement) {
        int index = groups.indexOf(toReplace);
        if (index != -1) {
            if (replacement != null) {
                groups.set(index, replacement);
            } else {
                groups.remove(index);
            }
        }
    }

    public static abstract class EnteredGroup {
        public boolean hardEnter = false;
        public boolean active = true;
        public double distanceToMutex;
        protected final MutexRailSlotMap occupiedRails;
        protected final ArrayList<EnteredGroup> otherGroupsToDeactivate;
        protected final ArrayList<EnteredGroup> groupsDeactivatingMe;
        protected IntVector3 groupsDeactivatingMeConflictRail;

        public EnteredGroup(double distanceToMutex) {
            this.occupiedRails = new MutexRailSlotMap();
            this.distanceToMutex = distanceToMutex;
            this.otherGroupsToDeactivate = new ArrayList(2);
            this.groupsDeactivatingMe = new ArrayList(2);
            this.groupsDeactivatingMeConflictRail = null;
        }

        protected EnteredGroup(EnteredGroup copy) {
            this.hardEnter = copy.hardEnter;
            this.active = copy.active;
            this.distanceToMutex = copy.distanceToMutex;
            this.occupiedRails = copy.occupiedRails;
            this.otherGroupsToDeactivate = copy.otherGroupsToDeactivate;
            this.groupsDeactivatingMe = copy.groupsDeactivatingMe;
            this.groupsDeactivatingMeConflictRail = copy.groupsDeactivatingMeConflictRail;
        }

        public abstract boolean isGroup(MinecartGroup var1);

        public abstract String getTrainName();

        public abstract LoadedEnteredGroup load(MutexZoneSlot var1, MinecartGroup var2);

        public abstract UnloadedEnteredGroup unload();

        public abstract int age();

        protected abstract boolean containsVerify(IntVector3 var1);

        protected abstract boolean refresh(MutexZoneSlot var1, Consumer<EnteredGroup> var2);
    }

    public static class UnloadedEnteredGroup
    extends EnteredGroup {
        public final TrainProperties trainProperties;
        public final int creationServerTime;

        private UnloadedEnteredGroup(TrainProperties trainProperties, double distanceToMutex, int age) {
            super(distanceToMutex);
            this.trainProperties = trainProperties;
            this.creationServerTime = CommonUtil.getServerTicks() - age;
        }

        public UnloadedEnteredGroup(LoadedEnteredGroup loadedGroup) {
            super(loadedGroup);
            this.trainProperties = loadedGroup.group.getProperties();
            this.creationServerTime = CommonUtil.getServerTicks() - loadedGroup.age();
        }

        public void save(TrainCarts plugin, OfflineDataBlock root) {
            OfflineDataBlock enteredGroupData;
            try {
                enteredGroupData = root.addChild("entered-group", stream -> {
                    stream.writeUTF(this.getTrainName());
                    stream.writeDouble(this.distanceToMutex);
                    stream.writeInt(this.age());
                    Util.writeVariableLengthInt(stream, this.otherGroupsToDeactivate.size());
                    for (EnteredGroup otherGroup : this.otherGroupsToDeactivate) {
                        stream.writeUTF(otherGroup.getTrainName());
                    }
                    Util.writeVariableLengthInt(stream, this.groupsDeactivatingMe.size());
                    for (EnteredGroup otherGroup : this.groupsDeactivatingMe) {
                        stream.writeUTF(otherGroup.getTrainName());
                    }
                    stream.writeBoolean(this.groupsDeactivatingMeConflictRail != null);
                    if (this.groupsDeactivatingMeConflictRail != null) {
                        this.groupsDeactivatingMeConflictRail.write(stream);
                    }
                });
            }
            catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save mutex entered group data of train " + this.getTrainName(), t);
                return;
            }
            try {
                this.occupiedRails.save(enteredGroupData);
            }
            catch (Throwable t) {
                root.children.remove(root.children.size() - 1);
                plugin.getLogger().log(Level.SEVERE, "Failed to save mutex entered group rail slot data of train " + this.getTrainName(), t);
                return;
            }
        }

        private static UnloadedEnteredGroupData loadData(TrainCarts plugin, OfflineDataBlock enteredGroupData) throws IOException {
            UnloadedEnteredGroup group;
            List<String> groupsDeactivatingMeNames;
            List<String> otherGroupsToDeactivateNames;
            try (DataInputStream stream = enteredGroupData.readData();){
                TrainProperties trainProperties = TrainPropertiesStore.get(stream.readUTF());
                if (trainProperties == null) {
                    UnloadedEnteredGroupData unloadedEnteredGroupData = null;
                    return unloadedEnteredGroupData;
                }
                double distanceToMutex = stream.readDouble();
                int age = stream.readInt();
                otherGroupsToDeactivateNames = UnloadedEnteredGroup.readListOfStrings(stream);
                groupsDeactivatingMeNames = UnloadedEnteredGroup.readListOfStrings(stream);
                IntVector3 groupsDeactivatingMeConflictRail = null;
                if (stream.readBoolean()) {
                    groupsDeactivatingMeConflictRail = IntVector3.read((DataInputStream)stream);
                }
                group = new UnloadedEnteredGroup(trainProperties, distanceToMutex, age);
                group.groupsDeactivatingMeConflictRail = groupsDeactivatingMeConflictRail;
            }
            group.occupiedRails.load(enteredGroupData);
            return new UnloadedEnteredGroupData(group, otherGroupsToDeactivateNames, groupsDeactivatingMeNames);
        }

        public static List<UnloadedEnteredGroup> loadAll(TrainCarts plugin, OfflineDataBlock mutexZoneSlotData) {
            List<OfflineDataBlock> enteredGroupDataList = mutexZoneSlotData.findChildren("entered-group");
            if (enteredGroupDataList.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<UnloadedEnteredGroupData> unloadedGroupDataList = new ArrayList<UnloadedEnteredGroupData>(enteredGroupDataList.size());
            for (OfflineDataBlock enteredGroupData : enteredGroupDataList) {
                try {
                    UnloadedEnteredGroupData data = UnloadedEnteredGroup.loadData(plugin, enteredGroupData);
                    if (data == null) continue;
                    unloadedGroupDataList.add(data);
                }
                catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load mutex entered group data", t);
                }
            }
            ArrayList<UnloadedEnteredGroup> unloadedGroups = new ArrayList<UnloadedEnteredGroup>(unloadedGroupDataList.size());
            for (UnloadedEnteredGroupData unloadedGroupData : unloadedGroupDataList) {
                unloadedGroups.add(unloadedGroupData.load(unloadedGroupDataList));
            }
            return Collections.unmodifiableList(unloadedGroups);
        }

        private static List<String> readListOfStrings(DataInputStream stream) throws IOException {
            int count = Util.readVariableLengthInt(stream);
            if (count <= 0) {
                return Collections.emptyList();
            }
            ArrayList<String> result = new ArrayList<String>(count);
            for (int i = 0; i < count; ++i) {
                result.add(stream.readUTF());
            }
            return Collections.unmodifiableList(result);
        }

        @Override
        public boolean isGroup(MinecartGroup group) {
            return group.getProperties() == this.trainProperties;
        }

        @Override
        public String getTrainName() {
            return this.trainProperties.getTrainName();
        }

        @Override
        public LoadedEnteredGroup load(MutexZoneSlot slot, MinecartGroup group) {
            return new LoadedEnteredGroup(slot, group, this);
        }

        @Override
        public UnloadedEnteredGroup unload() {
            return this;
        }

        @Override
        public int age() {
            return CommonUtil.getServerTicks() - this.creationServerTime;
        }

        @Override
        protected boolean containsVerify(IntVector3 rail) {
            return this.occupiedRails.isFullyLocked() || this.occupiedRails.isSmartLocked(rail);
        }

        @Override
        protected boolean refresh(MutexZoneSlot slot, Consumer<EnteredGroup> swap) {
            if (this.trainProperties.isRemoved()) {
                return false;
            }
            MinecartGroup group = this.trainProperties.getHolder();
            if (group != null) {
                swap.accept(this.load(slot, group));
            }
            return true;
        }
    }

    public static class LoadedEnteredGroup
    extends EnteredGroup {
        private final MutexZoneSlot slot;
        public final MinecartGroup group;
        protected final int creationTick;
        protected int probeTick;
        public int occupiedTick;
        private MutexZoneConflictEvent conflict = null;

        public LoadedEnteredGroup(MutexZoneSlot slot, MinecartGroup group, double distanceToMutex, int creationTick, int nowTicks) {
            super(distanceToMutex);
            this.slot = slot;
            this.group = group;
            this.creationTick = creationTick;
            this.probeTick = nowTicks;
            this.occupiedTick = nowTicks;
        }

        public LoadedEnteredGroup(MutexZoneSlot slot, MinecartGroup group, UnloadedEnteredGroup unloadedGroup) {
            super(unloadedGroup);
            int nowTicks = group.getObstacleTracker().getTickCounter();
            this.slot = slot;
            this.group = group;
            this.creationTick = nowTicks - unloadedGroup.age();
            this.probeTick = nowTicks;
            this.occupiedTick = nowTicks;
            this.occupiedRails.keepAlive(nowTicks);
        }

        @Override
        public boolean isGroup(MinecartGroup group) {
            return this.group == group;
        }

        @Override
        public String getTrainName() {
            return this.group.getProperties().getTrainName();
        }

        @Override
        public LoadedEnteredGroup load(MutexZoneSlot slot, MinecartGroup group) {
            return this;
        }

        @Override
        public UnloadedEnteredGroup unload() {
            return new UnloadedEnteredGroup(this);
        }

        @Override
        public int age() {
            return this.getObstacleTickCounter() - this.creationTick;
        }

        public int serverTickLastProbed() {
            return CommonUtil.getServerTicks() + this.probeTick - this.getObstacleTickCounter();
        }

        public boolean isOccupiedFully() {
            return this.occupiedRails.isFullyLocked();
        }

        public List<MutexRailSlot> getLastPath() {
            return this.occupiedRails.getLastPath();
        }

        public MutexZoneConflictEvent getConflict() {
            return this.conflict;
        }

        public EnterResult enter(MutexZoneSlotType type, IntVector3 railBlock, boolean hard) {
            EnterResult successResult = EnterResult.SUCCESS;
            if (this.wasOccupiedLastTick()) {
                successResult = this.conflict != null ? EnterResult.CONFLICT_ONGOING : EnterResult.OCCUPIED_DISCOVER;
            }
            boolean wasFullyLocked = this.occupiedRails.isFullyLocked();
            boolean addedNewSlot = this.occupiedRails.add(type, railBlock, this.probeTick);
            if (wasFullyLocked && hard == this.hardEnter && this.conflict == null) {
                return successResult;
            }
            if (type == MutexZoneSlotType.SMART && !addedNewSlot && hard == this.hardEnter && this.conflict == null) {
                return successResult;
            }
            for (EnteredGroup enteredGroup : this.slot.entered) {
                if (enteredGroup == this) continue;
                if (!enteredGroup.active) {
                    if (enteredGroup.age() <= this.age() || this.slot.tickLastHardEntered >= this.serverTickLastProbed() + 5 || this.creationTick != this.probeTick && !this.wasOccupiedLastTick() || !enteredGroup.containsVerify(railBlock)) continue;
                    this.hardEnter = false;
                    this.deactivate(railBlock);
                    return EnterResult.OCCUPIED;
                }
                if (!enteredGroup.containsVerify(railBlock)) continue;
                if (hard) {
                    boolean hadConflict;
                    if (!enteredGroup.hardEnter && this.age() > enteredGroup.age()) {
                        this.deactivateOtherGroup(enteredGroup, railBlock);
                        continue;
                    }
                    boolean bl = hadConflict = this.conflict != null;
                    if (hadConflict || this.creationTick == this.probeTick || !this.wasOccupiedLastTick()) {
                        if (enteredGroup instanceof LoadedEnteredGroup) {
                            this.conflict = new MutexZoneConflictEvent(this.group, ((LoadedEnteredGroup)enteredGroup).group, this.slot, railBlock);
                            this.occupiedTick = this.probeTick;
                            return hadConflict ? EnterResult.CONFLICT_ONGOING : EnterResult.CONFLICT;
                        }
                        return hadConflict ? EnterResult.CONFLICT_ONGOING : EnterResult.OCCUPIED;
                    }
                }
                this.hardEnter = false;
                this.deactivate(railBlock);
                return EnterResult.OCCUPIED;
            }
            if (hard && successResult == EnterResult.SUCCESS && !this.hardEnter) {
                this.hardEnter = true;
                this.slot.tickLastHardEntered = CommonUtil.getServerTicks();
                this.slot.setLevers(true);
            }
            if (successResult == EnterResult.SUCCESS && this.conflict != null) {
                this.conflict = null;
                this.occupiedRails.clearOldRails(this.probeTick);
            }
            return successResult;
        }

        private boolean wasOccupiedLastTick() {
            return this.probeTick - this.occupiedTick <= 1;
        }

        private void deactivate(IntVector3 conflictRail) {
            this.active = false;
            this.occupiedRails.clearConflict(conflictRail);
            this.occupiedTick = this.probeTick;
            if (!this.otherGroupsToDeactivate.isEmpty()) {
                for (EnteredGroup group : this.otherGroupsToDeactivate) {
                    group.groupsDeactivatingMe.remove(this);
                    if (!group.groupsDeactivatingMe.isEmpty()) continue;
                    group.groupsDeactivatingMeConflictRail = null;
                }
                this.otherGroupsToDeactivate.clear();
            }
        }

        private void deactivateByOtherGroups() {
            if (!this.groupsDeactivatingMe.isEmpty()) {
                for (EnteredGroup g : this.groupsDeactivatingMe) {
                    g.otherGroupsToDeactivate.remove(this);
                }
                this.groupsDeactivatingMe.clear();
                this.deactivate(this.groupsDeactivatingMeConflictRail);
                this.groupsDeactivatingMeConflictRail = null;
            }
        }

        private void deactivateOtherGroup(EnteredGroup otherGroup, IntVector3 conflictRail) {
            if (!this.otherGroupsToDeactivate.contains(otherGroup)) {
                this.otherGroupsToDeactivate.add(otherGroup);
                otherGroup.groupsDeactivatingMe.add(this);
                otherGroup.groupsDeactivatingMeConflictRail = conflictRail;
            }
        }

        @Override
        protected boolean containsVerify(IntVector3 rail) {
            int nowTicks = this.probeTick;
            return this.occupiedRails.isFullyLockedVerify(this.group, nowTicks) || this.occupiedRails.isSmartLockedVerify(this.group, nowTicks, rail);
        }

        @Override
        protected boolean refresh(MutexZoneSlot slot, Consumer<EnteredGroup> swap) {
            if (this.group.isUnloaded() || !MinecartGroupStore.getGroups().contains((Object)this.group)) {
                return false;
            }
            int nowTicks = this.getObstacleTickCounter();
            if (nowTicks - this.probeTick < 6) {
                return true;
            }
            if (!this.occupiedRails.verifyHasRailsUsedByGroup(this.group)) {
                return false;
            }
            this.probeTick = nowTicks;
            return true;
        }

        private int getObstacleTickCounter() {
            return this.group.getObstacleTracker().getTickCounter();
        }
    }

    private final class IgnoredEnteredGroup
    extends LoadedEnteredGroup {
        public IgnoredEnteredGroup(MutexZoneSlot slot, MinecartGroup group, double distanceToMutex, int nowTicks) {
            super(slot, group, distanceToMutex, nowTicks, nowTicks);
        }

        @Override
        public EnterResult enter(MutexZoneSlotType type, IntVector3 railBlock, boolean hard) {
            return EnterResult.IGNORED;
        }
    }

    private static class UnloadedEnteredGroupData {
        public final UnloadedEnteredGroup group;
        public final List<String> otherGroupsToDeactivateNames;
        public final List<String> groupsDeactivatingMeNames;

        public UnloadedEnteredGroupData(UnloadedEnteredGroup group, List<String> otherGroupsToDeactivateNames, List<String> groupsDeactivatingMeNames) {
            this.group = group;
            this.otherGroupsToDeactivateNames = otherGroupsToDeactivateNames;
            this.groupsDeactivatingMeNames = groupsDeactivatingMeNames;
        }

        public UnloadedEnteredGroup load(List<UnloadedEnteredGroupData> otherEnteredGroups) {
            UnloadedEnteredGroupData.loadEnteredGroupList(this.otherGroupsToDeactivateNames, otherEnteredGroups, this.group.otherGroupsToDeactivate);
            UnloadedEnteredGroupData.loadEnteredGroupList(this.groupsDeactivatingMeNames, otherEnteredGroups, this.group.groupsDeactivatingMe);
            return this.group;
        }

        private static void loadEnteredGroupList(List<String> groupNames, List<UnloadedEnteredGroupData> otherEnteredGroups, List<EnteredGroup> groupsTarget) {
            groupsTarget.clear();
            block0: for (String name : groupNames) {
                for (UnloadedEnteredGroupData data : otherEnteredGroups) {
                    if (!name.equals(data.group.getTrainName())) continue;
                    groupsTarget.add(data.group);
                    continue block0;
                }
            }
        }
    }

    public static enum EnterResult {
        IGNORED(false, false),
        SUCCESS(false, false),
        CONFLICT(false, true),
        CONFLICT_ONGOING(false, true),
        OCCUPIED(true, false),
        OCCUPIED_DISCOVER(true, false);

        private final boolean occupied;
        private final boolean conflict;

        private EnterResult(boolean occupied, boolean conflict) {
            this.occupied = occupied;
            this.conflict = conflict;
        }

        public boolean isOccupied() {
            return this.occupied;
        }

        public boolean isConflict() {
            return this.conflict;
        }
    }
}

