/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.tools.reflect;

import com.bergerkiller.mountiplex.dep.javassist.tools.reflect.ClassMetaobject;
import com.bergerkiller.mountiplex.dep.javassist.tools.reflect.Metaobject;

public interface Metalevel {
    public ClassMetaobject _getClass();

    public Metaobject _getMetaobject();

    public void _setMetaobject(Metaobject var1);
}

