/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisHelper;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ClassFieldCopier;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedHook;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class ClassInterceptor {
    private static final Object globalMethodDelegatesLock = new Object();
    private static Map<Class<?>, Map<Method, Invoker<?>>> globalMethodDelegatesMap = new HashMap();
    private static Map<ClassPair, EnhancedClass> enhancedTypes = new HashMap<ClassPair, EnhancedClass>();
    private boolean useGlobalCallbacks = true;
    private final ClassLoader hookClassLoader;
    private final Map<Method, Invoker<?>> globalMethodDelegates;
    private final InstanceHolder lastHookedObject = new InstanceHolder();
    private final ThreadLocal<StackInformation> stackInfo = ThreadLocal.withInitial(() -> new StackInformation());

    public ClassInterceptor() {
        this.hookClassLoader = this.getClass().getClassLoader();
        this.globalMethodDelegates = this.initMethodDelegatesMap();
    }

    public ClassInterceptor(ClassLoader hookClassLoader) {
        this.hookClassLoader = hookClassLoader;
        this.globalMethodDelegates = this.initMethodDelegatesMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<Method, Invoker<?>> initMethodDelegatesMap() {
        Object object = globalMethodDelegatesLock;
        synchronized (object) {
            return globalMethodDelegatesMap.computeIfAbsent(this.getClass(), k -> new HashMap());
        }
    }

    protected abstract Invoker<?> getCallback(Method var1);

    protected Invoker<?> getCallback(Class<?> hookedType, Method method) {
        return this.getCallback(method);
    }

    protected void onClassGenerated(Class<?> hookedType) {
    }

    public ClassLoader getHookClassLoader() {
        return this.hookClassLoader;
    }

    public <T> T createInstance(Class<T> type) {
        return ClassInterceptor.createEnhancedClass(this, type, null, null, null);
    }

    public <T> T constructInstance(Class<T> type, Class<?>[] paramTypes, Object[] params) {
        return ClassInterceptor.createEnhancedClass(this, type, null, paramTypes, params);
    }

    public <T> T hook(T object) {
        return ClassInterceptor.createEnhancedClass(this, object.getClass(), object, null, null);
    }

    public void mock(Object object) {
        this.lastHookedObject.value = object;
    }

    public static <T> T unhook(T object) {
        if (object instanceof EnhancedObject) {
            EnhancedObject enhancedObject = (EnhancedObject)object;
            ClassInterceptor ci = enhancedObject.CI_getInterceptor();
            EnhancedClass eh = enhancedObject.CI_getEnhancedClass();
            if (ci.lastHookedObject.value == object) {
                ci.lastHookedObject.value = null;
            }
            return eh.createBase(object);
        }
        return object;
    }

    public static <T extends ClassInterceptor> T get(Object object, Class<T> interceptorClass) {
        ClassInterceptor interceptor = ClassInterceptor.get(object);
        return (T)(interceptorClass.isInstance(interceptor) ? interceptor : null);
    }

    protected static ClassInterceptor get(Object object) {
        return object instanceof EnhancedObject ? ((EnhancedObject)object).CI_getInterceptor() : null;
    }

    protected void storeCallbackForCurrentThread(Method method, Invoker<?> callback) {
        this.stackInfo.get().storeCallback(method, callback);
    }

    public final void setUseGlobalCallbacks(boolean global) {
        this.useGlobalCallbacks = global;
    }

    protected final Object instance() {
        Object stack_instance = this.stackInfo.get().currentInstance();
        if (stack_instance != null) {
            return stack_instance;
        }
        if (this.lastHookedObject.value == null) {
            throw new IllegalStateException("No object is handled right now");
        }
        return this.lastHookedObject.value;
    }

    protected final Class<?> instanceBaseType() {
        return ClassInterceptor.findInstanceBaseType(this.instance());
    }

    public final Object invokeSuperMethod(Method method, Object instance, Object[] args) {
        return GeneratedHook.createSuperInvoker(instance.getClass(), method).invokeVA(instance, args);
    }

    public static Class<?> findInstanceBaseType(Object instance) {
        if (instance instanceof EnhancedObject) {
            return ((EnhancedObject)instance).CI_getBaseType();
        }
        if (instance != null) {
            return instance.getClass();
        }
        return null;
    }

    public static Class<?> findBaseType(Class<?> enhancedType) {
        if (enhancedType == null) {
            return null;
        }
        while (EnhancedObject.class.isAssignableFrom(enhancedType)) {
            enhancedType = enhancedType.getSuperclass();
        }
        return enhancedType;
    }

    private static synchronized <T> T createEnhancedClass(ClassInterceptor interceptor, Class<?> objectType, T object, Class<?>[] paramTypes, Object[] params) {
        if (objectType == null) {
            throw new IllegalArgumentException("Input class type to be intercepted is null");
        }
        ClassPair key = new ClassPair(interceptor.getClass(), objectType);
        EnhancedClass enhanced = enhancedTypes.get(key);
        if (enhanced == null) {
            EnhancedClass new_enhanced = new EnhancedClass(objectType);
            StackInformation current_stack = interceptor.stackInfo.get();
            new_enhanced.setupEnhancedType(GeneratedHook.generate(interceptor.getHookClassLoader(), objectType, Arrays.asList(EnhancedObject.class), method -> {
                Invoker<?> callback;
                String name = MPLType.getName(method);
                if (method.getParameterCount() == 0) {
                    if (name.equals("CI_getInterceptor")) {
                        return new_enhanced.getInterceptorCallback;
                    }
                    if (name.equals("CI_getBaseType")) {
                        return ConstantReturningInvoker.of(objectType);
                    }
                    if (name.equals("CI_getEnhancedClass")) {
                        return ConstantReturningInvoker.of(new_enhanced);
                    }
                }
                if ((callback = interceptor.getCallback(objectType, (Method)method)) == null) {
                    return null;
                }
                current_stack.storeCallback((Method)method, callback);
                return new CallbackMethodInterceptor((Method)method);
            }));
            enhanced = new_enhanced;
            enhancedTypes.put(key, enhanced);
            interceptor.onClassGenerated(enhanced.enhancedType);
        }
        enhanced.currentInterceptor = interceptor;
        T enhancedObject = enhanced.createEnhanced(object, paramTypes, params);
        interceptor.lastHookedObject.value = enhancedObject;
        ((EnhancedObject)enhancedObject).CI_getInterceptor();
        enhanced.currentInterceptor = null;
        return enhancedObject;
    }

    static {
        MountiplexUtil.registerUnloader(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Object object = globalMethodDelegatesLock;
                synchronized (object) {
                    globalMethodDelegatesMap = new HashMap(0);
                    enhancedTypes = new HashMap(0);
                }
            }
        });
    }

    private static class InstanceHolder {
        public Object value = null;

        private InstanceHolder() {
        }
    }

    protected static interface EnhancedObject {
        public ClassInterceptor CI_getInterceptor();

        public Class<?> CI_getBaseType();

        public EnhancedClass CI_getEnhancedClass();
    }

    public static final class EnhancedClass {
        public final Class<?> baseType;
        public final ObjectInstantiator<?> baseInstantiator;
        public final Invoker<?> getInterceptorCallback;
        private final ClassFieldCopier<Object> baseFieldCopier;
        public Class<?> enhancedType;
        public ObjectInstantiator<?> enhancedInstantiator;
        public ClassInterceptor currentInterceptor;

        public EnhancedClass(Class<?> baseType) {
            this.baseType = baseType;
            if (baseType.isInterface()) {
                this.baseInstantiator = () -> {
                    throw new UnsupportedOperationException("Base type " + baseType.getName() + " is an interface and cannot be instantiated");
                };
            } else {
                this.baseInstantiator = ObjenesisHelper.getInstantiatorOf(baseType);
                if (this.baseInstantiator == null) {
                    throw new RuntimeException("Base Class " + MPLType.getName(baseType) + " has no instantiator");
                }
            }
            this.baseFieldCopier = ClassFieldCopier.of(baseType);
            this.getInterceptorCallback = GeneratedHook.createLocalField(() -> this.currentInterceptor);
        }

        public void setupEnhancedType(Class<?> enhancedType) {
            this.enhancedType = enhancedType;
            this.enhancedInstantiator = ObjenesisHelper.getInstantiatorOf(enhancedType);
            if (this.enhancedInstantiator == null) {
                throw new RuntimeException("Enhanced Class " + MPLType.getName(enhancedType) + " has no instantiator");
            }
        }

        public <T> T createBase(T enhanced) {
            Object base = this.baseInstantiator.newInstance();
            if (base == null) {
                throw new RuntimeException("Class " + MPLType.getName(this.baseType) + " could not be instantiated (newInstance failed)");
            }
            this.baseFieldCopier.copy(enhanced, base);
            return (T)base;
        }

        public <T> T createEnhanced(T base, Class<?>[] paramTypes, Object[] params) {
            Object enhanced = null;
            if (paramTypes == null) {
                enhanced = this.enhancedInstantiator.newInstance();
            } else {
                Constructor<?> constructor = null;
                try {
                    constructor = this.enhancedType.getConstructor(paramTypes);
                    enhanced = constructor.newInstance(params);
                }
                catch (Throwable t) {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to construct " + MPLType.getName(this.enhancedType), t);
                }
            }
            if (enhanced == null) {
                throw new RuntimeException("Class " + MPLType.getName(this.enhancedType) + " could not be instantiated (newInstance failed)");
            }
            if (base != null) {
                this.baseFieldCopier.copy(base, enhanced);
            }
            return enhanced;
        }
    }

    private static final class StackInformation {
        private final Map<Method, Invoker<?>> methodDelegates_fast = new IdentityHashMap();
        private final Map<Method, Invoker<?>> methodDelegates = new HashMap();
        private Method last_method = null;
        private Invoker<?> last_method_callback = null;
        public StackFrame frame = new StackFrame(null);

        private StackInformation() {
        }

        public Object currentInstance() {
            return this.frame.instance;
        }

        public Invoker<?> getCallback(Method method) {
            if (method == this.last_method) {
                return this.last_method_callback;
            }
            Invoker<?> callback = this.methodDelegates_fast.get(method);
            if (callback != null) {
                this.last_method = method;
                this.last_method_callback = callback;
                return callback;
            }
            callback = this.methodDelegates.get(method);
            if (callback != null) {
                this.methodDelegates_fast.put(method, callback);
                this.last_method = method;
                this.last_method_callback = callback;
                return callback;
            }
            return null;
        }

        public void storeCallback(Method method, Invoker<?> callback) {
            this.last_method = method;
            this.last_method_callback = callback;
            this.methodDelegates.put(method, callback);
        }
    }

    private static final class ClassPair {
        public final Class<?> hookClass;
        public final Class<?> instanceClass;
        private final int hashcode;

        public ClassPair(Class<?> hookClass, Class<?> instanceClass) {
            this.hookClass = hookClass;
            this.instanceClass = instanceClass;
            this.hashcode = (hookClass.hashCode() >> 1) + (instanceClass.hashCode() >> 1);
        }

        public int hashCode() {
            return this.hashcode;
        }

        public boolean equals(Object other) {
            if (other instanceof ClassPair) {
                ClassPair p = (ClassPair)other;
                return this.hookClass.equals(p.hookClass) && this.instanceClass.equals(p.instanceClass);
            }
            return false;
        }
    }

    private static class CallbackMethodInterceptor
    implements Invoker<Object> {
        private final Method method;

        public CallbackMethodInterceptor(Method method) {
            this.method = method;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object invokeVA(Object instance, Object ... args) {
            ClassInterceptor interceptor = ((EnhancedObject)instance).CI_getInterceptor();
            StackInformation stack = (StackInformation)interceptor.stackInfo.get();
            StackFrame frame = stack.frame.next;
            if (frame == null) {
                frame = stack.frame.next = new StackFrame(stack.frame);
            }
            stack.frame = frame;
            try {
                Map map;
                frame.instance = instance;
                Invoker<Object> callback = stack.getCallback(this.method);
                if (callback == null) {
                    map = interceptor.globalMethodDelegates;
                    synchronized (map) {
                        callback = (Invoker<?>)interceptor.globalMethodDelegates.get(this.method);
                    }
                    if (callback == null) {
                        callback = interceptor.getCallback(interceptor.instanceBaseType(), this.method);
                        if (callback == null) {
                            callback = GeneratedHook.createSuperInvoker(instance.getClass(), this.method);
                        }
                        if (interceptor.useGlobalCallbacks) {
                            map = interceptor.globalMethodDelegates;
                            synchronized (map) {
                                interceptor.globalMethodDelegates.put(this.method, callback);
                            }
                        }
                    }
                    stack.storeCallback(this.method, callback);
                }
                if (callback instanceof InterceptorCallback) {
                    callback = ((InterceptorCallback)callback).interceptorCallback;
                    instance = interceptor;
                }
                map = callback.invokeVA(instance, args);
                return map;
            }
            finally {
                frame.instance = null;
                stack.frame = frame.prev;
            }
        }
    }

    private static final class StackFrame {
        public Object instance = null;
        public final StackFrame prev;
        public StackFrame next = null;

        public StackFrame(StackFrame prev) {
            this.prev = prev;
        }
    }

    public static abstract class InterceptorCallback
    implements Invoker<Object> {
        public Invoker<Object> interceptorCallback = InitInvoker.unavailableMethod();

        @Override
        public Object invokeVA(Object instance, Object ... args) {
            return this.interceptorCallback.invokeVA(((EnhancedObject)instance).CI_getInterceptor(), args);
        }
    }
}

