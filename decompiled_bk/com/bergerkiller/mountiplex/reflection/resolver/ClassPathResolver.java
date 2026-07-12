/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

public interface ClassPathResolver {
    public String resolveClassPath(String var1);

    default public boolean canLoadClassPath(String classPath) {
        return true;
    }
}

