/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases;

import java.util.function.Supplier;

public final class DeferredSupplier<T>
implements Supplier<T> {
    private volatile Supplier<T> supplier;
    private T value;

    private DeferredSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
        this.value = null;
    }

    public boolean isInitialized() {
        return this.supplier == null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T get() {
        if (this.supplier == null) {
            return this.value;
        }
        DeferredSupplier deferredSupplier = this;
        synchronized (deferredSupplier) {
            Supplier<T> theSupplier = this.supplier;
            if (theSupplier != null) {
                this.value = theSupplier.get();
                this.supplier = null;
            }
            return this.value;
        }
    }

    public static <T> DeferredSupplier<T> of(Supplier<T> supplier) {
        return new DeferredSupplier<T>(supplier);
    }
}

