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
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.bukkit.event.Event;

class SignActionLookupMapImpl
implements SignActionLookupMap {
    private final PriorityEntryList allEntries = new PriorityEntryList();
    private final NavigableMap<String, List<TrainCartsEntry>> traincartsEntries = new TreeMap<String, List<TrainCartsEntry>>();
    private final PriorityEntryList nonTrainCartsEntries = new PriorityEntryList();

    SignActionLookupMapImpl() {
    }

    @Override
    public Optional<SignActionLookupMap.Entry> lookup(SignActionEvent event, SignActionLookupMap.LookupMode lookupMode) {
        if (!event.getHeader().isValid()) {
            return this.lookupNonTrainCarts(event, lookupMode);
        }
        String signIdentifier = event.getLowerCaseSecondCleanedLine();
        if (signIdentifier.isEmpty()) {
            return this.lookupNonTrainCarts(event, lookupMode);
        }
        ArrayList allTypeMatchingEntries = Collections.emptyList();
        boolean allTypeMatchingEntriesModifiable = false;
        Set orderedMatchingEntries = this.traincartsEntries.headMap(signIdentifier, true).descendingMap().entrySet();
        for (Map.Entry e : orderedMatchingEntries) {
            if (!signIdentifier.startsWith((String)e.getKey())) break;
            if (allTypeMatchingEntries.isEmpty()) {
                allTypeMatchingEntries = (ArrayList)e.getValue();
                continue;
            }
            if (!allTypeMatchingEntriesModifiable) {
                allTypeMatchingEntriesModifiable = true;
                allTypeMatchingEntries = new ArrayList(allTypeMatchingEntries);
            }
            allTypeMatchingEntries.addAll((Collection)e.getValue());
            Collections.sort(allTypeMatchingEntries);
        }
        if (allTypeMatchingEntries.isEmpty()) {
            return this.lookupNonTrainCarts(event, lookupMode);
        }
        int prevNonTrainCartsIndex = 0;
        for (TrainCartsEntry tcEntry : allTypeMatchingEntries) {
            int nextNonTrainCartsIndex = tcEntry.getFirstIndexAfterOrder(this.nonTrainCartsEntries);
            if (nextNonTrainCartsIndex > prevNonTrainCartsIndex) {
                Optional<SignActionLookupMap.Entry> nonTCEntry = this.lookupNonTrainCartsRange(event, lookupMode, prevNonTrainCartsIndex, nextNonTrainCartsIndex);
                if (nonTCEntry.isPresent()) {
                    return nonTCEntry;
                }
                prevNonTrainCartsIndex = nextNonTrainCartsIndex;
            }
            SignAction tcAction = tcEntry.action;
            if (!lookupMode.test(tcEntry) || !tcAction.verify(event)) continue;
            return Optional.of(tcEntry);
        }
        return this.lookupNonTrainCartsRange(event, lookupMode, prevNonTrainCartsIndex, this.nonTrainCartsEntries.size());
    }

    private Optional<SignActionLookupMap.Entry> lookupNonTrainCartsRange(SignActionEvent event, SignActionLookupMap.LookupMode lookupMode, int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; ++i) {
            EntryImpl nonTCEntry = this.nonTrainCartsEntries.getAt(i);
            SignAction nonTCAction = nonTCEntry.action;
            if (!lookupMode.test(nonTCEntry) || !nonTCAction.match(event) || !nonTCAction.verify(event)) continue;
            return Optional.of(nonTCEntry);
        }
        return Optional.empty();
    }

    private Optional<SignActionLookupMap.Entry> lookupNonTrainCarts(SignActionEvent event, SignActionLookupMap.LookupMode lookupMode) {
        for (EntryImpl e : this.nonTrainCartsEntries) {
            SignAction action = e.action;
            if (!lookupMode.test(e) || !action.match(event) || !action.verify(event)) continue;
            return Optional.of(e);
        }
        return Optional.empty();
    }

    @Override
    public <T extends SignAction> T register(T action, boolean priority) {
        if (action == null) {
            throw new IllegalArgumentException("SignAction is null");
        }
        if (action instanceof TrainCartsSignAction) {
            TrainCartsEntry entry = new TrainCartsEntry((TrainCartsSignAction)action, priority);
            this.allEntries.add(entry);
            this.allEntries.refreshEntryOrder();
            for (String typeIdentifier : entry.typeIdentifiers) {
                this.traincartsEntries.compute(typeIdentifier, (key, list) -> {
                    if (list == null) {
                        return Collections.singletonList(entry);
                    }
                    if (list.size() == 1) {
                        ArrayList<TrainCartsEntry> newEntries = new ArrayList<TrainCartsEntry>(2);
                        newEntries.addAll((Collection<TrainCartsEntry>)list);
                        newEntries.add(entry);
                        Collections.sort(newEntries);
                        return newEntries;
                    }
                    list.add(entry);
                    Collections.sort(list);
                    return list;
                });
            }
        } else {
            EntryImpl entry = new EntryImpl(action, priority);
            this.allEntries.add(entry);
            this.allEntries.refreshEntryOrder();
            this.nonTrainCartsEntries.add(entry);
        }
        if (!Common.IS_TEST_MODE) {
            CommonUtil.callEvent((Event)new SignActionRegisterEvent(action, priority));
        }
        return action;
    }

    @Override
    public void unregister(SignAction action) {
        EntryImpl e = this.allEntries.remove(action);
        if (e == null) {
            return;
        }
        this.allEntries.refreshEntryOrder();
        if (e instanceof TrainCartsEntry) {
            TrainCartsEntry tcEntry = (TrainCartsEntry)e;
            for (String typeIdentifier : tcEntry.typeIdentifiers) {
                this.traincartsEntries.computeIfPresent(typeIdentifier, (key, list) -> {
                    if (list.size() == 1) {
                        return list.get(0) == tcEntry ? null : list;
                    }
                    list.remove(tcEntry);
                    return list;
                });
            }
        } else {
            this.nonTrainCartsEntries.remove(e);
        }
        if (!Common.IS_TEST_MODE) {
            CommonUtil.callEvent((Event)new SignActionUnregisterEvent(action));
        }
    }

    private static class PriorityEntryList
    implements Iterable<EntryImpl> {
        private final List<EntryImpl> entries = new ArrayList<EntryImpl>();

        private PriorityEntryList() {
        }

        @Override
        public Iterator<EntryImpl> iterator() {
            return this.entries.iterator();
        }

        public EntryImpl getAt(int index) {
            return this.entries.get(index);
        }

        public int size() {
            return this.entries.size();
        }

        public int getFirstIndexAfterOrder(int orderIndex) {
            int size = this.entries.size();
            for (int i = 0; i < size; ++i) {
                EntryImpl e = this.entries.get(i);
                if (e.orderIndex <= orderIndex) continue;
                return i;
            }
            return size;
        }

        public void add(EntryImpl entry) {
            if (entry.priority) {
                this.entries.add(0, entry);
            } else {
                this.entries.add(entry);
            }
        }

        public EntryImpl remove(SignAction action) {
            int size = this.entries.size();
            for (int i = 0; i < size; ++i) {
                EntryImpl e = this.entries.get(i);
                if (!e.action.equals(action)) continue;
                this.entries.remove(i);
                return e;
            }
            return null;
        }

        public void remove(EntryImpl entry) {
            int index;
            int n = index = entry.priority ? this.entries.indexOf(entry) : this.entries.lastIndexOf(entry);
            if (index != -1) {
                this.entries.remove(index);
            }
        }

        public void refreshEntryOrder() {
            int size = this.entries.size();
            for (int i = 0; i < size; ++i) {
                this.entries.get(i).onOrderUpdated(i);
            }
        }
    }

    private static class TrainCartsEntry
    extends EntryImpl {
        public final List<String> typeIdentifiers;
        private int nonTrainCartsAfterEntryIndex = -1;

        public TrainCartsEntry(TrainCartsSignAction action, boolean priority) {
            super(action, priority);
            this.typeIdentifiers = action.getTypeIdentifiers();
        }

        public int getFirstIndexAfterOrder(PriorityEntryList nonTrainCartsEntries) {
            int index = this.nonTrainCartsAfterEntryIndex;
            if (index == -1) {
                this.nonTrainCartsAfterEntryIndex = index = nonTrainCartsEntries.getFirstIndexAfterOrder(this.orderIndex);
            }
            return index;
        }

        @Override
        public void onOrderUpdated(int orderIndex) {
            super.onOrderUpdated(orderIndex);
            this.nonTrainCartsAfterEntryIndex = -1;
        }
    }

    private static class EntryImpl
    implements SignActionLookupMap.Entry,
    Comparable<EntryImpl> {
        public final SignAction action;
        public final boolean priority;
        public final boolean hasLoadedChangedHandler;
        public int orderIndex = -1;

        public EntryImpl(SignAction action, boolean priority) {
            this.action = action;
            this.priority = priority;
            this.hasLoadedChangedHandler = CommonUtil.isMethodOverrided(SignAction.class, action.getClass(), (String)"loadedChanged", (Class[])new Class[]{SignActionEvent.class, Boolean.TYPE});
        }

        public void onOrderUpdated(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        @Override
        public SignAction action() {
            return this.action;
        }

        @Override
        public boolean hasLoadedChangedHandler() {
            return this.hasLoadedChangedHandler;
        }

        @Override
        public int compareTo(EntryImpl entry) {
            return Integer.compare(this.orderIndex, entry.orderIndex);
        }
    }
}

