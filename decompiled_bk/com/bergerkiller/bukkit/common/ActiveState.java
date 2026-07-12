/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import java.util.LinkedList;

public class ActiveState<T> {
    private T state;
    private LinkedList<T> oldStates = new LinkedList();

    public ActiveState(T state) {
        this.state = state;
    }

    public void next(T newState) {
        this.oldStates.addLast(this.state);
        this.state = newState;
    }

    public void previous() {
        if (!this.oldStates.isEmpty()) {
            this.state = this.oldStates.pollLast();
        }
    }

    public void reset(T newState) {
        this.state = newState;
        this.oldStates.clear();
    }

    public T get() {
        return this.state;
    }
}

