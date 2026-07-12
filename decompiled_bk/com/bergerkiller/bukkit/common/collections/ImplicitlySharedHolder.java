/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public abstract class ImplicitlySharedHolder<T>
implements AutoCloseable {
    protected final AtomicReference<Reference<T>> ref;

    public ImplicitlySharedHolder(T value) {
        this(value, LogicUtil.findCloneMethod(value));
    }

    public <V extends T> ImplicitlySharedHolder(V value, UnaryOperator<V> cloneFunction) {
        this.ref = new AtomicReference(new Reference(value, cloneFunction, 1));
    }

    protected ImplicitlySharedHolder(Reference<T> reference) {
        reference.openRead();
        this.ref = new AtomicReference<Reference<T>>(reference);
    }

    public final void assign(ImplicitlySharedHolder<T> sharedHolder) {
        Reference<T> new_ref = sharedHolder.ref.get();
        new_ref.openRead();
        Reference<T> old_ref = this.ref.getAndSet(new_ref);
        if (old_ref != null) {
            old_ref.close();
        }
    }

    public final boolean refEquals(ImplicitlySharedHolder<?> sharedHolder) {
        return sharedHolder != null && sharedHolder.ref.get() == this.ref.get();
    }

    @Override
    public final void close() {
        Reference old_ref = this.ref.getAndSet(null);
        if (old_ref != null) {
            old_ref.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Reference<T> write() {
        Reference<T> old_ref = this.ref.get();
        try {
            if (old_ref.openWrite()) {
                return old_ref;
            }
        }
        catch (RuntimeException ex) {
            if (old_ref == null) {
                throw new SharedResourceClosedException();
            }
            throw ex;
        }
        old_ref.openRead();
        try {
            UnaryOperator cloneFunc = old_ref.cloneFunction;
            Reference new_ref = new Reference(cloneFunc.apply(old_ref.val), cloneFunc, -2147483647);
            this.ref.set(new_ref);
            Reference reference = new_ref;
            return reference;
        }
        finally {
            old_ref.close();
        }
    }

    protected final Reference<T> read() {
        Reference<T> ref = this.ref.get();
        try {
            ref.openRead();
            return ref;
        }
        catch (RuntimeException ex) {
            if (ref == null) {
                throw new SharedResourceClosedException();
            }
            throw ex;
        }
    }

    protected static final class Reference<T>
    implements AutoCloseable {
        private static final int WRITE_TOKEN = -2147483647;
        private final AtomicInteger readers;
        public final T val;
        public final UnaryOperator<T> cloneFunction;

        private Reference(T value, UnaryOperator<T> cloneFunction, int initialReaders) {
            this.readers = new AtomicInteger(initialReaders);
            this.val = value;
            this.cloneFunction = cloneFunction;
        }

        public final boolean openWrite() {
            return this.readers.compareAndSet(1, -2147483647);
        }

        public final void openRead() {
            int value;
            while ((value = this.readers.incrementAndGet()) < 0) {
                this.readers.compareAndSet(value, -2147483647);
                Thread.yield();
            }
        }

        @Override
        public final void close() {
            if (this.readers.decrementAndGet() < 0) {
                this.readers.set(1);
            }
        }
    }

    public static final class SharedResourceClosedException
    extends RuntimeException {
        private static final long serialVersionUID = -5807941780901855505L;

        public SharedResourceClosedException() {
            super("Shared resource was accessed after it was closed");
        }
    }
}

