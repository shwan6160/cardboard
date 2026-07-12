/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Constructor;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;

public abstract class GeneratedConstructor
implements Constructor<Object> {
    private final java.lang.reflect.Constructor<Object> c;

    public GeneratedConstructor(java.lang.reflect.Constructor<?> constructor) {
        this.c = constructor;
    }

    @Override
    public Object newInstance() {
        throw this.failArgs(0);
    }

    @Override
    public Object newInstance(Object arg0) {
        throw this.failArgs(1);
    }

    @Override
    public Object newInstance(Object arg0, Object arg1) {
        throw this.failArgs(2);
    }

    @Override
    public Object newInstance(Object arg0, Object arg1, Object arg2) {
        throw this.failArgs(3);
    }

    @Override
    public Object newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
        throw this.failArgs(4);
    }

    @Override
    public Object newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        throw this.failArgs(5);
    }

    protected final InvalidArgumentCountException failArgs(int numArgs) {
        return new InvalidArgumentCountException("constructor", numArgs, this.c.getParameterTypes().length);
    }

    public static GeneratedConstructor create(java.lang.reflect.Constructor<?> constructor) {
        ExtendedClassWriter cw = ExtendedClassWriter.builder(GeneratedConstructor.class).setFlags(1).setAccess(16).build();
        Class<?> instanceType = constructor.getDeclaringClass();
        String instanceName = MPLType.getInternalName(instanceType);
        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length > 5) {
            throw new IllegalArgumentException("Constructor has too many parameters to be optimizable");
        }
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(Ljava/lang/reflect/Constructor;)V", "(Ljava/lang/reflect/Constructor<*>;)V", null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(183, MPLType.getInternalName(GeneratedConstructor.class), "<init>", "(Ljava/lang/reflect/Constructor;)V", false);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        String argsStr_obj_token = "Ljava/lang/Object;";
        StringBuilder argsStr_build = new StringBuilder(argsStr_obj_token.length() * (paramTypes.length + 1));
        argsStr_build.append('(');
        for (int i = 0; i < paramTypes.length; ++i) {
            argsStr_build.append(argsStr_obj_token);
        }
        argsStr_build.append(")Ljava/lang/Object;");
        String argsStr = argsStr_build.toString();
        mv = cw.visitMethod(129, "newInstanceVA", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitInsn(190);
        Label l_validArgs = new Label();
        if (paramTypes.length > 0) {
            mv.visitInsn(3 + paramTypes.length);
            mv.visitJumpInsn(159, l_validArgs);
        } else {
            mv.visitJumpInsn(153, l_validArgs);
        }
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitInsn(190);
        mv.visitMethodInsn(182, MPLType.getInternalName(GeneratedConstructor.class), "failArgs", "(I)" + MPLType.getDescriptor(InvalidArgumentCountException.class), false);
        mv.visitInsn(191);
        mv.visitLabel(l_validArgs);
        mv.visitFrame(3, 0, null, 0, null);
        mv.visitTypeInsn(187, instanceName);
        mv.visitInsn(89);
        for (int i = 0; i < paramTypes.length; ++i) {
            mv.visitVarInsn(25, 1);
            mv.visitInsn(3 + i);
            mv.visitInsn(50);
            ExtendedClassWriter.visitUnboxObjectVariable(mv, paramTypes[i]);
        }
        ExtendedClassWriter.visitInit(mv, instanceType, constructor);
        mv.visitInsn(176);
        mv.visitMaxs(5, 2);
        mv.visitEnd();
        mv = cw.visitMethod(1, "newInstance", argsStr, null, null);
        mv.visitCode();
        mv.visitTypeInsn(187, instanceName);
        mv.visitInsn(89);
        for (int i = 0; i < paramTypes.length; ++i) {
            mv.visitVarInsn(25, 1 + i);
            ExtendedClassWriter.visitUnboxObjectVariable(mv, paramTypes[i]);
        }
        ExtendedClassWriter.visitInit(mv, instanceType, constructor);
        mv.visitInsn(176);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
        return (GeneratedConstructor)cw.generateInstance(new Class[]{java.lang.reflect.Constructor.class}, new Object[]{constructor});
    }
}

