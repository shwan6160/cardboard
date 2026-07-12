/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.PacketPlayOutBed")
public abstract class PacketPlayOutBedHandle
extends PacketHandle {
    public static final PacketPlayOutBedClass T = Template.Class.create(PacketPlayOutBedClass.class, Common.TEMPLATE_RESOLVER);

    public static PacketPlayOutBedHandle createHandle(Object handleInstance) {
        return (PacketPlayOutBedHandle)T.createHandle(handleInstance);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract IntVector3 getBedPosition();

    public abstract void setBedPosition(IntVector3 var1);

    public static final class PacketPlayOutBedClass
    extends Template.Class<PacketPlayOutBedHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> bedPosition = new Template.Field.Converted();
    }
}

