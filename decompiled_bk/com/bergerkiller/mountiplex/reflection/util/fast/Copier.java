/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import java.lang.reflect.Field;

public interface Copier {
    public void copy(Object var1, Object var2);

    public Field getCopyField();

    public void checkCanCopy();
}

