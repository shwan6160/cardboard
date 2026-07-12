/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedExactSignatureInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedInvoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class RecordClassFieldChanger<T>
implements GeneratedExactSignatureInvoker<T>,
IgnoresRemapping {
    public static <T> RecordClassFieldChanger<T> create(Class<?> type, String nameAlias, List<String> recordNamesToSet) {
        ArrayList<RecordComponent> records = null;
        try {
            Field[] recordComponentType = Class.forName("java.lang.reflect.RecordComponent");
            Object[] recordComponents = (Object[])type.getMethod("getRecordComponents", new Class[0]).invoke(type, new Object[0]);
            if (recordComponents != null) {
                Method getAccessorMethod = recordComponentType.getMethod("getAccessor", new Class[0]);
                Method getTypeMethod = recordComponentType.getMethod("getType", new Class[0]);
                records = new ArrayList(recordComponents.length);
                for (Object comp : recordComponents) {
                    Method r_acc = (Method)getAccessorMethod.invoke(comp, new Object[0]);
                    Class r_type = (Class)getTypeMethod.invoke(comp, new Object[0]);
                    records.add(new RecordComponent(MPLType.getName(r_acc), r_type));
                }
            }
        }
        catch (Throwable recordComponentType) {
            // empty catch block
        }
        if (records == null) {
            records = new ArrayList<RecordComponent>();
            for (Field f : type.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                records.add(new RecordComponent(f.getName(), f.getType()));
            }
        }
        ArrayList<RecordComponent> recordsToSet = new ArrayList<RecordComponent>(recordNamesToSet.size());
        int position = 1;
        for (String name : recordNamesToSet) {
            boolean found = false;
            for (RecordComponent comp : records) {
                if (!comp.name.equals(name)) continue;
                comp.changed = true;
                comp.argPosition = position;
                position += Type.getType(comp.type).getSize();
                found = true;
                recordsToSet.add(comp);
                break;
            }
            if (found) continue;
            throw new IllegalArgumentException("Record class " + type.getName() + " does not contain record with name '" + name + "'!");
        }
        ExtendedClassWriter classWriter = ExtendedClassWriter.builder(RecordClassFieldChanger.class).setFlags(1).setSingleton(true).build();
        String typeInternalName = MPLType.getInternalName(type);
        StringBuilder desc = new StringBuilder();
        desc.append('(');
        desc.append(MPLType.getDescriptor(type));
        for (RecordComponent comp : recordsToSet) {
            desc.append(MPLType.getDescriptor(comp.type));
        }
        desc.append(')');
        desc.append(MPLType.getDescriptor(type));
        String changeMethodDescriptor = desc.toString();
        StringBuilder ctorDesc = new StringBuilder();
        ctorDesc.append('(');
        for (RecordComponent comp : records) {
            ctorDesc.append(MPLType.getDescriptor(comp.type));
        }
        ctorDesc.append(")V");
        MethodVisitor methodVisitor = classWriter.visitMethod(9, nameAlias, changeMethodDescriptor, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitTypeInsn(187, typeInternalName);
        methodVisitor.visitInsn(89);
        for (RecordComponent comp : records) {
            if (comp.changed) {
                methodVisitor.visitVarInsn(Type.getType(comp.type).getOpcode(21), comp.argPosition);
                continue;
            }
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, typeInternalName, comp.name, "()" + MPLType.getDescriptor(comp.type), false);
        }
        methodVisitor.visitMethodInsn(183, typeInternalName, "<init>", ctorDesc.toString(), false);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        if (recordsToSet.size() <= 5) {
            methodVisitor = classWriter.visitMethod(1, "invoke", GeneratedInvoker.buildInvokeDescriptor(recordsToSet.size()), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitTypeInsn(192, typeInternalName);
            int position2 = 2;
            for (RecordComponent comp : recordsToSet) {
                methodVisitor.visitVarInsn(25, position2++);
                ExtendedClassWriter.visitUnboxObjectVariable(methodVisitor, comp.type);
            }
            methodVisitor.visitMethodInsn(184, classWriter.getInternalName(), nameAlias, changeMethodDescriptor, false);
            methodVisitor.visitInsn(176);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        methodVisitor = classWriter.visitMethod(129, "invokeVA", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        methodVisitor.visitCode();
        GeneratedInvoker.visitInvokeVAArgCountCheck(methodVisitor, recordsToSet.size());
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitTypeInsn(192, typeInternalName);
        int index = 0;
        for (RecordComponent comp : recordsToSet) {
            methodVisitor.visitVarInsn(25, 2);
            ExtendedClassWriter.visitPushInt(methodVisitor, index++);
            methodVisitor.visitInsn(50);
            ExtendedClassWriter.visitUnboxObjectVariable(methodVisitor, comp.type);
        }
        methodVisitor.visitMethodInsn(184, classWriter.getInternalName(), nameAlias, changeMethodDescriptor, false);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        return (RecordClassFieldChanger)classWriter.generateInstance();
    }

    @Override
    public String getInvokerClassInternalName() {
        return MPLType.getInternalName(this.getClass());
    }

    @Override
    public String getInvokerClassTypeDescriptor() {
        return MPLType.getDescriptor(this.getClass());
    }

    private static final class RecordComponent {
        public final String name;
        public final Class<?> type;
        public boolean changed = false;
        public int argPosition = -1;

        public RecordComponent(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }
    }
}

