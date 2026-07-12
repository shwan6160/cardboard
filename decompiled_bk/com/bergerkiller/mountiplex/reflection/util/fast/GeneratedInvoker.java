/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class GeneratedInvoker<T>
implements Invoker<T> {
    public static boolean canCreate(Executable executable) {
        Class<?>[] paramTypes = executable.getParameterTypes();
        if (paramTypes.length > 5) {
            return false;
        }
        if (executable instanceof Method) {
            return Resolver.isPublic((Method)executable);
        }
        if (executable instanceof Constructor) {
            return Resolver.isPublic((Constructor)executable);
        }
        return false;
    }

    public static <T> GeneratedInvoker<T> create(Executable executable) {
        int i;
        boolean isStatic;
        Class<?> returnType;
        Class<?> instanceType = executable.getDeclaringClass();
        ExtendedClassWriter cw = ExtendedClassWriter.builder(GeneratedInvoker.class).setClassLoader(instanceType.getClassLoader()).setFlags(1).setAccess(16).build();
        String instanceName = MPLType.getInternalName(instanceType);
        Class<?>[] paramTypes = executable.getParameterTypes();
        if (paramTypes.length > 5) {
            throw new IllegalArgumentException("Method has too many parameters to be optimizable");
        }
        if (executable instanceof Constructor) {
            returnType = executable.getDeclaringClass();
            isStatic = true;
        } else if (executable instanceof Method) {
            returnType = ((Method)executable).getReturnType();
            isStatic = Modifier.isStatic(executable.getModifiers());
        } else {
            throw new IllegalArgumentException("Not a constructor or method");
        }
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, MPLType.getInternalName(GeneratedInvoker.class), "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        String argsStr = GeneratedInvoker.buildInvokeDescriptor(paramTypes.length);
        mv = cw.visitMethod(145, "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        GeneratedInvoker.visitInvokeVAArgCountCheck(mv, paramTypes.length);
        if (executable instanceof Constructor) {
            mv.visitTypeInsn(187, MPLType.getInternalName(instanceType));
            mv.visitInsn(89);
        }
        if (!isStatic) {
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, instanceName);
        }
        for (i = 0; i < paramTypes.length; ++i) {
            mv.visitVarInsn(25, 2);
            ExtendedClassWriter.visitPushInt(mv, i);
            mv.visitInsn(50);
            ExtendedClassWriter.visitUnboxObjectVariable(mv, paramTypes[i]);
        }
        if (executable instanceof Method) {
            ExtendedClassWriter.visitInvoke(mv, instanceType, (Method)executable);
        } else if (executable instanceof Constructor) {
            mv.visitMethodInsn(183, MPLType.getInternalName(instanceType), "<init>", MPLType.getConstructorDescriptor((Constructor)executable), false);
        }
        MPLType.visitBoxVariable(mv, returnType);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        mv = cw.visitMethod(1, "invoke", argsStr, null, null);
        mv.visitCode();
        if (executable instanceof Constructor) {
            mv.visitTypeInsn(187, MPLType.getInternalName(instanceType));
            mv.visitInsn(89);
        }
        if (!isStatic) {
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, instanceName);
        }
        for (i = 0; i < paramTypes.length; ++i) {
            mv.visitVarInsn(25, 2 + i);
            ExtendedClassWriter.visitUnboxObjectVariable(mv, paramTypes[i]);
        }
        if (executable instanceof Method) {
            ExtendedClassWriter.visitInvoke(mv, instanceType, (Method)executable);
        } else if (executable instanceof Constructor) {
            mv.visitMethodInsn(183, MPLType.getInternalName(instanceType), "<init>", MPLType.getConstructorDescriptor((Constructor)executable), false);
        }
        MPLType.visitBoxVariable(mv, returnType);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return (GeneratedInvoker)cw.generateInstance();
    }

    public static String buildInvokeDescriptor(int numArgs) {
        String argsStr_obj_token = "Ljava/lang/Object;";
        StringBuilder argsStr_build = new StringBuilder(argsStr_obj_token.length() * (numArgs + 1) + 2);
        argsStr_build.append('(');
        for (int i = 0; i <= numArgs; ++i) {
            argsStr_build.append(argsStr_obj_token);
        }
        argsStr_build.append(')');
        argsStr_build.append(argsStr_obj_token);
        return argsStr_build.toString();
    }

    public static void visitInvokeVAArgCountCheck(MethodVisitor mv, int argCount) {
        mv.visitVarInsn(25, 2);
        mv.visitInsn(190);
        Label l_validArgs = new Label();
        if (argCount > 0) {
            ExtendedClassWriter.visitPushInt(mv, argCount);
            mv.visitJumpInsn(159, l_validArgs);
        } else {
            mv.visitJumpInsn(153, l_validArgs);
        }
        String exceptionTypeName = MPLType.getInternalName(InvalidArgumentCountException.class);
        mv.visitTypeInsn(187, exceptionTypeName);
        mv.visitInsn(89);
        mv.visitLdcInsn("method");
        mv.visitVarInsn(25, 2);
        mv.visitInsn(190);
        ExtendedClassWriter.visitPushInt(mv, argCount);
        mv.visitMethodInsn(183, exceptionTypeName, "<init>", "(Ljava/lang/String;II)V", false);
        mv.visitInsn(191);
        mv.visitLabel(l_validArgs);
        mv.visitFrame(3, 0, null, 0, null);
    }
}

