/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ListCallbackCollector<T>
implements Consumer<T> {
    private List<T> buffer = Collections.emptyList();
    private List<T> result = this.buffer;

    public List<T> result() {
        return this.result;
    }

    @Override
    public void accept(T t) {
        List<T> buffer = this.buffer;
        int size = buffer.size();
        if (size == 0) {
            this.buffer = Collections.singletonList(t);
            this.result = this.buffer;
        } else if (size == 1) {
            ArrayList<T> newList = new ArrayList<T>(16);
            newList.add(buffer.get(0));
            newList.add(t);
            this.buffer = newList;
            this.result = Collections.unmodifiableList(newList);
        } else {
            buffer.add(t);
        }
    }
}

