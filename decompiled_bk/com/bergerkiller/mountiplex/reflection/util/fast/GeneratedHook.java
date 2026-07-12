/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.ReflectionInvoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GeneratedHook {
    public static <T> Class<? extends T> generate(ClassLoader classLoader, Class<T> baseClass, Collection<Class<?>> interfaces, Function<Method, Invoker<?>> callbacks) {
        ExtendedClassWriter cw = ExtendedClassWriter.builder(baseClass).setFlags(1).addInterfaces(interfaces).setClassLoader(classLoader).build();
        String superMethodPrefix = GeneratedHook.getSuperMethodPrefix(baseClass);
        AtomicInteger counter = new AtomicInteger(0);
        for (Constructor<?> constructor : baseClass.getDeclaredConstructors()) {
            String constructorDesc = MPLType.getConstructorDescriptor(constructor);
            MethodVisitor mv = cw.visitMethod(constructor.getModifiers(), "<init>", constructorDesc, null, MPLType.getInternalNames(constructor.getExceptionTypes()));
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            MPLType.visitVarILoad(mv, 1, constructor.getParameterTypes());
            mv.visitMethodInsn(183, MPLType.getInternalName(baseClass), "<init>", constructorDesc, false);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        Stream.concat(ReflectionUtil.getAllClassesAndInterfaces(baseClass), interfaces.stream()).flatMap(ReflectionUtil::getDeclaredMethods).filter(method -> {
            int modifiers = method.getModifiers();
            return !Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers);
        }).filter(ReflectionUtil.createDuplicateMethodFilter()).filter(method -> !Modifier.isFinal(method.getModifiers())).forEachOrdered(method -> {
            Invoker callback = (Invoker)callbacks.apply((Method)method);
            if (callback != null) {
                GeneratedHook.implement(cw, method, callback, superMethodPrefix, counter);
            }
        });
        return cw.generate();
    }

    public static <T> Invoker<T> createSuperInvoker(Class<?> generatedClass, Method method) {
        String superMethodName = GeneratedHook.getSuperMethodPrefix(generatedClass.getSuperclass()) + MPLType.getName(method);
        Method methodToInvoke = method;
        try {
            methodToInvoke = generatedClass.getDeclaredMethod(superMethodName, method.getParameterTypes());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (GeneratedInvoker.canCreate(methodToInvoke)) {
            return GeneratedInvoker.create(methodToInvoke);
        }
        return ReflectionInvoker.create(methodToInvoke);
    }

    private static String getSuperMethodPrefix(Class<?> type) {
        int depth = 0;
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            ++depth;
        }
        return "mplsuper" + depth + "_";
    }

    public static <T> Invoker<T> createLocalField(Supplier<T> initialValue) {
        return new LocalFieldInvoker<T>(initialValue);
    }

    private static void implement(ExtendedClassWriter<?> cw, Method method, Invoker<?> callback, String superMethodPrefix, AtomicInteger counter) {
        MethodVisitor mv = cw.visitMethod(17, superMethodPrefix + MPLType.getName(method), MPLType.getMethodDescriptor(method), null, MPLType.getInternalNames(method.getExceptionTypes()));
        mv.visitCode();
        if (Modifier.isAbstract(method.getModifiers())) {
            mv.visitTypeInsn(187, "java/lang/UnsupportedOperationException");
            mv.visitInsn(89);
            mv.visitLdcInsn("Method is abstract");
            mv.visitMethodInsn(183, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(191);
        } else {
            mv.visitVarInsn(25, 0);
            MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
            cw.visitCallSuperMethod(mv, method);
            mv.visitInsn(MPLType.getOpcode(method.getReturnType(), 172));
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        String postfix = MPLType.getName(method) + "_" + counter.incrementAndGet();
        if (callback instanceof LocalFieldInvoker) {
            String returnTypeDesc = MPLType.getDescriptor(method.getReturnType());
            String fieldSupplierFieldName = "supplier_" + postfix;
            cw.visitStaticField(fieldSupplierFieldName, Supplier.class, ((LocalFieldInvoker)callback).initialValue);
            String localIsInitializedFieldName = "init_" + postfix;
            FieldVisitor fv = cw.visitField(1, localIsInitializedFieldName, "Z", null, null);
            fv.visitEnd();
            String localValueFieldName = "value_" + postfix;
            fv = cw.visitField(1, localValueFieldName, returnTypeDesc, null, null);
            fv.visitEnd();
            mv = cw.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, MPLType.getInternalNames(method.getExceptionTypes()));
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), localIsInitializedFieldName, "Z");
            Label label0 = new Label();
            mv.visitJumpInsn(153, label0);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), localValueFieldName, returnTypeDesc);
            mv.visitInsn(MPLType.getOpcode(method.getReturnType(), 172));
            mv.visitLabel(label0);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitFieldInsn(178, cw.getInternalName(), fieldSupplierFieldName, MPLType.getDescriptor(Supplier.class));
            mv.visitMethodInsn(185, MPLType.getInternalName(Supplier.class), "get", "()Ljava/lang/Object;", true);
            ExtendedClassWriter.visitUnboxObjectVariable(mv, method.getReturnType());
            int varResultIdx = 1;
            for (Class<?> type : method.getParameterTypes()) {
                varResultIdx = MPLType.getType(type).getSize();
            }
            mv.visitVarInsn(58, varResultIdx);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, varResultIdx);
            mv.visitFieldInsn(181, cw.getInternalName(), localValueFieldName, returnTypeDesc);
            mv.visitVarInsn(25, 0);
            mv.visitInsn(4);
            mv.visitFieldInsn(181, cw.getInternalName(), localIsInitializedFieldName, "Z");
            mv.visitVarInsn(25, varResultIdx);
            mv.visitInsn(176);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            return;
        }
        String invokerFieldName = "invoker_" + postfix;
        Class<?>[] parameterTypes = method.getParameterTypes();
        cw.visitStaticField(invokerFieldName, Invoker.class, callback);
        mv = cw.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, MPLType.getInternalNames(method.getExceptionTypes()));
        mv.visitCode();
        mv.visitFieldInsn(178, cw.getInternalName(), invokerFieldName, MPLType.getDescriptor(Invoker.class));
        mv.visitVarInsn(25, 0);
        if (parameterTypes.length <= 5) {
            int varIdx = 1;
            for (Class<?> parameterType : parameterTypes) {
                varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, parameterType);
            }
            StringBuilder descriptor = new StringBuilder();
            descriptor.append('(');
            for (int i = 0; i <= parameterTypes.length; ++i) {
                descriptor.append("Ljava/lang/Object;");
            }
            descriptor.append(")Ljava/lang/Object;");
            mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invoke", descriptor.toString(), true);
        } else {
            ExtendedClassWriter.visitPushInt(mv, parameterTypes.length);
            mv.visitTypeInsn(189, "java/lang/Object");
            int varIdx = 1;
            for (int i = 0; i < parameterTypes.length; ++i) {
                mv.visitInsn(89);
                ExtendedClassWriter.visitPushInt(mv, i);
                varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, parameterTypes[i]);
                mv.visitInsn(83);
            }
            mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", true);
        }
        if (method.getReturnType() == Void.TYPE) {
            mv.visitInsn(87);
            mv.visitInsn(177);
        } else {
            ExtendedClassWriter.visitUnboxObjectVariable(mv, method.getReturnType());
            mv.visitInsn(MPLType.getOpcode(method.getReturnType(), 172));
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static class LocalFieldInvoker<T>
    implements Invoker<T> {
        private final Supplier<T> initialValue;

        public LocalFieldInvoker(Supplier<T> initialValue) {
            this.initialValue = initialValue;
        }

        @Override
        public T invokeVA(Object instance, Object ... args) {
            return this.initialValue.get();
        }
    }
}

