/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class SafeMethod<T>
implements MethodAccessor<T> {
    private final FastMethod<T> method;

    public SafeMethod(FastMethod<T> method) {
        if (method == null) {
            throw new IllegalArgumentException("Backing method can not be null");
        }
        this.method = method;
    }

    public SafeMethod(Method method) {
        this.method = new FastMethod();
        this.method.init(method);
    }

    public SafeMethod(String methodPath, Class<?> ... parameterTypes) {
        this.method = new FastMethod();
        if (methodPath == null || methodPath.isEmpty() || !methodPath.contains(".")) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Method path contains no class: " + methodPath);
            return;
        }
        try {
            String className = MountiplexUtil.getLastBefore(methodPath, ".");
            String methodName = methodPath.substring(className.length() + 1);
            Class<?> type = Resolver.loadClass(className, false);
            this.load(type, methodName, parameterTypes);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to load method '" + methodPath + "'", t);
        }
    }

    public SafeMethod(Object value, String name, Class<?> ... parameterTypes) {
        this.method = new FastMethod();
        this.load(value == null ? null : value.getClass(), name, parameterTypes);
    }

    public SafeMethod(Class<?> source, String name, Class<?> ... parameterTypes) {
        this.method = new FastMethod();
        this.load(source, name, parameterTypes);
    }

    private void load(Class<?> source, String name, Class<?> ... parameterTypes) {
        if (source == null) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Can not load method '" + name + "' because the class is null!", new IllegalStateException("Missing source"));
            return;
        }
        String fixedName = Resolver.resolveMethodName(source, name, parameterTypes);
        String dispName = name.equals(fixedName) ? name : name + ":" + fixedName;
        dispName = dispName + "(";
        for (int i = 0; i < parameterTypes.length; ++i) {
            if (i > 0) {
                dispName = dispName + ", ";
            }
            dispName = dispName + parameterTypes[i].getSimpleName();
        }
        dispName = dispName + ")";
        this.method.init(SafeMethod.findRaw(source, fixedName, parameterTypes));
        if (!this.method.isAvailable()) {
            String message = "Method '" + dispName + "' could not be found in class " + MPLType.getName(source);
            this.method.initUnavailable(message);
            MountiplexUtil.LOGGER.warning(message);
        }
    }

    public String getName() {
        return this.method.getName();
    }

    public boolean isOverridedIn(Class<?> type) {
        try {
            Method sm = this.method.getMethod();
            if (sm == null) {
                return false;
            }
            String name = MPLType.getName(sm);
            Method m = MPLType.getDeclaredMethod(type, name, sm.getParameterTypes());
            return m.getDeclaringClass() != sm.getDeclaringClass();
        }
        catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isValid() {
        return this.method.isAvailable();
    }

    @Override
    public T invoke(Object instance, Object ... args) {
        return this.method.invokeVA(instance, args);
    }

    public static boolean contains(Class<?> type, String name, Class<?> ... parameterTypes) {
        return SafeMethod.findRaw(type, Resolver.resolveMethodName(type, name, parameterTypes), parameterTypes) != null;
    }

    private static Method findRaw(Class<?> type, String name, Class<?> ... parameterTypes) {
        for (Class<?> tmp = type; tmp != null; tmp = tmp.getSuperclass()) {
            try {
                return MPLType.getDeclaredMethod(tmp, name, parameterTypes);
            }
            catch (NoSuchMethodException ex) {
                continue;
            }
        }
        for (Class<?> interfaceClass : type.getInterfaces()) {
            try {
                return interfaceClass.getDeclaredMethod(name, parameterTypes);
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        return null;
    }

    @Override
    public boolean isMethod(Method method) {
        Class<?> declare_b;
        Class<?>[] args_b;
        Method sm = this.method.getMethod();
        if (sm == null) {
            return false;
        }
        if (sm.equals(method)) {
            return true;
        }
        if (!MPLType.getName(sm).equals(MPLType.getName(method))) {
            return false;
        }
        if (!sm.getReturnType().equals(method.getReturnType())) {
            return false;
        }
        Class<?>[] args_a = sm.getParameterTypes();
        if (args_a.length != (args_b = method.getParameterTypes()).length) {
            return false;
        }
        for (int i = 0; i < args_a.length; ++i) {
            if (args_a[i].equals(args_b[i])) continue;
            return false;
        }
        Class<?> declare_a = sm.getDeclaringClass();
        return declare_a.isAssignableFrom(declare_b = method.getDeclaringClass()) || declare_b.isAssignableFrom(declare_a);
    }
}

