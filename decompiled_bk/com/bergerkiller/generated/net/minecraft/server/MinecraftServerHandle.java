/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.server.MinecraftServer")
public abstract class MinecraftServerHandle
extends Template.Handle {
    public static final MinecraftServerClass T = Template.Class.create(MinecraftServerClass.class, Common.TEMPLATE_RESOLVER);
    private static MinecraftServerHandle _cached_instance = null;

    public static MinecraftServerHandle createHandle(Object handleInstance) {
        return (MinecraftServerHandle)T.createHandle(handleInstance);
    }

    public abstract String getResourcePack();

    public abstract String getResourcePackHash();

    public abstract String getProperty(String var1, String var2);

    public abstract String getLevelName();

    public abstract int getTicksSinceUnixEpoch();

    public abstract int getTicks();

    public abstract boolean isMainThread();

    public static MinecraftServerHandle instance() {
        if (_cached_instance == null) {
            _cached_instance = CraftServerHandle.instance().getServer();
        }
        return _cached_instance;
    }

    public abstract boolean isHasStopped();

    public abstract void setHasStopped(boolean var1);

    public static final class MinecraftServerClass
    extends Template.Class<MinecraftServerHandle> {
        public final Template.Field.Boolean hasStopped = new Template.Field.Boolean();
        public final Template.Method<String> getResourcePack = new Template.Method();
        public final Template.Method<String> getResourcePackHash = new Template.Method();
        public final Template.Method<String> getProperty = new Template.Method();
        public final Template.Method<String> getLevelName = new Template.Method();
        public final Template.Method<Integer> getTicksSinceUnixEpoch = new Template.Method();
        public final Template.Method<Integer> getTicks = new Template.Method();
        public final Template.Method<Boolean> isMainThread = new Template.Method();
    }
}

