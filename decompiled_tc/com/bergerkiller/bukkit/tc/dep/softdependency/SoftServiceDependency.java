/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.ServiceRegisterEvent
 *  org.bukkit.event.server.ServiceUnregisterEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.bergerkiller.bukkit.tc.dep.softdependency;

import com.bergerkiller.bukkit.tc.dep.softdependency.SoftDependency;
import com.bergerkiller.bukkit.tc.dep.softdependency.SoftDetectableDependency;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public abstract class SoftServiceDependency<T>
implements SoftDetectableDependency {
    protected final Plugin owningPlugin;
    protected final String dependencyServiceClassName;
    protected final T defaultValue;
    private Object currentService = null;
    private Plugin currentServicePlugin = null;
    private T current;
    private boolean detecting = false;
    private boolean enabled = true;

    public static <T> Builder<T> build(Plugin owningPlugin, String serviceClassName) {
        return new Builder(owningPlugin, serviceClassName);
    }

    public SoftServiceDependency(Plugin owningPlugin, String serviceClassName) {
        this(owningPlugin, serviceClassName, null);
    }

    public SoftServiceDependency(Plugin owningPlugin, String serviceClassName, T defaultValue) {
        this.owningPlugin = owningPlugin;
        this.dependencyServiceClassName = serviceClassName;
        this.defaultValue = defaultValue;
        this.current = defaultValue;
        SoftDependency.whenEnabled(owningPlugin, this::detect);
    }

    protected abstract T initialize(Object var1) throws Error, Exception;

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                this.detect();
            } else if (this.currentServicePlugin != null) {
                this.handleDisable(this.currentServicePlugin);
            }
        }
    }

    @Override
    public void detect() {
        RegisteredServiceProvider provider;
        Class<?> serviceClass;
        if (!this.enabled || !this.owningPlugin.isEnabled()) {
            return;
        }
        if (!this.detecting) {
            this.detecting = true;
            Bukkit.getPluginManager().registerEvents(new Listener(){

                @EventHandler
                public void onServiceEnable(ServiceRegisterEvent event) {
                    if (!SoftServiceDependency.this.enabled) {
                        return;
                    }
                    Class serviceClass = SoftServiceDependency.this.tryGetServiceClass();
                    if (serviceClass != null && serviceClass.isAssignableFrom(event.getProvider().getService())) {
                        SoftServiceDependency.this.handleEnable(event.getProvider());
                    }
                }

                @EventHandler
                public void onPluginDisable(PluginDisableEvent event) {
                    if (SoftServiceDependency.this.enabled && event.getPlugin() == SoftServiceDependency.this.owningPlugin) {
                        SoftServiceDependency.this.setEnabled(false);
                    }
                }

                @EventHandler
                public void onServiceDisable(ServiceUnregisterEvent event) {
                    if (SoftServiceDependency.this.enabled && event.getProvider().getProvider() == SoftServiceDependency.this.currentService) {
                        SoftServiceDependency.this.handleDisable(event.getProvider().getPlugin());
                    }
                }
            }, this.owningPlugin);
        }
        if ((serviceClass = this.tryGetServiceClass()) != null && (provider = Bukkit.getServer().getServicesManager().getRegistration(serviceClass)) != null) {
            this.handleEnable(provider);
        }
    }

    private Class<?> tryGetServiceClass() {
        try {
            return Class.forName(this.dependencyServiceClassName);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public Plugin owner() {
        return this.owningPlugin;
    }

    public String name() {
        return this.dependencyServiceClassName;
    }

    public T get() {
        return this.current;
    }

    public Object getService() {
        return this.currentService;
    }

    public Plugin getServicePlugin() {
        return this.currentServicePlugin;
    }

    public boolean isEnabled() {
        return this.currentService != null;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    private void handleEnable(RegisteredServiceProvider<?> serviceProvider) {
        T initialized;
        Object service = serviceProvider.getProvider();
        Plugin servicePlugin = serviceProvider.getPlugin();
        if (this.currentService != null && this.currentService != service) {
            this.handleDisable(this.currentServicePlugin);
        }
        try {
            initialized = this.initialize(service);
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while initializing use of service dependency " + this.dependencyServiceClassName + " (" + servicePlugin.getName() + ")", t);
            return;
        }
        this.current = initialized;
        this.currentService = service;
        this.currentServicePlugin = servicePlugin;
        try {
            this.onEnable();
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while enabling use of service dependency " + this.dependencyServiceClassName + " (" + servicePlugin.getName() + ")", t);
            this.current = this.defaultValue;
            this.currentService = null;
            this.currentServicePlugin = null;
        }
    }

    private void handleDisable(Plugin servicePlugin) {
        try {
            this.onDisable();
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while disabling use of service dependency " + this.dependencyServiceClassName + " (" + servicePlugin.getName() + ")", t);
        }
        this.current = this.defaultValue;
        this.currentService = null;
        this.currentServicePlugin = null;
    }

    static {
        PluginDisableEvent.getHandlerList();
        ServiceRegisterEvent.getHandlerList();
        ServiceUnregisterEvent.getHandlerList();
    }

    public static class Builder<T> {
        private static final Consumer NOOP_CALLBACK = s -> {};
        private final Plugin owningPlugin;
        private final String serviceClassName;
        private T defaultValue = null;
        private Initializer<T> initializer;
        private Consumer<SoftServiceDependency<T>> whenEnable;
        private Consumer<SoftServiceDependency<T>> whenDisable;

        private static <T> Consumer<SoftServiceDependency<T>> noop_callback() {
            return NOOP_CALLBACK;
        }

        private static <T> Consumer<T> chainConsumer(Consumer<T> prev, Consumer<T> next) {
            if (next == null) {
                return NOOP_CALLBACK;
            }
            if (prev == NOOP_CALLBACK) {
                return next;
            }
            return input -> {
                prev.accept(input);
                next.accept(input);
            };
        }

        private Builder(Plugin owningPlugin, String serviceClassName) {
            this.owningPlugin = owningPlugin;
            this.serviceClassName = serviceClassName;
            this.initializer = null;
            this.whenEnable = Builder.noop_callback();
            this.whenDisable = Builder.noop_callback();
        }

        public <T2> Builder<T2> withDefaultValue(T2 defaultValue) {
            return this.update(b -> {
                b.defaultValue = defaultValue;
            });
        }

        public <T2> Builder<T2> withInitializer(Initializer<T2> initializer) {
            return this.update(b -> {
                b.initializer = initializer;
            });
        }

        public <T2> Builder<T2> withInitializer(InitializerOnlyService<T2> initializer) {
            return this.withInitializer(initializer == null ? null : (s, p) -> initializer.initialize(p));
        }

        public Builder<T> whenEnable(Consumer<SoftServiceDependency<T>> callback) {
            this.whenEnable = Builder.chainConsumer(this.whenEnable, callback);
            return this;
        }

        public Builder<T> whenEnable(Runnable callback) {
            return this.whenEnable((SoftServiceDependency<T> s) -> callback.run());
        }

        public Builder<T> whenDisable(Consumer<SoftServiceDependency<T>> callback) {
            this.whenDisable = Builder.chainConsumer(this.whenDisable, callback);
            return this;
        }

        public Builder<T> whenDisable(Runnable callback) {
            return this.whenDisable((SoftServiceDependency<T> s) -> callback.run());
        }

        public <T2> SoftServiceDependency<T2> create() {
            return new CallbackBasedSoftServiceDependency<T2>(this.update(b -> {}));
        }

        private <T2> Builder<T2> update(Consumer<Builder<T2>> updator) {
            Builder newBuilder = this;
            updator.accept(newBuilder);
            return newBuilder;
        }
    }

    private static class CallbackBasedSoftServiceDependency<T>
    extends SoftServiceDependency<T> {
        private final T defaultValue;
        private final Initializer<T> initializer;
        private final Consumer<SoftServiceDependency<T>> whenEnable;
        private final Consumer<SoftServiceDependency<T>> whenDisable;

        public CallbackBasedSoftServiceDependency(Builder<T> builder) {
            super(((Builder)builder).owningPlugin, ((Builder)builder).serviceClassName);
            this.defaultValue = ((Builder)builder).defaultValue;
            this.initializer = ((Builder)builder).initializer == null ? (s, p) -> this.defaultValue : ((Builder)builder).initializer;
            this.whenEnable = ((Builder)builder).whenEnable;
            this.whenDisable = ((Builder)builder).whenDisable;
        }

        @Override
        protected T initialize(Object service) throws Error, Exception {
            return this.initializer.initialize(this, service);
        }

        @Override
        protected void onEnable() {
            this.whenEnable.accept(this);
        }

        @Override
        protected void onDisable() {
            this.whenDisable.accept(this);
        }
    }

    @FunctionalInterface
    public static interface InitializerOnlyService<T> {
        public T initialize(Object var1) throws Error, Exception;
    }

    @FunctionalInterface
    public static interface Initializer<T> {
        public T initialize(SoftServiceDependency<T> var1, Object var2) throws Error, Exception;
    }
}

