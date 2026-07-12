/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SetCallbackCollector<T>
implements Consumer<T> {
    private Set<T> buffer = Collections.emptySet();
    private Set<T> result = this.buffer;

    public Set<T> result() {
        return this.result;
    }

    @Override
    public void accept(T t) {
        this.acceptCheckAdded(t);
    }

    public boolean acceptCheckAdded(T t) {
        Set<T> buffer = this.buffer;
        int size = buffer.size();
        if (size == 0) {
            this.buffer = Collections.singleton(t);
            this.result = this.buffer;
            return true;
        }
        if (size == 1) {
            if (!(buffer instanceof HashSet)) {
                HashSet<T> newSet = new HashSet<T>(16);
                newSet.addAll(buffer);
                buffer = newSet;
                this.buffer = buffer;
            }
            if (buffer.add(t)) {
                this.result = Collections.unmodifiableSet(buffer);
                return true;
            }
            return false;
        }
        return buffer.add(t);
    }
}

