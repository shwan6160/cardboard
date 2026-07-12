/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.handling;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface ExceptionContext<C, T extends Throwable> {
    public @NonNull T exception();

    public @NonNull CommandContext<C> context();

    public @NonNull ExceptionController<C> controller();

    @API(status=API.Status.INTERNAL)
    public static final class ExceptionContextImpl<C, T extends Throwable>
    implements ExceptionContext<C, T> {
        private final T exception;
        private final CommandContext<C> context;
        private final ExceptionController<C> controller;

        ExceptionContextImpl(@NonNull T exception, @NonNull CommandContext<C> context, @NonNull ExceptionController<C> controller) {
            this.exception = exception;
            this.context = context;
            this.controller = controller;
        }

        @Override
        public @NonNull T exception() {
            return this.exception;
        }

        @Override
        public @NonNull CommandContext<C> context() {
            return this.context;
        }

        @Override
        public @NonNull ExceptionController<C> controller() {
            return this.controller;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ExceptionContextImpl that = (ExceptionContextImpl)object;
            return Objects.equals(this.exception, that.exception) && Objects.equals(this.context, that.context) && Objects.equals(this.controller, that.controller);
        }

        public int hashCode() {
            return Objects.hash(this.exception, this.context, this.controller);
        }
    }
}

