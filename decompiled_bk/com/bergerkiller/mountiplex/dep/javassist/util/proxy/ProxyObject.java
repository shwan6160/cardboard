/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.util.proxy;

import com.bergerkiller.mountiplex.dep.javassist.util.proxy.MethodHandler;
import com.bergerkiller.mountiplex.dep.javassist.util.proxy.Proxy;

public interface ProxyObject
extends Proxy {
    @Override
    public void setHandler(MethodHandler var1);

    public MethodHandler getHandler();
}

