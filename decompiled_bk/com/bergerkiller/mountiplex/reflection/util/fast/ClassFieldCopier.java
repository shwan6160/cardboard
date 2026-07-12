/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class ClassFieldCopier<T> {
    private static final Map<Class<?>, ClassFieldCopier<?>> cache = new HashMap();
    private static final ClassFieldCopier<?> NOOP_COPIER = new ClassFieldCopier<Object>(){

        @Override
        protected void tryCopy(Object from, Object to) {
        }

        @Override
        protected Stream<ClassFieldCopier<?>> all() {
            return Stream.empty();
        }
    };

    public final void copy(T from, T to) {
        try {
            this.tryCopy(from, to);
        }
        catch (Throwable t) {
            if (from == null) {
                throw new IllegalArgumentException("Object to copy fields from is null");
            }
            if (to == null) {
                throw new IllegalArgumentException("Object to copy fields to is null");
            }
            throw new UnsupportedOperationException("Failed to copy fields of " + from.getClass() + " to " + to.getClass(), t);
        }
    }

    protected abstract void tryCopy(T var1, T var2) throws Throwable;

    protected Stream<ClassFieldCopier<?>> all() {
        return MountiplexUtil.toStream(this);
    }

    protected final void copyAllWithReflection(ReflectionCopier[] copiers, Object from, Object to) throws Throwable {
        int idx = copiers.length;
        while (--idx >= 0) {
            copiers[idx].copy(from, to);
        }
    }

    public static synchronized <T> ClassFieldCopier<T> of(Class<T> type) {
        ClassFieldCopier<Object> copier = cache.get(type);
        if (copier == null) {
            copier = ClassFieldCopier.generateOneAndCombine(type);
            cache.put(type, copier);
        }
        return copier;
    }

    private static ClassFieldCopier<?> generateOneAndCombine(Class<?> type) {
        ClassFieldCopier<?> copier = ClassFieldCopier.generateOne(type);
        ClassFieldCopier<?> superCopier = NOOP_COPIER;
        Class<?> superType = type.getSuperclass();
        if (superType != null && superType != Object.class) {
            superCopier = ClassFieldCopier.of(superType);
        }
        if (superCopier == NOOP_COPIER) {
            return copier;
        }
        if (copier == NOOP_COPIER) {
            return superCopier;
        }
        return new MultiCopier((ClassFieldCopier[])Stream.concat(superCopier.all(), copier.all()).toArray(ClassFieldCopier[]::new));
    }

    private static ClassFieldCopier<?> generateOne(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        ArrayList<ReflectionCopier> copiers = new ArrayList<ReflectionCopier>(fields.length);
        ArrayList<Field> publicFields = new ArrayList<Field>(fields.length);
        if (Resolver.isPublic(type)) {
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) continue;
                if (Modifier.isPublic(modifiers)) {
                    publicFields.add(field);
                    if (!Modifier.isFinal(modifiers)) continue;
                    field.setAccessible(true);
                    continue;
                }
                field.setAccessible(true);
                copiers.add(ReflectionCopier.of(field));
            }
        } else {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                copiers.add(ReflectionCopier.of(field));
            }
        }
        if (publicFields.isEmpty()) {
            return copiers.isEmpty() ? NOOP_COPIER : new MultiFieldReflectionCopier(copiers);
        }
        ExtendedClassWriter writer = ExtendedClassWriter.builder(ClassFieldCopier.class).setFlags(1).setAccess(16).build();
        String typeInternalName = MPLType.getInternalName(type);
        String typeDescriptor = MPLType.getDescriptor(type);
        if (!copiers.isEmpty()) {
            writer.visitStaticField("copiers", ReflectionCopier[].class, copiers.toArray(new ReflectionCopier[copiers.size()]));
        }
        MethodVisitor mv = writer.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, MPLType.getInternalName(ClassFieldCopier.class), "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = writer.visitMethod(4, "tryCopy", "(" + typeDescriptor + typeDescriptor + ")V", null, new String[]{"java/lang/Throwable"});
        mv.visitCode();
        if (!copiers.isEmpty()) {
            String arrCopierDesc = MPLType.getDescriptor(ReflectionCopier[].class);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(178, writer.getInternalName(), "copiers", arrCopierDesc);
            mv.visitVarInsn(25, 1);
            mv.visitVarInsn(25, 2);
            mv.visitMethodInsn(183, MPLType.getInternalName(ClassFieldCopier.class), "copyAllWithReflection", "(" + arrCopierDesc + "Ljava/lang/Object;Ljava/lang/Object;)V", false);
        }
        for (Field field : publicFields) {
            String fieldName = MPLType.getName(field);
            String fieldDesc = MPLType.getDescriptor(field.getType());
            if (Modifier.isFinal(field.getModifiers())) {
                String refFieldName = "field_" + fieldName;
                writer.visitStaticField(refFieldName, Field.class, field);
                mv.visitFieldInsn(178, writer.getInternalName(), refFieldName, "Ljava/lang/reflect/Field;");
                mv.visitVarInsn(25, 2);
                mv.visitVarInsn(25, 1);
                mv.visitFieldInsn(180, typeInternalName, fieldName, fieldDesc);
                ReflectionCopier.callSetter(mv, field.getType());
                continue;
            }
            mv.visitVarInsn(25, 2);
            mv.visitVarInsn(25, 1);
            mv.visitFieldInsn(180, typeInternalName, fieldName, fieldDesc);
            mv.visitFieldInsn(181, typeInternalName, fieldName, fieldDesc);
        }
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        mv = writer.visitMethod(4164, "tryCopy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[]{"java/lang/Throwable"});
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, typeInternalName);
        mv.visitVarInsn(25, 2);
        mv.visitTypeInsn(192, typeInternalName);
        mv.visitMethodInsn(182, writer.getInternalName(), "tryCopy", "(" + typeDescriptor + typeDescriptor + ")V", false);
        mv.visitInsn(177);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
        return (ClassFieldCopier)writer.generateInstance();
    }

    public static abstract class ReflectionCopier {
        private static Map<Class<?>, Function<Field, ReflectionCopier>> primitiveCopierLookup = new IdentityHashMap();
        private static Map<Class<?>, ReflectionSetterMethod> reflectionSetterMethods = new IdentityHashMap();
        private static final ReflectionSetterMethod defaultSetterMethod = new ReflectionSetterMethod("set", Object.class);
        private final Field field;

        private static void register(Class<?> type, String setterName, Function<Field, ReflectionCopier> copierFunc) {
            primitiveCopierLookup.put(type, copierFunc);
            reflectionSetterMethods.put(type, new ReflectionSetterMethod(setterName, type));
        }

        public static ReflectionCopier of(Field field) {
            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                return primitiveCopierLookup.get(type).apply(field);
            }
            return new ReflectionCopier(field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.set(to, f.get(from));
                }
            };
        }

        protected static void callSetter(MethodVisitor mv, Class<?> type) {
            ReflectionSetterMethod method = reflectionSetterMethods.getOrDefault(type, defaultSetterMethod);
            mv.visitMethodInsn(182, "java/lang/reflect/Field", method.name, method.descriptor, false);
        }

        public ReflectionCopier(Field field) {
            this.field = field;
        }

        public final void copy(Object from, Object to) throws Throwable {
            this.copyBase(this.field, from, to);
        }

        protected abstract void copyBase(Field var1, Object var2, Object var3) throws Throwable;

        static {
            ReflectionCopier.register(Boolean.TYPE, "setBoolean", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setBoolean(to, f.getBoolean(from));
                }
            });
            ReflectionCopier.register(Byte.TYPE, "setByte", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setByte(to, f.getByte(from));
                }
            });
            ReflectionCopier.register(Character.TYPE, "setChar", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setChar(to, f.getChar(from));
                }
            });
            ReflectionCopier.register(Short.TYPE, "setShort", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setShort(to, f.getShort(from));
                }
            });
            ReflectionCopier.register(Integer.TYPE, "setInt", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setInt(to, f.getInt(from));
                }
            });
            ReflectionCopier.register(Long.TYPE, "setLong", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setLong(to, f.getLong(from));
                }
            });
            ReflectionCopier.register(Float.TYPE, "setFloat", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setFloat(to, f.getFloat(from));
                }
            });
            ReflectionCopier.register(Double.TYPE, "setDouble", field -> new ReflectionCopier((Field)field){

                @Override
                protected void copyBase(Field f, Object from, Object to) throws Throwable {
                    f.setDouble(to, f.getDouble(from));
                }
            });
        }

        private static class ReflectionSetterMethod {
            public final String name;
            public final String descriptor;

            public ReflectionSetterMethod(String name, Class<?> type) {
                this.name = name;
                this.descriptor = "(Ljava/lang/Object;" + MPLType.getDescriptor(type) + ")V";
            }
        }
    }

    private static final class MultiCopier
    extends ClassFieldCopier<Object> {
        private final ClassFieldCopier<Object>[] copiers;

        public MultiCopier(ClassFieldCopier<Object>[] copiers) {
            this.copiers = copiers;
        }

        @Override
        protected void tryCopy(Object from, Object to) {
            for (ClassFieldCopier<Object> copier : this.copiers) {
                copier.copy(from, to);
            }
        }

        @Override
        protected Stream<ClassFieldCopier<?>> all() {
            return Stream.of(this.copiers);
        }
    }

    private static final class MultiFieldReflectionCopier
    extends ClassFieldCopier<Object> {
        private final ReflectionCopier[] copiers;

        public MultiFieldReflectionCopier(List<ReflectionCopier> copiers) {
            this.copiers = copiers.toArray(new ReflectionCopier[copiers.size()]);
        }

        @Override
        protected void tryCopy(Object from, Object to) throws Throwable {
            this.copyAllWithReflection(this.copiers, from, to);
        }
    }
}

