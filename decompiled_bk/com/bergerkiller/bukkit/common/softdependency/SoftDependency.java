/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.softdependency;

import com.bergerkiller.bukkit.common.softdependency.SoftDetectableDependency;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public abstract class SoftDependency<T>
implements SoftDetectableDependency {
    protected final Plugin owningPlugin;
    protected final String dependencyName;
    protected final T defaultValue;
    private Plugin currentPlugin = null;
    private T current;
    private boolean detecting = false;
    private boolean enabled = true;

    public static <T> Builder<T> build(Plugin owningPlugin, String dependencyName) {
        return new Builder(owningPlugin, dependencyName);
    }

    public SoftDependency(Plugin owningPlugin, String dependencyName) {
        this(owningPlugin, dependencyName, null);
    }

    public SoftDependency(Plugin owningPlugin, String dependencyName, T defaultValue) {
        this.owningPlugin = owningPlugin;
        this.dependencyName = dependencyName;
        this.defaultValue = defaultValue;
        this.current = defaultValue;
        SoftDependency.whenEnabled(owningPlugin, this::detect);
    }

    protected boolean identify(Plugin plugin) {
        return true;
    }

    protected abstract T initialize(Plugin var1) throws Error, Exception;

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                this.detect();
            } else if (this.currentPlugin != null) {
                this.handleDisable(this.currentPlugin);
            }
        }
    }

    public static void detectAll(Object fieldContainer) {
        SoftDetectableDependency.detectAll(fieldContainer);
    }

    @Override
    public void detect() {
        Plugin plugin;
        if (!this.enabled || !this.owningPlugin.isEnabled()) {
            return;
        }
        if (!this.detecting) {
            this.detecting = true;
            Bukkit.getPluginManager().registerEvents(new Listener(){

                @EventHandler
                public void onPluginEnabled(PluginEnableEvent event) {
                    if (!SoftDependency.this.enabled) {
                        return;
                    }
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(SoftDependency.this.dependencyName);
                    if (plugin != null && event.getPlugin() == plugin && plugin.isEnabled() && SoftDependency.this.handleIdentify(plugin)) {
                        SoftDependency.this.handleEnable(plugin);
                    }
                }

                @EventHandler
                public void onPluginDisable(PluginDisableEvent event) {
                    if (!SoftDependency.this.enabled) {
                        return;
                    }
                    if (event.getPlugin() == SoftDependency.this.owningPlugin) {
                        SoftDependency.this.setEnabled(false);
                        return;
                    }
                    if (event.getPlugin() == SoftDependency.this.currentPlugin) {
                        SoftDependency.this.handleDisable(event.getPlugin());
                    }
                }
            }, this.owningPlugin);
        }
        if ((plugin = Bukkit.getPluginManager().getPlugin(this.dependencyName)) != null && plugin.isEnabled() && this.handleIdentify(plugin)) {
            this.handleEnable(plugin);
        }
    }

    public Plugin owner() {
        return this.owningPlugin;
    }

    public String name() {
        return this.dependencyName;
    }

    public T get() {
        return this.current;
    }

    public Plugin getPlugin() {
        return this.currentPlugin;
    }

    public boolean isEnabled() {
        return this.currentPlugin != null;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    private boolean handleIdentify(Plugin plugin) {
        try {
            return this.identify(plugin);
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while identifying dependency " + plugin.getName(), t);
            this.failDependencyEnable();
            return false;
        }
    }

    private void handleEnable(Plugin plugin) {
        T initialized;
        if (this.currentPlugin != null && this.currentPlugin != plugin) {
            this.handleDisable(this.currentPlugin);
        }
        try {
            initialized = this.initialize(plugin);
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while initializing use of dependency " + plugin.getName(), t);
            this.failDependencyEnable();
            return;
        }
        if (initialized == this.defaultValue) {
            return;
        }
        this.current = initialized;
        this.currentPlugin = plugin;
        try {
            this.onEnable();
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while enabling use of dependency " + plugin.getName(), t);
            this.failDependencyEnable();
            this.current = this.defaultValue;
            this.currentPlugin = null;
        }
    }

    private void handleDisable(Plugin plugin) {
        try {
            this.onDisable();
        }
        catch (Throwable t) {
            this.owningPlugin.getLogger().log(Level.SEVERE, "An error occurred while disabling use of dependency " + plugin.getName(), t);
        }
        this.current = this.defaultValue;
        this.currentPlugin = null;
    }

    private void failDependencyEnable() {
        this.owningPlugin.getLogger().log(Level.SEVERE, "Integrated support is not enabled for this plugin!");
    }

    public static void whenEnabled(Plugin plugin, Runnable callback) {
        EnableEntry e = new EnableEntry(plugin, callback);
        if (e.plugin.isEnabled()) {
            e.run();
        } else {
            AfterPluginEnableHook.INSTANCE.schedule(e);
        }
    }

    static {
        PluginEnableEvent.getHandlerList();
        PluginDisableEvent.getHandlerList();
    }

    public static class Builder<T> {
        private static final Consumer NOOP_CALLBACK = s -> {};
        private final Plugin owningPlugin;
        private final String dependencyName;
        private T defaultValue = null;
        private Initializer<T> initializer;
        private Predicate<Plugin> identify;
        private Consumer<SoftDependency<T>> whenEnable;
        private Consumer<SoftDependency<T>> whenDisable;

        private static <T> Consumer<SoftDependency<T>> noop_callback() {
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

        private Builder(Plugin owningPlugin, String dependencyName) {
            this.owningPlugin = owningPlugin;
            this.dependencyName = dependencyName;
            this.initializer = null;
            this.identify = p -> true;
            this.whenEnable = Builder.noop_callback();
            this.whenDisable = Builder.noop_callback();
        }

        public <T2> Builder<T2> withDefaultValue(T2 defaultValue) {
            return this.update(b -> {
                b.defaultValue = defaultValue;
            });
        }

        public Builder<T> withIdentify(Predicate<Plugin> identify) {
            if (identify == null) {
                throw new IllegalArgumentException("Identify predicate cannot be null");
            }
            this.identify = identify;
            return this;
        }

        public <T2> Builder<T2> withInitializer(Initializer<T2> initializer) {
            return this.update(b -> {
                b.initializer = initializer;
            });
        }

        public <T2> Builder<T2> withInitializer(InitializerOnlyPlugin<T2> initializer) {
            return this.withInitializer(initializer == null ? null : (s, p) -> initializer.initialize(p));
        }

        public Builder<T> whenEnable(Consumer<SoftDependency<T>> callback) {
            if (callback == null) {
                throw new IllegalArgumentException("Enable callback cannot be null");
            }
            this.whenEnable = Builder.chainConsumer(this.whenEnable, callback);
            return this;
        }

        public Builder<T> whenEnable(Runnable callback) {
            if (callback == null) {
                throw new IllegalArgumentException("Enable callback cannot be null");
            }
            return this.whenEnable((SoftDependency<T> s) -> callback.run());
        }

        public Builder<T> whenDisable(Consumer<SoftDependency<T>> callback) {
            if (callback == null) {
                throw new IllegalArgumentException("Disable callback cannot be null");
            }
            this.whenDisable = Builder.chainConsumer(this.whenDisable, callback);
            return this;
        }

        public Builder<T> whenDisable(Runnable callback) {
            if (callback == null) {
                throw new IllegalArgumentException("Disable callback cannot be null");
            }
            return this.whenDisable((SoftDependency<T> s) -> callback.run());
        }

        public <T2> SoftDependency<T2> create() {
            return new CallbackBasedSoftDependency<T2>(this.update(b -> {}));
        }

        private <T2> Builder<T2> update(Consumer<Builder<T2>> updator) {
            Builder newBuilder = this;
            updator.accept(newBuilder);
            return newBuilder;
        }
    }

    private static class EnableEntry {
        public final Plugin plugin;
        public final Runnable callback;

        public EnableEntry(Plugin plugin, Runnable callback) {
            this.plugin = plugin;
            this.callback = callback;
        }

        public void run() {
            try {
                this.callback.run();
            }
            catch (Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to run post-enable task", t);
            }
        }
    }

    private static class AfterPluginEnableHook
    extends HandlerList {
        public static final AfterPluginEnableHook INSTANCE = new AfterPluginEnableHook();
        private final ArrayList<EnableEntry> pending = new ArrayList();

        private AfterPluginEnableHook() {
        }

        public synchronized void schedule(EnableEntry entry) {
            if (entry.plugin.isEnabled()) {
                entry.run();
            } else {
                this.pending.add(entry);
            }
        }

        public synchronized void unregister(Plugin plugin) {
            super.unregister(plugin);
            Iterator<EnableEntry> iter = this.pending.iterator();
            while (iter.hasNext()) {
                if (iter.next().plugin != plugin) continue;
                iter.remove();
            }
        }

        public synchronized void bake() {
            super.bake();
            Iterator<EnableEntry> iter = this.pending.iterator();
            while (iter.hasNext()) {
                EnableEntry e = iter.next();
                if (!e.plugin.isEnabled()) continue;
                iter.remove();
                e.run();
            }
        }
    }

    private static class CallbackBasedSoftDependency<T>
    extends SoftDependency<T> {
        private final T defaultValue;
        private final Initializer<T> initializer;
        private final Predicate<Plugin> identify;
        private final Consumer<SoftDependency<T>> whenEnable;
        private final Consumer<SoftDependency<T>> whenDisable;

        public CallbackBasedSoftDependency(Builder<T> builder) {
            super(((Builder)builder).owningPlugin, ((Builder)builder).dependencyName);
            this.defaultValue = ((Builder)builder).defaultValue;
            this.identify = ((Builder)builder).identify;
            this.initializer = ((Builder)builder).initializer == null ? (s, p) -> this.defaultValue : ((Builder)builder).initializer;
            this.whenEnable = ((Builder)builder).whenEnable;
            this.whenDisable = ((Builder)builder).whenDisable;
        }

        @Override
        protected boolean identify(Plugin plugin) {
            return this.identify.test(plugin);
        }

        @Override
        protected T initialize(Plugin plugin) throws Error, Exception {
            return this.initializer.initialize(this, plugin);
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
    public static interface InitializerOnlyPlugin<T> {
        public T initialize(Plugin var1) throws Error, Exception;
    }

    @FunctionalInterface
    public static interface Initializer<T> {
        public T initialize(SoftDependency<T> var1, Plugin var2) throws Error, Exception;
    }
}

