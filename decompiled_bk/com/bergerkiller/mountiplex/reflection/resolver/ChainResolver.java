/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledFieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledMethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.NoOpResolver;
import java.util.Map;

public class ChainResolver {
    public static ClassDeclarationResolver chain(final ClassDeclarationResolver previous, final ClassDeclarationResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : new ClassDeclarationResolver(){

            @Override
            public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> classType) {
                ClassDeclaration result = previous.resolveClassDeclaration(classPath, classType);
                if (result == null) {
                    result = next.resolveClassDeclaration(classPath, classType);
                }
                return result;
            }

            @Override
            public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
                previous.resolveClassVariables(classPath, classType, variables);
                next.resolveClassVariables(classPath, classType, variables);
            }
        };
    }

    public static ClassPathResolver chain(final ClassPathResolver previous, final ClassPathResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : new ClassPathResolver(){

            @Override
            public String resolveClassPath(String classPath) {
                classPath = previous.resolveClassPath(classPath);
                classPath = next.resolveClassPath(classPath);
                return classPath;
            }

            @Override
            public boolean canLoadClassPath(String classPath) {
                return previous.canLoadClassPath(classPath) && next.canLoadClassPath(classPath);
            }
        };
    }

    public static CompiledFieldNameResolver chain(CompiledFieldNameResolver previous, CompiledFieldNameResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : (declaringClass, fieldName) -> {
            fieldName = previous.resolveCompiledFieldName(declaringClass, fieldName);
            fieldName = next.resolveCompiledFieldName(declaringClass, fieldName);
            return fieldName;
        };
    }

    public static CompiledMethodNameResolver chain(CompiledMethodNameResolver previous, CompiledMethodNameResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : (declaringClass, methodName, parameterTypes) -> {
            methodName = previous.resolveCompiledMethodName(declaringClass, methodName, parameterTypes);
            methodName = next.resolveCompiledMethodName(declaringClass, methodName, parameterTypes);
            return methodName;
        };
    }

    public static FieldNameResolver chain(FieldNameResolver previous, FieldNameResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : (declaringClass, fieldName) -> {
            fieldName = previous.resolveFieldName(declaringClass, fieldName);
            fieldName = next.resolveFieldName(declaringClass, fieldName);
            return fieldName;
        };
    }

    public static FieldAliasResolver chain(FieldAliasResolver previous, FieldAliasResolver next) {
        return (field, name) -> {
            String alias = next.resolveFieldAlias(field, name);
            return alias != null ? alias : previous.resolveFieldAlias(field, name);
        };
    }

    public static MethodNameResolver chain(MethodNameResolver previous, MethodNameResolver next) {
        return previous == NoOpResolver.INSTANCE ? next : (declaringClass, methodName, parameterTypes) -> {
            methodName = previous.resolveMethodName(declaringClass, methodName, parameterTypes);
            methodName = next.resolveMethodName(declaringClass, methodName, parameterTypes);
            return methodName;
        };
    }

    public static MethodAliasResolver chain(MethodAliasResolver previous, MethodAliasResolver next) {
        return (method, name) -> {
            String alias = next.resolveMethodAlias(method, name);
            return alias != null ? alias : previous.resolveMethodAlias(method, name);
        };
    }
}

