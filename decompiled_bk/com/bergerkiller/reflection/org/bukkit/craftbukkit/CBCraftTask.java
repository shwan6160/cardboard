/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.generated.org.bukkit.craftbukkit.scheduler.CraftTaskHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import org.bukkit.plugin.Plugin;

@Deprecated
public class CBCraftTask {
    public static final ClassTemplate<?> T = ClassTemplate.create(CraftTaskHandle.T.getType());
    public static final FieldAccessor<Runnable> task = CraftTaskHandle.T.task.toFieldAccessor();
    public static final FieldAccessor<Plugin> plugin = CraftTaskHandle.T.plugin.toFieldAccessor();
}

