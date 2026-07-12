/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ClassFieldCopier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ClassTemplate<T> {
    private Class<T> type;
    private List<SafeField<?>> fields;
    private ClassFieldCopier<T> copier;
    private NullInstantiator<T> instantiator;
    private List<FieldDeclaration> typeFields;
    private List<MethodDeclaration> typeMethods;
    private Queue<FieldDeclaration> nextFieldQueue;
    private ClassResolver resolver = new ClassResolver();

    protected ClassTemplate() {
    }

    protected ClassTemplate<T> setClass(Class<T> type) {
        this.type = type;
        this.instantiator = NullInstantiator.of(type);
        this.fields = null;
        this.typeFields = null;
        this.resolver = new ClassResolver();
        this.resolver.setDeclaredClass(type);
        return this;
    }

    private ClassTemplate<T> setClassAndLog(Class<?> type, String mode, String className) {
        if (type == null) {
            String mm = mode.length() == 0 ? " " : " " + mode + " ";
            MountiplexUtil.LOGGER.severe("Failed to find" + mm + "Class '" + className + "'");
        } else {
            this.setClass(type);
        }
        return this;
    }

    protected ClassTemplate<T> setClass(String className) {
        return this.setClassAndLog(Resolver.loadClass(className, false), "", className);
    }

    public ClassTemplate<T> addImport(String importPath) {
        this.resolver.addImport(importPath);
        return this;
    }

    public Class<T> getType() {
        return this.type;
    }

    public List<SafeField<?>> getFields() {
        if (this.fields == null) {
            this.fields = this.type == null ? Collections.emptyList() : Collections.unmodifiableList(ReflectionUtil.getAllNonStaticFields(this.type).map(SafeField::new).collect(Collectors.toList()));
        }
        return this.fields;
    }

    public SafeField<?> getFieldAt(int index) {
        List<SafeField<?>> fields = this.getFields();
        if (index < 0 || index >= fields.size()) {
            throw new IllegalArgumentException("No field exists at index " + index);
        }
        return fields.get(index);
    }

    public T newInstance() {
        if (this.type == null) {
            throw new IllegalStateException("Class was not found or is not set");
        }
        try {
            return this.type.newInstance();
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize new instance of " + this.type, t);
            return null;
        }
    }

    public T newInstanceNull() {
        if (this.type == null) {
            throw new IllegalStateException("Class was not found or is not set");
        }
        return this.instantiator.create();
    }

    public T cloneInstance(Object input) {
        T output = this.newInstanceNull();
        this.transfer(input, output);
        return output;
    }

    public boolean isInstance(Object object) {
        return this.type.isInstance(object);
    }

    public boolean isAssignableFrom(Class<?> clazz) {
        return this.type.isAssignableFrom(clazz);
    }

    public boolean isType(Object object) {
        return object != null && this.isType(object.getClass());
    }

    public boolean isType(Class<?> clazz) {
        return clazz != null && this.type.equals(clazz);
    }

    public T cast(Object obj) {
        return this.type.cast(obj);
    }

    public T tryCast(Object obj) {
        if (this.isInstance(obj)) {
            return this.type.cast(obj);
        }
        return null;
    }

    public void transfer(Object from, Object to) {
        if (this.copier == null) {
            this.copier = ClassFieldCopier.of(this.type);
        }
        this.copier.copy(from, to);
    }

    public boolean isValid() {
        return this.type != null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(500);
        builder.append("Class path: ").append(MPLType.getName(this.getType())).append('\n');
        builder.append("Fields (").append(this.getFields().size()).append("):");
        for (FieldAccessor fieldAccessor : this.getFields()) {
            builder.append("\n    ").append(fieldAccessor.toString());
        }
        return builder.toString();
    }

    public SafeConstructor<T> getConstructor(Class<?> ... parameterTypes) {
        return new SafeConstructor<T>(this.getType(), parameterTypes);
    }

    public <K> SafeField<K> getField(String name, Class<?> type) {
        return new SafeField(this.getType(), name, type);
    }

    public <K> SafeField<K> getField(String name) {
        return new SafeField(this.getType(), name, null);
    }

    public <K> SafeMethod<K> getMethod(String name, Class<?> ... parameterTypes) {
        return new SafeMethod(this.getType(), name, parameterTypes);
    }

    public <K> K getStaticFieldValue(String name, Class<K> fieldType) {
        return SafeField.get(this.getType(), name, fieldType);
    }

    public <K> K getStaticFieldValue(String name, Converter<?, K> converter) {
        return converter.convert(this.getStaticFieldValue(name, (Class)null));
    }

    public <K> void setStaticFieldValue(String name, K value) {
        SafeField.setStatic(this.getType(), name, value);
    }

    public static ClassTemplate<?> create(String path) {
        return new ClassTemplate().setClass(path);
    }

    public static <T> ClassTemplate<T> create(T value) {
        return new ClassTemplate().setClass(value.getClass());
    }

    public static <T> ClassTemplate<T> create(Class<T> clazz) {
        return new ClassTemplate<T>().setClass(clazz);
    }

    private void logFieldWarning(String declaration, String message) {
        String cname = this.type == null ? "null" : MPLType.getName(this.type);
        MountiplexUtil.LOGGER.warning("Field '" + declaration + "' in class " + cname + " " + message);
    }

    private void logMethodWarning(String declaration, String message) {
        String cname = this.type == null ? "null" : MPLType.getName(this.type);
        MountiplexUtil.LOGGER.warning("Method '" + declaration + "' in class " + cname + " " + message);
    }

    public Class<?> resolveClass(String className) {
        return this.resolveClass(className, true);
    }

    public Class<?> resolveClass(String className, boolean logErrors) {
        Class<?> type = this.resolver.resolveClass(className);
        if (type == null && logErrors) {
            MountiplexUtil.LOGGER.warning("Could not find type: " + className);
        }
        return type;
    }

    public String resolveClassName(Class<?> type) {
        return this.resolver.resolveName(type);
    }

    private void loadFields(boolean initNextQueue) {
        if (this.typeFields == null) {
            this.typeFields = new LinkedList<FieldDeclaration>();
            if (this.type != null) {
                for (Field f : this.type.getDeclaredFields()) {
                    this.typeFields.add(new FieldDeclaration(this.resolver, f));
                }
            }
        }
        if (initNextQueue || this.nextFieldQueue == null) {
            this.nextFieldQueue = new LinkedList<FieldDeclaration>(this.typeFields);
        }
    }

    private Comparator<Method> createMethodComparator() {
        return new Comparator<Method>(){

            String paramsStr(Method m) {
                Class<?>[] params = m.getParameterTypes();
                String result = "";
                for (int i = 0; i < params.length; ++i) {
                    if (i > 0) {
                        result = result + ",";
                    }
                    result = result + ClassTemplate.this.resolveClassName(params[i]);
                }
                return result;
            }

            @Override
            public int compare(Method o1, Method o2) {
                String o2p;
                String o1p = this.paramsStr(o1);
                if (o1p.equals(o2p = this.paramsStr(o2))) {
                    String o2r;
                    String o1r = ClassTemplate.this.resolveClassName(o1.getReturnType());
                    if (o1r.equals(o2r = ClassTemplate.this.resolveClassName(o2.getReturnType()))) {
                        String o1name = MPLType.getName(o1);
                        String o2name = MPLType.getName(o2);
                        return o1name.compareTo(o2name);
                    }
                    return o1r.compareTo(o2r);
                }
                return o1p.compareTo(o2p);
            }
        };
    }

    private void loadMethods() {
        if (this.typeMethods == null) {
            this.typeMethods = ReflectionUtil.getAllClassesAndInterfaces(this.getType()).flatMap(c -> ReflectionUtil.getDeclaredMethods(c).sorted(this.createMethodComparator())).filter(ReflectionUtil.createDuplicateMethodFilter()).map(m -> new MethodDeclaration(this.resolver, (Method)m)).collect(Collectors.toList());
        }
    }

    public <F> FieldAccessor<F> nextField(String declaration) {
        if (this.type == null) {
            this.logFieldWarning(declaration, "can not be found because class to find it in is null");
            return SafeField.createNull(declaration);
        }
        FieldDeclaration declare = new FieldDeclaration(this.resolver, StringBuffer.of(declaration));
        if (!declare.isValid()) {
            this.logFieldWarning(declaration, "could not be parsed");
            return SafeField.createNull(declaration);
        }
        if (!declare.isResolved()) {
            this.logFieldWarning(declare.toString(), "has some unresolved types");
            return SafeField.createNull(declaration);
        }
        declare = declare.resolveName();
        this.loadFields(true);
        for (FieldDeclaration field : this.nextFieldQueue) {
            if (!declare.match(field)) continue;
            while (this.nextFieldQueue.remove() != field) {
            }
            return new SafeField(field.field);
        }
        ArrayList<FieldDeclaration> similar = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : this.typeFields) {
            if (!declare.matchSignature(field)) continue;
            similar.add(field);
        }
        if (similar.size() == 0) {
            for (FieldDeclaration field : this.typeFields) {
                if (!declare.name.match(field.name)) continue;
                similar.add(field);
            }
        }
        if (similar.size() == 0) {
            this.logFieldWarning(declaration, "not found; no alternatives available. (Removed?)");
        } else {
            this.logFieldWarning(declaration, "not found; there are " + similar.size() + " close matches:");
            for (FieldDeclaration field : similar) {
                MountiplexUtil.LOGGER.warning("  - " + field.toString());
            }
        }
        return SafeField.createNull(declaration);
    }

    public void skipFieldSignature(String declaration) {
        this.nextFieldSignature(declaration);
    }

    public void skipField(String declaration) {
        this.nextField(declaration);
    }

    public <F> FieldAccessor<F> nextFieldSignature(String declaration) {
        FieldDeclaration next;
        this.loadFields(false);
        if (this.nextFieldQueue.isEmpty()) {
            this.logFieldWarning(declaration, "could not be found (no more fields)");
            return SafeField.createNull(declaration);
        }
        FieldDeclaration declare = new FieldDeclaration(this.resolver, StringBuffer.of(declaration));
        if (!declare.isValid()) {
            this.logFieldWarning(declaration, "could not be parsed");
            return SafeField.createNull(declaration);
        }
        if (!declare.isResolved()) {
            this.logFieldWarning(declare.toString(), "has some unresolved types");
            return SafeField.createNull(declaration);
        }
        if ((declare = declare.resolveName()).match(next = this.nextFieldQueue.peek())) {
            this.nextFieldQueue.remove();
        } else {
            next = null;
            ArrayList<FieldDeclaration> skipped = new ArrayList<FieldDeclaration>();
            while (!this.nextFieldQueue.isEmpty()) {
                FieldDeclaration ff = this.nextFieldQueue.remove();
                if (declare.matchSignature(ff)) {
                    next = ff;
                    break;
                }
                skipped.add(ff);
            }
            if (next == null) {
                this.logFieldWarning(declaration, "could not be found (no more fields)");
                return SafeField.createNull(declaration);
            }
            if (skipped.size() > 0) {
                this.logFieldWarning(declaration, "skipped " + skipped.size() + " fields during lookup:");
                for (FieldDeclaration f : skipped) {
                    MountiplexUtil.LOGGER.warning("  - " + f.toString());
                }
            }
        }
        if (!declare.match(next)) {
            this.logFieldWarning(declaration, "has an incorrect name. New name: " + next.name.toString());
        }
        return new SafeField(next.field);
    }

    public <F> F selectStaticValue(String declaration) {
        FieldAccessor<F> field = this.selectField(declaration);
        return field.isValid() ? (F)field.get(null) : null;
    }

    public <F> FieldAccessor<F> selectField(String declaration) {
        FieldDeclaration declare = new FieldDeclaration(this.resolver, StringBuffer.of(declaration));
        if (!declare.isValid()) {
            this.logFieldWarning(declaration, "could not be parsed");
            return SafeField.createNull(declaration);
        }
        if (!declare.isResolved()) {
            this.logFieldWarning(declare.toString(), "has some unresolved types");
            return SafeField.createNull(declaration);
        }
        declare = declare.resolveName();
        this.loadFields(false);
        for (FieldDeclaration field : this.typeFields) {
            if (!declare.match(field)) continue;
            return new SafeField(field.field);
        }
        ArrayList<FieldDeclaration> similar = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : this.typeFields) {
            if (!declare.matchSignature(field)) continue;
            similar.add(field);
        }
        if (similar.size() == 0) {
            for (FieldDeclaration field : this.typeFields) {
                if (field.modifiers.isStatic() != declare.modifiers.isStatic() || !declare.name.match(field.name) || !declare.type.match(field.type)) continue;
                similar.add(field);
            }
        }
        if (similar.size() == 0) {
            for (FieldDeclaration field : this.typeFields) {
                if (field.modifiers.isStatic() != declare.modifiers.isStatic() || !declare.type.match(field.type)) continue;
                similar.add(field);
            }
        }
        if (similar.size() == 0) {
            this.logFieldWarning(declaration, "not found; no alternatives available. (Removed?)");
        } else {
            this.logFieldWarning(declaration, "not found; there are " + similar.size() + " close matches:");
            for (FieldDeclaration field : similar) {
                MountiplexUtil.LOGGER.warning("  - " + field.toString());
            }
        }
        return SafeField.createNull(declaration);
    }

    public <M> MethodAccessor<M> selectMethod(String declaration) {
        return new SafeMethod(this.selectRawMethod(declaration, true));
    }

    public Method selectRawMethod(String declaration, boolean logErrors) {
        MethodDeclaration declare = new MethodDeclaration(this.resolver, declaration);
        if (!declare.isValid()) {
            if (logErrors) {
                this.logMethodWarning(declaration, "could not be parsed");
            }
            return null;
        }
        if (!declare.isResolved()) {
            if (logErrors) {
                this.logMethodWarning(declare.toString(), "has some unresolved types");
            }
            return null;
        }
        return this.selectRawMethod(declare, logErrors);
    }

    public Method selectRawMethod(MethodDeclaration declare, boolean logErrors) {
        declare = declare.resolveName();
        if (this.type == null) {
            if (logErrors) {
                this.logMethodWarning(declare.toString(), "can not be loaded because this class is null");
            }
            return null;
        }
        this.loadMethods();
        for (MethodDeclaration method : this.typeMethods) {
            if (!declare.match(method) || method.method == null) continue;
            try {
                method.method.setAccessible(true);
                return method.method;
            }
            catch (SecurityException ex) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Security exception trying to access method " + method, ex);
            }
        }
        ArrayList<MethodDeclaration> similar = new ArrayList<MethodDeclaration>();
        for (MethodDeclaration method : this.typeMethods) {
            if (!declare.matchSignature(method)) continue;
            similar.add(method);
        }
        if (logErrors) {
            if (similar.size() == 0) {
                this.logMethodWarning(declare.toString(), "not found; no alternatives available. (Removed?)");
            } else {
                this.logMethodWarning(declare.toString(), "not found; there are " + similar.size() + " close matches:");
                for (MethodDeclaration method : similar) {
                    MountiplexUtil.LOGGER.warning("  - " + method.toString());
                }
            }
        }
        return null;
    }
}

