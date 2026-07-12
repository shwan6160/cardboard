/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface MPLTypeHelper {
    public String getClassName(Class<?> var1);

    public String getMethodName(Method var1);

    public String getFieldName(Field var1);

    public Method getDeclaredMethod(Class<?> var1, String var2, Class<?> ... var3) throws NoSuchMethodException, SecurityException;

    public Field getDeclaredField(Class<?> var1, String var2) throws NoSuchFieldException, SecurityException;

    public Class<?> getClassByName(String var1, boolean var2, ClassLoader var3) throws ClassNotFoundException;
}

