/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.softdependency;

import com.bergerkiller.bukkit.common.softdependency.SoftDependency;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public interface SoftDetectableDependency {
    public void detect();

    public static void detectAll(Object fieldContainer) {
        Field[] fields;
        for (Field field : fields = fieldContainer instanceof Class ? ((Class)fieldContainer).getDeclaredFields() : fieldContainer.getClass().getDeclaredFields()) {
            if (!SoftDependency.class.isAssignableFrom(field.getType())) continue;
            try {
                SoftDetectableDependency dep;
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    dep = (SoftDetectableDependency)field.get(null);
                } else {
                    if (fieldContainer instanceof Class) continue;
                    dep = (SoftDetectableDependency)field.get(fieldContainer);
                }
                if (dep == null) continue;
                dep.detect();
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Can't detect dependency", t);
            }
        }
    }
}

