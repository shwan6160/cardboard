/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledFieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledMethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

public final class NoOpResolver
implements ClassPathResolver,
FieldNameResolver,
MethodNameResolver,
CompiledFieldNameResolver,
CompiledMethodNameResolver,
ClassDeclarationResolver,
FieldAliasResolver,
MethodAliasResolver {
    public static final NoOpResolver INSTANCE = new NoOpResolver();

    private NoOpResolver() {
    }

    @Override
    public String resolveCompiledMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return methodName;
    }

    @Override
    public String resolveCompiledFieldName(Class<?> declaringClass, String fieldName) {
        return fieldName;
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return methodName;
    }

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return fieldName;
    }

    @Override
    public String resolveClassPath(String classPath) {
        return classPath;
    }

    @Override
    public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> classType) {
        return null;
    }

    @Override
    public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
    }

    @Override
    public String resolveFieldAlias(Field field, String name) {
        try {
            return field.getName();
        }
        catch (Throwable t) {
            String decName = MPLType.getName(field.getDeclaringClass());
            MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to retrieve field name alias for " + decName + ":" + name + ":", t);
            return null;
        }
    }

    @Override
    public String resolveMethodAlias(Method method, String name) {
        try {
            return method.getName();
        }
        catch (Throwable t) {
            String decName = MPLType.getName(method.getDeclaringClass());
            MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to retrieve method name alias for " + decName + ":" + name + ":", t);
            return null;
        }
    }
}

