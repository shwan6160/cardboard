/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import java.util.Map;

public interface ClassDeclarationResolver {
    public ClassDeclaration resolveClassDeclaration(String var1, Class<?> var2);

    public void resolveClassVariables(String var1, Class<?> var2, Map<String, String> var3);

    default public ClassResolver getRootClassResolver(String classPath, Class<?> classType) {
        return ClassResolver.DEFAULT;
    }
}

