/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.AnnotatedMethodServiceFactory;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePipelineBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServicePump;
import com.bergerkiller.bukkit.common.dep.cloud.services.ServiceRepository;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ServicePipeline {
    private final Object lock = new Object();
    private final Map<Type, ServiceRepository<?, ?>> repositories = new HashMap();
    private final Executor executor;

    ServicePipeline(@NonNull Executor executor) {
        this.executor = executor;
    }

    public static @NonNull ServicePipelineBuilder builder() {
        return new ServicePipelineBuilder();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <Context, Result> @NonNull ServicePipeline registerServiceType(@NonNull TypeToken<? extends Service<@NonNull Context, @NonNull Result>> type, @NonNull Service<@NonNull Context, @NonNull Result> defaultImplementation) {
        Object object = this.lock;
        synchronized (object) {
            if (this.repositories.containsKey(type.getType())) {
                throw new IllegalArgumentException(String.format("Service of type '%s' has already been registered", type.getType().getTypeName()));
            }
            ServiceRepository repository = new ServiceRepository(type);
            repository.registerImplementation(defaultImplementation, Collections.emptyList());
            this.repositories.put(type.getType(), repository);
            return this;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> @NonNull ServicePipeline registerMethods(@NonNull T instance) throws Exception {
        Object object = this.lock;
        synchronized (object) {
            Map<Service<?, ?>, TypeToken<Service<?, ?>>> services = AnnotatedMethodServiceFactory.INSTANCE.lookupServices(instance);
            for (Map.Entry<Service<?, ?>, TypeToken<Service<?, ?>>> serviceEntry : services.entrySet()) {
                TypeToken<Service<?, ?>> type = serviceEntry.getValue();
                ServiceRepository<?, ?> repository = this.repositories.get(type.getType());
                if (repository == null) {
                    throw new IllegalArgumentException(String.format("No service registered for type '%s'", type.getType().getTypeName()));
                }
                repository.registerImplementation(serviceEntry.getKey(), Collections.emptyList());
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <Context, Result> ServicePipeline registerServiceImplementation(@NonNull TypeToken<? extends Service<Context, Result>> type, @NonNull Service<Context, Result> implementation, @NonNull Collection<Predicate<Context>> filters) {
        Object object = this.lock;
        synchronized (object) {
            ServiceRepository<Context, Result> repository = this.getRepository(type);
            repository.registerImplementation(implementation, filters);
        }
        return this;
    }

    public <Context, Result> ServicePipeline registerServiceImplementation(@NonNull Class<? extends Service<Context, Result>> type, @NonNull Service<Context, Result> implementation, @NonNull Collection<Predicate<Context>> filters) {
        return this.registerServiceImplementation(TypeToken.get(type), implementation, filters);
    }

    public <Context> @NonNull ServicePump<Context> pump(@NonNull Context context) {
        return new ServicePump<Context>(this, context);
    }

    <Context, Result> @NonNull ServiceRepository<Context, Result> getRepository(@NonNull TypeToken<? extends Service<Context, Result>> type) {
        ServiceRepository<?, ?> repository = this.repositories.get(type.getType());
        if (repository == null) {
            throw new IllegalArgumentException(String.format("No service registered for type '%s'", type.getType().getTypeName()));
        }
        return repository;
    }

    public @NonNull Collection<Type> recognizedTypes() {
        return Collections.unmodifiableCollection(this.repositories.keySet());
    }

    public <Context, Result, S extends Service<Context, Result>> @NonNull Collection<TypeToken<? extends S>> getImplementations(@NonNull TypeToken<S> type) {
        ServiceRepository<Context, Result> repository = this.getRepository(type);
        LinkedList collection = new LinkedList();
        LinkedList<ServiceRepository.ServiceWrapper<Service<Context, Result>>> queue = repository.queue();
        queue.sort(null);
        Collections.reverse(queue);
        for (ServiceRepository.ServiceWrapper serviceWrapper : queue) {
            collection.add(TypeToken.get(serviceWrapper.implementation().getClass()));
        }
        return Collections.unmodifiableList(collection);
    }

    @NonNull Executor executor() {
        return this.executor;
    }
}

