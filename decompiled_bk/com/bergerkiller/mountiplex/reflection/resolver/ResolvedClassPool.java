/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.Descriptor;
import com.bergerkiller.mountiplex.dep.javassist.compiler.MemberResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.bergerkiller.mountiplex.reflection.util.asm.ClassBytecodeLoader;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class ResolvedClassPool
extends ClassPool
implements Closeable {
    private static final Remapping.Lookup NO_REMAPPINGS = Remapping.createLookup();
    private static final List<ResolvedClassPool> CACHED_CLASS_POOLS = new ArrayList<ResolvedClassPool>();
    private static final Map<ClassPool, Reference<Map<String, String>>> globalInvalidNamesMap = ResolvedClassPool.findInvalidNamesMap();
    private boolean ignoreRemapper = false;
    private Remapping.Lookup remappings = NO_REMAPPINGS;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ResolvedClassPool create() {
        List<ResolvedClassPool> list = CACHED_CLASS_POOLS;
        synchronized (list) {
            if (!CACHED_CLASS_POOLS.isEmpty()) {
                return CACHED_CLASS_POOLS.remove(CACHED_CLASS_POOLS.size() - 1);
            }
        }
        return new ResolvedClassPool();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        this.clearImportedPackages();
        this.remappings = NO_REMAPPINGS;
        ResolvedClassPool.resetInvalidNames(this);
        List<ResolvedClassPool> list = CACHED_CLASS_POOLS;
        synchronized (list) {
            CACHED_CLASS_POOLS.add(this);
        }
    }

    private ResolvedClassPool() {
        this.appendClassPath(ClassBytecodeLoader.CLASSPATH);
    }

    public Remapping.Lookup getRemappings() {
        return this.remappings;
    }

    public void setRemappings(Remapping.Lookup remappings) {
        this.remappings = remappings;
    }

    @Override
    public Class<?> toClass(CtClass ct, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
        if (loader instanceof GeneratorClassLoader && neighbor == null) {
            GeneratorClassLoader generator = (GeneratorClassLoader)loader;
            try {
                return generator.createClassFromBytecode(ct.getName(), ct.toBytecode(), domain);
            }
            catch (IOException e) {
                throw new CannotCompileException(e);
            }
        }
        return super.toClass(ct, neighbor, loader, domain);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CtClass getWithoutResolving(String classname) throws NotFoundException {
        boolean old = this.ignoreRemapper;
        try {
            this.ignoreRemapper = true;
            CtClass ctClass = super.get(classname);
            return ctClass;
        }
        finally {
            this.ignoreRemapper = old;
        }
    }

    public IgnoreToken ignoreResolver() {
        boolean current = this.ignoreRemapper;
        this.ignoreRemapper = true;
        return new IgnoreToken(current);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CtClass get(String classname) throws NotFoundException {
        if (this.ignoreRemapper) {
            return super.get(classname);
        }
        if (classname == null) {
            return null;
        }
        String resolvedName = this.resolveClassPath(classname);
        if (resolvedName == null) {
            throw new NotFoundException(classname);
        }
        CtClass fromCache = this.getCached(resolvedName);
        if (fromCache != null) {
            return fromCache;
        }
        try {
            this.ignoreRemapper = true;
            CtClass found = super.get0(resolvedName, false);
            if (found == null) {
                throw new NotFoundException(classname);
            }
            Resolver.getPackageNameCache().addPackageOfClassName(classname);
            CtClass ctClass = found;
            return ctClass;
        }
        finally {
            this.ignoreRemapper = false;
        }
    }

    @Override
    public URL find(String classname) {
        if (this.ignoreRemapper) {
            return super.find(classname);
        }
        if (classname == null) {
            return null;
        }
        String resolvedName = this.resolveClassPath(classname);
        if (resolvedName == null) {
            return null;
        }
        URL url = super.find(resolvedName);
        if (url == null) {
            return null;
        }
        Resolver.getPackageNameCache().addPackage(classname);
        return url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected CtClass createCtClass(String classname, boolean useCache) {
        if (this.ignoreRemapper) {
            return super.createCtClass(classname, useCache);
        }
        String resolvedName = classname;
        if (resolvedName.charAt(0) == '[') {
            resolvedName = Descriptor.toClassName(classname);
        }
        int numArrayDims = 0;
        while (resolvedName.endsWith("[]")) {
            resolvedName = resolvedName.substring(0, resolvedName.length() - 2);
            ++numArrayDims;
        }
        if ((resolvedName = this.resolveClassPath(resolvedName)) == null) {
            return null;
        }
        while (numArrayDims-- > 0) {
            resolvedName = resolvedName + "[]";
        }
        try {
            this.ignoreRemapper = true;
            CtClass found = super.createCtClass(resolvedName, useCache);
            if (found != null) {
                Resolver.getPackageNameCache().addPackageOfClassName(classname);
            }
            CtClass ctClass = found;
            return ctClass;
        }
        finally {
            this.ignoreRemapper = false;
        }
    }

    private final String resolveClassPath(String classname) {
        if (classname.startsWith("MPL_NOREMAP$")) {
            String cleanedName = classname.substring("MPL_NOREMAP$".length());
            if (!Resolver.getPackageNameCache().canExist(cleanedName)) {
                return null;
            }
            return cleanedName;
        }
        if (!Resolver.getPackageNameCache().canExist(classname)) {
            return null;
        }
        return Resolver.resolveClassPath(classname);
    }

    private static Map<ClassPool, Reference<Map<String, String>>> findInvalidNamesMap() {
        try {
            Field invalidNamesMapField = MemberResolver.class.getDeclaredField("invalidNamesMap");
            invalidNamesMapField.setAccessible(true);
            Object map = invalidNamesMapField.get(null);
            invalidNamesMapField.setAccessible(false);
            return (Map)map;
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void resetInvalidNames(ClassPool pool) {
        Class<MemberResolver> clazz = MemberResolver.class;
        synchronized (MemberResolver.class) {
            globalInvalidNamesMap.computeIfPresent(pool, (p, curr) -> {
                Map invalidNames = (Map)curr.get();
                if (invalidNames == null || invalidNames.isEmpty()) {
                    return curr;
                }
                return new WeakReference(new Hashtable());
            });
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    public final class IgnoreToken
    implements AutoCloseable {
        private final boolean ignoreResolver;

        private IgnoreToken(boolean ignoreResolver) {
            this.ignoreResolver = ignoreResolver;
        }

        @Override
        public void close() {
            ResolvedClassPool.this.ignoreRemapper = this.ignoreResolver;
        }
    }
}

