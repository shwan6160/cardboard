/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.component;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentHolder;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class LibraryComponentList<E>
extends LibraryComponentHolder<E> {
    private final Deque<EnabledComponent> enabledComponents = new LinkedList<EnabledComponent>();

    public LibraryComponentList(E environment, Logger logger, String identifier) {
        super(environment, logger, identifier);
    }

    @Override
    public LibraryComponentList<E> runFirst(CheckedRunnable runnable) {
        super.runFirst(runnable);
        return this;
    }

    public <L extends LibraryComponent> L enableForVersions(String identifier, String minimumMinecraftVersion, String maximumMinecraftVersion, CheckedSupplier<L> componentSupplier) {
        return this.enable(LibraryComponent.forVersions(identifier, minimumMinecraftVersion, maximumMinecraftVersion, componentSupplier));
    }

    public <L extends LibraryComponent> L enableForVersions(String identifier, String minimumMinecraftVersion, String maximumMinecraftVersion, CheckedFunction<E, L> componentFunction) {
        return this.enable(LibraryComponent.forVersions(identifier, minimumMinecraftVersion, maximumMinecraftVersion, componentFunction));
    }

    public <L extends LibraryComponent> L enable(final String identifier, final L component) {
        return this.enable(new LibraryComponent.Conditional<E, L>(){
            final /* synthetic */ LibraryComponentList this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public String getIdentifier() {
                return identifier;
            }

            @Override
            public boolean isSupported(E environment) {
                return true;
            }

            @Override
            public L create(E environment) throws Throwable {
                return component;
            }
        });
    }

    public <L extends LibraryComponent> L enable(L component) {
        return this.enable(component.getClass().getSimpleName(), component);
    }

    public <L extends LibraryComponent> L enableCreate(final Function<E, L> creator) {
        return this.enable(new LibraryComponent.Conditional<E, L>(){
            final /* synthetic */ LibraryComponentList this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public String getIdentifier() {
                return creator.getClass().getSimpleName();
            }

            @Override
            public boolean isSupported(E environment) {
                return true;
            }

            @Override
            public L create(E environment) throws Throwable {
                return (LibraryComponent)creator.apply(environment);
            }
        });
    }

    public <L extends LibraryComponent> L enable(LibraryComponent.Conditional<E, L> conditional) {
        if (!this.runInitializers()) {
            return null;
        }
        if (!conditional.isSupported(this.environment)) {
            return null;
        }
        L component = this.tryCreateAndEnableComponent(conditional);
        if (component != null) {
            this.enabledComponents.offer(new EnabledComponent((LibraryComponent)component, conditional.getIdentifier()));
        }
        return component;
    }

    public void disable() {
        EnabledComponent enabledComponent;
        while ((enabledComponent = this.enabledComponents.pollLast()) != null) {
            this.tryDisableComponent(enabledComponent.component, enabledComponent.identifier);
        }
    }

    public static <P extends Plugin> LibraryComponentList<P> forPlugin(P plugin) {
        return new LibraryComponentList<P>(plugin, plugin.getLogger(), plugin.getName());
    }

    private static class EnabledComponent {
        public final LibraryComponent component;
        public final String identifier;

        public EnabledComponent(LibraryComponent component, String identifier) {
            this.component = component;
            this.identifier = identifier;
        }
    }
}

