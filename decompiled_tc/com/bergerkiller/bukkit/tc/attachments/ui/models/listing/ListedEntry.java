/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedNamespace;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ListedEntry
implements Comparable<ListedEntry> {
    private ListedEntry parent = null;
    private List<ListedEntry> children = Collections.emptyList();
    protected boolean childrenNeedSorting = true;
    protected int nestedItemCount = 0;

    public abstract CommonItemStack createIconItem(DialogBuilder var1);

    public abstract String name();

    public abstract String nameLowerCase();

    public abstract String fullPath();

    public abstract int sortPriority();

    public abstract ListedNamespace namespace();

    public final ListedEntry parent() {
        return this.parent;
    }

    public final int nestedItemCount() {
        return this.nestedItemCount;
    }

    public final List<ListedEntry> children() {
        if (this.childrenNeedSorting) {
            this.childrenNeedSorting = false;
            if (!this.children.isEmpty()) {
                this.children.sort((a, b) -> Integer.compare(a.nestedItemCount, b.nestedItemCount));
            }
        }
        return this.children;
    }

    public List<ListedItemModel> explode() {
        ArrayList<ListedItemModel> items = new ArrayList<ListedItemModel>(this.nestedItemCount);
        this.fillItems(items);
        Collections.sort(items);
        return items;
    }

    protected void fillItems(List<ListedItemModel> items) {
        for (ListedEntry child : this.children()) {
            child.fillItems(items);
        }
    }

    public final List<ListedEntry> matchWithPathPrefix(Iterable<String> pathParts) {
        return this.matchAgainstPath(pathParts, false);
    }

    public final Optional<ListedEntry> findAtPath(Iterable<String> pathParts) {
        List<ListedEntry> result = this.matchAgainstPath(pathParts, true);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    private final List<ListedEntry> matchAgainstPath(Iterable<String> pathParts, boolean exact) {
        Optional<ListedEntry> r;
        String token;
        ListedEntry curr;
        block4: {
            Iterator<String> iter = pathParts.iterator();
            if (!iter.hasNext()) {
                return Collections.singletonList(this);
            }
            curr = this;
            while (true) {
                token = iter.next();
                if (!iter.hasNext()) break block4;
                r = curr.findChildByName(token);
                if (!r.isPresent()) break;
                curr = r.get();
            }
            return Collections.emptyList();
        }
        if (exact) {
            r = curr.findChildByName(token);
            return r.map(Collections::singletonList).orElse(Collections.emptyList());
        }
        String tokenLowerCase = token.toLowerCase(Locale.ENGLISH);
        ArrayList<ListedEntry> result = new ArrayList<ListedEntry>(3);
        for (ListedEntry e : curr.children()) {
            if (!e.nameLowerCase().startsWith(tokenLowerCase)) continue;
            result.add(e);
        }
        return result;
    }

    public final Optional<ListedEntry> findChildByName(String name) {
        String nameLowerCase = name.toLowerCase(Locale.ENGLISH);
        Optional<ListedEntry> result = Optional.empty();
        for (ListedEntry e : this.children()) {
            if (!e.nameLowerCase().equals(nameLowerCase)) continue;
            result = Optional.of(e);
            if (e instanceof ListedItemModel) continue;
            break;
        }
        return result;
    }

    public final List<ListedEntry> matchChildrenNameContains(String token) {
        String tokenLower = token.toLowerCase(Locale.ENGLISH);
        ArrayList<ListedEntry> result = new ArrayList<ListedEntry>(10);
        for (ListedEntry child : this.children()) {
            child.fillMatchingContains(tokenLower, result);
        }
        return result;
    }

    private void fillMatchingContains(String tokenLower, List<ListedEntry> result) {
        if (this.nameLowerCase().contains(tokenLower)) {
            result.add(this);
        } else {
            for (ListedEntry child : this.children()) {
                child.fillMatchingContains(tokenLower, result);
            }
        }
    }

    public final ListedEntry compactIf(boolean condition) {
        return condition ? this.compact() : this;
    }

    public final ListedEntry compact() {
        ListedEntry e = this;
        while (e.children().size() == 1) {
            e = e.children().get(0);
        }
        return e;
    }

    public final List<? extends ListedEntry> displayedItems(int numDisplayed) {
        return this.displayedItems(numDisplayed, true);
    }

    public final List<? extends ListedEntry> displayedItems(int numDisplayed, boolean compact) {
        if (!compact) {
            return this.children();
        }
        int numChildren = this.children().size();
        if (numChildren >= numDisplayed) {
            return this.children().stream().map(ListedEntry::compact).sorted().collect(Collectors.toList());
        }
        if (this.nestedItemCount <= numDisplayed) {
            return this.explode();
        }
        int spaceRemaining = numDisplayed - numChildren;
        ArrayList<ListedEntry> entries = new ArrayList<ListedEntry>(numDisplayed);
        for (ListedEntry child : this.children()) {
            ListedEntry e = child.compact();
            if (e.nestedItemCount > 1 && e.nestedItemCount - 1 <= spaceRemaining) {
                spaceRemaining -= e.nestedItemCount - 1;
                entries.addAll(e.explode());
                continue;
            }
            entries.add(e);
        }
        Collections.sort(entries);
        return entries;
    }

    @Override
    public int compareTo(ListedEntry o) {
        int sortOrder = Integer.compare(this.sortPriority(), o.sortPriority());
        if (sortOrder != 0) {
            return sortOrder;
        }
        return this.name().compareTo(o.name());
    }

    protected void setParent(ListedEntry parent) {
        if (this.parent != parent) {
            if (this.parent != null) {
                this.parent.children.remove(this);
                this.parent.updateNestedItemCount(-this.nestedItemCount);
            }
            this.parent = parent;
            if (parent.children.isEmpty()) {
                parent.children = new ArrayList<ListedEntry>();
            }
            parent.children.add(this);
            parent.childrenNeedSorting = true;
            parent.updateNestedItemCount(this.nestedItemCount);
        }
    }

    protected void updateNestedItemCount(int increase) {
        ListedEntry e = this;
        while (e != null) {
            e.nestedItemCount += increase;
            e = e.parent;
        }
    }

    protected final ListedEntry assignCloneTo(ListedEntry newParent) {
        ListedEntry clone = this.unsafeClone(newParent);
        clone.parent = null;
        clone.setParent(newParent);
        return clone;
    }

    private final ListedEntry unsafeClone(ListedEntry newParent) {
        ListedEntry clone = this.cloneSelf(newParent == null ? null : newParent.namespace());
        clone.parent = newParent;
        clone.nestedItemCount = this.nestedItemCount;
        List<ListedEntry> selfChildren = this.children();
        if (!selfChildren.isEmpty()) {
            clone.children = new ArrayList<ListedEntry>(selfChildren.size());
            for (ListedEntry child : selfChildren) {
                clone.children.add(child.unsafeClone(clone));
            }
            clone.childrenNeedSorting = false;
        }
        return clone;
    }

    protected abstract ListedEntry findOrCreateInRoot(ListedRoot var1);

    protected final ListedEntry assignToRoot(ListedRoot root) {
        ListedEntry parent = this.parent().findOrCreateInRoot(root);
        return this.assignCloneTo(parent);
    }

    protected abstract ListedEntry cloneSelf(ListedNamespace var1);

    public static List<String> tokenizePath(String path) {
        if (path.isEmpty()) {
            return new ArrayList<String>();
        }
        int firstPartEnd = StringUtil.firstIndexOf((String)path, (char[])new char[]{'/', '\\', ':'});
        if (path.charAt(firstPartEnd) == ':' && path.length() >= firstPartEnd) {
            path = path.substring(0, firstPartEnd + 1) + "/" + path.substring(firstPartEnd + 1);
        }
        return Arrays.stream(path.split("/|\\\\")).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
    }
}

