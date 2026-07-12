/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket")
public abstract class ClientboundSetEntityMotionPacketHandle
extends PacketHandle {
    public static final ClientboundSetEntityMotionPacketClass T = Template.Class.create(ClientboundSetEntityMotionPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetEntityMotionPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetEntityMotionPacketHandle)T.createHandle(handleInstance);
    }

    public static final ClientboundSetEntityMotionPacketHandle createNew(Entity entity) {
        return ClientboundSetEntityMotionPacketHandle.T.constr_entity.newInstance(entity);
    }

    public static ClientboundSetEntityMotionPacketHandle createNew(int entityId, double motX, double motY, double motZ) {
        return ClientboundSetEntityMotionPacketHandle.T.createNew.invoke(entityId, motX, motY, motZ);
    }

    public abstract Vector getMotVector();

    public abstract double getMotX();

    public abstract double getMotY();

    public abstract double getMotZ();

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_VELOCITY;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundSetEntityMotionPacketClass
    extends Template.Class<ClientboundSetEntityMotionPacketHandle> {
        public final Template.Constructor.Converted<ClientboundSetEntityMotionPacketHandle> constr_entity = new Template.Constructor.Converted();
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundSetEntityMotionPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<Vector> getMotVector = new Template.Method.Converted();
        public final Template.Method<Double> getMotX = new Template.Method();
        public final Template.Method<Double> getMotY = new Template.Method();
        public final Template.Method<Double> getMotZ = new Template.Method();
    }
}

