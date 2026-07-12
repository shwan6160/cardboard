/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import java.util.concurrent.atomic.AtomicInteger;

public class UniqueHash {
    private final AtomicInteger value = new AtomicInteger(0);

    public int next() {
        return UniqueHash.hash(this.value.incrementAndGet());
    }

    public String nextHex() {
        return Integer.toHexString(this.next());
    }

    public static int hash(int x) {
        int prime = Integer.MAX_VALUE;
        int hash = x ^ 0x5BF03635;
        if (hash >= Integer.MAX_VALUE) {
            return hash;
        }
        int residue = (int)((long)hash * (long)hash % Integer.MAX_VALUE);
        return hash <= 0x3FFFFFFF ? residue : Integer.MAX_VALUE - residue;
    }
}

