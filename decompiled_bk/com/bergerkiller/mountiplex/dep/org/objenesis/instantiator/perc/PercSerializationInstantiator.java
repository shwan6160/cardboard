/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.perc;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Instantiator(value=Typology.SERIALIZATION)
public class PercSerializationInstantiator<T>
implements ObjectInstantiator<T> {
    private final Object[] typeArgs;
    private final Method newInstanceMethod;

    public PercSerializationInstantiator(Class<T> type) {
        Class<T> unserializableType = type;
        while (Serializable.class.isAssignableFrom(unserializableType)) {
            unserializableType = unserializableType.getSuperclass();
        }
        try {
            Class<?> percMethodClass = Class.forName("COM.newmonics.PercClassLoader.Method");
            this.newInstanceMethod = ObjectInputStream.class.getDeclaredMethod("noArgConstruct", Class.class, Object.class, percMethodClass);
            this.newInstanceMethod.setAccessible(true);
            Class<?> percClassClass = Class.forName("COM.newmonics.PercClassLoader.PercClass");
            Method getPercClassMethod = percClassClass.getDeclaredMethod("getPercClass", Class.class);
            Object someObject = getPercClassMethod.invoke(null, unserializableType);
            Method findMethodMethod = someObject.getClass().getDeclaredMethod("findMethod", String.class);
            Object percMethod = findMethodMethod.invoke(someObject, "<init>()V");
            this.typeArgs = new Object[]{unserializableType, type, percMethod};
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return (T)this.newInstanceMethod.invoke(null, this.typeArgs);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

