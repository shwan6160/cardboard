/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.scheduler;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.Plugin;

@Template.InstanceType(value="org.bukkit.craftbukkit.scheduler.CraftTask")
public abstract class CraftTaskHandle
extends Template.Handle {
    public static final CraftTaskClass T = Template.Class.create(CraftTaskClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftTaskHandle createHandle(Object handleInstance) {
        return (CraftTaskHandle)T.createHandle(handleInstance);
    }

    public abstract Runnable getTask();

    public abstract void setTask(Runnable var1);

    public abstract Plugin getPlugin();

    public abstract void setPlugin(Plugin var1);

    public static final class CraftTaskClass
    extends Template.Class<CraftTaskHandle> {
        public final Template.Field<Runnable> task = new Template.Field();
        public final Template.Field<Plugin> plugin = new Template.Field();
    }
}

