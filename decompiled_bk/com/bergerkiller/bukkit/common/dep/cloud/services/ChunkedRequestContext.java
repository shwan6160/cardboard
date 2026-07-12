/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ChunkedRequestContext<Context, Result> {
    private final Object lock = new Object();
    private final List<Context> requests;
    private final Map<Context, Result> results;

    protected ChunkedRequestContext(@NonNull Collection<Context> requests) {
        this.requests = new ArrayList<Context>(requests);
        this.results = new HashMap<Context, Result>(requests.size());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final @NonNull Map<Context, Result> availableResults() {
        Object object = this.lock;
        synchronized (object) {
            return Collections.unmodifiableMap(this.results);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final @NonNull List<Context> remaining() {
        Object object = this.lock;
        synchronized (object) {
            return Collections.unmodifiableList(this.requests);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void storeResult(@NonNull Context context, @NonNull Result result) {
        Object object = this.lock;
        synchronized (object) {
            this.results.put(context, result);
            this.requests.remove(context);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean isCompleted() {
        Object object = this.lock;
        synchronized (object) {
            return this.requests.isEmpty();
        }
    }
}

