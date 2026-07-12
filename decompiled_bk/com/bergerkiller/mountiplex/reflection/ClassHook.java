/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedCodeInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedHook;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ClassHook<T extends ClassHook<?>>
extends ClassInterceptor {
    private static Map<Class<?>, HookMethodList> hookMethodMap = new HashMap();
    public T base;
    private final HookMethodList methods = ClassHook.loadMethodList(this.getClass());

    public ClassHook(ClassLoader hookClassLoader) {
        super(hookClassLoader);
        this.base = this.initBaseHook();
    }

    public ClassHook() {
        this.base = this.initBaseHook();
    }

    private T initBaseHook() {
        return (T)new BaseClassInterceptor(this.getHookClassLoader(), this.methods).hook(this);
    }

    @Override
    protected final Invoker<?> getCallback(Method method) {
        throw new UnsupportedOperationException("Should not be called");
    }

    @Override
    protected Invoker<?> getCallback(Class<?> hookedType, Method method) {
        TypeDeclaration method_type = TypeDeclaration.fromClass(method.getDeclaringClass());
        MethodDeclaration methodDec = Resolver.resolveMethodAlias(TypeDeclaration.fromClass(hookedType), method);
        ClassResolver classLevelResolver = methodDec.getResolver();
        classLevelResolver.setClassLoader(this.methods.hookClassLoader);
        if (this.methods.classImports.length > 0 || this.methods.classPackage != null) {
            classLevelResolver = classLevelResolver.clone();
            if (this.methods.classPackage != null) {
                classLevelResolver.setPackage(this.methods.classPackage, false);
            }
            classLevelResolver.addImports(Arrays.asList(this.methods.classImports));
        }
        for (HookMethodEntry entry : this.methods.entries) {
            MethodDeclaration m;
            ClassResolver resolver = classLevelResolver;
            if (entry.hookImports.length > 0 || entry.hookPackage != null || entry.variablesResolver != null) {
                resolver = resolver.clone();
                if (entry.hookPackage != null) {
                    resolver.setPackage(entry.hookPackage, false);
                }
                if (entry.hookImports.length > 0) {
                    resolver.addImports(Arrays.asList(entry.hookImports));
                }
                if (entry.variablesResolver != null) {
                    resolver.setAllVariables(entry.variablesResolver);
                }
            }
            if (!(m = new MethodDeclaration(resolver, entry.declaration).resolveName()).isValid() || !m.isResolved() || !m.match(methodDec)) continue;
            entry.setMethod(method_type, method);
            return entry;
        }
        return null;
    }

    @Override
    protected void onClassGenerated(Class<?> hookedType) {
        super.onClassGenerated(hookedType);
        TypeDeclaration ht = TypeDeclaration.fromClass(hookedType);
        for (HookMethodEntry method : this.methods.entries) {
            if (method.optional || method.foundMethod(ht)) continue;
            Class<?> baseType = hookedType;
            if (ClassInterceptor.EnhancedObject.class.isAssignableFrom(hookedType) && (baseType = hookedType.getSuperclass()).equals(Object.class) && hookedType.getInterfaces().length > 1) {
                for (Class<?> interfaceType : hookedType.getInterfaces()) {
                    if (interfaceType == ClassInterceptor.EnhancedObject.class) continue;
                    baseType = interfaceType;
                    break;
                }
            }
            MountiplexUtil.LOGGER.warning("Hooked method " + method.toString() + " was not found in " + MPLType.getName(baseType));
        }
    }

    private static HookMethodList loadMethodList(Class<?> hookClass) {
        if (!ClassHook.class.isAssignableFrom(hookClass)) {
            return new HookMethodList();
        }
        HookMethodList list = hookMethodMap.get(hookClass);
        if (list == null) {
            list = new HookMethodList(hookClass);
            for (Method method : hookClass.getDeclaredMethods()) {
                HookMethod hm = method.getAnnotation(HookMethod.class);
                if (hm == null) continue;
                HookMethodEntry entry = new HookMethodEntry(list, method, hm.value(), hm.optional());
                if (!entry.enabled) continue;
                list.entries.add(entry);
            }
            list.entries.addAll(ClassHook.loadMethodList(hookClass.getSuperclass()).entries);
            hookMethodMap.put(hookClass, list);
        }
        return list;
    }

    private static ClassDeclarationResolver loadHookVariablesResolver(Class<?> declaringClass, String code) {
        if (code == null) {
            return null;
        }
        try {
            ClassResolver resolver = new ClassResolver();
            resolver.setClassLoader(declaringClass.getClassLoader());
            resolver.setDeclaredClass(ClassDeclarationResolver.class);
            MethodDeclaration decl = new MethodDeclaration(resolver, "public static ClassDeclarationResolver run() {\n    return " + code + ";\n}");
            GeneratedCodeInvoker invoker = GeneratedCodeInvoker.create(decl);
            return (ClassDeclarationResolver)invoker.invoke(null);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize hook load variables: " + code, t);
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to load Hook Variables for " + declaringClass.getName());
            return null;
        }
    }

    static {
        MountiplexUtil.registerUnloader(new Runnable(){

            @Override
            public void run() {
                hookMethodMap = new HashMap(0);
            }
        });
    }

    private static class HookMethodList {
        public final ClassLoader hookClassLoader;
        public final List<HookMethodEntry> entries = new ArrayList<HookMethodEntry>();
        public final String[] classImports;
        public final String classPackage;
        public final ClassDeclarationResolver variablesResolver;

        public HookMethodList() {
            this.hookClassLoader = HookMethodList.class.getClassLoader();
            this.classImports = new String[0];
            this.classPackage = null;
            this.variablesResolver = null;
        }

        public HookMethodList(Class<?> hookClassType) {
            this.hookClassLoader = hookClassType.getClassLoader();
            this.classImports = (String[])ReflectionUtil.getAllClassesAndInterfaces(hookClassType).flatMap(c -> Stream.of((HookImport[])c.getDeclaredAnnotationsByType(HookImport.class))).map(HookImport::value).toArray(String[]::new);
            HookPackage packageAnnot = hookClassType.getAnnotation(HookPackage.class);
            this.classPackage = packageAnnot == null ? null : packageAnnot.value();
            this.variablesResolver = ClassHook.loadHookVariablesResolver(hookClassType, ReflectionUtil.recurseFindAnnotationValue(hookClassType, HookLoadVariables.class, HookLoadVariables::value, null));
        }
    }

    private static class BaseClassInterceptor
    extends ClassInterceptor {
        private final HookMethodList methodList;

        public BaseClassInterceptor(ClassLoader classLoader, HookMethodList methodList) {
            super(classLoader);
            this.methodList = methodList;
        }

        @Override
        protected Invoker<?> getCallback(Method method) {
            HookMethodEntry foundEntry = null;
            Iterator<HookMethodEntry> iter = this.methodList.entries.iterator();
            do {
                if (!iter.hasNext()) {
                    return null;
                }
                foundEntry = iter.next();
            } while (!foundEntry.method.equals(method));
            return foundEntry.baseInvokable;
        }
    }

    private static class HookMethodEntry
    extends ClassInterceptor.InterceptorCallback {
        public final InputTypeMap<Method> superMethodMap = new InputTypeMap();
        public final Map<Class<?>, Invoker<?>> superInvokerMap = new HashMap();
        public final ClassDeclarationResolver variablesResolver;
        public final HookMethodList owner;
        public final String declaration;
        public final boolean optional;
        public final boolean enabled;
        public final Method method;
        public final String[] hookImports;
        public final String hookPackage;
        public final Invoker<?> baseInvokable = (instance, args) -> {
            Object enhancedInstance = ((ClassHook)instance).instance();
            Class<?> enhancedType = enhancedInstance.getClass();
            if (enhancedInstance instanceof ClassInterceptor.EnhancedObject) {
                Invoker invoker = this.superInvokerMap.computeIfAbsent(enhancedType, type -> {
                    ClassInterceptor.EnhancedObject enhanced = (ClassInterceptor.EnhancedObject)enhancedInstance;
                    Method m = this.findMethodIn(TypeDeclaration.fromClass(enhanced.CI_getBaseType()));
                    if (m == null) {
                        throw new UnsupportedOperationException("Class " + MPLType.getName(enhanced.CI_getBaseType()) + " does not contain method " + this.toString());
                    }
                    return GeneratedHook.createSuperInvoker(enhancedType, m);
                });
                return invoker.invokeVA(enhancedInstance, args);
            }
            Method m = this.findMethodIn(TypeDeclaration.fromClass(enhancedType));
            if (m == null) {
                throw new UnsupportedOperationException("Class " + MPLType.getName(enhancedType) + " does not contain method " + this.toString());
            }
            try {
                return m.invoke(enhancedInstance, args);
            }
            catch (Throwable ex) {
                throw ReflectionUtil.fixMethodInvokeException(m, enhancedInstance, args, ex);
            }
        };

        public HookMethodEntry(HookMethodList list, Method method, String name, boolean optional) {
            this.owner = list;
            this.method = method;
            this.declaration = name;
            this.optional = optional;
            this.interceptorCallback = InitInvoker.forMethod(this.getClass().getClassLoader(), this, "interceptorCallback", method);
            this.hookImports = (String[])Stream.of((HookImport[])method.getDeclaredAnnotationsByType(HookImport.class)).map(HookImport::value).toArray(String[]::new);
            HookPackage hookAnnot = method.getAnnotation(HookPackage.class);
            this.hookPackage = hookAnnot == null ? null : hookAnnot.value();
            HookLoadVariables loadVarsAnnot = method.getAnnotation(HookLoadVariables.class);
            this.variablesResolver = loadVarsAnnot == null ? list.variablesResolver : ClassHook.loadHookVariablesResolver(method.getDeclaringClass(), loadVarsAnnot.value());
            HookMethodCondition conditionAnnot = method.getAnnotation(HookMethodCondition.class);
            if (conditionAnnot != null) {
                ClassResolver resolver = new ClassResolver();
                resolver.setDeclaredClass(Object.class);
                if (this.variablesResolver != null) {
                    resolver.setAllVariables(this.variablesResolver);
                }
                this.enabled = resolver.evaluateExpression(conditionAnnot.value());
            } else {
                this.enabled = true;
            }
        }

        public String toString() {
            return this.declaration;
        }

        public boolean foundMethod(TypeDeclaration type) {
            return this.superMethodMap.containsKey(type);
        }

        public void setMethod(TypeDeclaration type, Method method) {
            this.superMethodMap.put(type, method);
        }

        public ClassResolver createResolver(Class<?> type) {
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(type);
            if (this.hookPackage != null) {
                resolver.setPackage(this.hookPackage);
            } else if (this.owner.classPackage != null) {
                resolver.setPackage(this.owner.classPackage);
            }
            resolver.addImports(Arrays.asList(this.owner.classImports));
            resolver.addImports(Arrays.asList(this.hookImports));
            resolver.setLogErrors(true);
            resolver.setClassLoader(this.owner.hookClassLoader);
            return resolver;
        }

        public Method findMethodIn(TypeDeclaration type) {
            if (type == null || !type.isResolved()) {
                return null;
            }
            Method m = (Method)this.superMethodMap.get(type);
            if (m == null) {
                ClassResolver resolver = this.createResolver(type.type);
                MethodDeclaration mDec = new MethodDeclaration(resolver, this.declaration);
                if ((mDec = mDec.discover()) != null && (m = mDec.method) != null) {
                    this.superMethodMap.put(type, m);
                }
            }
            return m;
        }
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HookMethod {
        public String value();

        public boolean optional() default false;
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HookMethodCondition {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HookLoadVariables {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HookImportList {
        public HookImport[] value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Repeatable(value=HookImportList.class)
    public static @interface HookImport {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HookPackage {
        public String value();
    }
}

