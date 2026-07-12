/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.fieldbacked;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldBackedCombinedTrainProperty<T> {
    private final List<Set<T>> previousSets = new ArrayList<Set<T>>();
    private Set<T> previousResult = Collections.emptySet();

    public Set<T> update(TrainProperties properties, FieldBackedStandardCartProperty<Set<T>> property) {
        boolean different = false;
        int index = 0;
        for (CartProperties cartProperties : properties) {
            Set<T> cartSet = property.get(cartProperties);
            if (index >= this.previousSets.size()) {
                different = true;
                this.previousSets.add(cartSet);
            } else if (this.previousSets.get(index) != cartSet) {
                different = true;
                this.previousSets.set(index, cartSet);
            }
            ++index;
        }
        while (this.previousSets.size() > index) {
            this.previousSets.remove(this.previousSets.size() - 1);
            different = true;
        }
        if (different) {
            HashSet combined = new HashSet(Math.max(8, this.previousResult.size()));
            this.previousSets.forEach(combined::addAll);
            this.previousResult = combined.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(combined);
        }
        return this.previousResult;
    }
}

