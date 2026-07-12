/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.TypeMap;

public class InputTypeMap<T>
extends TypeMap<T>
implements Cloneable {
    public InputTypeMap() {
    }

    protected InputTypeMap(InputTypeMap<T> map) {
        super(map);
    }

    @Override
    protected boolean isParentTypeOf(TypeDeclaration parent, TypeDeclaration child) {
        return child.isInstanceOf(parent);
    }

    public InputTypeMap<T> clone() {
        return new InputTypeMap<T>(this);
    }
}

