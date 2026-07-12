/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelection;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AttachmentNameLookup {
    public static final AttachmentNameLookup EMPTY = new AttachmentNameLookup(Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
    private final List<Attachment> all;
    private final List<Attachment> parents;
    private final Map<String, List<Attachment>> byName;
    private final List<String> names;
    private boolean valid = true;

    private AttachmentNameLookup(AttachmentNameLookup original) {
        this.all = original.all;
        this.parents = original.parents;
        this.byName = original.byName;
        this.names = original.names;
        this.valid = original.valid;
    }

    private AttachmentNameLookup(List<Attachment> all, List<Attachment> parents, Map<String, List<Attachment>> byName) {
        this.all = all;
        this.parents = parents;
        this.byName = byName;
        this.names = byName.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<String>(byName.keySet()));
    }

    private static void makeListsImmutable(Map<String, List<Attachment>> attachments) {
        for (Map.Entry<String, List<Attachment>> e : attachments.entrySet()) {
            e.setValue(Collections.unmodifiableList(e.getValue()));
        }
    }

    private static void fill(List<Attachment> all, Map<String, List<Attachment>> attachments, Attachment attachment) {
        for (String name : attachment.getNames()) {
            attachments.computeIfAbsent(name, n -> new ArrayList(4)).add(attachment);
        }
        all.add(attachment);
        for (Attachment child : attachment.getChildren()) {
            AttachmentNameLookup.fill(all, attachments, child);
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public void invalidate() {
        this.valid = false;
    }

    public List<String> names() {
        return this.names;
    }

    public List<String> names(Predicate<Attachment> filter) {
        return (List)this.byName.entrySet().stream().filter(e -> AttachmentNameLookup.containsMatching((List)e.getValue(), filter)).map(Map.Entry::getKey).collect(StreamUtil.toUnmodifiableList());
    }

    public List<Attachment> get(String name) {
        return this.byName.getOrDefault(name, Collections.emptyList());
    }

    public <T extends Attachment> List<T> getOfType(String name, Class<T> type) {
        return this.get(name, type::isInstance);
    }

    public List<Attachment> get(String name, Predicate<Attachment> filter) {
        return Util.filterList(this.get(name), filter);
    }

    public <T extends Attachment> List<T> allOfType(Class<T> type) {
        return this.all(type::isInstance);
    }

    public List<Attachment> all(Predicate<Attachment> filter) {
        return Util.filterList(this.all, filter);
    }

    public List<Attachment> all() {
        return this.all;
    }

    public List<Attachment> parents() {
        return this.parents;
    }

    public List<Attachment> parents(Predicate<Attachment> filter) {
        return (List)this.parents.stream().filter(filter).collect(StreamUtil.toUnmodifiableList());
    }

    public Stream<Entity> matchSeatSelector(CommandSender sender, SelectorCondition condition) {
        List<CartAttachmentSeat> seats = condition.hasKeyPath() ? this.getOfType(condition.getKeyPath(), CartAttachmentSeat.class) : this.allOfType(CartAttachmentSeat.class);
        if (condition.isBoolean()) {
            return seats.stream().map(CartAttachmentSeat::getEntity).filter(Objects::nonNull);
        }
        boolean includePlayer = condition.matchesText("@p");
        return seats.stream().map(CartAttachmentSeat::getEntity).filter(e -> e instanceof Player).filter(p -> includePlayer && p == sender || condition.matchesText(((Player)p).getName()));
    }

    public List<String> selectNames(AttachmentSelector<?> selector, Set<Attachment> excluding) {
        switch (selector.strategy()) {
            case NONE: {
                return Collections.emptyList();
            }
            case PARENTS: {
                return Util.filterAndMultiMapList(this.parents, a -> selector.matches((Attachment)a) && !excluding.contains(a), Attachment::getNames);
            }
        }
        Predicate<Attachment> filter = a -> selector.matchesExceptName((Attachment)a) && !excluding.contains(a);
        if (selector.nameFilter().isPresent()) {
            return Util.filterAndMultiMapList(this.get(selector.nameFilter().get()), filter, Attachment::getNames);
        }
        return this.names(filter);
    }

    public <T> List<T> selectValues(AttachmentSelector<T> selector, Set<Attachment> excluding) {
        switch (selector.strategy()) {
            case NONE: {
                return Collections.emptyList();
            }
            case PARENTS: {
                return this.parents(a -> selector.matches((Attachment)a) && !excluding.contains(a));
            }
        }
        Predicate<Attachment> filter = a -> selector.matchesExceptName((Attachment)a) && !excluding.contains(a);
        if (selector.nameFilter().isPresent()) {
            return this.get(selector.nameFilter().get(), filter);
        }
        return this.all(filter);
    }

    private static boolean containsMatching(List<Attachment> attachments, Predicate<Attachment> filter) {
        for (Attachment attachment : attachments) {
            if (!filter.test(attachment)) continue;
            return true;
        }
        return false;
    }

    public static AttachmentNameLookup create(Attachment root) {
        List<Attachment> parents;
        HashMap<String, List<Attachment>> attachments = new HashMap<String, List<Attachment>>();
        ArrayList<Attachment> all = new ArrayList<Attachment>();
        AttachmentNameLookup.fill(all, attachments, root);
        AttachmentNameLookup.makeListsImmutable(attachments);
        Attachment p = root.getParent();
        if (p != null) {
            parents = new ArrayList<Attachment>();
            parents.add(root);
            parents.add(p);
            while ((p = p.getParent()) != null) {
                parents.add(p);
            }
            parents = Collections.unmodifiableList(parents);
        } else {
            parents = Collections.singletonList(root);
        }
        return new AttachmentNameLookup(Collections.unmodifiableList(all), parents, attachments);
    }

    public static AttachmentNameLookup merge(Collection<AttachmentNameLookup> nameLookups) {
        if (nameLookups.isEmpty()) {
            return EMPTY;
        }
        if (nameLookups.size() == 1) {
            return nameLookups.iterator().next();
        }
        HashMap<String, List<Attachment>> resultByName = new HashMap<String, List<Attachment>>(32);
        ArrayList<Attachment> resultAll = new ArrayList<Attachment>(64);
        ArrayList<Attachment> resultParents = new ArrayList<Attachment>(16);
        for (AttachmentNameLookup lookup : nameLookups) {
            if (!lookup.byName.isEmpty()) {
                for (Map.Entry<String, List<Attachment>> e : lookup.byName.entrySet()) {
                    resultByName.computeIfAbsent(e.getKey(), n -> new ArrayList()).addAll((Collection)e.getValue());
                }
            }
            resultAll.addAll(lookup.all);
            resultParents.addAll(lookup.parents);
        }
        AttachmentNameLookup.makeListsImmutable(resultByName);
        return new AttachmentNameLookupMerged(Collections.unmodifiableList(resultAll), Collections.unmodifiableList(resultParents), resultByName, nameLookups);
    }

    static {
        EMPTY.invalidate();
    }

    private static class AttachmentNameLookupMerged
    extends AttachmentNameLookup {
        private final Collection<AttachmentNameLookup> originalLookups;

        private AttachmentNameLookupMerged(AttachmentNameLookup original, Collection<AttachmentNameLookup> originalLookups) {
            super(original);
            this.originalLookups = originalLookups;
        }

        private AttachmentNameLookupMerged(List<Attachment> all, List<Attachment> parents, Map<String, List<Attachment>> byName, Collection<AttachmentNameLookup> originalLookups) {
            super(all, parents, byName);
            this.originalLookups = originalLookups;
        }

        @Override
        public boolean isValid() {
            if (!super.isValid()) {
                return false;
            }
            for (AttachmentNameLookup lookup : this.originalLookups) {
                if (lookup.isValid()) continue;
                this.invalidate();
                return false;
            }
            return true;
        }
    }

    @Deprecated
    public static final class NameGroup<T extends Attachment>
    implements Iterable<T> {
        private static final NameGroup<Attachment> NONE = new NameGroup<Attachment>(AttachmentSelection.NONE);
        private final AttachmentSelection<T> selection;

        public static <T extends Attachment> NameGroup<T> of(Supplier lookupSupplier, String name, Class<T> type) {
            return new NameGroup<T>(lookupSupplier.getSelection(AttachmentSelector.named(AttachmentSelector.SearchStrategy.CHILDREN, name).withType(type)));
        }

        public static <T extends Attachment> NameGroup<T> none() {
            return NONE;
        }

        private NameGroup(AttachmentSelection<T> selection) {
            this.selection = selection;
        }

        public List<T> values() {
            return this.selection.values();
        }

        public void sync() {
            this.selection.sync();
        }

        @Override
        public Iterator<T> iterator() {
            return this.selection.iterator();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            this.selection.forEach(action);
        }
    }

    private static final class SelectionImpl<T>
    implements AttachmentSelection<T> {
        private final Supplier lookupSupplier;
        private final AttachmentSelector<T> selector;
        private AttachmentNameLookup cachedLookup;
        private Set<Attachment> cachedExcluding;
        private List<T> values = null;
        private List<String> names = null;

        public SelectionImpl(Supplier lookupSupplier, AttachmentSelector<T> selector) {
            if (lookupSupplier == null) {
                throw new IllegalArgumentException("Lookup Supplier is null");
            }
            if (selector == null) {
                throw new IllegalArgumentException("Attachment Selector is null");
            }
            this.lookupSupplier = lookupSupplier;
            this.cachedExcluding = Collections.emptySet();
            this.selector = selector;
            this.cachedLookup = EMPTY;
            this.sync();
        }

        @Override
        public AttachmentSelector<T> selector() {
            return this.selector;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<String> names() {
            List<String> names = this.names;
            if (names != null) {
                return names;
            }
            SelectionImpl selectionImpl = this;
            synchronized (selectionImpl) {
                names = this.names;
                if (names != null) {
                    return names;
                }
                this.names = this.cachedLookup.selectNames(this.selector, this.cachedExcluding);
                return this.names;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<T> values() {
            List<T> values = this.values;
            if (values != null) {
                return values;
            }
            SelectionImpl selectionImpl = this;
            synchronized (selectionImpl) {
                values = this.values;
                if (values != null) {
                    return values;
                }
                this.values = this.cachedLookup.selectValues(this.selector, this.cachedExcluding);
                return this.values;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean sync() {
            if (this.cachedLookup.isValid()) {
                return false;
            }
            AttachmentNameLookup lookup = this.lookupSupplier.getNameLookup(this.selector.strategy());
            Set excluding = this.selector.isExcludingSelf() ? this.lookupSupplier.getSelfFilterOfNameLookup() : Collections.emptySet();
            SelectionImpl selectionImpl = this;
            synchronized (selectionImpl) {
                this.cachedLookup = lookup;
                this.cachedExcluding = excluding;
                this.values = null;
                this.names = null;
            }
            return true;
        }
    }

    @FunctionalInterface
    public static interface Supplier {
        public AttachmentNameLookup getNameLookup();

        default public AttachmentNameLookup getNameLookup(AttachmentSelector.SearchStrategy strategy) {
            return this.getNameLookup();
        }

        default public Set<Attachment> getSelfFilterOfNameLookup() {
            return Collections.emptySet();
        }

        default public <T> AttachmentSelection<T> getSelection(AttachmentSelector<T> selector) {
            return new SelectionImpl<T>(this, selector);
        }

        public static <T> AttachmentSelection<T> getSelection(final AttachmentSelector<T> selector, final java.util.function.Supplier<Collection<? extends Supplier>> suppliers) {
            Supplier deferMerged = new Supplier(){

                @Override
                public AttachmentNameLookup getNameLookup() {
                    Collection currSuppliers = (Collection)suppliers.get();
                    if (currSuppliers.isEmpty()) {
                        return EMPTY;
                    }
                    if (currSuppliers.size() == 1) {
                        return ((Supplier)currSuppliers.iterator().next()).getNameLookup(selector.strategy());
                    }
                    ArrayList<AttachmentNameLookup> lookups = new ArrayList<AttachmentNameLookup>(currSuppliers.size());
                    for (Supplier supplier : currSuppliers) {
                        lookups.add(supplier.getNameLookup(selector.strategy()));
                    }
                    return AttachmentNameLookup.merge(lookups);
                }

                @Override
                public Set<Attachment> getSelfFilterOfNameLookup() {
                    Collection currSuppliers = (Collection)suppliers.get();
                    if (currSuppliers.isEmpty()) {
                        return Collections.emptySet();
                    }
                    if (currSuppliers.size() == 1) {
                        return ((Supplier)currSuppliers.iterator().next()).getSelfFilterOfNameLookup();
                    }
                    HashSet<Attachment> excluding = new HashSet<Attachment>();
                    for (Supplier supplier : currSuppliers) {
                        excluding.addAll(supplier.getSelfFilterOfNameLookup());
                    }
                    return Collections.unmodifiableSet(excluding);
                }
            };
            return deferMerged.getSelection(selector);
        }
    }
}

