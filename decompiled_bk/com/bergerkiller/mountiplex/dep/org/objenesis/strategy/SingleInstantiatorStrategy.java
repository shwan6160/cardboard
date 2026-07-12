/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.strategy;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.InstantiatorStrategy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingleInstantiatorStrategy
implements InstantiatorStrategy {
    private final Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
        try {
            this.constructor = instantiator.getConstructor(Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator)this.constructor.newInstance(type);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

