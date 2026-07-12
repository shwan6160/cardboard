/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TrainCartsSignAction
extends SignAction {
    private final String[] typeIdentifiers;

    public TrainCartsSignAction(String ... typeIdentifiers) {
        if (typeIdentifiers.length == 0) {
            throw new IllegalArgumentException("Must have at least one unique type identifier set");
        }
        this.typeIdentifiers = typeIdentifiers;
    }

    public final List<String> getTypeIdentifiers() {
        return Collections.unmodifiableList(Arrays.asList(this.typeIdentifiers));
    }

    @Override
    public final boolean match(SignActionEvent event) {
        return event.getMode() != SignActionMode.NONE && event.isType(this.typeIdentifiers);
    }
}

