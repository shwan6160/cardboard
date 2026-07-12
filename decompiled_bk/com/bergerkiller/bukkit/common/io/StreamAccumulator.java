/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamAccumulator<T> {
    private final ArrayList<T> buffer = new ArrayList();
    private Iterator<T> iterator = null;

    public void open(Stream<T> stream) {
        this.buffer.clear();
        this.iterator = stream.iterator();
    }

    public boolean isEmpty() {
        return this.buffer.isEmpty() && !this.iterator.hasNext();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 1280){
            private int index;
            {
                this.index = 0;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> consumer) {
                Object object;
                if (this.index >= StreamAccumulator.this.buffer.size()) {
                    if (!StreamAccumulator.this.iterator.hasNext()) {
                        return false;
                    }
                    object = StreamAccumulator.this.iterator.next();
                    StreamAccumulator.this.buffer.add(object);
                } else {
                    object = StreamAccumulator.this.buffer.get(this.index);
                }
                ++this.index;
                consumer.accept(object);
                return true;
            }
        }, false);
    }
}

