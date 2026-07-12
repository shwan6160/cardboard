/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.signature;

public final class FieldSignature {
    private final String name;

    public FieldSignature(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof FieldSignature) {
            return this.name.equals(((FieldSignature)o).name);
        }
        return false;
    }
}

