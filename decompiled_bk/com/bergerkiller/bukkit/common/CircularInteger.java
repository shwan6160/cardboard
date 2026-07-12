/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import java.util.Iterator;

public class CircularInteger
implements Iterable<Integer> {
    private int value = 0;
    private int size;

    public CircularInteger(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int next() {
        ++this.value;
        if (this.value >= this.size) {
            this.value = 0;
        }
        return this.value;
    }

    public int previous() {
        --this.value;
        if (this.value <= -1) {
            this.value = this.size - 1;
        }
        return this.value;
    }

    public boolean nextBool() {
        return this.next() == 0;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>(){

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                return CircularInteger.this.next();
            }

            @Override
            public void remove() {
                CircularInteger.this.previous();
            }
        };
    }
}

