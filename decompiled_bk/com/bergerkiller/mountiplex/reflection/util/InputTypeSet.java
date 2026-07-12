/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;

public class InputTypeSet {
    private final InputTypeMap<Object> map = new InputTypeMap();

    public void add(TypeDeclaration type) {
        this.map.put(type, new Object());
    }

    public boolean contains(TypeDeclaration type) {
        return this.map.containsKey(type);
    }

    public void clear() {
        this.map.clear();
    }
}

