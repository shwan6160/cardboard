/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

public class SafeConstructor<T> {
    private final FastConstructor<T> constructor;

    public SafeConstructor(FastConstructor<T> constructor) {
        this.constructor = constructor;
    }

    public SafeConstructor(Constructor<?> constructor) {
        this.constructor = new FastConstructor();
        this.constructor.init(constructor);
    }

    public SafeConstructor(Class<T> type, Class<?> ... parameterTypes) {
        this.constructor = new FastConstructor();
        try {
            if (type == null) {
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to find constructor because type is null");
                this.constructor.initUnavailable("NULL_UNKNOWN_TYPE()");
            } else {
                this.constructor.init(type.getDeclaredConstructor(parameterTypes));
            }
        }
        catch (SecurityException e) {
            MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to access constructor", e);
        }
        catch (NoSuchMethodException e) {
            MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to find constructor", e);
        }
    }

    public boolean isValid() {
        return this.constructor.isAvailable();
    }

    public T newInstance(Object ... parameters) {
        return this.constructor.newInstanceVA(parameters);
    }

    public <K> SafeConstructor<K> translateOutput(final Converter<?, K> converter) {
        return new SafeConstructor<K>(this, this.constructor){
            final /* synthetic */ SafeConstructor this$0;
            {
                this.this$0 = this$0;
                super(constructor);
            }

            @Override
            public K newInstance(Object ... parameters) {
                return converter.convert(super.newInstance(parameters));
            }
        };
    }

    public static <T> SafeConstructor<T> create(Class<T> type, Class<?> ... parameterTypes) {
        return new SafeConstructor<T>(type, parameterTypes);
    }
}

