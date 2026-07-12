/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedNamespace;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListedRootLoader {
    protected ListedRoot root = new ListedRoot();

    protected void loadFromListing(ListedRoot listedRoot, String query) {
        List<ListedNamespace> namespacesToCheck;
        boolean isNameSearch;
        boolean bl = isNameSearch = StringUtil.firstIndexOf((String)query, (char[])new char[]{'/', '\\', ':'}) == -1;
        if (!query.isEmpty() && isNameSearch) {
            for (ListedNamespace namespace : listedRoot.namespaces()) {
                for (ListedEntry e : namespace.matchChildrenNameContains(query)) {
                    e.assignToRoot(this.root);
                }
            }
            return;
        }
        List<String> parts = ListedEntry.tokenizePath(query);
        if (!parts.isEmpty() && parts.get(0).endsWith(":")) {
            String namespace = parts.remove(0);
            ListedNamespace match = listedRoot.namespacesByName.get(namespace);
            if (match == null) {
                String namespaceLower = namespace.toLowerCase(Locale.ENGLISH);
                for (ListedNamespace n : listedRoot.namespaces()) {
                    if (!n.nameLowerCase().equals(namespaceLower)) continue;
                    match = n;
                    break;
                }
            }
            if (match == null) {
                return;
            }
            namespacesToCheck = Collections.singletonList(match);
        } else {
            namespacesToCheck = listedRoot.namespaces();
        }
        for (ListedNamespace namespace : namespacesToCheck) {
            for (ListedEntry e : namespace.matchWithPathPrefix(parts)) {
                e.assignToRoot(this.root);
            }
        }
    }
}

