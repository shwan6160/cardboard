/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.generated.net.minecraft.network.syncher.EntityDataAccessorHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.ItemEntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;
import java.lang.reflect.Method;

public class CommonDisabledEntity {
    public static final EntityHandle INSTANCE;

    static {
        Object entity = ItemEntityHandle.T.newInstanceNull();
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            entity = new ClassInterceptor(){

                @Override
                protected Invoker<?> getCallback(Method method) {
                    Class<?> argType;
                    if (method.getParameterCount() == 1 && EntityDataAccessorHandle.T.isAssignableFrom(argType = method.getParameters()[0].getType())) {
                        return new NullInvoker();
                    }
                    return null;
                }
            }.hook(entity);
        }
        INSTANCE = EntityHandle.createHandle(entity);
    }
}

