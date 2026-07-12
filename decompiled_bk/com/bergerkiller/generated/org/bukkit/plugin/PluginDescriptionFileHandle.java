/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

@Template.InstanceType(value="org.bukkit.plugin.PluginDescriptionFile")
public abstract class PluginDescriptionFileHandle
extends Template.Handle {
    public static final PluginDescriptionFileClass T = Template.Class.create(PluginDescriptionFileClass.class, Common.TEMPLATE_RESOLVER);

    public static PluginDescriptionFileHandle createHandle(Object handleInstance) {
        return (PluginDescriptionFileHandle)T.createHandle(handleInstance);
    }

    public abstract Map<String, Map<String, Object>> getCommands();

    public abstract void setCommands(Map<String, Map<String, Object>> var1);

    public static final class PluginDescriptionFileClass
    extends Template.Class<PluginDescriptionFileHandle> {
        public final Template.Field<Map<String, Map<String, Object>>> commands = new Template.Field();
    }
}

