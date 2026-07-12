/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLGeneratorClassLoaderBuilder;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedCodeInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedConstructor;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedInvoker;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;

public abstract class GeneratorClassLoader
extends ClassLoader {
    private static final Map<String, Class<?>> staticClasses = new HashMap();
    private static WeakHashMap<ClassLoader, GeneratorClassLoader> loaders = new WeakHashMap();
    private static final Constructor<? extends GeneratorClassLoader> implementationFactory;
    private static final GeneratorNotSupportedException implementationTypeFailure;

    private static void registerStaticClass(Class<?> type) {
        staticClasses.put(type.getName(), type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GeneratorClassLoader get(ClassLoader baseClassLoader) {
        GeneratorClassLoader loader = loaders.get(baseClassLoader);
        if (loader != null) {
            return loader;
        }
        Class<GeneratorClassLoader> clazz = GeneratorClassLoader.class;
        synchronized (GeneratorClassLoader.class) {
            loader = loaders.get(baseClassLoader);
            if (loader != null) {
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return loader;
            }
            loader = GeneratorClassLoader.create(baseClassLoader);
            WeakHashMap<ClassLoader, GeneratorClassLoader> newLoaders = new WeakHashMap<ClassLoader, GeneratorClassLoader>(loaders);
            newLoaders.put(baseClassLoader, loader);
            loaders = newLoaders;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return loader;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void remove(ClassLoader baseClassLoader) {
        Class<GeneratorClassLoader> clazz = GeneratorClassLoader.class;
        synchronized (GeneratorClassLoader.class) {
            if (loaders.containsKey(baseClassLoader)) {
                WeakHashMap<ClassLoader, GeneratorClassLoader> newLoaders = new WeakHashMap<ClassLoader, GeneratorClassLoader>(loaders);
                newLoaders.remove(baseClassLoader);
                loaders = newLoaders;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    public static Class<?> findGeneratedClass(String name) {
        for (GeneratorClassLoader loader : loaders.values()) {
            Class<?> loaded = loader.findLoadedClass(name);
            if (loaded == null) continue;
            return loaded;
        }
        return null;
    }

    private static GeneratorClassLoader create(ClassLoader base) {
        if (base instanceof GeneratorClassLoader) {
            return (GeneratorClassLoader)base;
        }
        if (implementationFactory != null) {
            try {
                return implementationFactory.newInstance(base);
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
        throw implementationTypeFailure;
    }

    protected GeneratorClassLoader(ClassLoader base) {
        super(base);
    }

    private Class<?> superFindClass(String name) throws MPLType.LoaderClosedException {
        Class<?> loaded = this.findLoadedClass(name);
        return loaded != null ? loaded : GeneratorClassLoader.tryFindClass(this.getParent(), name);
    }

    private static Class<?> tryFindClass(ClassLoader loader, String name) throws MPLType.LoaderClosedException {
        try {
            return MPLType.getClassByName(name, false, loader);
        }
        catch (MPLType.LoaderClosedException ex) {
            throw ex;
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> foundInStaticClasses = staticClasses.get(name);
        if (foundInStaticClasses != null) {
            return foundInStaticClasses;
        }
        try {
            return super.loadClass(name, resolve);
        }
        catch (IllegalStateException is_ex) {
            if ("zip file closed".equals(is_ex.getMessage())) {
                throw new MPLType.LoaderClosedException();
            }
            throw new ClassNotFoundException("Failed to load class " + name, is_ex);
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> loaded = ExtendedClassWriter.Deferred.load(name);
        if (loaded != null) {
            return loaded;
        }
        GeneratorClassLoader mountiplexLoader = loaders.get(GeneratorClassLoader.class.getClassLoader());
        if (mountiplexLoader != this) {
            try {
                if (mountiplexLoader != null ? (loaded = mountiplexLoader.superFindClass(name)) != null : (loaded = GeneratorClassLoader.tryFindClass(GeneratorClassLoader.class.getClassLoader(), name)) != null) {
                    return loaded;
                }
            }
            catch (MPLType.LoaderClosedException ex) {
                GeneratorClassLoader.remove(ExtendedClassWriter.class.getClassLoader());
            }
        }
        for (GeneratorClassLoader otherLoader : loaders.values()) {
            if (otherLoader == mountiplexLoader || otherLoader == this) continue;
            try {
                loaded = otherLoader.superFindClass(name);
                if (loaded == null) continue;
                return loaded;
            }
            catch (MPLType.LoaderClosedException ex) {
                GeneratorClassLoader.remove(otherLoader.getParent());
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) {
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    @Override
    @Deprecated
    public Package getPackage(String name) {
        return super.getPackage(name);
    }

    public Class<?> createClassFromBytecode(String name, byte[] b, ProtectionDomain protectionDomain) {
        return this.createClassFromBytecode(name, b, protectionDomain, true);
    }

    public Class<?> createClassFromBytecode(String name, byte[] b, ProtectionDomain protectionDomain, boolean allowRemapping) {
        if (allowRemapping && Resolver.isClassLoaderRemappingEnabled()) {
            return super.defineClass(name, b, 0, b.length, protectionDomain);
        }
        return this.defineClassFromBytecode(name, b, protectionDomain);
    }

    protected abstract Class<?> defineClassFromBytecode(String var1, byte[] var2, ProtectionDomain var3);

    static {
        MountiplexUtil.registerUnloader(new Runnable(){

            @Override
            public void run() {
                loaders = new WeakHashMap(0);
            }
        });
        Class<? extends GeneratorClassLoader> theImplementationType = null;
        Constructor<? extends GeneratorClassLoader> theImplementationFactory = null;
        GeneratorNotSupportedException theImplementationTypeFailure = null;
        try {
            theImplementationType = MPLGeneratorClassLoaderBuilder.buildImplementation();
            theImplementationFactory = theImplementationType.getConstructor(ClassLoader.class);
        }
        catch (Throwable t) {
            theImplementationTypeFailure = new GeneratorNotSupportedException(t);
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize generator class builder", t);
        }
        implementationFactory = theImplementationFactory;
        implementationTypeFailure = theImplementationTypeFailure;
        GeneratorClassLoader.registerStaticClass(GeneratedInvoker.class);
        GeneratorClassLoader.registerStaticClass(GeneratedCodeInvoker.class);
        GeneratorClassLoader.registerStaticClass(GeneratedConstructor.class);
    }

    public static class GeneratorNotSupportedException
    extends RuntimeException {
        private static final long serialVersionUID = -362584700480972819L;

        public GeneratorNotSupportedException(Throwable reason) {
            super("Generating classes is not supported on this JDK", reason);
        }
    }
}

