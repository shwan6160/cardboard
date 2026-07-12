/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.Iterator;

public class CharacterIterable
implements Iterable<Character> {
    private final CharSequence seq;

    public CharacterIterable(CharSequence sequence) {
        this.seq = sequence;
    }

    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>(){
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return this.idx < CharacterIterable.this.seq.length();
            }

            @Override
            public Character next() {
                return Character.valueOf(CharacterIterable.this.seq.charAt(this.idx++));
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for Strings");
            }
        };
    }
}

