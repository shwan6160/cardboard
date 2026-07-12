/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.AnnotatedMethodService;
import com.bergerkiller.bukkit.common.dep.cloud.services.annotation.ServiceImplementation;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

enum AnnotatedMethodServiceFactory {
    INSTANCE;


    @NonNull Map<? extends Service<?, ?>, TypeToken<? extends Service<?, ?>>> lookupServices(@NonNull Object instance) throws Exception {
        HashMap map = new HashMap();
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            ServiceImplementation serviceImplementation = method.getAnnotation(ServiceImplementation.class);
            if (serviceImplementation == null) continue;
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException(String.format("Method '%s' in class '%s' has wrong parameter count. Expected 1, got %d", method.getName(), instance.getClass().getCanonicalName(), method.getParameterCount()));
            }
            map.put(new AnnotatedMethodService(instance, method), TypeToken.get(serviceImplementation.value()));
        }
        return map;
    }
}

