/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.ImplicitlySharedHolder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ImplicitlySharedList<E>
extends ImplicitlySharedHolder<List<E>>
implements List<E>,
AutoCloseable {
    public ImplicitlySharedList() {
        super(new ArrayList(), a -> (ArrayList)a.clone());
    }

    public ImplicitlySharedList(List<E> list) {
        super(list);
    }

    public <V extends List<E>> ImplicitlySharedList(V list, UnaryOperator<V> cloneFunction) {
        super(list, cloneFunction);
    }

    public ImplicitlySharedList(ImplicitlySharedList<E> sharedList) {
        super((ImplicitlySharedHolder.Reference)sharedList.ref.get());
    }

    @Override
    public int size() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            int n = ((List)ref.val).size();
            return n;
        }
    }

    @Override
    public boolean isEmpty() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((List)ref.val).isEmpty();
            return bl;
        }
    }

    @Override
    public boolean contains(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((List)ref.val).contains(o);
            return bl;
        }
    }

    @Override
    public Object[] toArray() {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            Object[] objectArray = ((List)ref.val).toArray();
            return objectArray;
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            T[] TArray = ((List)ref.val).toArray(a);
            return TArray;
        }
    }

    @Override
    public boolean add(E e) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).add(e);
            return bl;
        }
    }

    @Override
    public boolean remove(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).remove(o);
            return bl;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            boolean bl = ((List)ref.val).containsAll(c);
            return bl;
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).addAll(c);
            return bl;
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.isEmpty()) {
            return false;
        }
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).retainAll(c);
            return bl;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).removeAll(c);
            return bl;
        }
    }

    @Override
    public void clear() {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            ((List)ref.val).clear();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            boolean bl = ((List)ref.val).addAll(c);
            return bl;
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            ((List)ref.val).forEach(action);
        }
    }

    @Override
    public E get(int index) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            Object e = ((List)ref.val).get(index);
            return e;
        }
    }

    @Override
    public E set(int index, E element) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            E e = ((List)ref.val).set(index, element);
            return e;
        }
    }

    @Override
    public void add(int index, E element) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            ((List)ref.val).add(index, element);
        }
    }

    @Override
    public E remove(int index) {
        try (ImplicitlySharedHolder.Reference ref = this.write();){
            Object e = ((List)ref.val).remove(index);
            return e;
        }
    }

    @Override
    public int indexOf(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            int n = ((List)ref.val).indexOf(o);
            return n;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        try (ImplicitlySharedHolder.Reference ref = this.read();){
            int n = ((List)ref.val).lastIndexOf(o);
            return n;
        }
    }

    public Iterable<E> cloneAsIterable() {
        return () -> new ReferencedListCopyIterator(this);
    }

    public void cloneAndForEach(Consumer<? super E> action) {
        try (Object clone = this.clone();){
            ((ImplicitlySharedList)clone).forEach(action);
        }
    }

    public ImplicitlySharedList<E> clone() {
        return new ImplicitlySharedList<E>(this);
    }

    @Override
    public Iterator<E> iterator() {
        return new ReferencedListIterator();
    }

    private final AbstractList<E> createAbstractList() {
        return new AbstractList<E>(){

            @Override
            public int size() {
                return ImplicitlySharedList.this.size();
            }

            @Override
            public boolean isEmpty() {
                return ImplicitlySharedList.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return ImplicitlySharedList.this.contains(o);
            }

            @Override
            public Object[] toArray() {
                return ImplicitlySharedList.this.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return ImplicitlySharedList.this.toArray(a);
            }

            @Override
            public boolean add(E e) {
                return ImplicitlySharedList.this.add(e);
            }

            @Override
            public boolean remove(Object o) {
                return ImplicitlySharedList.this.remove(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return ImplicitlySharedList.this.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                return ImplicitlySharedList.this.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return ImplicitlySharedList.this.retainAll(c);
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return ImplicitlySharedList.this.removeAll(c);
            }

            @Override
            public void clear() {
                ImplicitlySharedList.this.clear();
            }

            @Override
            public boolean addAll(int index, Collection<? extends E> c) {
                return ImplicitlySharedList.this.addAll(index, c);
            }

            @Override
            public E get(int index) {
                return ImplicitlySharedList.this.get(index);
            }

            @Override
            public E set(int index, E element) {
                return ImplicitlySharedList.this.set(index, element);
            }

            @Override
            public void add(int index, E element) {
                ImplicitlySharedList.this.add(index, element);
            }

            @Override
            public E remove(int index) {
                return ImplicitlySharedList.this.remove(index);
            }

            @Override
            public int indexOf(Object o) {
                return ImplicitlySharedList.this.indexOf(o);
            }

            @Override
            public int lastIndexOf(Object o) {
                return ImplicitlySharedList.this.lastIndexOf(o);
            }

            @Override
            public Iterator<E> iterator() {
                return ImplicitlySharedList.this.iterator();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.createAbstractList().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return this.createAbstractList().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.createAbstractList().subList(fromIndex, toIndex);
    }

    private final class ReferencedListIterator
    implements Iterator<E> {
        private ImplicitlySharedHolder.Reference<List<E>> ref;
        private Iterator<E> baseIter;
        private int index;
        private boolean hasLastElement;

        public ReferencedListIterator() {
            this.ref = (ImplicitlySharedHolder.Reference)ImplicitlySharedList.this.ref.get();
            this.baseIter = ((List)this.ref.val).iterator();
            this.index = -1;
            this.hasLastElement = false;
        }

        @Override
        public boolean hasNext() {
            return this.baseIter.hasNext();
        }

        @Override
        public E next() {
            Object result = this.baseIter.next();
            ++this.index;
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
            try (ImplicitlySharedHolder.Reference ref_writable = ImplicitlySharedList.this.write();){
                if (ref_writable == this.ref) {
                    this.baseIter.remove();
                } else {
                    ((List)ref_writable.val).remove(this.index);
                }
                --this.index;
            }
        }
    }

    private static final class ReferencedListCopyIterator<E>
    implements Iterator<E> {
        private ImplicitlySharedList<E> copy;
        private Iterator<E> copyIter;

        public ReferencedListCopyIterator(ImplicitlySharedList<E> list) {
            this.copy = list.clone();
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

