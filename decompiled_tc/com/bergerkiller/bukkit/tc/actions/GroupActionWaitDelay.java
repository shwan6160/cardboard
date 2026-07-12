/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.actions.GroupActionWaitTill;
import com.bergerkiller.bukkit.tc.actions.WaitAction;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.DataInputStream;
import java.io.IOException;

public class GroupActionWaitDelay
extends GroupActionWaitTill
implements WaitAction {
    private long delay;

    public GroupActionWaitDelay(long delayMS) {
        super(System.currentTimeMillis() + delayMS);
        this.delay = delayMS;
    }

    public long getRemainingDelay() {
        if (this.hasActionStarted()) {
            return Math.max(0L, this.getTime() - System.currentTimeMillis());
        }
        return this.delay;
    }

    @Override
    public void start() {
        this.setTime(System.currentTimeMillis() + this.delay);
    }

    public static class Serializer
    implements ActionRegistry.Serializer<GroupActionWaitDelay> {
        @Override
        public boolean save(GroupActionWaitDelay action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            data.addChild("wait-delay", stream -> stream.writeLong(action.getRemainingDelay()));
            return true;
        }

        @Override
        public GroupActionWaitDelay load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            long delay;
            try (DataInputStream stream = data.findChildOrThrow("wait-delay").readData();){
                delay = stream.readLong();
            }
            return new GroupActionWaitDelay(delay);
        }
    }
}

