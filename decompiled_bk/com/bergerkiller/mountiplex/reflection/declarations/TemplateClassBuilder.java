/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateClassBuilder<C extends Template.Class<H>, H extends Template.Handle> {
    public final ClassDeclarationResolver classDeclarationResolver;
    public final Class<C> classType;
    public final Class<H> handleType;
    public final Class<?> instanceType;
    public final String instanceClassPath;
    public final String classPackage;
    public final List<String> classImports;
    public final boolean isOptional;
    public final ClassDeclaration classDec;

    public TemplateClassBuilder(Class<C> classType, ClassDeclarationResolver classDeclarationResolver) {
        this.classDeclarationResolver = classDeclarationResolver;
        this.classType = classType;
        Class<?> handleType = this.classType.getDeclaringClass();
        this.handleType = handleType != null && Template.Handle.class.isAssignableFrom(handleType) ? handleType : Template.Handle.class;
        this.instanceClassPath = ReflectionUtil.recurseFindAnnotationValue(classType, Template.InstanceType.class, Template.InstanceType::value, "java.lang.Object");
        this.isOptional = ReflectionUtil.recurseFindAnnotationValue(classType, Template.Optional.class, a -> Boolean.TRUE, Boolean.FALSE);
        this.classPackage = ReflectionUtil.recurseFindAnnotationValue(classType, Template.Package.class, Template.Package::value, null);
        this.classImports = ReflectionUtil.getAllDeclaringClasses(classType).flatMap(c -> Stream.of((Template.Import[])c.getAnnotationsByType(Template.Import.class))).map(Template.Import::value).collect(Collectors.toList());
        Collections.reverse(this.classImports);
        this.instanceType = Resolver.loadClass(this.instanceClassPath, false);
        if (this.instanceType == null && !this.isOptional) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Class " + this.instanceClassPath + " not found; Template '" + handleType.getSimpleName() + " not initialized.");
        }
        this.classDec = this.instanceType != null && this.instanceType != Object.class && classDeclarationResolver != null ? classDeclarationResolver.resolveClassDeclaration(this.instanceClassPath, this.instanceType) : null;
    }

    public C build() {
        if (Modifier.isAbstract(this.classType.getModifiers())) {
            return this.buildExtend();
        }
        return this.buildDefault();
    }

    private C buildDefault() {
        try {
            Template.Class generatedClass = (Template.Class)this.classType.newInstance();
            generatedClass.init(this);
            return (C)generatedClass;
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private C buildExtend() {
        ExtendedClassWriter cw = ExtendedClassWriter.builder(this.classType).setFlags(1).setAccess(16).setClassLoader(this.classType.getClassLoader()).setPostfix("$impl").build();
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, MPLType.getInternalName(this.classType), "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getSelfClassType", "()Ljava/lang/Class;", "()Ljava/lang/Class<*>;", null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mv.visitMethodInsn(182, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
        mv.visitInsn(176);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        int fieldNameIdx = 1;
        ClassResolver resolver = null;
        for (Method method : this.classType.getDeclaredMethods()) {
            String preprocessedDeclarationStr;
            Declaration parsedDeclaration;
            Template.Generated generatedAnnot = method.getAnnotation(Template.Generated.class);
            if (generatedAnnot == null) continue;
            if (resolver == null) {
                resolver = new ClassResolver();
                resolver.setClassLoader(cw.getClassLoader());
                resolver.setDeclaredClass(this.instanceType, this.instanceClassPath);
                if (this.classDeclarationResolver != null) {
                    resolver.setAllVariables(this.classDeclarationResolver);
                } else {
                    resolver.setAllVariables(Resolver.resolveClassVariables(this.instanceClassPath, this.instanceType));
                }
                if (this.classPackage != null) {
                    resolver.setPackage(this.classPackage, false);
                }
                resolver.addImports(this.classImports);
                StringBuilder requirementsText = new StringBuilder();
                for (Template.Require require : (Template.Require[])this.classType.getAnnotationsByType(Template.Require.class)) {
                    String body = SourceDeclaration.preprocess(require.value(), resolver).trim();
                    if (body.isEmpty()) continue;
                    if (!require.declaring().isEmpty()) {
                        requirementsText.append("#require ");
                        requirementsText.append(require.declaring()).append(' ');
                    } else if (!body.startsWith("#")) {
                        requirementsText.append("#require ");
                    }
                    requirementsText.append(body).append('\n');
                }
                if (requirementsText.length() > 0) {
                    requirementsText.insert(0, "class DummyClassPleaseIgnore {\n");
                    requirementsText.append("}");
                    ClassDeclaration cc = new ClassDeclaration(resolver, requirementsText.toString());
                    for (Requirement r : cc.getResolver().getRequirements()) {
                        resolver.storeRequirement(r);
                    }
                }
            }
            if ((parsedDeclaration = Declaration.parseDeclaration(resolver, preprocessedDeclarationStr = SourceDeclaration.preprocess(generatedAnnot.value(), resolver))) == null || !parsedDeclaration.isValid()) {
                MountiplexUtil.LOGGER.warning("Declaration for method " + MPLType.getName(method) + " could not be parsed: " + preprocessedDeclarationStr);
                cw.visitMethodUnsupported(method, "Declaration for this generated method could not be parsed");
                continue;
            }
            if (!parsedDeclaration.isResolved()) {
                MountiplexUtil.LOGGER.warning("Declaration for method " + MPLType.getName(method) + " could not be resolved: " + parsedDeclaration);
                cw.visitMethodUnsupported(method, "Declaration for this generated method could not be resolved (missing types)");
                continue;
            }
            Declaration declaration = parsedDeclaration.discover();
            if (declaration == null) {
                cw.visitMethodUnsupported(method, "Failed to find: " + parsedDeclaration);
                continue;
            }
            if (declaration instanceof FieldDeclaration) {
                cw.visitMethodUnsupported(method, "Field getters/setters not implemented yet");
                continue;
            }
            if (declaration instanceof MethodDeclaration) {
                int varIdx;
                boolean isPublic;
                MethodDeclaration methodDec = (MethodDeclaration)declaration;
                if (!methodDec.modifiers.isStatic() && methodDec.constructor == null) {
                    cw.visitMethodUnsupported(method, "Local methods cannot be called statically");
                    continue;
                }
                if (methodDec.body == null && methodDec.method == null && methodDec.constructor == null) {
                    cw.visitMethodUnsupported(method, "Static method '" + methodDec.name.toString() + "' was not found");
                    continue;
                }
                boolean hasConversion = false;
                if (methodDec.returnType.cast != null && !methodDec.returnType.cast.isAssignableFrom(methodDec.returnType) && !methodDec.returnType.isAssignableFrom(methodDec.returnType.cast)) {
                    hasConversion = true;
                } else {
                    for (ParameterDeclaration parameterDeclaration : methodDec.parameters.parameters) {
                        if (parameterDeclaration.type.cast == null || parameterDeclaration.type.isAssignableFrom(parameterDeclaration.type.cast) || parameterDeclaration.type.cast.isAssignableFrom(parameterDeclaration.type)) continue;
                        hasConversion = true;
                        break;
                    }
                }
                if (hasConversion) {
                    cw.visitMethodUnsupported(method, "Conversion of parameters/return type is not supported yet");
                    continue;
                }
                boolean bl = isPublic = methodDec.method != null && Modifier.isPublic(methodDec.method.getModifiers()) || methodDec.constructor != null && Modifier.isPublic(methodDec.constructor.getModifiers());
                if (methodDec.body == null && isPublic && Resolver.isPublic(this.instanceType)) {
                    mv = cw.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
                    mv.visitCode();
                    if (methodDec.constructor != null) {
                        mv.visitTypeInsn(187, MPLType.getInternalName(methodDec.constructor.getDeclaringClass()));
                        mv.visitInsn(89);
                    }
                    int varIdx2 = 1;
                    for (ParameterDeclaration param : methodDec.parameters.parameters) {
                        varIdx2 = MPLType.visitVarILoad(mv, varIdx2, param.type.exposed().type);
                        if (param.type.cast == null) continue;
                        ExtendedClassWriter.visitUnboxObjectVariable(mv, param.type.type);
                    }
                    if (methodDec.constructor != null) {
                        mv.visitMethodInsn(183, MPLType.getInternalName(this.instanceType), "<init>", MPLType.getInternalMethodDescriptor(methodDec), false);
                    } else {
                        mv.visitMethodInsn(184, MPLType.getInternalName(this.instanceType), methodDec.name.value(), MPLType.getInternalMethodDescriptor(methodDec), false);
                    }
                    if (methodDec.returnType.cast != null) {
                        ExtendedClassWriter.visitUnboxObjectVariable(mv, methodDec.returnType.cast.type);
                    }
                    mv.visitInsn(MPLType.getOpcode(method.getReturnType(), 172));
                    if (methodDec.constructor != null) {
                        mv.visitMaxs(varIdx2 + 1, varIdx2);
                    } else {
                        mv.visitMaxs(varIdx2, varIdx2);
                    }
                    mv.visitEnd();
                    continue;
                }
                String invoker_name = methodDec.name.real() + "_invoker_" + fieldNameIdx++;
                if (invoker_name.startsWith("<init>_")) {
                    invoker_name = "initializer_" + invoker_name.substring(7);
                }
                cw.visitStaticInvokerField(invoker_name, methodDec);
                mv = cw.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
                mv.visitCode();
                mv.visitFieldInsn(178, cw.getInternalName(), invoker_name, MPLType.getDescriptor(Invoker.class));
                mv.visitInsn(1);
                if (methodDec.parameters.parameters.length > 5) {
                    ExtendedClassWriter.visitPushInt(mv, methodDec.parameters.parameters.length);
                    mv.visitTypeInsn(189, "java/lang/Object");
                    varIdx = 1;
                    for (Class<?> paramType : method.getParameterTypes()) {
                        mv.visitInsn(89);
                        ExtendedClassWriter.visitPushInt(mv, varIdx - 1);
                        varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, paramType);
                        mv.visitInsn(83);
                    }
                    mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                } else {
                    varIdx = 1;
                    for (Class<?> paramType : method.getParameterTypes()) {
                        varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, paramType);
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append('(');
                    for (int i = 0; i <= methodDec.parameters.parameters.length; ++i) {
                        stringBuilder.append("Ljava/lang/Object;");
                    }
                    stringBuilder.append(")Ljava/lang/Object;");
                    mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invoke", stringBuilder.toString(), true);
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
                continue;
            }
            cw.visitMethodUnsupported(method, "Not supported yet");
        }
        Template.Class generatedClass = (Template.Class)cw.generateInstance(new Class[0], new Object[0]);
        generatedClass.init(this);
        return (C)generatedClass;
    }
}

