/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.SimpleCommandMap
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.dedicated.DedicatedPlayerListHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

@Template.InstanceType(value="org.bukkit.craftbukkit.CraftServer")
public abstract class CraftServerHandle
extends Template.Handle {
    public static final CraftServerClass T = Template.Class.create(CraftServerClass.class, Common.TEMPLATE_RESOLVER);
    private static CraftServerHandle _instance = null;

    public static CraftServerHandle createHandle(Object handleInstance) {
        return (CraftServerHandle)T.createHandle(handleInstance);
    }

    public abstract SimpleCommandMap getCommandMap();

    public abstract DedicatedPlayerListHandle getPlayerList();

    public abstract MinecraftServerHandle getServer();

    public abstract File getPluginsDirectory();

    public static CraftServerHandle instance() {
        if (_instance == null) {
            _instance = CraftServerHandle.createHandle(Bukkit.getServer());
        }
        return _instance;
    }

    public static final class CraftServerClass
    extends Template.Class<CraftServerHandle> {
        public final Template.Method<SimpleCommandMap> getCommandMap = new Template.Method();
        public final Template.Method.Converted<DedicatedPlayerListHandle> getPlayerList = new Template.Method.Converted();
        public final Template.Method.Converted<MinecraftServerHandle> getServer = new Template.Method.Converted();
        public final Template.Method<File> getPluginsDirectory = new Template.Method();
    }
}

