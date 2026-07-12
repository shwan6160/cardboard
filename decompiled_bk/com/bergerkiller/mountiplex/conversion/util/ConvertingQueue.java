/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import java.util.Queue;

public class ConvertingQueue<T>
extends ConvertingCollection<T>
implements Queue<T> {
    public ConvertingQueue(Queue<?> queue, DuplexConverter<?, T> converterPair) {
        super(queue, converterPair);
    }

    public Queue<Object> getBase() {
        return (Queue)super.getBase();
    }

    @Override
    public boolean offer(T e) {
        return this.getBase().offer(this.converter.convertReverse(e));
    }

    @Override
    public T remove() {
        return (T)this.converter.convert(this.getBase().remove());
    }

    @Override
    public T poll() {
        return (T)this.converter.convert(this.getBase().poll());
    }

    @Override
    public T element() {
        return (T)this.converter.convert(this.getBase().element());
    }

    @Override
    public T peek() {
        return (T)this.converter.convert(this.getBase().peek());
    }
}

