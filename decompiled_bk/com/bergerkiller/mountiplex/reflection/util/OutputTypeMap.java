/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.TypeMap;

public class OutputTypeMap<T>
extends TypeMap<T>
implements Cloneable {
    public OutputTypeMap() {
    }

    protected OutputTypeMap(OutputTypeMap<T> map) {
        super(map);
    }

    @Override
    protected boolean isParentTypeOf(TypeDeclaration parent, TypeDeclaration child) {
        return parent.isInstanceOf(child);
    }

    public OutputTypeMap<T> clone() {
        return new OutputTypeMap<T>(this);
    }
}

