/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ReflectionAccessor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

public class GeneratedAccessor<T>
extends ReflectionAccessor<T> {
    protected GeneratedAccessor(Field field) {
        super(field);
    }

    public static <T> GeneratedAccessor<T> create(Field field) {
        Class<?> boxed;
        String selfName = MPLType.getInternalName(GeneratedAccessor.class);
        int mod = field.getModifiers();
        boolean isStaticFinal = Modifier.isFinal(mod) && Modifier.isStatic(mod);
        Class baseClass = isStaticFinal ? GeneratedStaticFinalAccessor.class : GeneratedAccessor.class;
        ExtendedClassWriter cw = ExtendedClassWriter.builder(baseClass).setFlags(1).setAccess(16).build();
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(Ljava/lang/reflect/Field;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(183, MPLType.getInternalName(baseClass), "<init>", "(" + MPLType.getDescriptor(Field.class) + ")V", false);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        String className = MPLType.getInternalName(field.getDeclaringClass());
        Type fieldType = MPLType.getType(field.getType());
        String fieldTypeName = fieldType.getDescriptor();
        String fieldName = MPLType.getName(field);
        String accessorName = null;
        if (field.getType().isPrimitive() && (boxed = BoxedType.getBoxedType(field.getType())) != null) {
            accessorName = boxed.getSimpleName();
        }
        if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
            if (accessorName == null) {
                String fieldTypeInternalName = MPLType.getInternalName(field.getType());
                mv = cw.visitMethod(1, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
                mv.visitCode();
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, className);
                mv.visitFieldInsn(180, className, fieldName, fieldTypeName);
                mv.visitInsn(176);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
                if (!Modifier.isFinal(mod)) {
                    mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(25, 1);
                    mv.visitTypeInsn(192, className);
                    mv.visitVarInsn(25, 2);
                    mv.visitTypeInsn(192, fieldTypeInternalName);
                    mv.visitFieldInsn(181, className, fieldName, fieldTypeName);
                    mv.visitInsn(177);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
            } else {
                mv = cw.visitMethod(1, "get" + accessorName, "(Ljava/lang/Object;)" + fieldTypeName, null, null);
                mv.visitCode();
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, className);
                mv.visitFieldInsn(180, className, fieldName, fieldTypeName);
                mv.visitInsn(fieldType.getOpcode(172));
                mv.visitMaxs(0, 0);
                mv.visitEnd();
                if (!Modifier.isFinal(mod)) {
                    mv = cw.visitMethod(1, "set" + accessorName, "(Ljava/lang/Object;" + fieldTypeName + ")V", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(25, 1);
                    mv.visitTypeInsn(192, className);
                    mv.visitVarInsn(fieldType.getOpcode(21), 2);
                    mv.visitFieldInsn(181, className, fieldName, fieldTypeName);
                    mv.visitInsn(177);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
            }
        }
        if (isStaticFinal && accessorName != null) {
            String unsafePutName = field.getType() == Integer.TYPE ? "putInt" : (field.getType() == Character.TYPE ? "putChar" : "put" + accessorName);
            mv = cw.visitMethod(1, "set" + accessorName, "(Ljava/lang/Object;" + fieldTypeName + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), "class_init", "Z");
            Label label0 = new Label();
            mv.visitJumpInsn(154, label0);
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, cw.getInternalName(), "initDeclaringClass", "()V", false);
            mv.visitLabel(label0);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), "unsafe", "Lsun/misc/Unsafe;");
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), "base", "Ljava/lang/Object;");
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), "offset", "J");
            mv.visitVarInsn(MPLType.getOpcode(field.getType(), 21), 2);
            mv.visitMethodInsn(182, "sun/misc/Unsafe", unsafePutName, "(Ljava/lang/Object;J" + fieldTypeName + ")V", false);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            String boxedName = MPLType.getInternalName(BoxedType.getBoxedType(field.getType()));
            mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 2);
            mv.visitTypeInsn(193, boxedName);
            Label label02 = new Label();
            mv.visitJumpInsn(153, label02);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 1);
            mv.visitVarInsn(25, 2);
            mv.visitTypeInsn(192, boxedName);
            mv.visitMethodInsn(182, boxedName, field.getType().getSimpleName() + "Value", "()" + fieldTypeName, false);
            mv.visitMethodInsn(182, cw.getInternalName(), "set" + accessorName, "(Ljava/lang/Object;" + fieldTypeName + ")V", false);
            Label label1 = new Label();
            mv.visitJumpInsn(167, label1);
            mv.visitLabel(label02);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 2);
            mv.visitMethodInsn(182, cw.getInternalName(), "checkValueType", "(Ljava/lang/Object;)V", false);
            mv.visitLabel(label1);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        if (Modifier.isStatic(mod)) {
            mv = cw.visitMethod(1, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(187, "java/lang/UnsupportedOperationException");
            mv.visitInsn(89);
            mv.visitLdcInsn("Static fields can not be copied");
            mv.visitMethodInsn(183, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(191);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else if (Modifier.isPublic(mod) && !Modifier.isFinal(mod)) {
            mv = cw.visitMethod(1, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 2);
            mv.visitTypeInsn(192, className);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, className);
            mv.visitFieldInsn(180, className, fieldName, fieldTypeName);
            mv.visitFieldInsn(181, className, fieldName, fieldTypeName);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else if (Modifier.isPublic(mod) && Modifier.isFinal(mod)) {
            String setMethod = accessorName == null ? "set" : "set" + accessorName;
            mv = cw.visitMethod(1, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 2);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, className);
            mv.visitFieldInsn(180, className, fieldName, fieldTypeName);
            mv.visitMethodInsn(182, selfName, setMethod, "(Ljava/lang/Object;" + fieldTypeName + ")V", false);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else if (accessorName != null) {
            mv = cw.visitMethod(1, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 2);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 1);
            mv.visitMethodInsn(182, selfName, "get" + accessorName, "(Ljava/lang/Object;)" + fieldTypeName, false);
            mv.visitMethodInsn(182, selfName, "set" + accessorName, "(Ljava/lang/Object;" + fieldTypeName + ")V", false);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        return (GeneratedAccessor)cw.generateInstance(new Class[]{Field.class}, new Object[]{field});
    }

    public static class GeneratedStaticFinalAccessor<T>
    extends GeneratedAccessor<T> {
        protected final Class<?> type;
        protected final Unsafe unsafe;
        protected final Object base;
        protected final long offset;
        protected boolean class_init;

        protected GeneratedStaticFinalAccessor(Field field) {
            super(field);
            Unsafe unsafe;
            try {
                Class<?> unsafeType = Class.forName("sun.misc.Unsafe");
                Field f = unsafeType.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe)f.get(null);
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to access sun.misc.Unsafe", t);
            }
            this.type = field.getType();
            this.unsafe = unsafe;
            this.base = unsafe.staticFieldBase(field);
            this.offset = unsafe.staticFieldOffset(field);
            this.class_init = false;
        }

        protected final synchronized void initDeclaringClass() {
            if (!this.class_init) {
                try {
                    Class.forName(this.getWriteField().getDeclaringClass().getName());
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Field declaring class failed to initialize", e);
                }
                this.class_init = true;
            }
        }

        protected void checkValueType(Object value) {
            if (this.type.isPrimitive()) {
                if (value == null) {
                    throw new IllegalArgumentException("Field of primitive type " + this.type.getName() + " cannot be assigned null");
                }
                Class<?> primValueType = BoxedType.getUnboxedType(value.getClass());
                if (primValueType != null && primValueType != this.type) {
                    throw new IllegalArgumentException("Field of primitive type " + this.type.getName() + " cannot be assigned a value of type " + primValueType);
                }
                throw new IllegalArgumentException("Field of primitive type " + this.type.getName() + " cannot be assigned a value of type " + value.getClass().getName());
            }
            if (value != null && !this.type.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("Field of type " + this.type.getName() + " cannot be assigned a value of type " + value.getClass().getName());
            }
        }

        @Override
        public void setDouble(Object o, double v) {
            this.checkValueType(v);
        }

        @Override
        public void setFloat(Object o, float v) {
            this.checkValueType(Float.valueOf(v));
        }

        @Override
        public void setByte(Object o, byte v) {
            this.checkValueType(v);
        }

        @Override
        public void setShort(Object o, short v) {
            this.checkValueType(v);
        }

        @Override
        public void setInteger(Object o, int v) {
            this.checkValueType(v);
        }

        @Override
        public void setLong(Object o, long v) {
            this.checkValueType(v);
        }

        @Override
        public void setCharacter(Object o, char v) {
            this.checkValueType(Character.valueOf(v));
        }

        @Override
        public void setBoolean(Object o, boolean v) {
            this.checkValueType(v);
        }

        @Override
        public void set(Object o, Object value) {
            if (!this.class_init) {
                this.initDeclaringClass();
            }
            if (value == null || this.type.isInstance(value)) {
                this.unsafe.putObject(this.base, this.offset, value);
            } else {
                this.checkValueType(value);
            }
        }

        public static void setUninitializedField(Field field, Object value) {
            GeneratedStaticFinalAccessor accessor = new GeneratedStaticFinalAccessor(field);
            if (value == null || accessor.type.isInstance(value)) {
                accessor.unsafe.putObject(accessor.base, accessor.offset, value);
            } else {
                accessor.checkValueType(value);
            }
        }
    }
}

