/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class MPLGeneratorClassLoaderBuilder {
    public static Class<? extends GeneratorClassLoader> buildImplementation() {
        Method methodHandlesAPIDefineClass = MPLGeneratorClassLoaderBuilder.getMethodHandlesAPIDefineClassMethod();
        if (methodHandlesAPIDefineClass != null) {
            ImplementationSource source = MPLGeneratorClassLoaderBuilder.buildSource(MPLGeneratorClassLoaderBuilder.class, "GeneratorClassLoaderImpl");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            try {
                return (Class)methodHandlesAPIDefineClass.invoke((Object)lookup, new Object[]{source.byteCode});
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
        ImplementationSource source = MPLGeneratorClassLoaderBuilder.buildSource(GeneratorClassLoader.class, "impl");
        ClassLoader loader = GeneratorClassLoader.class.getClassLoader();
        Method classLoaderDefineClassMethod = MPLGeneratorClassLoaderBuilder.getClassLoaderAPIDefineClassMethod();
        Object protectionDomain = null;
        try {
            return (Class)classLoaderDefineClassMethod.invoke((Object)loader, source.className, source.byteCode, 0, source.byteCode.length, protectionDomain);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static Method getMethodHandlesAPIDefineClassMethod() {
        Method getMethodMethod;
        try {
            Class.forName("java.lang.invoke.MethodHandles$Lookup");
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
        try {
            getMethodMethod = Class.class.getMethod(String.join((CharSequence)"", "get", "Method"), String.class, Class[].class);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        try {
            return (Method)getMethodMethod.invoke(MethodHandles.Lookup.class, String.join((CharSequence)"", "define", "Class"), new Class[]{byte[].class});
        }
        catch (InvocationTargetException inv_ex) {
            if (inv_ex.getCause() instanceof NoSuchMethodException) {
                return null;
            }
            throw MountiplexUtil.uncheckedRethrow(inv_ex.getCause());
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static Method getClassLoaderAPIDefineClassMethod() {
        Method defineClassMethod;
        Method getDeclaredMethodMethod;
        try {
            getDeclaredMethodMethod = Class.class.getMethod(String.join((CharSequence)"", "get", "Declared", "Method"), String.class, Class[].class);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        try {
            defineClassMethod = (Method)getDeclaredMethodMethod.invoke(ClassLoader.class, String.join((CharSequence)"", "define", "Class"), new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class});
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        try {
            defineClassMethod.setAccessible(true);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.severe("Failed to make ClassLoader.defineClass() accessible. Try adding one of these JVM flags:\n  - JDK9 to JDK14: --illegal-access=permit\n  - JDK15 and beyond: --add-opens java.base/java.lang=ALL-UNNAMED");
            throw new UnsupportedOperationException("Failed to make ClassLoader.defineClass() accessible", t);
        }
        return defineClassMethod;
    }

    private static ImplementationSource buildSource(Class<?> baseClass, String prefix) {
        String implName = prefix;
        UniqueHash hash = new UniqueHash();
        while (true) {
            try {
                Class.forName(baseClass.getName() + "$" + implName);
            }
            catch (ClassNotFoundException ex) {
                break;
            }
            implName = prefix + hash.nextHex();
        }
        ClassWriter classWriter = new ClassWriter(0);
        String base_internalName = Type.getInternalName(baseClass);
        String gcl_internalName = Type.getInternalName(GeneratorClassLoader.class);
        classWriter.visit(52, 33, base_internalName + "$" + implName, null, gcl_internalName, null);
        MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", "(Ljava/lang/ClassLoader;)V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitMethodInsn(183, gcl_internalName, "<init>", "(Ljava/lang/ClassLoader;)V", false);
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(4, "defineClassFromBytecode", "(Ljava/lang/String;[BLjava/security/ProtectionDomain;)Ljava/lang/Class;", "(Ljava/lang/String;[BLjava/security/ProtectionDomain;)Ljava/lang/Class<*>;", null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitVarInsn(25, 2);
        methodVisitor.visitInsn(3);
        methodVisitor.visitVarInsn(25, 2);
        methodVisitor.visitInsn(190);
        methodVisitor.visitVarInsn(25, 3);
        methodVisitor.visitMethodInsn(183, gcl_internalName, "defineClass", "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;", false);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(6, 4);
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        byte[] byteCode = classWriter.toByteArray();
        return new ImplementationSource(baseClass.getName() + "$" + implName, byteCode);
    }

    private static class ImplementationSource {
        public final String className;
        public final byte[] byteCode;

        public ImplementationSource(String className, byte[] byteCode) {
            this.className = className;
            this.byteCode = byteCode;
        }
    }
}

