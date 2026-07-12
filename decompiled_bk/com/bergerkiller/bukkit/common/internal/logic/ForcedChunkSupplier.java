/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.generated.net.minecraft.world.level.ForcedChunksSavedDataHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ForcedChunkSupplier
implements Supplier<Object>,
Function<String, Object> {
    public static final ForcedChunkSupplier INSTANCE = new ForcedChunkSupplier();
    private final Constructor<?> constructor;

    public ForcedChunkSupplier() {
        Constructor<?> constructor = null;
        Class<?> type = ForcedChunksSavedDataHandle.T.getType();
        if (type != null) {
            try {
                try {
                    constructor = type.getConstructor(String.class);
                }
                catch (Throwable t) {
                    constructor = type.getConstructor(new Class[0]);
                }
            }
            catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error finding ForcedChunkSupplier constructor", t);
            }
        }
        this.constructor = constructor;
    }

    @Override
    public Object get() {
        try {
            return this.constructor.newInstance(new Object[0]);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    @Override
    public Object apply(String name) {
        try {
            return this.constructor.newInstance(name);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}

