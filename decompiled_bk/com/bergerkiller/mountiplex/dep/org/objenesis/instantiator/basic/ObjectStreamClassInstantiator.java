/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.annotations.Typology;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

@Instantiator(value=Typology.SERIALIZATION)
public class ObjectStreamClassInstantiator<T>
implements ObjectInstantiator<T> {
    private static Method newInstanceMethod;
    private final ObjectStreamClass objStreamClass;

    private static void initialize() {
        if (newInstanceMethod == null) {
            try {
                newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[0]);
                newInstanceMethod.setAccessible(true);
            }
            catch (NoSuchMethodException | RuntimeException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public ObjectStreamClassInstantiator(Class<T> type) {
        ObjectStreamClassInstantiator.initialize();
        this.objStreamClass = ObjectStreamClass.lookup(type);
    }

    @Override
    public T newInstance() {
        try {
            return (T)newInstanceMethod.invoke((Object)this.objStreamClass, new Object[0]);
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

