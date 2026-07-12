/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import org.bukkit.World;

public class CBCraftServer {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.CraftServer").addImport("org.bukkit.World");
    public static final FieldAccessor<Map<String, World>> worlds = T.selectField("private final Map<String, World> worlds");
    public static final MethodAccessor<Object> getServer = ((Template.Method)CraftServerHandle.T.getServer.raw).toMethodAccessor();
    public static final MethodAccessor<Object> getPlayerList = ((Template.Method)CraftServerHandle.T.getPlayerList.raw).toMethodAccessor();
}

