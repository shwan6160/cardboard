/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.GeneratorArgumentStore;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLJavac;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ExtendedClassWriter<T>
extends ClassWriter {
    private static final UniqueHash generatedClassCtr = new UniqueHash();
    private static final UniqueHash generatedStaticFieldCtr = new UniqueHash();
    private final GeneratedClassName name;
    private final GeneratorClassLoader loader;
    private final Class<?> superType;
    private final Class<?>[] superInterfaces;
    private final List<StaticFieldInit> pendingStaticFields = new ArrayList<StaticFieldInit>();
    private final boolean singleton;
    private final List<StaticFieldInit> singletonMemberFields;
    private CtClass ctClass = null;
    private List<JavassistAction> javassistActions = Collections.emptyList();

    private ExtendedClassWriter(GeneratorClassLoader loader, Builder<T> options) {
        super(((Builder)options).flags);
        this.loader = loader;
        this.name = ((Builder)options).exactName;
        this.superType = ((Builder)options).superClass.isInterface() ? Object.class : ((Builder)options).superClass;
        this.superInterfaces = ((Builder)options).interfaces.toArray(new Class[0]);
        this.singleton = ((Builder)options).singleton;
        this.singletonMemberFields = ((Builder)options).singleton ? new ArrayList(5) : Collections.emptyList();
        String signature = null;
        if (!((Builder)options).interfaces.isEmpty()) {
            signature = MPLType.getDescriptor(this.superType);
            for (Class interfaceType : ((Builder)options).interfaces) {
                signature = signature + MPLType.getDescriptor(interfaceType);
            }
        }
        String superName = MPLType.getInternalName(this.superType);
        String[] interfaceNames = null;
        if (!((Builder)options).interfaces.isEmpty()) {
            interfaceNames = new String[((Builder)options).interfaces.size()];
            for (int i = 0; i < interfaceNames.length; ++i) {
                interfaceNames[i] = MPLType.getInternalName((Class)((Builder)options).interfaces.get(i));
            }
        }
        this.visit(52, ((Builder)options).access, this.name.internalName, signature, superName, interfaceNames);
        if (this.singleton) {
            FieldVisitor fv = this.visitField(25, "INSTANCE", this.getTypeDescriptor(), null, null);
            fv.visitEnd();
        }
    }

    public final String getName() {
        return this.name.name;
    }

    public final String getInternalName() {
        return this.name.internalName;
    }

    public final String getTypeDescriptor() {
        return this.name.typeDescriptor;
    }

    @Override
    public final ClassLoader getClassLoader() {
        return this.loader;
    }

    private void closeASM() {
        MethodVisitor mv;
        if (this.singleton || !this.pendingStaticFields.isEmpty()) {
            mv = this.visitMethod(8, "<clinit>", "()V", null, null);
            mv.visitCode();
            if (this.singleton) {
                mv.visitTypeInsn(187, this.getInternalName());
                mv.visitInsn(89);
                mv.visitMethodInsn(183, this.getInternalName(), "<init>", "()V", false);
                mv.visitFieldInsn(179, this.getInternalName(), "INSTANCE", this.getTypeDescriptor());
            }
            for (StaticFieldInit init : this.pendingStaticFields) {
                ExtendedClassWriter.visitPushInt(mv, init.record);
                mv.visitMethodInsn(184, MPLType.getInternalName(GeneratorArgumentStore.class), "fetch", "(I)Ljava/lang/Object;", false);
                if (init.fieldType != Object.class) {
                    mv.visitTypeInsn(192, MPLType.getInternalName(init.fieldType));
                }
                mv.visitFieldInsn(179, this.getInternalName(), init.fieldName, MPLType.getDescriptor(init.fieldType));
            }
            mv.visitInsn(177);
            mv.visitMaxs(this.singleton ? 2 : 1, 0);
            mv.visitEnd();
        }
        if (this.singleton) {
            mv = this.visitMethod(2, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(183, MPLType.getInternalName(this.superType), "<init>", "()V", false);
            for (StaticFieldInit init : this.singletonMemberFields) {
                mv.visitVarInsn(25, 0);
                ExtendedClassWriter.visitPushInt(mv, init.record);
                mv.visitMethodInsn(184, MPLType.getInternalName(GeneratorArgumentStore.class), "fetch", "(I)Ljava/lang/Object;", false);
                if (init.fieldType != Object.class) {
                    mv.visitTypeInsn(192, MPLType.getInternalName(init.fieldType));
                }
                mv.visitFieldInsn(181, this.getInternalName(), init.fieldName, MPLType.getDescriptor(init.fieldType));
            }
            mv.visitInsn(177);
            mv.visitMaxs(this.singletonMemberFields.isEmpty() ? 1 : 2, 1);
            mv.visitEnd();
        }
        this.visitEnd();
    }

    public void addJavassist(JavassistAction action) throws NotFoundException, CannotCompileException {
        if (this.ctClass != null) {
            action.run(this.ctClass);
        } else {
            if (this.javassistActions.isEmpty()) {
                this.javassistActions = new ArrayList<JavassistAction>(3);
            }
            this.javassistActions.add(action);
        }
    }

    public CtClass getCtClass() throws NotFoundException, CannotCompileException {
        return this.getCtClass(ClassPool.getDefault());
    }

    public CtClass getCtClass(ClassPool classPool) throws NotFoundException, CannotCompileException {
        if (this.ctClass == null) {
            CtClass newCtClass;
            this.closeASM();
            try {
                newCtClass = this.singleton ? MPLJavac.makeNewClass(classPool, this.toByteArray()) : MPLJavac.makeNewClassWithDefaultConstructor(classPool, this.name.name, this.toByteArray());
            }
            catch (NotFoundException e) {
                throw new RuntimeException("Failed to instantiate CtClass " + this.name.name + " from bytecode");
            }
            for (int i = 0; i < this.javassistActions.size(); ++i) {
                this.javassistActions.get(i).run(newCtClass);
            }
            this.javassistActions = Collections.emptyList();
            this.ctClass = newCtClass;
        }
        return this.ctClass;
    }

    public Class<T> generate() {
        try {
            if (this.ctClass == null && !this.javassistActions.isEmpty()) {
                this.getCtClass();
            }
            if (this.ctClass == null) {
                this.closeASM();
                return this.loader.createClassFromBytecode(this.name.name, this.toByteArray(), null);
            }
            return this.ctClass.toClass(this.loader, null);
        }
        catch (CannotCompileException e) {
            throw new RuntimeException("Failed to compile class " + this.name.name, e);
        }
        catch (NotFoundException e) {
            throw new RuntimeException("Compiling failed because a class was not found", e);
        }
    }

    public Constructor<T> generateConstructor(Class<?> ... parameterTypes) {
        if (this.singleton) {
            throw new UnsupportedOperationException("Cannot create new instances of a singleton class");
        }
        Class<T> type = this.generate();
        try {
            return type.getConstructor(parameterTypes);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to generate class", t);
        }
    }

    public T generateInstance() {
        if (this.singleton) {
            Class<T> type = this.generate();
            try {
                return (T)MPLType.getDeclaredField(type, "INSTANCE").get(null);
            }
            catch (VerifyError e) {
                throw new RuntimeException("Failed to load generated class " + this.name.name, e);
            }
            catch (Throwable t) {
                throw new IllegalStateException("INSTANCE not found in singleton. This should not happen!", t);
            }
        }
        return this.generateInstance(new Class[0], new Object[0]);
    }

    public T generateInstance(Class<?>[] parameterTypes, Object[] initArgs) {
        Constructor<T> constructor = this.generateConstructor(parameterTypes);
        try {
            return constructor.newInstance(initArgs);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to generate class", t);
        }
    }

    public T generateInstanceNull() {
        return NullInstantiator.of(this.generate()).create();
    }

    public static String getNextPostfix() {
        return "$mplgen" + generatedClassCtr.nextHex();
    }

    public static String getAvailableClassName(String name) {
        String resultName = name;
        int i = 1;
        while (true) {
            try {
                MPLType.getClassByName(resultName);
                resultName = name + "_" + i;
            }
            catch (ClassNotFoundException ex) {
                return resultName;
            }
            ++i;
        }
    }

    public static void visitPushChar(MethodVisitor mv, char value) {
        ExtendedClassWriter.visitPushInt(mv, value);
    }

    public static void visitPushBoolean(MethodVisitor mv, boolean value) {
        mv.visitInsn(value ? 4 : 3);
    }

    public static void visitPushFloat(MethodVisitor mv, float value) {
        if (value == 0.0f) {
            mv.visitInsn(11);
        } else if (value == 1.0f) {
            mv.visitInsn(12);
        } else if (value == 2.0f) {
            mv.visitInsn(13);
        } else {
            mv.visitLdcInsn(Float.valueOf(value));
        }
    }

    public static void visitPushDouble(MethodVisitor mv, double value) {
        if (value == 0.0) {
            mv.visitInsn(14);
        } else if (value == 1.0) {
            mv.visitInsn(15);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    public void visitPush(MethodVisitor mv, Class<?> type, Object value) {
        if (type == Void.TYPE) {
            return;
        }
        if (type.isPrimitive() && value == null) {
            value = BoxedType.getDefaultValue(type);
        }
        if (value == null) {
            mv.visitInsn(1);
        } else if (type == Byte.TYPE) {
            ExtendedClassWriter.visitPushByte(mv, (Byte)value);
        } else if (type == Short.TYPE) {
            ExtendedClassWriter.visitPushShort(mv, (Short)value);
        } else if (type == Integer.TYPE) {
            ExtendedClassWriter.visitPushInt(mv, (Integer)value);
        } else if (type == Long.TYPE) {
            ExtendedClassWriter.visitPushLong(mv, (Long)value);
        } else if (type == Float.TYPE) {
            ExtendedClassWriter.visitPushFloat(mv, ((Float)value).floatValue());
        } else if (type == Double.TYPE) {
            ExtendedClassWriter.visitPushDouble(mv, (Double)value);
        } else if (type == Character.TYPE) {
            ExtendedClassWriter.visitPushChar(mv, ((Character)value).charValue());
        } else if (type == Boolean.TYPE) {
            ExtendedClassWriter.visitPushBoolean(mv, (Boolean)value);
        } else if (type == String.class) {
            mv.visitLdcInsn(value);
        } else {
            String name = "mplgen_cinit_field_" + generatedStaticFieldCtr.nextHex();
            this.visitStaticField(name, type, value);
            mv.visitFieldInsn(178, this.name.internalName, name, MPLType.getDescriptor(type));
        }
    }

    public static void visitPushByte(MethodVisitor mv, byte value) {
        ExtendedClassWriter.visitPushInt(mv, value);
    }

    public static void visitPushShort(MethodVisitor mv, short value) {
        ExtendedClassWriter.visitPushInt(mv, value);
    }

    public static void visitPushInt(MethodVisitor mv, int value) {
        if (value >= 0 && value <= 5) {
            mv.visitInsn(3 + value);
        } else if (value >= -128 && value <= 127) {
            mv.visitIntInsn(16, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(17, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    public static void visitPushLong(MethodVisitor mv, long value) {
        if (value >= 0L && value <= 5L) {
            mv.visitInsn(9 + (int)value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    public static void visitUnboxObjectVariable(MethodVisitor mv, Class<?> outType) {
        if (outType.isPrimitive()) {
            Class<?> boxedType = BoxedType.getBoxedType(outType);
            if (boxedType != null) {
                mv.visitTypeInsn(192, MPLType.getInternalName(boxedType));
                MPLType.visitUnboxVariable(mv, outType);
            }
        } else if (outType.isArray()) {
            mv.visitTypeInsn(192, MPLType.getDescriptor(outType));
        } else if (!Object.class.equals(outType)) {
            mv.visitTypeInsn(192, MPLType.getInternalName(outType));
        }
    }

    public static void visitInvoke(MethodVisitor mv, Class<?> instanceType, Method method) {
        String instanceName = MPLType.getInternalName(instanceType);
        boolean isInterface = instanceType.isInterface();
        if (Modifier.isStatic(method.getModifiers())) {
            mv.visitMethodInsn(184, instanceName, MPLType.getName(method), MPLType.getMethodDescriptor(method), isInterface);
        } else if (instanceType.isInterface()) {
            mv.visitMethodInsn(185, instanceName, MPLType.getName(method), MPLType.getMethodDescriptor(method), isInterface);
        } else {
            mv.visitMethodInsn(182, instanceName, MPLType.getName(method), MPLType.getMethodDescriptor(method), isInterface);
        }
    }

    public static void visitInit(MethodVisitor mv, Class<?> instanceType, Constructor<?> constructor) {
        String instanceName = MPLType.getInternalName(instanceType);
        mv.visitMethodInsn(183, instanceName, "<init>", MPLType.getConstructorDescriptor(constructor), false);
    }

    public void visitMethodUnsupported(Method method, String message) {
        MethodVisitor mv = this.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
        mv.visitCode();
        mv.visitTypeInsn(187, "java/lang/UnsupportedOperationException");
        mv.visitInsn(89);
        mv.visitLdcInsn(message);
        mv.visitMethodInsn(183, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
        mv.visitMaxs(3, 1 + method.getParameterCount());
        mv.visitEnd();
    }

    public void visitMethodReturnConstant(Method method, Object value) {
        Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE) {
            if (returnType.isPrimitive()) {
                if (value == null) {
                    throw new IllegalArgumentException("Cannot return 'null' from method " + method);
                }
                Class<?> primType = BoxedType.getUnboxedType(value.getClass());
                if (primType != returnType) {
                    throw new IllegalArgumentException("Cannot return type " + primType.getName() + " from method " + method);
                }
            } else if (value != null && !returnType.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("Cannot return type " + value.getClass().getName() + " from method " + method);
            }
        }
        int maxLocals = 1;
        for (Class<?> param : method.getParameterTypes()) {
            maxLocals += MPLType.getType(param).getSize();
        }
        MethodVisitor mv = this.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
        mv.visitCode();
        this.visitPush(mv, returnType, value);
        mv.visitInsn(MPLType.getOpcode(returnType, 172));
        mv.visitMaxs(MPLType.getType(returnType).getSize(), maxLocals);
        mv.visitEnd();
    }

    public void visitStaticInvokerField(String fieldName, MethodDeclaration methodDec) {
        this.visitStaticField(fieldName, Invoker.class, InitInvoker.forMethodLate(this.getName(), fieldName, methodDec));
    }

    public void visitStaticField(String fieldName, Class<?> fieldType, Object value) {
        StaticFieldInit field = new StaticFieldInit(fieldName, fieldType, value);
        this.pendingStaticFields.add(field);
        FieldVisitor fv = this.visitField(25, fieldName, MPLType.getDescriptor(field.fieldType), null, null);
        fv.visitEnd();
    }

    public void visitSingletonField(String fieldName, Class<?> fieldType, Object value) {
        if (this.singletonMemberFields == null) {
            throw new IllegalStateException("The Class being generated isn't a singleton. Missing setSingleton(true) in Builder?");
        }
        StaticFieldInit field = new StaticFieldInit(fieldName, fieldType, value);
        this.singletonMemberFields.add(field);
        FieldVisitor fv = this.visitField(18, fieldName, MPLType.getDescriptor(field.fieldType), null, null);
        fv.visitEnd();
    }

    public void visitCallSuperMethod(MethodVisitor mv, Method method) {
        Class<?> directDescendant;
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.isAssignableFrom(this.superType)) {
            directDescendant = this.superType;
        } else {
            directDescendant = null;
            for (Class<?> c : this.superInterfaces) {
                if (!declaringClass.isAssignableFrom(c)) continue;
                directDescendant = c;
                break;
            }
            if (directDescendant == null) {
                throw new IllegalArgumentException("Method " + method + " is not a superclass method");
            }
        }
        mv.visitMethodInsn(183, MPLType.getInternalName(directDescendant), MPLType.getName(method), MPLType.getMethodDescriptor(method), false);
    }

    public static <T> Builder<T> builder(Class<? super T> superClass) {
        return new Builder(superClass);
    }

    public static final class Builder<T> {
        private final Class<T> superClass;
        private List<Class<?>> interfaces = new ArrayList(0);
        private int flags = 0;
        private int access = 9;
        private boolean singleton = false;
        private String postfix = null;
        private GeneratedClassName exactName = null;
        private ClassLoader classLoader = null;

        private Builder(Class<T> superClass) {
            this.superClass = superClass;
            if (superClass.isInterface()) {
                this.interfaces.add(superClass);
            }
        }

        public Builder<T> addInterface(Class<?> interfaceType) {
            this.interfaces.add(interfaceType);
            return this;
        }

        public Builder<T> addInterfaces(Collection<Class<?>> interfaceTypes) {
            this.interfaces.addAll(interfaceTypes);
            return this;
        }

        public Builder<T> setFlags(int flags) {
            this.flags |= flags;
            return this;
        }

        public Builder<T> setAccess(int access) {
            this.access |= access;
            return this;
        }

        public Builder<T> setPostfix(String postfix) {
            this.postfix = postfix;
            return this;
        }

        public Builder<T> setSingleton(boolean isSingleton) {
            this.singleton = isSingleton;
            return this;
        }

        public Builder<T> setExactName(String name) {
            this.exactName = new GeneratedClassName(name);
            return this;
        }

        public Builder<T> setClassLoader(ClassLoader loader) {
            this.classLoader = loader;
            return this;
        }

        public <TO> ExtendedClassWriter<TO> build() {
            GeneratorClassLoader classLoader = this.initClassLoader();
            this.computeExactName(classLoader);
            return new ExtendedClassWriter(classLoader, this);
        }

        public Deferred<T> defer(Function<ExtendedClassWriter<T>, T> callback) {
            GeneratorClassLoader classLoader = this.initClassLoader();
            this.computeExactName(classLoader);
            return new Deferred(classLoader, this, callback);
        }

        private GeneratorClassLoader initClassLoader() {
            if (this.classLoader == null) {
                return GeneratorClassLoader.get(this.superClass.getClassLoader());
            }
            if (this.classLoader instanceof GeneratorClassLoader) {
                return (GeneratorClassLoader)this.classLoader;
            }
            return GeneratorClassLoader.get(this.classLoader);
        }

        private void computeExactName(GeneratorClassLoader loader) {
            if (this.exactName != null) {
                return;
            }
            String postfix = this.postfix != null ? this.postfix : "$mpldefgen" + generatedClassCtr.nextHex();
            Class<Object> superClass = this.superClass;
            String superClassName = MPLType.getName(superClass);
            if (superClassName.startsWith("java.") || superClassName.startsWith("sun.")) {
                superClass = ExtendedClassWriter.class;
                superClassName = MPLType.getName(superClass);
            }
            String postfix_original = postfix;
            int i = 1;
            while (true) {
                String tmpClassPath = superClassName + postfix;
                boolean classExists = false;
                try {
                    loader.loadClass(tmpClassPath);
                    classExists = true;
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                try {
                    Resolver.getClassByExactName(tmpClassPath);
                    classExists = true;
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                if (!classExists) break;
                postfix = postfix_original + "_" + i;
                ++i;
            }
            this.exactName = new GeneratedClassName(superClass, postfix);
        }
    }

    private static final class GeneratedClassName {
        public final String name;
        public final String internalName;
        public final String typeDescriptor;

        public GeneratedClassName(String name) {
            this.name = name;
            this.internalName = name.replace('.', '/');
            this.typeDescriptor = "L" + this.internalName + ";";
        }

        public GeneratedClassName(Class<?> superClass, String postfix) {
            this.name = MPLType.getName(superClass) + postfix;
            this.internalName = MPLType.getInternalName(superClass) + postfix;
            this.typeDescriptor = GeneratedClassName.computeNameDescriptor(superClass, postfix);
        }

        private static String computeNameDescriptor(Class<?> type, String postfix) {
            String basePath = MPLType.getDescriptor(type);
            return basePath.substring(0, basePath.length() - 1) + postfix + ";";
        }
    }

    private static class StaticFieldInit {
        public final String fieldName;
        public final Class<?> fieldType;
        public final int record;

        public StaticFieldInit(String fieldName, Class<?> fieldType, Object value) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.record = GeneratorArgumentStore.store(value);
        }
    }

    @FunctionalInterface
    public static interface JavassistAction {
        public void run(CtClass var1) throws NotFoundException, CannotCompileException;
    }

    public static final class Deferred<T>
    implements LazyInitializedObject {
        private static final Map<String, Deferred<?>> pending = new ConcurrentHashMap();
        private final GeneratorClassLoader classLoader;
        private final Builder<T> builder;
        private final GeneratedClassName name;
        private final Function<ExtendedClassWriter<T>, T> callback;
        private T generated;
        private RuntimeException generateError;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Deferred(GeneratorClassLoader classLoader, Builder<T> builder, Function<ExtendedClassWriter<T>, T> callback) {
            this.classLoader = classLoader;
            this.builder = builder;
            this.name = ((Builder)builder).exactName;
            this.callback = callback;
            this.generated = null;
            this.generateError = null;
            Map<String, Deferred<?>> map = pending;
            synchronized (map) {
                pending.put(this.name.name, this);
            }
        }

        static Class<?> load(String name) throws ClassNotFoundException {
            Deferred<?> deferred = pending.get(name);
            if (deferred == null) {
                return null;
            }
            try {
                return deferred.generate().getClass();
            }
            catch (Throwable t) {
                throw new ClassNotFoundException("Failed to generate class " + name, t);
            }
        }

        public String getName() {
            return this.name.name;
        }

        public String getInternalName() {
            return this.name.internalName;
        }

        public String getTypeDescriptor() {
            return this.name.typeDescriptor;
        }

        public synchronized T generate() {
            T generated = this.generated;
            if (generated != null) {
                return generated;
            }
            RuntimeException err = this.generateError;
            if (err != null) {
                throw err;
            }
            try {
                ExtendedClassWriter writer = new ExtendedClassWriter(this.classLoader, this.builder);
                T result = this.callback.apply(writer);
                if (result == null) {
                    throw new IllegalStateException("Deferred callback " + this.callback.getClass().getName() + " returned null");
                }
                this.generated = result;
                this.generateError = null;
                pending.remove(this.name.name);
                return result;
            }
            catch (RuntimeException err2) {
                this.generated = null;
                this.generateError = err2;
                throw err2;
            }
        }

        @Override
        public void forceInitialization() {
            this.generate();
        }
    }
}

