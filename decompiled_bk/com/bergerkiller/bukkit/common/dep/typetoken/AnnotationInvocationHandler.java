/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.typetoken;

import com.bergerkiller.bukkit.common.dep.typetoken.AnnotationFormatException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

class AnnotationInvocationHandler
implements Annotation,
InvocationHandler,
Serializable {
    private static final long serialVersionUID = 8615044376674805680L;
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap();
    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;
    private final int hashCode;

    AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) throws AnnotationFormatException {
        Class<?>[] interfaces = annotationType.getInterfaces();
        if (!annotationType.isAnnotation() || interfaces.length != 1 || interfaces[0] != Annotation.class) {
            throw new AnnotationFormatException(annotationType.getName() + " is not an annotation type");
        }
        this.annotationType = annotationType;
        this.values = Collections.unmodifiableMap(AnnotationInvocationHandler.normalize(annotationType, values));
        this.hashCode = this.calculateHashCode();
    }

    static Map<String, Object> normalize(Class<? extends Annotation> annotationType, Map<String, Object> values) throws AnnotationFormatException {
        HashSet<String> missing = new HashSet<String>();
        HashSet<String> invalid = new HashSet<String>();
        HashMap<String, Object> valid = new HashMap<String, Object>();
        for (Method element : annotationType.getDeclaredMethods()) {
            String elementName = element.getName();
            if (values.containsKey(elementName)) {
                Class<?> returnType = element.getReturnType();
                if (returnType.isPrimitive()) {
                    returnType = primitiveWrapperMap.get(returnType);
                }
                if (returnType.isInstance(values.get(elementName))) {
                    valid.put(elementName, values.get(elementName));
                    continue;
                }
                invalid.add(elementName);
                continue;
            }
            if (element.getDefaultValue() != null) {
                valid.put(elementName, element.getDefaultValue());
                continue;
            }
            missing.add(elementName);
        }
        if (!missing.isEmpty()) {
            throw new AnnotationFormatException("Missing value(s) for " + String.join((CharSequence)",", missing));
        }
        if (!invalid.isEmpty()) {
            throw new AnnotationFormatException("Incompatible type(s) provided for " + String.join((CharSequence)",", invalid));
        }
        return valid;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.values.containsKey(method.getName())) {
            return this.values.get(method.getName());
        }
        return method.invoke((Object)this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!this.annotationType.isInstance(other)) {
            return false;
        }
        Annotation that = this.annotationType.cast(other);
        for (Map.Entry<String, Object> element : this.values.entrySet()) {
            Object otherValue;
            Object value = element.getValue();
            try {
                otherValue = that.annotationType().getMethod(element.getKey(), new Class[0]).invoke((Object)that, new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            if (Objects.deepEquals(value, otherValue)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(this.annotationType.getName()).append('(');
        TreeSet<String> sorted = new TreeSet<String>(this.values.keySet());
        for (String elementName : sorted) {
            String value = this.values.get(elementName).getClass().isArray() ? Arrays.deepToString(new Object[]{this.values.get(elementName)}).replaceAll("^\\[\\[", "[").replaceAll("]]$", "]") : this.values.get(elementName).toString();
            result.append(elementName).append('=').append(value).append(", ");
        }
        if (this.values.size() > 0) {
            result.delete(result.length() - 2, result.length());
        }
        result.append(")");
        return result.toString();
    }

    private int calculateHashCode() {
        int hashCode = 0;
        for (Map.Entry<String, Object> element : this.values.entrySet()) {
            hashCode += 127 * element.getKey().hashCode() ^ this.calculateHashCode(element.getValue());
        }
        return hashCode;
    }

    private int calculateHashCode(Object element) {
        if (!element.getClass().isArray()) {
            return element.hashCode();
        }
        if (element instanceof Object[]) {
            return Arrays.hashCode((Object[])element);
        }
        if (element instanceof byte[]) {
            return Arrays.hashCode((byte[])element);
        }
        if (element instanceof short[]) {
            return Arrays.hashCode((short[])element);
        }
        if (element instanceof int[]) {
            return Arrays.hashCode((int[])element);
        }
        if (element instanceof long[]) {
            return Arrays.hashCode((long[])element);
        }
        if (element instanceof char[]) {
            return Arrays.hashCode((char[])element);
        }
        if (element instanceof float[]) {
            return Arrays.hashCode((float[])element);
        }
        if (element instanceof double[]) {
            return Arrays.hashCode((double[])element);
        }
        if (element instanceof boolean[]) {
            return Arrays.hashCode((boolean[])element);
        }
        return Objects.hashCode(element);
    }

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
    }
}

