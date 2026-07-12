/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeMapHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DummyAttributeMapBase {
    public static final Object INSTANCE;

    static {
        ClassInterceptor interceptor = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    return new NullInvoker(method.getReturnType());
                }
                return null;
            }
        };
        INSTANCE = interceptor.createInstance(AttributeMapHandle.T.getType());
    }
}

