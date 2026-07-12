/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.dep.javassist.CtField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorArgumentStore {
    private static final Map<Integer, Object> _constructionArgs = new ConcurrentHashMap<Integer, Object>();
    private static final AtomicInteger _constructionArgCtr = new AtomicInteger(0);

    public static CtField.Initializer initializeField(Object value) {
        if (value == null) {
            return CtField.Initializer.byExpr("null");
        }
        return GeneratorArgumentStore.initializeField(value, value.getClass());
    }

    public static CtField.Initializer initializeField(Object value, Class<?> cast) {
        if (value == null) {
            return CtField.Initializer.byExpr("null");
        }
        int record = GeneratorArgumentStore.store(value);
        return CtField.Initializer.byExpr("(" + MPLType.getName(cast) + ") " + GeneratorArgumentStore.class.getName() + ".fetch(" + record + ");");
    }

    public static Object fetch(int record) {
        return _constructionArgs.remove(record);
    }

    public static int store(Object value) {
        int record = _constructionArgCtr.incrementAndGet();
        _constructionArgs.put(record, value);
        return record;
    }
}

