/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.handling;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContextFactory;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionHandlerRegistration;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public final class ExceptionController<C> {
    private final ExceptionContextFactory<C> exceptionContextFactory = new ExceptionContextFactory(this);
    private final Map<@NonNull Type, @NonNull LinkedList<@NonNull ExceptionHandlerRegistration<C, ?>>> registrations = new HashMap();

    public static @NonNull Throwable unwrapCompletionException(@NonNull Throwable throwable) {
        if (throwable instanceof CompletionException) {
            return ExceptionController.unwrapCompletionException(throwable.getCause());
        }
        return throwable;
    }

    public <T extends Throwable> void handleException(@NonNull CommandContext<C> commandContext, @NonNull T exception) throws Throwable {
        ExceptionContext<C, T> exceptionContext = this.exceptionContextFactory.createContext(commandContext, exception);
        for (Class<?> exceptionClass = exception.getClass(); exceptionClass != Object.class; exceptionClass = exceptionClass.getSuperclass()) {
            List<ExceptionHandlerRegistration<C, ?>> registrations = this.registrations(exceptionClass);
            for (ExceptionHandlerRegistration<C, ?> registration : registrations) {
                if (!registration.exceptionFilter().test(exception)) continue;
                try {
                    registration.exceptionHandler().handle(exceptionContext);
                }
                catch (Throwable throwable) {
                    if (throwable.equals(exception)) continue;
                    this.handleException(commandContext, throwable);
                }
                return;
            }
        }
        throw exception;
    }

    public synchronized <T extends Throwable> @This @NonNull ExceptionController<C> register(@NonNull ExceptionHandlerRegistration<C, ? extends T> registration) {
        this.registrations.computeIfAbsent(registration.exceptionType().getType(), t -> new LinkedList()).addFirst(registration);
        return this;
    }

    public <T extends Throwable> @This @NonNull ExceptionController<C> register(@NonNull TypeToken<T> exceptionType, @NonNull ExceptionHandlerRegistration.BuilderDecorator<C, T> decorator) {
        return this.register(decorator.decorate(ExceptionHandlerRegistration.builder(exceptionType)).build());
    }

    public <T extends Throwable> @This @NonNull ExceptionController<C> register(@NonNull Class<T> exceptionType, @NonNull ExceptionHandlerRegistration.BuilderDecorator<C, T> decorator) {
        return this.register(decorator.decorate(ExceptionHandlerRegistration.builder(TypeToken.get(exceptionType))).build());
    }

    public <T extends Throwable> @This @NonNull ExceptionController<C> registerHandler(@NonNull TypeToken<T> exceptionType, @NonNull ExceptionHandler<C, ? extends T> exceptionHandler) {
        return this.register(ExceptionHandlerRegistration.of(exceptionType, exceptionHandler));
    }

    public <T extends Throwable> @This @NonNull ExceptionController<C> registerHandler(@NonNull Class<T> exceptionType, @NonNull ExceptionHandler<C, ? extends T> exceptionHandler) {
        return this.register(ExceptionHandlerRegistration.of(TypeToken.get(exceptionType), exceptionHandler));
    }

    public void clearHandlers() {
        this.registrations.clear();
    }

    private @NonNull List<@NonNull ExceptionHandlerRegistration<C, ?>> registrations(@NonNull Type type) {
        return Collections.unmodifiableList(this.registrations.getOrDefault(type, new LinkedList()));
    }
}

