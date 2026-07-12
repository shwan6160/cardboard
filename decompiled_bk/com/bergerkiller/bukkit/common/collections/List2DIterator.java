/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class List2DIterator<E>
implements Iterator<E> {
    private final Iterator<List<E>> collectionIter;
    private Iterator<E> elemIter;
    private Iterator<E> oldIter;

    public List2DIterator(Collection<List<E>> collection2D) {
        this.collectionIter = collection2D.iterator();
        this.nextElemIter();
    }

    private void nextElemIter() {
        if (!this.hasNext()) {
            while (this.collectionIter.hasNext()) {
                this.elemIter = this.collectionIter.next().iterator();
                if (!this.elemIter.hasNext()) continue;
                return;
            }
            this.elemIter = null;
        }
    }

    @Override
    public boolean hasNext() {
        return this.elemIter != null && this.elemIter.hasNext();
    }

    @Override
    public E next() {
        if (this.elemIter == null) {
            throw new NoSuchElementException("Ran out of elements to return (forgot a hasNext check?)");
        }
        E elem = this.elemIter.next();
        this.oldIter = this.elemIter;
        this.nextElemIter();
        return elem;
    }

    @Override
    public void remove() {
        if (this.oldIter == null) {
            throw new IllegalStateException("Can not remove an element: Next is not called this run");
        }
        this.oldIter.remove();
    }
}

