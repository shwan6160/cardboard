/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class BasicWrapper<T extends Template.Handle> {
    protected T handle;

    protected void setHandle(T handle) {
        if (handle == null) {
            throw new IllegalArgumentException("The handle can not be null");
        }
        this.handle = handle;
    }

    public T getBackingHandle() {
        return this.handle;
    }

    public Object getRawHandle() {
        return ((Template.Handle)this.handle).getRaw();
    }

    public <H> H getRawHandle(Class<H> type) {
        return LogicUtil.tryCast(((Template.Handle)this.handle).getRaw(), type);
    }

    @Deprecated
    public Object getHandle() {
        return ((Template.Handle)this.handle).getRaw();
    }

    @Deprecated
    public <H> H getHandle(Class<H> type) {
        return LogicUtil.tryCast(((Template.Handle)this.handle).getRaw(), type);
    }

    public int hashCode() {
        return this.handle == null ? 0 : ((Template.Handle)this.handle).hashCode();
    }

    public String toString() {
        return this.handle == null ? "{NULL}" : ((Template.Handle)this.handle).toString();
    }

    public boolean equals(Object o) {
        if (o instanceof BasicWrapper) {
            o = ((BasicWrapper)o).getRawHandle();
        }
        return this.handle != null && ((Template.Handle)this.handle).getRaw().equals(o);
    }
}

