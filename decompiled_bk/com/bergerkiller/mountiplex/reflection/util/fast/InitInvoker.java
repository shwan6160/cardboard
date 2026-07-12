/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedAccessor;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedCodeInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedExactSignatureInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.RecordClassFieldChanger;
import com.bergerkiller.mountiplex.reflection.util.fast.ReflectionInvoker;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class InitInvoker<T>
implements Invoker<T>,
LazyInitializedObject {
    private static final InitInvoker<?> defaultUnavailableMethod = InitInvoker.unavailable("method", "!!UNKNOWN!!");
    private final FieldAccessor<Invoker<T>> fieldAccessor;
    private final Object fieldInstance;

    protected InitInvoker() {
        this.fieldInstance = null;
        this.fieldAccessor = new IgnoredFieldAccessor<InitInvoker>(this);
    }

    public <I> InitInvoker(I instance, FieldAccessor<Invoker<T>> accessor) {
        this.fieldInstance = instance;
        this.fieldAccessor = accessor;
    }

    protected abstract Invoker<T> create();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Invoker<T> initializeInvoker() {
        Invoker<T> invoker = this.fieldAccessor.get(this.fieldInstance);
        if (invoker == this) {
            InitInvoker initInvoker = this;
            synchronized (initInvoker) {
                invoker = this.fieldAccessor.get(this.fieldInstance);
                if (invoker == this) {
                    invoker = this.create();
                    this.fieldAccessor.set(this.fieldInstance, invoker);
                }
            }
        }
        return invoker;
    }

    @Override
    public final T invoke(Object instance) {
        return this.initializeInvoker().invoke(instance);
    }

    @Override
    public final T invoke(Object instance, Object arg0) {
        return this.initializeInvoker().invoke(instance, arg0);
    }

    @Override
    public final T invoke(Object instance, Object arg0, Object arg1) {
        return this.initializeInvoker().invoke(instance, arg0, arg1);
    }

    @Override
    public final T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.initializeInvoker().invoke(instance, arg0, arg1, arg2);
    }

    @Override
    public final T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.initializeInvoker().invoke(instance, arg0, arg1, arg2, arg3);
    }

    @Override
    public final T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.initializeInvoker().invoke(instance, arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final T invokeVA(Object instance, Object ... args) {
        return this.initializeInvoker().invokeVA(instance, args);
    }

    public static <T> InitInvoker<T> unavailable(String type, String missingInfo) {
        return new UnavailableInvoker(type, missingInfo);
    }

    public static <T> InitInvoker<T> unavailableMethod() {
        return defaultUnavailableMethod;
    }

    public static <T> InitInvoker<T> forMethod(ClassLoader classLoader, Object fieldInstance, String fieldName, Method method) {
        ClassResolver resolver = new ClassResolver();
        resolver.setClassLoader(classLoader);
        return InitInvoker.forMethod(fieldInstance, fieldName, new MethodDeclaration(resolver, method));
    }

    public static <T> InitInvoker<T> forMethod(Object fieldInstance, String fieldName, MethodDeclaration method) {
        return InitInvoker.forMethod(fieldInstance, new ReflectionFieldAccessor(fieldInstance.getClass(), fieldName), method);
    }

    public static <T> InitInvoker<T> forMethodLate(String fieldDeclaringClass, String fieldName, MethodDeclaration method) {
        ClassLoader classLoader = method == null ? InitInvoker.class.getClassLoader() : method.getResolver().getClassLoader();
        return InitInvoker.forMethod(null, new ReflectionFieldAccessorLate(classLoader, fieldDeclaringClass, fieldName), method);
    }

    public static <T> InitInvoker<T> forMethod(Object instance, FieldAccessor<Invoker<T>> accessor, MethodDeclaration method) {
        if (method == null) {
            return InitInvoker.unavailableMethod();
        }
        if (method.body != null) {
            return InitGeneratedCodeInvoker.create(instance, accessor, method);
        }
        if (method.method != null) {
            return new InitGeneratedExecutableInvoker<T>(instance, accessor, method.method);
        }
        if (method.constructor != null) {
            return new InitGeneratedExecutableInvoker<T>(instance, accessor, method.constructor);
        }
        if (method.isRecordFieldChanger) {
            String nameAlias = method.name.hasAlias() ? method.name.alias() : "change";
            ArrayList<String> fields = new ArrayList<String>(method.parameters.parameters.length);
            for (ParameterDeclaration param : method.parameters.parameters) {
                fields.add(param.name.value());
            }
            return new InitGeneratedRecordFieldChangerInvoker<T>(instance, accessor, method.getDeclaringClass(), nameAlias, fields);
        }
        return InitInvoker.unavailable("method", method.toString());
    }

    public static <T> InitInvoker<T> proxy(Object fieldInstance, String fieldName, Invoker<T> invoker) {
        return InitInvoker.proxy(fieldInstance, new ReflectionFieldAccessor(fieldInstance.getClass(), fieldName), invoker);
    }

    public static <T> InitInvoker<T> proxy(Object instance, FieldAccessor<Invoker<T>> accessor, Invoker<T> invoker) {
        return new ProxyInvoker<T>(instance, accessor, invoker);
    }

    public static final class UnavailableInvoker<T>
    extends InitInvoker<T> {
        private final String type;
        private final String missingInfo;

        private UnavailableInvoker(String type, String missingInfo) {
            this.type = type;
            this.missingInfo = missingInfo;
        }

        @Override
        public final Invoker<T> create() {
            throw new UnsupportedOperationException(this.type + " " + this.missingInfo + " is not available");
        }
    }

    private static final class ReflectionFieldAccessor<T>
    extends ReflectionFieldAccessorBase<T> {
        private final Field field;

        public ReflectionFieldAccessor(Class<?> declaringClass, String fieldName) {
            try {
                this.field = ReflectionFieldAccessor.findField(declaringClass, fieldName);
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        @Override
        public Field getField() {
            return this.field;
        }
    }

    private static final class ReflectionFieldAccessorLate<T>
    extends ReflectionFieldAccessorBase<T> {
        private final ClassLoader classLoader;
        private final String declaringClassName;
        private final String fieldName;
        private Field field = null;

        public ReflectionFieldAccessorLate(ClassLoader classLoader, String declaringClassName, String fieldName) {
            this.classLoader = classLoader;
            this.declaringClassName = declaringClassName;
            this.fieldName = fieldName;
        }

        @Override
        public Field getField() {
            if (this.field == null) {
                try {
                    Class<?> declaringClass = this.classLoader.loadClass(this.declaringClassName);
                    this.field = ReflectionFieldAccessorLate.findField(declaringClass, this.fieldName);
                }
                catch (Throwable t) {
                    throw MountiplexUtil.uncheckedRethrow(t);
                }
            }
            return this.field;
        }
    }

    public static class InitGeneratedCodeInvoker
    extends InitInvoker<Object>
    implements GeneratedExactSignatureInvoker<Object> {
        private final ExtendedClassWriter.Deferred<? extends GeneratedCodeInvoker<Object>> invoker;

        private InitGeneratedCodeInvoker(Object instance, FieldAccessor<Invoker<Object>> accessor, ExtendedClassWriter.Deferred<? extends GeneratedCodeInvoker<Object>> invoker) {
            super(instance, accessor);
            this.invoker = invoker;
        }

        @Override
        public String getInvokerClassInternalName() {
            return this.invoker.getInternalName();
        }

        @Override
        public String getInvokerClassTypeDescriptor() {
            return this.invoker.getTypeDescriptor();
        }

        @Override
        protected Invoker<Object> create() {
            return this.invoker.generate();
        }

        public static <T> InitGeneratedCodeInvoker create(Object instance, FieldAccessor<Invoker<T>> accessor, MethodDeclaration methodDeclaration) {
            ExtendedClassWriter.Deferred invoker = GeneratedCodeInvoker.createDefer(methodDeclaration);
            return new InitGeneratedCodeInvoker(instance, accessor, invoker);
        }
    }

    private static final class InitGeneratedExecutableInvoker<T>
    extends InitInvoker<T> {
        private final Executable executable;

        protected InitGeneratedExecutableInvoker(Object instance, FieldAccessor<Invoker<T>> accessor, Executable executable) {
            super(instance, accessor);
            this.executable = executable;
        }

        @Override
        protected Invoker<T> create() {
            if (GeneratedInvoker.canCreate(this.executable)) {
                return GeneratedInvoker.create(this.executable);
            }
            return ReflectionInvoker.create(this.executable);
        }
    }

    private static final class InitGeneratedRecordFieldChangerInvoker<T>
    extends InitInvoker<T> {
        private final Class<?> declaringClass;
        private final String nameAlias;
        private final List<String> recordFields;

        protected InitGeneratedRecordFieldChangerInvoker(Object instance, FieldAccessor<Invoker<T>> accessor, Class<?> declaringClass, String nameAlias, List<String> recordFields) {
            super(instance, accessor);
            this.declaringClass = declaringClass;
            this.nameAlias = nameAlias;
            this.recordFields = recordFields;
        }

        @Override
        protected Invoker<T> create() {
            return RecordClassFieldChanger.create(this.declaringClass, this.nameAlias, this.recordFields);
        }
    }

    private static final class ProxyInvoker<T>
    extends InitInvoker<T> {
        private final Invoker<T> invoker;

        public ProxyInvoker(Object fieldInstance, FieldAccessor<Invoker<T>> fieldAccessor, Invoker<T> invoker) {
            super(fieldInstance, fieldAccessor);
            this.invoker = invoker;
        }

        @Override
        public Invoker<T> create() {
            return this.invoker.initializeInvoker();
        }
    }

    private static abstract class ReflectionFieldAccessorBase<T>
    implements FieldAccessor<Invoker<T>> {
        private ReflectionFieldAccessorBase() {
        }

        public abstract Field getField();

        protected static Field findField(Class<?> type, String fieldName) throws Throwable {
            try {
                return MPLType.getDeclaredField(type, fieldName);
            }
            catch (NoSuchFieldException err) {
                Class<?> supertype = type.getSuperclass();
                if (supertype == null) {
                    throw err;
                }
                return ReflectionFieldAccessorBase.findField(supertype, fieldName);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized Invoker<T> get(Object instance) {
            try {
                Object value;
                Field field = this.getField();
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    value = field.get(instance);
                }
                finally {
                    field.setAccessible(wasAccessible);
                }
                return (Invoker)value;
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized boolean set(Object instance, Invoker<T> value) {
            try {
                Field field = this.getField();
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    field.get(instance);
                    GeneratedAccessor.GeneratedStaticFinalAccessor.setUninitializedField(field, value);
                } else {
                    boolean wasAccessible = field.isAccessible();
                    try {
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                    finally {
                        field.setAccessible(wasAccessible);
                    }
                }
                return true;
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
    }
}

