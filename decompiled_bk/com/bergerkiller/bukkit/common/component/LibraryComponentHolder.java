/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.component;

import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryComponentHolder<E> {
    protected final E environment;
    protected final Logger logger;
    protected final String holderIdentifier;
    private final Deque<CheckedRunnable> initializers = new LinkedList<CheckedRunnable>();
    private Throwable lastError = null;

    public LibraryComponentHolder(E environment, Logger logger, String identifier) {
        this.environment = environment;
        this.logger = logger;
        this.holderIdentifier = identifier;
    }

    public E getEnvironment() {
        return this.environment;
    }

    public LibraryComponentHolder<E> runFirst(CheckedRunnable runnable) {
        this.initializers.offerLast(runnable);
        return this;
    }

    public Throwable getLastError() {
        return this.lastError;
    }

    protected boolean runInitializers() {
        CheckedRunnable runnable;
        while ((runnable = this.initializers.poll()) != null) {
            try {
                runnable.run();
            }
            catch (Throwable t) {
                this.logger.log(Level.SEVERE, "Failed to run initializer for " + this.holderIdentifier, t);
                this.lastError = t;
                this.initializers.offerFirst(runnable);
                return false;
            }
        }
        return true;
    }

    protected boolean checkIsSupported(LibraryComponent.Conditional<E, ?> conditional) {
        try {
            return conditional.isSupported(this.environment);
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Failed to detect whether component '" + conditional.getIdentifier() + "' of " + this.holderIdentifier + " is supported", t);
            this.lastError = t;
            return false;
        }
    }

    protected <L extends LibraryComponent> L tryCreateAndEnableComponent(LibraryComponent.Conditional<E, L> conditional) {
        L component;
        try {
            component = conditional.create(this.environment);
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Failed to create component '" + conditional.getIdentifier() + "' of " + this.holderIdentifier, t);
            this.lastError = t;
            return null;
        }
        try {
            component.enable();
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Failed to enable component '" + conditional.getIdentifier() + "' of " + this.holderIdentifier, t);
            this.lastError = t;
            return null;
        }
        return component;
    }

    protected void tryDisableComponent(LibraryComponent component, String identifier) {
        try {
            component.disable();
        }
        catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Failed to disable component '" + identifier + "' of " + this.holderIdentifier, t);
        }
    }
}

