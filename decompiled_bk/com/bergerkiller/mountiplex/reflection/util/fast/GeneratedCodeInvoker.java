/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.CtMethod;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.resolver.ResolvedClassPool;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLCtNewMethod;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedExactSignatureInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedInvoker;
import java.util.logging.Level;

public abstract class GeneratedCodeInvoker<T>
implements GeneratedExactSignatureInvoker<T>,
IgnoresRemapping {
    @Override
    public String getInvokerClassInternalName() {
        return MPLType.getInternalName(this.getClass());
    }

    @Override
    public String getInvokerClassTypeDescriptor() {
        return MPLType.getDescriptor(this.getClass());
    }

    public static <T> GeneratedCodeInvoker<T> create(MethodDeclaration declaration) {
        ExtendedClassWriter<GeneratedCodeInvoker<T>> writer = ExtendedClassWriter.builder(GeneratedCodeInvoker.class).setFlags(1).setClassLoader(declaration.getResolver().getClassLoader()).setSingleton(true).build();
        return GeneratedCodeInvoker.generate(writer, declaration);
    }

    public static <T> ExtendedClassWriter.Deferred<GeneratedCodeInvoker<T>> createDefer(MethodDeclaration declaration) {
        return ExtendedClassWriter.builder(GeneratedCodeInvoker.class).setFlags(1).setClassLoader(declaration.getResolver().getClassLoader()).setSingleton(true).defer(writer -> GeneratedCodeInvoker.generate(writer, declaration));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static <T> GeneratedCodeInvoker<T> generate(ExtendedClassWriter<GeneratedCodeInvoker<T>> writer, MethodDeclaration declaration) {
        if (!declaration.isResolved()) {
            throw new IllegalArgumentException("Declaration not resolved: " + declaration.toString());
        }
        declaration.checkTemplateErrors();
        try (ResolvedClassPool pool = ResolvedClassPool.create();){
            String class_path;
            String package_path;
            int argCount = declaration.parameters.parameters.length;
            pool.setRemappings(declaration.getResolver().getRemappings());
            GeneratedCodeInvoker.asmAddInvokeMethod(writer, declaration, true);
            if (argCount <= 5) {
                GeneratedCodeInvoker.asmAddInvokeMethod(writer, declaration, false);
            }
            for (Requirement req : declaration.bodyRequirements) {
                req.declaration.addAsRequirement(writer, req, req.name);
            }
            ClassResolver classResolver = declaration.getResolver();
            classResolver.getAllImports().forEachOrdered(importName -> {
                if (importName.endsWith(".*")) {
                    String packagePath = importName.substring(0, importName.length() - 2);
                    if (!packagePath.contains("*")) {
                        pool.importPackage(packagePath);
                    }
                } else {
                    pool.importPackage((String)importName);
                }
            });
            if (classResolver.hasPackage()) {
                pool.importPackage(classResolver.getPackage());
            } else if (classResolver.getDeclaredClass() != null && !(package_path = MountiplexUtil.getPackagePathFromClassPath(class_path = declaration.getResolver().getDeclaredClassName())).isEmpty()) {
                pool.importPackage(package_path);
            }
            CtClass invoker = writer.getCtClass(pool);
            StringBuilder methodBody = new StringBuilder();
            methodBody.setLength(0);
            methodBody.append("public ").append(ReflectionUtil.getAccessibleTypeName(declaration.returnType.type)).append(" ").append(declaration.name.real()).append("(");
            if (!declaration.modifiers.isStatic()) {
                methodBody.append(ReflectionUtil.getAccessibleTypeName(declaration.getDeclaringClass())).append(" instance");
                if (argCount > 0) {
                    methodBody.append(',');
                }
            }
            for (int i = 0; i < argCount; ++i) {
                ParameterDeclaration param = declaration.parameters.parameters[i];
                methodBody.append(ReflectionUtil.getAccessibleTypeName(param.type.type)).append(' ').append(param.name.real());
                if (i >= argCount - 1) continue;
                methodBody.append(',');
            }
            methodBody.append(") {\n");
            methodBody.append(declaration.body);
            if (declaration.returnType.type == Void.TYPE) {
                methodBody.append("return;");
            } else {
                methodBody.append("return ").append(BoxedType.getDefaultValue(declaration.returnType.type)).append(';');
            }
            methodBody.append('}');
            invoker.addMethod(GeneratedCodeInvoker.makeMethodAndLog(methodBody.toString(), invoker));
            try {
                GeneratedCodeInvoker<T> i = writer.generateInstance();
                return i;
            }
            catch (VerifyError ex) {
                MountiplexUtil.LOGGER.severe("Failed to verify generated method: " + declaration.body);
                throw ex;
            }
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static CtMethod makeMethodAndLog(String methodBody, CtClass invoker) {
        try {
            return MPLCtNewMethod.make(methodBody, invoker);
        }
        catch (CannotCompileException ex) {
            MountiplexUtil.LOGGER.severe("Failed to compile method body (" + ex.getReason() + "):");
            MountiplexUtil.LOGGER.severe(methodBody);
            throw MountiplexUtil.uncheckedRethrow(ex.getCause());
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to generate method body:", t);
            MountiplexUtil.LOGGER.severe(methodBody);
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static boolean mustCastType(Class<?> type) {
        return type != null && type != Object.class && Resolver.isPublic(type);
    }

    private static <T> void asmAddInvokeMethod(ExtendedClassWriter<GeneratedCodeInvoker<T>> writer, MethodDeclaration declaration, boolean invokeVA) {
        MethodVisitor mv;
        int argCount = declaration.parameters.parameters.length;
        int stackStart = invokeVA ? 3 : 2 + argCount;
        Class<?> instanceType = declaration.modifiers.isStatic() ? null : declaration.getDeclaringClass();
        Class<?> returnType = declaration.returnType.type;
        if (invokeVA) {
            mv = writer.visitMethod(145, "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            GeneratedInvoker.visitInvokeVAArgCountCheck(mv, argCount);
        } else {
            StringBuilder desc = new StringBuilder();
            desc.append("(Ljava/lang/Object;");
            for (int n = 0; n < argCount; ++n) {
                desc.append("Ljava/lang/Object;");
            }
            desc.append(")Ljava/lang/Object;");
            mv = writer.visitMethod(17, "invoke", desc.toString(), null, null);
        }
        int storeIndex = stackStart;
        for (int i = 0; i < argCount; ++i) {
            ParameterDeclaration param = declaration.parameters.parameters[i];
            if (!GeneratedCodeInvoker.mustCastType(param.type.type)) continue;
            if (invokeVA) {
                mv.visitVarInsn(25, 2);
                ExtendedClassWriter.visitPushInt(mv, i);
                mv.visitInsn(50);
            } else {
                mv.visitVarInsn(25, 2 + i);
            }
            ExtendedClassWriter.visitUnboxObjectVariable(mv, param.type.type);
            storeIndex = MPLType.visitVarIStore(mv, storeIndex, param.type.type);
        }
        StringBuilder invokeDescriptor = new StringBuilder();
        mv.visitVarInsn(25, 0);
        invokeDescriptor.append('(');
        if (instanceType != null) {
            mv.visitVarInsn(25, 1);
            if (GeneratedCodeInvoker.mustCastType(instanceType)) {
                ExtendedClassWriter.visitUnboxObjectVariable(mv, instanceType);
                invokeDescriptor.append(MPLType.getDescriptor(instanceType));
            } else {
                invokeDescriptor.append("Ljava/lang/Object;");
            }
        }
        int storeIndex2 = stackStart;
        for (int i = 0; i < argCount; ++i) {
            ParameterDeclaration param = declaration.parameters.parameters[i];
            if (GeneratedCodeInvoker.mustCastType(param.type.type)) {
                invokeDescriptor.append(MPLType.getDescriptor(param.type.type));
                storeIndex2 = MPLType.visitVarILoad(mv, storeIndex2, param.type.type);
                continue;
            }
            if (invokeVA) {
                invokeDescriptor.append("Ljava/lang/Object;");
                mv.visitVarInsn(25, 2);
                ExtendedClassWriter.visitPushInt(mv, i);
                mv.visitInsn(50);
                continue;
            }
            invokeDescriptor.append("Ljava/lang/Object;");
            mv.visitVarInsn(25, 2 + i);
        }
        invokeDescriptor.append(')');
        if (GeneratedCodeInvoker.mustCastType(returnType)) {
            invokeDescriptor.append(MPLType.getDescriptor(returnType));
        } else {
            invokeDescriptor.append("Ljava/lang/Object;");
        }
        mv.visitMethodInsn(182, writer.getInternalName(), declaration.name.real(), invokeDescriptor.toString(), false);
        MPLType.visitBoxVariable(mv, returnType);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}

