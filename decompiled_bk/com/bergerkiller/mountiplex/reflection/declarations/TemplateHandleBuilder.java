/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TemplateGenerator;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedExactSignatureInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class TemplateHandleBuilder<H extends Template.Handle> {
    private final Template.Class<H> templateClass;
    private final Class<H> handleType;
    private Class<? extends H> handleImplType;
    private final FastConstructor<H> handleConstructor = new FastConstructor();

    public TemplateHandleBuilder(Template.Class<H> templateClass) {
        this.templateClass = templateClass;
        this.handleType = templateClass.getHandleType();
        this.handleConstructor.initUnavailable("new " + this.handleType.getName() + "()");
    }

    public Class<? extends H> getImplType() {
        return this.handleImplType;
    }

    public H create(Object instance) {
        return (H)((Template.Handle)this.handleConstructor.newInstance(instance));
    }

    private Template.Class<?> getTemplateClass(Class<?> handleClass) {
        try {
            return (Template.Class)handleClass.getField("T").get(null);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static boolean isGeneratedInvoker(Template.TemplateElement<?> templateElement) {
        return templateElement instanceof Template.AbstractMethod && ((Template.AbstractMethod)templateElement).invoker instanceof GeneratedExactSignatureInvoker;
    }

    public void build() {
        if (this.templateClass.getType() == null) {
            throw new IllegalStateException("Handle internal type of " + this.handleType + " is null");
        }
        if (this.handleType == Template.Handle.class) {
            this.handleImplType = FallbackHandle.class;
            try {
                Constructor<? extends H> constructor;
                try {
                    constructor = this.handleImplType.getConstructor(Object.class);
                }
                catch (Throwable t) {
                    throw new IllegalStateException("Failed to find generated handle constructor for fallback handle", t);
                }
                this.handleConstructor.init(constructor);
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
            return;
        }
        ExtendedClassWriter cw = ExtendedClassWriter.builder(this.handleType).setFlags(1).setAccess(16).setPostfix("$impl").build();
        Class<Object> topInstanceType = this.templateClass.getType();
        boolean instanceAccessible = Resolver.getMeta(topInstanceType).isPublic;
        if (!instanceAccessible) {
            topInstanceType = Object.class;
        }
        String instanceTypeDesc = MPLType.getDescriptor(topInstanceType);
        String instanceTypeName = MPLType.getInternalName(topInstanceType);
        FieldVisitor fv = cw.visitField(17, "instance", instanceTypeDesc, null, null);
        fv.visitEnd();
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(" + instanceTypeDesc + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, MPLType.getInternalName(this.handleType), "<init>", "()V", false);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitFieldInsn(181, cw.getInternalName(), "instance", instanceTypeDesc);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        mv = cw.visitMethod(17, "getRaw", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
        mv.visitInsn(176);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        Class<H> currentHandleType = this.handleType;
        Template.Class<H> currentTemplateClass = this.templateClass;
        while (true) {
            String currentHandleName = MPLType.getInternalName(currentHandleType);
            Class<?> templateClassType = currentTemplateClass.getSelfClassType();
            String templateClassDesc = MPLType.getDescriptor(templateClassType);
            String templateClassName = MPLType.getInternalName(templateClassType);
            Class<?> instanceType = currentTemplateClass.getType();
            ClassDeclaration classDec = currentTemplateClass.getClassDeclaration();
            if (classDec == null) {
                throw new IllegalStateException("Template Handle Class " + currentTemplateClass + " has no Class Declaration! Not initialized?");
            }
            for (FieldDeclaration fieldDeclaration : classDec.fields) {
                String templateElementDesc;
                String templateElementName;
                if (fieldDeclaration.modifiers.isStatic() || fieldDeclaration.modifiers.isUnknown() || fieldDeclaration.modifiers.isOptional() || fieldDeclaration.isEnum) continue;
                Class<?> fieldType = fieldDeclaration.type.exposed().type;
                String fieldTypeDesc = MPLType.getDescriptor(fieldType);
                String fieldName = fieldDeclaration.name.real();
                try {
                    Class<?> templateElement = templateClassType.getField(fieldName).getType();
                    templateElementName = MPLType.getInternalName(templateElement);
                    templateElementDesc = MPLType.getDescriptor(templateElement);
                }
                catch (Throwable t) {
                    throw MountiplexUtil.uncheckedRethrow(t);
                }
                String accessorName = "";
                String accessorType = "Ljava/lang/Object;";
                for (Class<?> boxedType : BoxedType.getBoxedTypes()) {
                    if (!templateElementName.endsWith(boxedType.getSimpleName())) continue;
                    accessorName = boxedType.getSimpleName();
                    accessorType = fieldTypeDesc;
                    break;
                }
                boolean isPublicField = instanceAccessible && fieldDeclaration.type.cast == null && fieldDeclaration.field != null && Modifier.isPublic(fieldDeclaration.field.getModifiers());
                boolean isPublicNonfinalField = isPublicField && !Modifier.isFinal(fieldDeclaration.field.getModifiers());
                String getterName = TemplateGenerator.getGetterName(fieldDeclaration);
                mv = cw.visitMethod(17, getterName, "()" + fieldTypeDesc, null, null);
                mv.visitCode();
                if (isPublicField) {
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    mv.visitFieldInsn(180, instanceTypeName, fieldDeclaration.getAccessedName(), fieldTypeDesc);
                } else {
                    mv.visitFieldInsn(178, currentHandleName, "T", templateClassDesc);
                    mv.visitFieldInsn(180, templateClassName, fieldName, templateElementDesc);
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    mv.visitMethodInsn(182, templateElementName, "get" + accessorName, "(Ljava/lang/Object;)" + accessorType, false);
                    if (accessorName.isEmpty()) {
                        ExtendedClassWriter.visitUnboxObjectVariable(mv, fieldType);
                    }
                }
                mv.visitInsn(MPLType.getOpcode(fieldType, 172));
                mv.visitMaxs(2, 1);
                mv.visitEnd();
                if (fieldDeclaration.modifiers.isReadonly()) continue;
                String setterName = TemplateGenerator.getSetterName(fieldDeclaration);
                mv = cw.visitMethod(17, setterName, "(" + fieldTypeDesc + ")V", null, null);
                mv.visitCode();
                if (isPublicNonfinalField) {
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    mv.visitVarInsn(MPLType.getOpcode(fieldType, 21), 1);
                    mv.visitFieldInsn(181, instanceTypeName, fieldDeclaration.getAccessedName(), fieldTypeDesc);
                } else {
                    mv.visitFieldInsn(178, currentHandleName, "T", templateClassDesc);
                    mv.visitFieldInsn(180, templateClassName, fieldName, templateElementDesc);
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    mv.visitVarInsn(MPLType.getOpcode(fieldType, 21), 1);
                    if (accessorName.isEmpty()) {
                        MPLType.visitBoxVariable(mv, fieldType);
                    }
                    mv.visitMethodInsn(182, templateElementName, "set" + accessorName, "(Ljava/lang/Object;" + accessorType + ")V", false);
                }
                mv.visitInsn(177);
                mv.visitMaxs(4, 3);
                mv.visitEnd();
            }
            for (Declaration declaration : classDec.methods) {
                String templateElementDesc;
                String templateElementName;
                Template.TemplateElement templateElement;
                if (((MethodDeclaration)declaration).modifiers.isStatic() || ((MethodDeclaration)declaration).modifiers.isUnknown() || ((MethodDeclaration)declaration).modifiers.isOptional()) continue;
                if (!((MethodDeclaration)declaration).isResolved()) {
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Method " + declaration + " not implemented in handle for " + classDec.type + " because some types are not resolved");
                    continue;
                }
                String methodName = ((MethodDeclaration)declaration).name.real();
                boolean hasTypeConversion = !((MethodDeclaration)declaration).returnType.canDownCast();
                Class<?> returnType = ((MethodDeclaration)declaration).returnType.exposed().type;
                Class[] paramTypes = new Class[((MethodDeclaration)declaration).parameters.parameters.length];
                for (int i = 0; i < paramTypes.length; ++i) {
                    hasTypeConversion |= !((MethodDeclaration)declaration).parameters.parameters[i].type.canUpCast();
                    paramTypes[i] = ((MethodDeclaration)declaration).parameters.parameters[i].type.exposed().type;
                }
                String methodDesc = MPLType.getMethodDescriptor(returnType, paramTypes);
                try {
                    Field templateField = templateClassType.getField(methodName);
                    templateElement = (Template.TemplateElement)templateField.get(currentTemplateClass);
                    templateElementName = MPLType.getInternalName(templateField.getType());
                    templateElementDesc = MPLType.getDescriptor(templateField.getType());
                }
                catch (Throwable t) {
                    throw MountiplexUtil.uncheckedRethrow(t);
                }
                boolean canInline = instanceAccessible && ((MethodDeclaration)declaration).method != null && Modifier.isPublic(((MethodDeclaration)declaration).method.getModifiers()) && !hasTypeConversion;
                mv = cw.visitMethod(1, methodName, methodDesc, null, null);
                mv.visitCode();
                if (canInline) {
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    MPLType.visitVarILoad(mv, 1, paramTypes);
                    ExtendedClassWriter.visitInvoke(mv, instanceType, ((MethodDeclaration)declaration).method);
                    mv.visitInsn(MPLType.getOpcode(returnType, 172));
                } else if (TemplateHandleBuilder.isGeneratedInvoker(templateElement)) {
                    GeneratedExactSignatureInvoker invoker = (GeneratedExactSignatureInvoker)((Template.AbstractMethod)templateElement).invoker;
                    String generatedInternalName = invoker.getInvokerClassInternalName();
                    String generatedTypeDescriptor = invoker.getInvokerClassTypeDescriptor();
                    mv.visitFieldInsn(178, generatedInternalName, "INSTANCE", generatedTypeDescriptor);
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    MPLType.visitVarILoad(mv, 1, ((MethodDeclaration)declaration).parameters);
                    mv.visitMethodInsn(182, generatedInternalName, methodName, ((MethodDeclaration)declaration).getASMInvokeDescriptor(), false);
                    mv.visitInsn(MPLType.getOpcode(returnType, 172));
                } else {
                    mv.visitFieldInsn(178, currentHandleName, "T", templateClassDesc);
                    mv.visitFieldInsn(180, templateClassName, methodName, templateElementDesc);
                    boolean useInvokerField = templateElement instanceof Template.AbstractMethod;
                    if (useInvokerField) {
                        mv.visitFieldInsn(180, templateElementName, "invoker", MPLType.getDescriptor(Invoker.class));
                    }
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "instance", instanceTypeDesc);
                    if (paramTypes.length <= 5) {
                        StringBuilder invokeDescBldr = new StringBuilder();
                        invokeDescBldr.append("(Ljava/lang/Object;");
                        int varIdx = 1;
                        for (int i = 0; i < paramTypes.length; ++i) {
                            varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, paramTypes[i]);
                            invokeDescBldr.append("Ljava/lang/Object;");
                        }
                        invokeDescBldr.append(")Ljava/lang/Object;");
                        if (useInvokerField) {
                            mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invoke", invokeDescBldr.toString(), true);
                        } else {
                            mv.visitMethodInsn(182, templateElementName, "invoke", invokeDescBldr.toString(), false);
                        }
                    } else {
                        ExtendedClassWriter.visitPushInt(mv, paramTypes.length);
                        mv.visitTypeInsn(189, "java/lang/Object");
                        int varIdx = 1;
                        for (int i = 0; i < paramTypes.length; ++i) {
                            mv.visitInsn(89);
                            ExtendedClassWriter.visitPushInt(mv, i);
                            varIdx = MPLType.visitVarILoadAndBox(mv, varIdx, paramTypes[i]);
                            mv.visitInsn(83);
                        }
                        if (useInvokerField) {
                            mv.visitMethodInsn(185, MPLType.getInternalName(Invoker.class), "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                        } else {
                            mv.visitMethodInsn(182, templateElementName, "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
                        }
                    }
                    if (returnType.equals(Void.TYPE)) {
                        mv.visitInsn(87);
                        mv.visitInsn(177);
                    } else if (returnType.equals(Object.class)) {
                        mv.visitInsn(176);
                    } else {
                        ExtendedClassWriter.visitUnboxObjectVariable(mv, returnType);
                        mv.visitInsn(MPLType.getOpcode(returnType, 172));
                    }
                }
                mv.visitMaxs(3, 2);
                mv.visitEnd();
            }
            if ((currentHandleType = currentHandleType.getSuperclass()) == Template.Handle.class) break;
            currentTemplateClass = this.getTemplateClass(currentHandleType);
        }
        this.handleImplType = cw.generate();
        try {
            Constructor<? extends H> constructor;
            try {
                constructor = this.handleImplType.getConstructor(topInstanceType);
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to find generated handle constructor of handle for " + MPLType.getName(topInstanceType), t);
            }
            this.handleConstructor.init(constructor);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    public static boolean isCreateHandleMethod(MethodDeclaration method) {
        return method.modifiers.isStatic() && method.name.value().equals("createHandle") && method.parameters.parameters.length == 1 && method.parameters.parameters[0].type.isResolved() && method.parameters.parameters[0].type.type.equals(Object.class) && method.parameters.parameters[0].type.cast == null;
    }

    static final class FallbackHandle
    extends Template.Handle {
        private final Object instance;

        public FallbackHandle(Object instance) {
            this.instance = instance;
        }

        @Override
        public Object getRaw() {
            return this.instance;
        }
    }
}

