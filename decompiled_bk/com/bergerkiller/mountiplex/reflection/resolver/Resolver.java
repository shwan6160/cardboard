/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ChainResolver;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledFieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledMethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.NoOpResolver;
import com.bergerkiller.mountiplex.reflection.resolver.PackageNameCache;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Resolver {
    private static Resolver resolver = new Resolver();
    private ClassDeclarationResolver classDeclarationResolverChain = NoOpResolver.INSTANCE;
    private ClassPathResolver classPathResolverChain = NoOpResolver.INSTANCE;
    private CompiledFieldNameResolver compiledFieldNameResolverChain = NoOpResolver.INSTANCE;
    private CompiledMethodNameResolver compiledMethodNameResolverChain = NoOpResolver.INSTANCE;
    private FieldNameResolver fieldNameResolverChain = NoOpResolver.INSTANCE;
    private FieldAliasResolver fieldAliasResolverChain = NoOpResolver.INSTANCE;
    private MethodNameResolver methodNameResolverChain = NoOpResolver.INSTANCE;
    private MethodAliasResolver methodAliasResolverChain = NoOpResolver.INSTANCE;
    private boolean enableClassLoaderRemapping = false;
    private final HashMap<String, ClassMeta> classCache = new HashMap();
    private final Map<Class<?>, ClassMeta> classTypeCache = new ConcurrentHashMap();
    private final PackageNameCache packageNameCache = new PackageNameCache();

    private Resolver() {
    }

    private void initResolver() {
        BoxedType.getBoxedTypes().forEach(type -> this.classTypeCache.put((Class<?>)type, new ClassMeta((Class<?>)type)));
        BoxedType.getUnboxedTypes().forEach(type -> this.classTypeCache.put((Class<?>)type, new ClassMeta((Class<?>)type)));
    }

    public static PackageNameCache getPackageNameCache() {
        return Resolver.resolver.packageNameCache;
    }

    public static boolean isClassLoaderRemappingEnabled() {
        return Resolver.resolver.enableClassLoaderRemapping;
    }

    public static void setClassLoaderRemappingEnabled(boolean enabled) {
        Resolver.resolver.enableClassLoaderRemapping = enabled;
        MPLType.setRemappingEnabled(enabled);
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers()) && Resolver.isPublic(field.getDeclaringClass());
    }

    public static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers()) && Resolver.isPublic(method.getDeclaringClass());
    }

    public static boolean isPublic(Constructor<?> constructor) {
        return Modifier.isPublic(constructor.getModifiers()) && Resolver.isPublic(constructor.getDeclaringClass());
    }

    public static boolean isPublic(Class<?> type) {
        return Resolver.getMeta(type).isPublic;
    }

    public static ClassMeta getMeta(Class<?> type) {
        try {
            return Resolver.resolver.classTypeCache.computeIfAbsent(type, ClassMeta::new);
        }
        catch (IllegalStateException ex) {
            MountiplexUtil.LOGGER.log(Level.WARNING, "Recursive getMeta called while initializing " + type, ex);
            ClassMeta meta = new ClassMeta(type);
            Resolver.resolver.classTypeCache.put(type, meta);
            return meta;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class<?> getClassByExactName(String name) throws ClassNotFoundException {
        HashMap<String, ClassMeta> classCache;
        HashMap<String, ClassMeta> hashMap = classCache = Resolver.resolver.classCache;
        synchronized (hashMap) {
            ClassMeta meta = classCache.get(name);
            if (meta != null && meta.type != null && MPLType.getName(meta.type).equals(name)) {
                return meta.type;
            }
        }
        return MPLType.getClassByName(name);
    }

    public static Class<?> loadClass(String path, boolean initialize) {
        return Resolver.loadClass(path, initialize, Resolver.class.getClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class<?> loadClass(String path, boolean initialize, ClassLoader loader) {
        HashMap<String, ClassMeta> classCache;
        HashMap<String, ClassMeta> hashMap = classCache = Resolver.resolver.classCache;
        synchronized (hashMap) {
            ClassMeta meta = classCache.get(path);
            if (meta == null) {
                Class<?> type = Resolver.loadClassImpl(path, initialize, loader);
                if (type == null) {
                    classCache.put(path, ClassMeta.MISSING);
                    return null;
                }
                meta = Resolver.getMeta(type);
                meta.loaded = initialize;
                classCache.put(path, meta);
            }
            if (!meta.loaded && initialize) {
                if (meta.type != null) {
                    Resolver.initializeClass(meta.type);
                }
                meta.loaded = true;
            }
            return meta.type;
        }
    }

    private static Class<?> loadClassImpl(String path, boolean initialize, ClassLoader loader) {
        Class<?> prim = BoxedType.getUnboxedType(path);
        if (prim != null) {
            return prim;
        }
        if (path.endsWith("[]")) {
            Class<?> componentType = Resolver.loadClass(path.substring(0, path.length() - 2), initialize);
            if (componentType == null) {
                return null;
            }
            return Array.newInstance(componentType, 0).getClass();
        }
        if (!Resolver.resolver.packageNameCache.canExist(path)) {
            return null;
        }
        String alterPath = Resolver.resolveClassPath(path);
        try {
            Class<?> result = MPLType.getClassByName(alterPath, initialize, loader);
            Resolver.resolver.packageNameCache.addPackageOfClassName(path);
            return result;
        }
        catch (ExceptionInInitializerError e) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize class '" + alterPath + "':", e.getCause());
            return null;
        }
        catch (ClassNotFoundException e) {
            String parentClass;
            int dot_before_last;
            int last_dot = path.lastIndexOf(46);
            if (last_dot != -1 && Character.isUpperCase(path.charAt((dot_before_last = path.lastIndexOf(46, last_dot - 1)) + 1)) && Resolver.loadClass(parentClass = path.substring(0, last_dot), false) != null) {
                String path_new = parentClass + "$" + path.substring(last_dot + 1);
                return Resolver.loadClass(path_new, initialize);
            }
            return null;
        }
    }

    public static void initializeClass(Class<?> classType) {
        String className = MPLType.getName(classType);
        try {
            MPLType.getClassByName(className, true, classType.getClassLoader());
        }
        catch (ExceptionInInitializerError e) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize class '" + className + "':", e.getCause());
        }
        catch (ClassNotFoundException e) {
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Unhandled error initializing class " + classType, t);
        }
    }

    public static void registerClassDeclarationResolver(ClassDeclarationResolver resolver) {
        Resolver.resolver.classDeclarationResolverChain = ChainResolver.chain(Resolver.resolver.classDeclarationResolverChain, resolver);
    }

    public static void registerClassResolver(ClassPathResolver resolver) {
        Resolver.resolver.classPathResolverChain = ChainResolver.chain(Resolver.resolver.classPathResolverChain, resolver);
        Resolver.resolver.classCache.clear();
        Resolver.resolver.packageNameCache.reset();
    }

    public static void registerCompiledFieldResolver(CompiledFieldNameResolver resolver) {
        Resolver.resolver.compiledFieldNameResolverChain = ChainResolver.chain(Resolver.resolver.compiledFieldNameResolverChain, resolver);
    }

    public static void registerCompiledMethodResolver(CompiledMethodNameResolver resolver) {
        Resolver.resolver.compiledMethodNameResolverChain = ChainResolver.chain(Resolver.resolver.compiledMethodNameResolverChain, resolver);
    }

    public static void registerFieldResolver(FieldNameResolver resolver) {
        Resolver.resolver.fieldNameResolverChain = ChainResolver.chain(Resolver.resolver.fieldNameResolverChain, resolver);
    }

    public static void registerFieldAliasResolver(FieldAliasResolver resolver) {
        Resolver.resolver.fieldAliasResolverChain = ChainResolver.chain(Resolver.resolver.fieldAliasResolverChain, resolver);
    }

    public static void registerMethodResolver(MethodNameResolver resolver) {
        Resolver.resolver.methodNameResolverChain = ChainResolver.chain(Resolver.resolver.methodNameResolverChain, resolver);
    }

    public static void registerMethodAliasResolver(MethodAliasResolver resolver) {
        Resolver.resolver.methodAliasResolverChain = ChainResolver.chain(Resolver.resolver.methodAliasResolverChain, resolver);
    }

    public static ClassDeclaration resolveClassDeclaration(String classPath) {
        Class<?> classType = Resolver.loadClass(classPath, false);
        return classType == null ? null : Resolver.resolveClassDeclaration(classPath, classType);
    }

    public static ClassDeclaration resolveClassDeclaration(Class<?> classType) {
        return Resolver.resolveClassDeclaration(MPLType.getName(classType), classType);
    }

    public static ClassDeclaration resolveClassDeclaration(String classPath, Class<?> classType) {
        return Resolver.resolver.classDeclarationResolverChain.resolveClassDeclaration(classPath, classType);
    }

    public static Map<String, String> resolveClassVariables(String classPath, Class<?> classType) {
        HashMap<String, String> variables = new HashMap<String, String>();
        Resolver.resolver.classDeclarationResolverChain.resolveClassVariables(classPath, classType, variables);
        return variables;
    }

    public static String resolveClassPath(String classPath) {
        return Resolver.resolver.classPathResolverChain.resolveClassPath(classPath);
    }

    public static boolean canLoadClassPath(String classPath) {
        return Resolver.resolver.classPathResolverChain.canLoadClassPath(classPath);
    }

    public static String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return Resolver.resolver.fieldNameResolverChain.resolveFieldName(declaringClass, fieldName);
    }

    public static String resolveFieldAlias(Field field, String name) {
        return Resolver.resolver.fieldAliasResolverChain.resolveFieldAlias(field, name);
    }

    public static String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return Resolver.resolver.methodNameResolverChain.resolveMethodName(declaringClass, methodName, parameterTypes);
    }

    public static String resolveMethodAlias(Method method, String name) {
        return Resolver.resolver.methodAliasResolverChain.resolveMethodAlias(method, name);
    }

    public static String resolveCompiledFieldName(Class<?> declaringClass, String fieldName) {
        return Resolver.resolver.compiledFieldNameResolverChain.resolveCompiledFieldName(declaringClass, fieldName);
    }

    public static String resolveCompiledMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return Resolver.resolver.compiledMethodNameResolverChain.resolveCompiledMethodName(declaringClass, methodName, parameterTypes);
    }

    public static MethodDeclaration resolveMethodAlias(TypeDeclaration declaringClass, Method method) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(method.getDeclaringClass());
        MethodDeclaration result = new MethodDeclaration(resolver, method);
        String alias = Resolver.resolveMethodAliasInType(declaringClass, method);
        if (alias != null) {
            return result.setAlias(alias);
        }
        for (TypeDeclaration superType : declaringClass.getSuperTypes()) {
            alias = Resolver.resolveMethodAliasInType(superType, method);
            if (alias == null) continue;
            return result.setAlias(alias);
        }
        return result;
    }

    private static String resolveMethodAliasInType(TypeDeclaration type, Method method) {
        ClassDeclaration cDec = Resolver.resolveClassDeclaration(type.type);
        return cDec == null ? null : cDec.resolveMethodAlias(method);
    }

    public static FieldDeclaration findField(Class<?> type, String declaration) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(type);
        resolver.setLogErrors(true);
        FieldDeclaration fDec = new FieldDeclaration(resolver, StringBuffer.of(declaration));
        return fDec.discover();
    }

    public static MethodDeclaration findMethod(Class<?> type, String declaration) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(type);
        resolver.setLogErrors(true);
        MethodDeclaration mDec = new MethodDeclaration(resolver, declaration);
        return mDec.discover();
    }

    public static MethodDeclaration findMethod(MethodDeclaration declaration) {
        return declaration.discover();
    }

    public static Method resolveAndGetDeclaredMethod(Class<?> declaringClass, String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException, SecurityException {
        String trueName = Resolver.resolveMethodName(declaringClass, methodName, parameterTypes);
        return MPLType.getDeclaredMethod(declaringClass, trueName, parameterTypes);
    }

    public static Field resolveAndGetDeclaredField(Class<?> declaringClass, String fieldName) throws NoSuchFieldException, SecurityException {
        String trueName = Resolver.resolveFieldName(declaringClass, fieldName);
        return MPLType.getDeclaredField(declaringClass, trueName);
    }

    static {
        resolver.initResolver();
        MountiplexUtil.registerUnloader(new Runnable(){

            @Override
            public void run() {
                resolver = new Resolver();
                resolver.initResolver();
            }
        });
    }

    public static final class ClassMeta {
        public static final ClassMeta MISSING = new ClassMeta(null, true);
        public final Class<?> type;
        protected boolean loaded;
        public final TypeDeclaration typeDec;
        public final TypeDeclaration[] interfaces;
        public final TypeDeclaration superType;
        public final boolean isPublic;

        public ClassMeta(Class<?> type) {
            this(type, false);
        }

        public ClassMeta(Class<?> type, boolean loaded) {
            this.type = type;
            this.loaded = loaded;
            this.isPublic = ClassMeta.isPublicClass(type);
            if (type != null) {
                this.typeDec = new TypeDeclaration(ClassResolver.DEFAULT, type);
                this.superType = ClassMeta.findSuperType(this.typeDec);
                this.interfaces = ClassMeta.findInterfaces(this.typeDec);
            } else {
                this.typeDec = TypeDeclaration.INVALID;
                this.superType = TypeDeclaration.OBJECT;
                this.interfaces = new TypeDeclaration[0];
            }
        }

        private static boolean isPublicClass(Class<?> type) {
            if (type == null) {
                return true;
            }
            if (!Modifier.isPublic(type.getModifiers())) {
                return false;
            }
            try {
                return ClassMeta.isPublicClass(type.getDeclaringClass());
            }
            catch (Throwable t) {
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to detect whether " + MPLType.getName(type) + " is public. Assuming it is not public.", t);
                return false;
            }
        }

        private static TypeDeclaration fixResolveGenericTypes(TypeDeclaration type, TypeDeclaration base) {
            return base;
        }

        private static TypeDeclaration findSuperType(TypeDeclaration type) {
            try {
                Type s = type.type.getGenericSuperclass();
                return s == null ? null : ClassMeta.toTypeDec(s);
            }
            catch (TypeNotPresentException | GenericSignatureFormatError | MalformedParameterizedTypeException ex) {
                Class<?> s = type.type.getSuperclass();
                return s == null ? null : ClassMeta.fixResolveGenericTypes(type, ClassMeta.toTypeDec(s));
            }
        }

        private static TypeDeclaration[] findInterfaces(TypeDeclaration type) {
            try {
                Type[] interfaces = type.type.getGenericInterfaces();
                TypeDeclaration[] result = new TypeDeclaration[interfaces.length];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = ClassMeta.toTypeDec(interfaces[i]);
                }
                return result;
            }
            catch (TypeNotPresentException | GenericSignatureFormatError | MalformedParameterizedTypeException ex) {
                Class<?>[] interfaces = type.type.getInterfaces();
                TypeDeclaration[] result = new TypeDeclaration[interfaces.length];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = ClassMeta.fixResolveGenericTypes(type, ClassMeta.toTypeDec(interfaces[i]));
                }
                return result;
            }
        }

        private static TypeDeclaration toTypeDec(Type type) {
            ClassMeta meta;
            if (type instanceof Class && (meta = (ClassMeta)resolver.classTypeCache.get(type)) != null) {
                return meta.typeDec;
            }
            return new TypeDeclaration(ClassResolver.DEFAULT, type);
        }
    }
}

