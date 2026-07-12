/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.server.dedicated;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.server.dedicated.DedicatedPlayerList")
public abstract class DedicatedPlayerListHandle
extends PlayerListHandle {
    public static final DedicatedPlayerListClass T = Template.Class.create(DedicatedPlayerListClass.class, Common.TEMPLATE_RESOLVER);

    public static DedicatedPlayerListHandle createHandle(Object handleInstance) {
        return (DedicatedPlayerListHandle)T.createHandle(handleInstance);
    }

    public static final class DedicatedPlayerListClass
    extends Template.Class<DedicatedPlayerListHandle> {
    }
}

