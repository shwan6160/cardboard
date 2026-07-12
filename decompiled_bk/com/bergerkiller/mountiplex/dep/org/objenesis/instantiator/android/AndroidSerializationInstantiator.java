/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.android;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Instantiator(value=Typology.SERIALIZATION)
public class AndroidSerializationInstantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;
    private final ObjectStreamClass objectStreamClass;
    private final Method newInstanceMethod;

    public AndroidSerializationInstantiator(Class<T> type) {
        Method m;
        this.type = type;
        this.newInstanceMethod = AndroidSerializationInstantiator.getNewInstanceMethod();
        try {
            m = ObjectStreamClass.class.getMethod("lookupAny", Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
        try {
            this.objectStreamClass = (ObjectStreamClass)m.invoke(null, type);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(this.newInstanceMethod.invoke((Object)this.objectStreamClass, this.type));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    private static Method getNewInstanceMethod() {
        try {
            Method newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class);
            newInstanceMethod.setAccessible(true);
            return newInstanceMethod;
        }
        catch (NoSuchMethodException | RuntimeException e) {
            throw new ObjenesisException(e);
        }
    }
}

