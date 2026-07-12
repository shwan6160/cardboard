/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.event.Event
 */
package com.bergerkiller.bukkit.tc.signactions.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.signactions.SignActionRegisterEvent;
import com.bergerkiller.bukkit.tc.events.signactions.SignActionUnregisterEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.bukkit.event.Event;

class SignActionLookupMapBasicImpl
implements SignActionLookupMap {
    private final List<SimpleEntry> entries = new ArrayList<SimpleEntry>();

    SignActionLookupMapBasicImpl() {
    }

    @Override
    public Optional<SignActionLookupMap.Entry> lookup(SignActionEvent event, SignActionLookupMap.LookupMode lookupMode) {
        for (SimpleEntry entry : this.entries) {
            SignAction action = entry.action;
            if (!lookupMode.test(entry) || !action.match(event) || !action.verify(event)) continue;
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    @Override
    public <T extends SignAction> T register(T action, boolean priority) {
        if (priority) {
            this.entries.add(0, new SimpleEntry(action));
        } else {
            this.entries.add(new SimpleEntry(action));
        }
        if (!Common.IS_TEST_MODE) {
            CommonUtil.callEvent((Event)new SignActionRegisterEvent(action, priority));
        }
        return action;
    }

    @Override
    public void unregister(SignAction action) {
        Iterator<SimpleEntry> iter = this.entries.iterator();
        while (iter.hasNext()) {
            SimpleEntry entry = iter.next();
            if (!entry.action.equals(action)) continue;
            iter.remove();
            if (!Common.IS_TEST_MODE) {
                CommonUtil.callEvent((Event)new SignActionUnregisterEvent(action));
            }
            return;
        }
    }

    private static class SimpleEntry
    implements SignActionLookupMap.Entry {
        public final SignAction action;
        public final boolean hasLoadedChangedHandler;

        public SimpleEntry(SignAction action) {
            this.action = action;
            this.hasLoadedChangedHandler = CommonUtil.isMethodOverrided(SignAction.class, action.getClass(), (String)"loadedChanged", (Class[])new Class[]{SignActionEvent.class, Boolean.TYPE});
        }

        @Override
        public SignAction action() {
            return this.action;
        }

        @Override
        public boolean hasLoadedChangedHandler() {
            return this.hasLoadedChangedHandler;
        }
    }
}

