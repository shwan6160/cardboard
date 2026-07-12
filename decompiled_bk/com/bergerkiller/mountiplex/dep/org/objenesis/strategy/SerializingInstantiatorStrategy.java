/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.strategy;

import com.bergerkiller.mountiplex.dep.org.objenesis.ObjenesisException;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.android.AndroidSerializationInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.perc.PercSerializationInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.sun.SunReflectionFactorySerializationInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.BaseInstantiatorStrategy;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.PlatformDescription;
import java.io.NotSerializableException;
import java.io.Serializable;

public class SerializingInstantiatorStrategy
extends BaseInstantiatorStrategy {
    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (!Serializable.class.isAssignableFrom(type)) {
            throw new ObjenesisException(new NotSerializableException(type + " not serializable"));
        }
        if (PlatformDescription.JVM_NAME.startsWith("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new SunReflectionFactorySerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("Dalvik")) {
            if (PlatformDescription.isAndroidOpenJDK()) {
                return new ObjectStreamClassInstantiator<T>(type);
            }
            return new AndroidSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("GNU libgcj")) {
            return new GCJSerializationInstantiator<T>(type);
        }
        if (PlatformDescription.JVM_NAME.startsWith("PERC")) {
            return new PercSerializationInstantiator<T>(type);
        }
        return new SunReflectionFactorySerializationInstantiator<T>(type);
    }
}

