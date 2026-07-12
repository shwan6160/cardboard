/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 */
package com.bergerkiller.bukkit.tc.actions.registry;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.GroupActionSizzle;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitDelay;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitStationRouting;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitTicks;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitTill;
import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunch;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchDirection;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchLocation;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitDistance;
import com.bergerkiller.bukkit.tc.actions.MemberActionWaitLocation;
import com.bergerkiller.bukkit.tc.actions.TrackedSignActionSetOutput;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.controller.components.ActionTrackerMember;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Level;

public class ActionRegistry {
    private final TrainCarts plugin;
    private final Map<String, RegisteredAction> byId = new HashMap<String, RegisteredAction>();
    private final WeakHashMap<Class<?>, RegisteredAction> byType = new WeakHashMap();

    public ActionRegistry(TrainCarts plugin) {
        this.plugin = plugin;
        this.registerTrainCartsActions();
    }

    private void registerTrainCartsActions() {
        this.register(MemberActionLaunch.class, new MemberActionLaunch.Serializer());
        this.register(MemberActionLaunchDirection.class, new MemberActionLaunchDirection.Serializer());
        this.register(MemberActionLaunchLocation.class, new MemberActionLaunchLocation.Serializer());
        this.register(MemberActionWaitDistance.class, new MemberActionWaitDistance.Serializer());
        this.register(MemberActionWaitLocation.class, new MemberActionWaitLocation.Serializer());
        this.register(GroupActionWaitForever.class, new GroupActionWaitForever.Serializer());
        this.register(GroupActionWaitTill.class, new GroupActionWaitTill.Serializer());
        this.register(GroupActionWaitTicks.class, new GroupActionWaitTicks.Serializer());
        this.register(GroupActionWaitDelay.class, new GroupActionWaitDelay.Serializer());
        this.register(TrackedSignActionSetOutput.class, new TrackedSignActionSetOutput.Serializer(this.plugin));
        this.register(GroupActionSizzle.class, new GroupActionSizzle.Serializer());
        this.register(GroupActionWaitStationRouting.class, new GroupActionWaitStationRouting.Serializer(this.plugin));
    }

    public <T extends Action> void register(Class<T> type, Serializer<T> serializer) {
        this.register(type.getName(), type, serializer);
    }

    public <T extends Action> void register(String id, Class<T> type, Serializer<T> serializer) {
        RegisteredAction registered = new RegisteredAction(id, type, serializer);
        this.byId.put(id, registered);
        this.byType.put(type, registered);
    }

    public void unregister(String id) {
        this.byId.remove(id);
    }

    public List<OfflineDataBlock> saveTracker(ActionTracker tracker) {
        if (tracker.hasAction()) {
            OfflineDataBlock root = OfflineDataBlock.create("root");
            for (Action action : tracker.getScheduledActions()) {
                this.saveAction(root, action, tracker);
            }
            return Collections.unmodifiableList(root.children);
        }
        return Collections.emptyList();
    }

    public void loadTracker(ActionTracker tracker, List<OfflineDataBlock> actionDataBlocks) {
        if (!actionDataBlocks.isEmpty()) {
            MinecartGroup group = tracker.getGroupOwner();
            for (OfflineDataBlock actionDataBlock : actionDataBlocks) {
                Action action = this.loadAction(group, actionDataBlock, tracker);
                if (action == null) continue;
                tracker.addAction(action);
            }
        }
    }

    public OfflineDataBlock saveAction(OfflineDataBlock root, Action action, ActionTracker tracker) {
        OfflineDataBlock child;
        RegisteredAction registeredAction = this.byType.get(action.getClass());
        if (registeredAction == null) {
            return null;
        }
        boolean addedToMember = tracker instanceof ActionTrackerMember;
        try {
            child = root.addChild("action", stream -> {
                MinecartMember<?> member;
                stream.writeUTF(registeredAction.id);
                int elapsedTicks = action.elapsedTicks();
                stream.writeInt(elapsedTicks);
                if (elapsedTicks > 0) {
                    stream.writeLong(action.elapsedTimeMillis());
                }
                Set<String> tags = action.getTags();
                Util.writeVariableLengthInt(stream, tags.size());
                for (String tag : tags) {
                    stream.writeUTF(tag);
                }
                if (!addedToMember && action instanceof MemberAction && (member = ((MemberAction)action).getMember()) != null) {
                    stream.writeBoolean(true);
                    StreamUtil.writeUUID((DataOutputStream)stream, (UUID)((CommonMinecart)member.getEntity()).getUniqueId());
                } else {
                    stream.writeBoolean(false);
                }
            });
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save action " + action.getClass().getName(), t);
            return null;
        }
        boolean success = false;
        try {
            success = registeredAction.serializer.save(action, child, tracker);
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save action " + action.getClass().getName(), t);
        }
        if (success) {
            return child;
        }
        root.children.remove(child);
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Action loadAction(MinecartGroup group, OfflineDataBlock dataBlock, ActionTracker tracker) {
        RegisteredAction registeredAction = null;
        try (DataInputStream stream = dataBlock.readData();){
            Object object;
            MinecartMember<?> member;
            List<String> tags;
            registeredAction = this.byId.get(stream.readUTF());
            if (registeredAction == null) {
                Action action = null;
                return action;
            }
            int elapsedTicks = stream.readInt();
            long elapsedTimeMillis = elapsedTicks > 0 ? stream.readLong() : 0L;
            int numTags = Util.readVariableLengthInt(stream);
            if (numTags > 0) {
                tags = new ArrayList<String>(numTags);
                for (int i = 0; i < numTags; ++i) {
                    tags.add(stream.readUTF());
                }
            } else {
                tags = Collections.emptyList();
            }
            if (stream.readBoolean()) {
                UUID memberUUID = StreamUtil.readUUID((DataInputStream)stream);
                MinecartMember<?> memberFound = null;
                for (MinecartMember<?> groupMember : group) {
                    if (!((CommonMinecart)groupMember.getEntity()).getUniqueId().equals(memberUUID)) continue;
                    memberFound = groupMember;
                    break;
                }
                if (memberFound == null) {
                    Iterator<MinecartMember<?>> iterator = null;
                    return iterator;
                }
                member = memberFound;
            } else {
                member = null;
            }
            Action action = registeredAction.serializer.load(dataBlock, tracker);
            if (action == null) {
                object = null;
                return object;
            }
            Action.loadElapsedTime(action, elapsedTicks, elapsedTimeMillis);
            for (String tag : tags) {
                action.addTag(tag);
            }
            if (member != null && action instanceof MemberAction) {
                ((MemberAction)action).setMember(member);
            }
            object = action;
            return object;
        }
        catch (Throwable t) {
            if (registeredAction != null) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to load action " + registeredAction.type.getName(), t);
                return null;
            }
            this.plugin.getLogger().log(Level.SEVERE, "Failed to load corrupted action", t);
            return null;
        }
    }

    public static interface Serializer<T extends Action> {
        public boolean save(T var1, OfflineDataBlock var2, ActionTracker var3) throws IOException;

        public T load(OfflineDataBlock var1, ActionTracker var2) throws IOException;
    }

    private static final class RegisteredAction {
        public final String id;
        public final Class<?> type;
        public final Serializer<Action> serializer;

        public <T extends Action> RegisteredAction(String id, Class<T> type, Serializer<T> serializer) {
            this.id = id;
            this.type = type;
            this.serializer = serializer;
        }
    }
}

