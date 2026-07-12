/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.EventExecutor
 */
package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.EventExecutor;

@Template.InstanceType(value="org.bukkit.plugin.RegisteredListener")
public abstract class RegisteredListenerHandle
extends Template.Handle {
    public static final RegisteredListenerClass T = Template.Class.create(RegisteredListenerClass.class, Common.TEMPLATE_RESOLVER);

    public static RegisteredListenerHandle createHandle(Object handleInstance) {
        return (RegisteredListenerHandle)T.createHandle(handleInstance);
    }

    public abstract EventExecutor getExecutor();

    public abstract void setExecutor(EventExecutor var1);

    public static final class RegisteredListenerClass
    extends Template.Class<RegisteredListenerHandle> {
        public final Template.Field<EventExecutor> executor = new Template.Field();
    }
}

