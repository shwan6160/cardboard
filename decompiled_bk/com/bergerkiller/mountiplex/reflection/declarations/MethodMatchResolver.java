/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldLCSResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.stream.Stream;

public class MethodMatchResolver {
    public static void match(Class<?> declaringClass, ClassResolver resolver, MethodDeclaration[] methods) {
        Declaration[] realMethods;
        try {
            realMethods = (MethodDeclaration[])Stream.concat(ReflectionUtil.getDeclaredMethods(declaringClass), ReflectionUtil.getMethods(declaringClass).filter(m -> !Modifier.isStatic(m.getModifiers()))).filter(ReflectionUtil.createDuplicateMethodFilter()).map(m -> new MethodDeclaration(resolver, (Method)m)).toArray(MethodDeclaration[]::new);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to identify methods of class " + MPLType.getName(declaringClass), t);
            return;
        }
        for (int i = 0; i < methods.length; ++i) {
            MethodDeclaration method = methods[i];
            if (method.body != null) continue;
            MethodDeclaration nameResolved = method.resolveName();
            boolean found = false;
            for (MethodDeclaration methodDeclaration : realMethods) {
                if (!methodDeclaration.match(nameResolved)) continue;
                if (!methodDeclaration.modifiers.match(method.modifiers) && methodDeclaration.modifiers.getProtectionLevel() > method.modifiers.getProtectionLevel()) {
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Method modifiers of " + resolver.getDeclaredClassName() + " " + method.toString() + " do not match (" + methodDeclaration.modifiers + " expected)");
                }
                method.method = methodDeclaration.method;
                method.constructor = methodDeclaration.constructor;
                found = true;
                break;
            }
            if (found || method.modifiers.isOptional()) continue;
            FieldLCSResolver.logAlternatives((String)"method", (Declaration[])realMethods, (Declaration)nameResolved, (boolean)false);
        }
    }
}

