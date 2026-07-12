/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class SafeField<T>
implements FieldAccessor<T> {
    private final FastField<T> field;

    public SafeField(FastField<T> field) {
        if (field == null) {
            throw new IllegalArgumentException("Backing field can not be null");
        }
        this.field = field;
    }

    public SafeField(Field field) {
        this.field = new FastField();
        this.field.init(field);
    }

    public SafeField(String fieldPath, Class<?> fieldType) {
        this.field = new FastField();
        if (fieldPath == null || fieldPath.isEmpty() || !fieldPath.contains(".")) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Field path contains no class: " + fieldPath);
            return;
        }
        try {
            String className = MountiplexUtil.getLastBefore(fieldPath, ".");
            String fieldName = fieldPath.substring(className.length() + 1);
            Class<?> type = Resolver.loadClass(className, false);
            this.load(type, fieldName, fieldType);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to load field '" + fieldPath + "'", t);
        }
    }

    public SafeField(Object value, String name, Class<?> fieldType) {
        this.field = new FastField();
        this.load(value == null ? null : value.getClass(), name, fieldType);
    }

    public SafeField(Class<?> source, String name, Class<?> fieldType) {
        this.field = new FastField();
        this.load(source, name, fieldType);
    }

    private void load(Class<?> source, String name, Class<?> fieldType) {
        if (source == null) {
            MountiplexUtil.LOGGER.log(Level.WARNING, "Can not load field '" + name + "' because the class is null!", new Exception());
            return;
        }
        String fixedName = Resolver.resolveFieldName(source, name);
        String dispName = name.equals(fixedName) ? name : name + ":" + fixedName;
        this.field.init(SafeField.findRaw(source, fixedName));
        if (this.field.getField() == null) {
            if (fieldType == null) {
                this.field.initUnavailable("[???] " + source.getSimpleName() + "::" + name + "[???]");
            } else {
                this.field.initUnavailable(fieldType.getSimpleName() + " " + source.getSimpleName() + "::" + name + "[???]");
            }
        } else {
            if (fieldType == null) {
                return;
            }
            Class<T> realType = this.field.getType();
            if (realType.equals(fieldType)) {
                return;
            }
            if (BoxedType.tryBoxType(realType).equals(BoxedType.tryBoxType(fieldType))) {
                return;
            }
            MountiplexUtil.LOGGER.log(Level.WARNING, "Field '" + name + "' in class " + MPLType.getName(source) + " is of type " + realType.getSimpleName() + " while we expect type " + fieldType.getSimpleName());
            this.field.initUnavailable(fieldType.getSimpleName() + "[?] " + source.getSimpleName() + "::" + name);
        }
        MountiplexUtil.LOGGER.warning("Field '" + dispName + "' could not be found in class " + MPLType.getName(source));
    }

    @Override
    public boolean isValid() {
        return this.field.getField() != null;
    }

    public boolean isStatic() {
        return this.field.isStatic();
    }

    @Override
    public T transfer(Object from, Object to) {
        if (this.field.getField() == null) {
            return null;
        }
        T old = this.get(to);
        this.set(to, this.get(from));
        return old;
    }

    @Override
    public T get(Object object) {
        try {
            return this.field.get(object);
        }
        catch (RuntimeException ex) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to get field " + this.field.getDescription(), ex);
            return null;
        }
    }

    @Override
    public boolean set(Object object, T value) {
        try {
            this.field.set(object, value);
            return true;
        }
        catch (RuntimeException ex) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to set field " + this.field.getDescription(), ex);
            return false;
        }
    }

    public String toString() {
        StringBuilder text = new StringBuilder(20);
        Field f = this.field.getField();
        if (f == null) {
            return "null";
        }
        int mod = f.getModifiers();
        if (Modifier.isPublic(mod)) {
            text.append("public ");
        } else if (Modifier.isPrivate(mod)) {
            text.append("private ");
        } else if (Modifier.isProtected(mod)) {
            text.append("protected ");
        }
        if (Modifier.isStatic(mod)) {
            text.append("static ");
        }
        String fieldTypeName = MPLType.getName(f.getType());
        String fieldName = MPLType.getName(f);
        return text.append(fieldTypeName).append(" ").append(fieldName).toString();
    }

    public String getName() {
        return this.field.getName();
    }

    public Class<?> getType() {
        return this.field.getType();
    }

    public FastField<T> getFastField() {
        return this.field;
    }

    @Override
    public <K> TranslatorFieldAccessor<K> translate(DuplexConverter<?, K> converterPair) {
        return new TranslatorFieldAccessor<K>(this, converterPair);
    }

    @Override
    public FieldAccessor<T> ignoreInvalid(T defaultValue) {
        if (this.isValid()) {
            return this;
        }
        return new IgnoredFieldAccessor<T>(defaultValue);
    }

    public static <T> void set(Object source, String fieldname, T value) {
        if (value == null) {
            new SafeField<T>(source, fieldname, null).set(source, value);
        } else {
            new SafeField<T>(source, fieldname, value.getClass()).set(source, value);
        }
    }

    public static <T> void setStatic(Class<?> clazz, String fieldname, T value) {
        if (value == null) {
            new SafeField<T>(clazz, fieldname, null).set(null, value);
        } else {
            new SafeField<T>(clazz, fieldname, value.getClass()).set(null, value);
        }
    }

    public static <T> T get(Object source, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(source, fieldname, fieldType).get(source);
    }

    public static <T> T get(Class<?> clazz, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(clazz, fieldname, fieldType).get(null);
    }

    public static <T> SafeField<T> create(Class<?> type, String fieldname, Class<T> fieldType) {
        return new SafeField<T>(type, fieldname, fieldType);
    }

    public static <T> TranslatorFieldAccessor<T> create(Class<?> type, String name, DuplexConverter<?, T> converterPair) {
        return SafeField.create(type, name, converterPair.output.type).translate(converterPair);
    }

    public static boolean contains(Class<?> type, String name, Class<?> fieldType) {
        Field f = SafeField.findRaw(type, Resolver.resolveFieldName(type, name));
        return f != null && (fieldType == null || fieldType.equals(f.getType()));
    }

    private static Field findRaw(Class<?> type, String fieldName) {
        for (Class<?> tmp = type; tmp != null; tmp = tmp.getSuperclass()) {
            try {
                return MPLType.getDeclaredField(tmp, fieldName);
            }
            catch (NoSuchFieldException ex) {
                continue;
            }
        }
        return null;
    }

    public static <T> SafeField<T> createNull(String missingInfo) {
        FastField ff = new FastField();
        ff.initUnavailable(missingInfo);
        return new SafeField(ff);
    }

    @Deprecated
    public static <T> SafeField<T> createNull() {
        return new SafeField<T>((Field)null);
    }
}

