/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services.type;

import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.SideEffectService;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface ConsumerService<Context>
extends SideEffectService<Context>,
Consumer<Context> {
    public static void interrupt() throws PipeBurst {
        throw new PipeBurst();
    }

    @Override
    default public @NonNull State handle(@NonNull Context context) {
        try {
            this.accept(context);
        }
        catch (PipeBurst burst) {
            return State.ACCEPTED;
        }
        return State.REJECTED;
    }

    @Override
    public void accept(@NonNull Context var1);

    public static final class PipeBurst
    extends RuntimeException {
        private PipeBurst() {
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

        @Override
        public synchronized Throwable initCause(Throwable cause) {
            return this;
        }
    }
}

