/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.org.objenesis.strategy;

import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.ObjectInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.android.Android10Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.android.Android17Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.android.Android18Instantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.gcj.GCJInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.perc.PercInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.instantiator.sun.UnsafeFactoryInstantiator;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.BaseInstantiatorStrategy;
import com.bergerkiller.mountiplex.dep.org.objenesis.strategy.PlatformDescription;

public class StdInstantiatorStrategy
extends BaseInstantiatorStrategy {
    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (PlatformDescription.isThisJVM("Java HotSpot") || PlatformDescription.isThisJVM("OpenJDK")) {
            return new SunReflectionFactoryInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("Dalvik")) {
            if (PlatformDescription.isAndroidOpenJDK()) {
                return new UnsafeFactoryInstantiator<T>(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 10) {
                return new Android10Instantiator<T>(type);
            }
            if (PlatformDescription.ANDROID_VERSION <= 17) {
                return new Android17Instantiator<T>(type);
            }
            return new Android18Instantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("GNU libgcj")) {
            return new GCJInstantiator<T>(type);
        }
        if (PlatformDescription.isThisJVM("PERC")) {
            return new PercInstantiator<T>(type);
        }
        return new UnsafeFactoryInstantiator<T>(type);
    }
}

