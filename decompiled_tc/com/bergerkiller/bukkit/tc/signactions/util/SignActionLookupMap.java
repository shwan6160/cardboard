/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.signactions.util;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMapBasicImpl;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMapImpl;
import java.util.Optional;
import java.util.function.Predicate;

public interface SignActionLookupMap {
    public static final SignActionLookupMap DISABLED = new SignActionLookupMap(){

        @Override
        public Optional<Entry> lookup(SignActionEvent event, LookupMode lookupMode) {
            return Optional.empty();
        }

        @Override
        public <T extends SignAction> T register(T action, boolean priority) {
            return action;
        }

        @Override
        public void unregister(SignAction action) {
        }
    };

    public static SignActionLookupMap create() {
        return new SignActionLookupMapImpl();
    }

    public static SignActionLookupMap createBasicUnoptimized() {
        return new SignActionLookupMapBasicImpl();
    }

    default public Optional<Entry> lookup(SignActionEvent event) {
        return this.lookup(event, LookupMode.ALL);
    }

    public Optional<Entry> lookup(SignActionEvent var1, LookupMode var2);

    default public <T extends SignAction> T register(T action) {
        return this.register(action, false);
    }

    public <T extends SignAction> T register(T var1, boolean var2);

    public void unregister(SignAction var1);

    public static enum LookupMode implements Predicate<Entry>
    {
        ALL{

            @Override
            public boolean test(Entry e) {
                return true;
            }
        }
        ,
        WITH_LOADED_CHANGED_HANDLER{

            @Override
            public boolean test(Entry e) {
                return e.hasLoadedChangedHandler();
            }
        };

    }

    public static interface Entry {
        public SignAction action();

        public boolean hasLoadedChangedHandler();
    }
}

