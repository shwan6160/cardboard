/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.plugin.Plugin;

@Template.InstanceType(value="org.bukkit.plugin.SimplePluginManager")
public abstract class SimplePluginManagerHandle
extends Template.Handle {
    public static final SimplePluginManagerClass T = Template.Class.create(SimplePluginManagerClass.class, Common.TEMPLATE_RESOLVER);

    public static SimplePluginManagerHandle createHandle(Object handleInstance) {
        return (SimplePluginManagerHandle)T.createHandle(handleInstance);
    }

    public abstract List<Plugin> getPlugins();

    public abstract void setPlugins(List<Plugin> var1);

    public static final class SimplePluginManagerClass
    extends Template.Class<SimplePluginManagerHandle> {
        public final Template.Field<List<Plugin>> plugins = new Template.Field();
    }
}

