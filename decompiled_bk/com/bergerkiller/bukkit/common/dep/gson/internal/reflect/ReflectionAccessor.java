/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.gson.internal.reflect;

import com.bergerkiller.bukkit.common.dep.gson.internal.JavaVersion;
import com.bergerkiller.bukkit.common.dep.gson.internal.reflect.PreJava9ReflectionAccessor;
import com.bergerkiller.bukkit.common.dep.gson.internal.reflect.UnsafeReflectionAccessor;
import java.lang.reflect.AccessibleObject;

public abstract class ReflectionAccessor {
    private static final ReflectionAccessor instance = JavaVersion.getMajorJavaVersion() < 9 ? new PreJava9ReflectionAccessor() : new UnsafeReflectionAccessor();

    public abstract void makeAccessible(AccessibleObject var1);

    public static ReflectionAccessor getInstance() {
        return instance;
    }
}

