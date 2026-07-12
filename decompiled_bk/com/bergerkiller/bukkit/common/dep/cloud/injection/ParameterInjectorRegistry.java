/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InjectionException;
import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionRequest;
import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionService;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjector;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipeline;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public final class ParameterInjectorRegistry<C>
implements InjectionService<C> {
    private final List<Pair<Predicate<TypeToken<?>>, ParameterInjector<C, ?>>> injectors = new ArrayList();
    private final ServicePipeline servicePipeline = ServicePipeline.builder().build();

    public ParameterInjectorRegistry() {
        this.servicePipeline.registerServiceType(new TypeToken<InjectionService<C>>(){}, this);
    }

    public synchronized <T> @This @NonNull ParameterInjectorRegistry<C> registerInjector(@NonNull Class<T> clazz, @NonNull ParameterInjector<C, T> injector) {
        return this.registerInjector(TypeToken.get(clazz), injector);
    }

    @API(status=API.Status.STABLE)
    public synchronized <T> @This @NonNull ParameterInjectorRegistry<C> registerInjector(@NonNull TypeToken<T> type, @NonNull ParameterInjector<C, T> injector) {
        return this.registerInjector((TypeToken<?> cl) -> GenericTypeReflector.isSuperType(cl.getType(), type.getType()), injector);
    }

    @API(status=API.Status.STABLE)
    public synchronized <T> @This @NonNull ParameterInjectorRegistry<C> registerInjector(@NonNull Predicate<TypeToken<?>> predicate, @NonNull ParameterInjector<C, T> injector) {
        this.injectors.add(Pair.of(predicate, injector));
        return this;
    }

    @Override
    public @Nullable Object handle(@NonNull InjectionRequest<C> request) {
        for (ParameterInjector<C, ?> injector : this.injectors(request.injectedType())) {
            Object value = injector.create(request.commandContext(), request.annotationAccessor());
            if (value == null) continue;
            return value;
        }
        return null;
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> getInjectable(@NonNull Class<T> clazz, @NonNull CommandContext<C> context, @NonNull AnnotationAccessor annotationAccessor) {
        return this.getInjectable(TypeToken.get(clazz), context, annotationAccessor);
    }

    @API(status=API.Status.STABLE)
    public <T> @NonNull Optional<T> getInjectable(@NonNull TypeToken<T> type, @NonNull CommandContext<C> context, @NonNull AnnotationAccessor annotationAccessor) {
        InjectionRequest<C> request = InjectionRequest.of(context, type, annotationAccessor);
        try {
            Object rawResult = this.servicePipeline.pump(request).through(new TypeToken<InjectionService<C>>(){}).complete();
            if (!request.injectedClass().isInstance(rawResult)) {
                throw new IllegalStateException(String.format("Injector returned type %s which is not an instance of %s", rawResult.getClass().getName(), request.injectedClass().getName()));
            }
            Object result = rawResult;
            return Optional.of(result);
        }
        catch (IllegalStateException ignored) {
            return Optional.empty();
        }
        catch (InjectionException injectionException) {
            throw injectionException;
        }
        catch (Exception e) {
            throw new InjectionException(String.format("Failed to inject type %s", type.getType().getTypeName()), e);
        }
    }

    @API(status=API.Status.STABLE)
    public @This @NonNull ParameterInjectorRegistry<C> registerInjectionService(InjectionService<C> service) {
        this.servicePipeline.registerServiceImplementation(new TypeToken<InjectionService<C>>(){}, service, Collections.emptyList());
        return this;
    }

    private synchronized <T> @NonNull Collection<@NonNull ParameterInjector<C, ?>> injectors(@NonNull TypeToken<T> type) {
        return Collections.unmodifiableCollection(this.injectors.stream().filter(pair -> ((Predicate)pair.first()).test(type)).map(Pair::second).collect(Collectors.toList()));
    }
}

