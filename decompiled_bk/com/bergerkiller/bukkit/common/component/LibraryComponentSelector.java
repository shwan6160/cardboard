/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.component;

import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentHolder;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class LibraryComponentSelector<E, L extends LibraryComponent>
extends LibraryComponentHolder<E>
implements LibraryComponent {
    private final List<LibraryComponent.Conditional<E, ? extends L>> registered = new ArrayList<LibraryComponent.Conditional<E, ? extends L>>();
    private CheckedSupplier<? extends L> defaultComponentSupplier = () -> null;
    private int currentRegisteredComponent = -1;
    private L currentComponent = null;
    private boolean enabled = true;

    public LibraryComponentSelector(E environment, Logger logger, String identifier) {
        super(environment, logger, identifier);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public LibraryComponentSelector<E, L> runFirst(CheckedRunnable runnable) {
        super.runFirst(runnable);
        return this;
    }

    public LibraryComponentSelector<E, L> setDefaultComponent(CheckedFunction<LibraryComponentSelector<E, L>, ? extends L> defaultLibraryComponentFunc) {
        return this.setDefaultComponent(() -> (LibraryComponent)defaultLibraryComponentFunc.apply(this));
    }

    public LibraryComponentSelector<E, L> setDefaultComponent(L defaultLibraryComponent) {
        return this.setDefaultComponent(() -> defaultLibraryComponent);
    }

    public LibraryComponentSelector<E, L> setDefaultComponent(CheckedSupplier<? extends L> defaultLibraryComponentSupplier) {
        this.defaultComponentSupplier = defaultLibraryComponentSupplier;
        return this;
    }

    public LibraryComponentSelector<E, L> addOption(LibraryComponent.Conditional<E, ? extends L> option) {
        this.registered.add(option);
        return this;
    }

    public LibraryComponentSelector<E, L> addWhen(String identifier, Predicate<E> isSupported, CheckedSupplier<L> componentSupplier) {
        return this.addOption(LibraryComponent.when(identifier, isSupported, componentSupplier));
    }

    public LibraryComponentSelector<E, L> addWhen(String identifier, Predicate<E> isSupported, CheckedFunction<E, L> componentFunction) {
        return this.addOption(LibraryComponent.when(identifier, isSupported, componentFunction));
    }

    public LibraryComponentSelector<E, L> addVersionOption(String minimumMinecraftVersion, String maximumMinecraftVersion, CheckedSupplier<L> componentSupplier) {
        return this.addOption(LibraryComponent.forVersions("", minimumMinecraftVersion, maximumMinecraftVersion, componentSupplier));
    }

    public LibraryComponentSelector<E, L> addVersionOption(String minimumMinecraftVersion, String maximumMinecraftVersion, CheckedFunction<E, L> componentFunction) {
        return this.addOption(LibraryComponent.forVersions("", minimumMinecraftVersion, maximumMinecraftVersion, componentFunction));
    }

    public L get() {
        return this.currentComponent;
    }

    public synchronized L update() {
        this.enabled = true;
        if (this.runInitializers()) {
            for (int i = 0; i < this.registered.size(); ++i) {
                LibraryComponent.Conditional<E, ? extends L> conditional = this.registered.get(i);
                if (!this.checkIsSupported(conditional)) continue;
                if (i == this.currentRegisteredComponent) {
                    return this.currentComponent;
                }
                this.disableCurrentComponent();
                L component = this.tryCreateAndEnableComponent(conditional);
                if (component == null) continue;
                this.currentRegisteredComponent = i;
                this.currentComponent = component;
                return component;
            }
        } else {
            this.disableCurrentComponent();
        }
        if (this.currentComponent == null) {
            this.enableDefaultComponent();
        }
        return this.currentComponent;
    }

    @Override
    public void enable() {
        this.update();
    }

    @Override
    public synchronized void disable() {
        this.disableCurrentComponent();
        this.enabled = false;
        this.enableDefaultComponent();
    }

    private void enableDefaultComponent() {
        LibraryComponent component;
        try {
            component = (LibraryComponent)this.defaultComponentSupplier.get();
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Failed to instantiate the default component", t);
            return;
        }
        if (component != null) {
            try {
                component.enable();
            }
            catch (Throwable t) {
                this.logger.log(Level.SEVERE, "Failed to enable the default component", t);
                return;
            }
        }
        this.currentComponent = component;
    }

    private void disableCurrentComponent() {
        L component = this.currentComponent;
        int index = this.currentRegisteredComponent;
        this.currentComponent = null;
        this.currentRegisteredComponent = -1;
        if (component == null) {
            return;
        }
        String identifier = this.currentRegisteredComponent == -1 ? "DEFAULT" : this.registered.get(index).getIdentifier();
        this.tryDisableComponent((LibraryComponent)component, identifier);
    }

    public static <P extends Plugin, L extends LibraryComponent> LibraryComponentSelector<P, L> forPlugin(P plugin) {
        return new LibraryComponentSelector<P, L>(plugin, plugin.getLogger(), plugin.getName());
    }

    public static <L extends LibraryComponent> LibraryComponentSelector<Void, L> forModule(Class<L> owningClass) {
        Plugin plugin = CommonUtil.getPluginByClass(owningClass);
        String identifier = owningClass.getSimpleName();
        if (plugin != null) {
            identifier = plugin.getName() + "." + identifier;
            return new LibraryComponentSelector<Object, L>(null, new ModuleLogger(plugin, owningClass.getSimpleName()), identifier);
        }
        return new LibraryComponentSelector<Object, L>(null, new ModuleLogger(owningClass.getSimpleName()), identifier);
    }
}

