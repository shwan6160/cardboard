/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.ImplicitlySharedHolder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ImplicitlySharedSet<E>
extends ImplicitlySharedHolder<Set<E>>
implements Set<E> {
    public ImplicitlySharedSet() {
        super(new HashSet(), a -> (HashSet)a.clone());
    }

    public ImplicitlySharedSet(Set<E> set) {
        super(set);
    }

    public <V extends Set<E>> ImplicitlySharedSet(V set, UnaryOperator<V> cloneFunction) {
        super(set, cloneFunction);
    }

    public ImplicitlySharedSet(ImplicitlySharedSet<E> sharedSet) {
        super((ImplicitlySharedHolder.Reference)sharedSet.ref.get());
    }

    @Override
    public int size() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            int n = ((Set)ref.val).size();
            return n;
        }
    }

    @Override
    public boolean isEmpty() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((Set)ref.val).isEmpty();
            return bl;
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            ((Set)ref.val).forEach(action);
        }
    }

    @Override
    public boolean contains(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((Set)ref.val).contains(o);
            return bl;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ReferencedSetIterator();
    }

    @Override
    public Object[] toArray() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            Object[] objectArray = ((Set)ref.val).toArray();
            return objectArray;
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            T[] TArray = ((Set)ref.val).toArray(a);
            return TArray;
        }
    }

    @Override
    public boolean add(E e) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((Set)ref.val).add(e);
            return bl;
        }
    }

    @Override
    public boolean remove(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((Set)ref.val).remove(o);
            return bl;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((Set)ref.val).containsAll(c);
            return bl;
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((Set)ref.val).addAll(c);
            return bl;
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.isEmpty()) {
            return false;
        }
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((Set)ref.val).retainAll(c);
            return bl;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }
        if (c instanceof ImplicitlySharedHolder && this.refEquals((ImplicitlySharedHolder)((Object)c))) {
            this.clear();
            return true;
        }
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((Set)ref.val).removeAll(c);
            return bl;
        }
    }

    @Override
    public void clear() {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            ((Set)ref.val).clear();
        }
    }

    public Iterable<E> cloneAsIterable() {
        return () -> new ReferencedSetCopyIterator(this);
    }

    public void cloneAndForEach(Consumer<? super E> action) {
        try (Object clone = this.clone();){
            ((ImplicitlySharedSet)clone).forEach(action);
        }
    }

    public ImplicitlySharedSet<E> clone() {
        return new ImplicitlySharedSet<E>(this);
    }

    private final class ReferencedSetIterator
    implements Iterator<E> {
        private ImplicitlySharedHolder.Reference<Set<E>> ref;
        private Iterator<E> baseIter;
        private boolean hasLastElement;
        private E lastElement;

        public ReferencedSetIterator() {
            this.ref = (ImplicitlySharedHolder.Reference)ImplicitlySharedSet.this.ref.get();
            this.baseIter = ((Set)this.ref.val).iterator();
            this.hasLastElement = false;
            this.lastElement = null;
        }

        @Override
        public boolean hasNext() {
            return this.baseIter.hasNext();
        }

        @Override
        public E next() {
            Object result = this.baseIter.next();
            this.lastElement = result;
            this.hasLastElement = true;
            return result;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            this.hasLastElement = false;
            this.baseIter.forEachRemaining(action);
        }

        @Override
        public void remove() {
            if (!this.hasLastElement) {
                throw new IllegalStateException("next() must be called before remove() is valid");
            }
            this.hasLastElement = false;
            try (ImplicitlySharedHolder.Reference ref_writable = ImplicitlySharedSet.this.write();){
                if (ref_writable == this.ref) {
                    this.baseIter.remove();
                } else {
                    ((Set)ref_writable.val).remove(this.lastElement);
                }
            }
        }
    }

    private static final class ReferencedSetCopyIterator<E>
    implements Iterator<E> {
        private ImplicitlySharedSet<E> copy;
        private Iterator<E> copyIter;

        public ReferencedSetCopyIterator(ImplicitlySharedSet<E> set) {
            this.copy = set.clone();
            this.copyIter = this.copy.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.copyIter.hasNext()) {
                return true;
            }
            this.copy.close();
            return false;
        }

        @Override
        public E next() {
            return this.copyIter.next();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            this.copyIter.forEachRemaining(action);
            this.copy.close();
        }

        @Override
        public void remove() {
        }
    }
}

