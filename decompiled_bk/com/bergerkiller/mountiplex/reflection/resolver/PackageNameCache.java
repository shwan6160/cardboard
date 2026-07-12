/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.resolver;

import java.util.HashSet;
import java.util.Set;

public final class PackageNameCache {
    private final Set<String> _defaultPackages = new HashSet<String>();
    private final Set<String> _packages = new HashSet<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PackageNameCache addDefaultPackage(String name) {
        Set<String> set = this._packages;
        synchronized (set) {
            PackageNameCache.addPackageToSet(this._defaultPackages, name);
            PackageNameCache.addPackageToSet(this._packages, name);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Set<String> set = this._packages;
        synchronized (set) {
            this._packages.clear();
            this._packages.addAll(this._defaultPackages);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isPackage(String name) {
        Set<String> set = this._packages;
        synchronized (set) {
            return this._packages.contains(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPackage(String name) {
        Set<String> set = this._packages;
        synchronized (set) {
            PackageNameCache.addPackageToSet(this._packages, name);
        }
    }

    public void addPackageOfClassName(String className) {
        int nameStart = className.lastIndexOf(46);
        if (nameStart != -1) {
            this.addPackage(className.substring(0, nameStart));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean canExist(String className) {
        String asPackage = className;
        int subClassStart = className.indexOf(36);
        if (subClassStart != -1) {
            asPackage = className.substring(0, subClassStart);
        }
        Set<String> set = this._packages;
        synchronized (set) {
            return !this._packages.contains(asPackage);
        }
    }

    private static void addPackageToSet(Set<String> set, String packageName) {
        int lastDot;
        while (set.add(packageName) && (lastDot = packageName.lastIndexOf(46)) != -1) {
            packageName = packageName.substring(0, lastDot);
        }
    }
}

