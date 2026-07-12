/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.SimplePluginManager
 */
package com.bergerkiller.reflection.org.bukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import java.util.Collection;
import java.util.List;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class BSimplePluginManager {
    public static final ClassTemplate<SimplePluginManager> T = ClassTemplate.create(SimplePluginManager.class);
    public static final FieldAccessor<Collection<Plugin>> plugins = T.getField("plugins", List.class);
}

