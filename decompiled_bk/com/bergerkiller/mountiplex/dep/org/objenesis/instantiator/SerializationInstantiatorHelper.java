/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator;

import java.io.Serializable;

public class SerializationInstantiatorHelper {
    public static <T> Class<? super T> getNonSerializableSuperClass(Class<T> type) {
        Class<T> result = type;
        while (Serializable.class.isAssignableFrom(result)) {
            if ((result = result.getSuperclass()) != null) continue;
            throw new Error("Bad class hierarchy: No non-serializable parents");
        }
        return result;
    }
}

