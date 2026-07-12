/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.actions.GroupAction;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.IOException;

public class GroupActionSizzle
extends GroupAction {
    @Override
    public void start() {
        for (int i = 0; i < this.getGroup().size(); ++i) {
            int j = i * 3;
            if (j >= this.getGroup().size()) continue;
            ((MinecartMember)this.getGroup().get(j)).playLinkEffect(false);
        }
    }

    public static class Serializer
    implements ActionRegistry.Serializer<GroupActionSizzle> {
        @Override
        public boolean save(GroupActionSizzle action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            return true;
        }

        @Override
        public GroupActionSizzle load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            return new GroupActionSizzle();
        }
    }
}

