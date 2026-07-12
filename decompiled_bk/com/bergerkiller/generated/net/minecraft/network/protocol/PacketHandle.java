/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.ProtocolMath;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.Packet")
public abstract class PacketHandle
extends Template.Handle {
    public static final PacketClass T = Template.Class.create(PacketClass.class, Common.TEMPLATE_RESOLVER);

    public static PacketHandle createHandle(Object handleInstance) {
        return (PacketHandle)T.createHandle(handleInstance);
    }

    public static Iterable<Object> tryUnwrapBundlePacket(Object packet) {
        return (Iterable)PacketHandle.T.tryUnwrapBundlePacket.invoker.invoke(null, packet);
    }

    public CommonPacket toCommonPacket() {
        return new CommonPacket(this.getRaw(), this.getPacketType());
    }

    public PacketType getPacketType() {
        return PacketType.getType(this.getRaw());
    }

    protected final double getProtocolPosition(Template.Field.Byte field_1_8_8, Template.Field.Integer field_1_10_2) {
        if (field_1_10_2.isAvailable()) {
            return ProtocolMath.deserializePosition_1_10_2(field_1_10_2.getInteger(this.getRaw()));
        }
        return ProtocolMath.deserializePosition_1_8_8((int)field_1_8_8.getByte(this.getRaw()));
    }

    protected final void setProtocolPosition(Template.Field.Byte field_1_8_8, Template.Field.Integer field_1_10_2, double position) {
        if (field_1_10_2.isAvailable()) {
            field_1_10_2.setInteger(this.getRaw(), ProtocolMath.serializePosition_1_10_2(position));
        } else {
            field_1_8_8.setByte(this.getRaw(), (byte)ProtocolMath.serializePosition_1_8_8(position));
        }
    }

    protected final double getProtocolPosition(Template.Field.Integer field_1_8_8, Template.Field.Double field_1_10_2) {
        if (field_1_10_2.isAvailable()) {
            return field_1_10_2.getDouble(this.getRaw());
        }
        return ProtocolMath.deserializePosition_1_8_8(field_1_8_8.getInteger(this.getRaw()));
    }

    protected final void setProtocolPosition(Template.Field.Integer field_1_8_8, Template.Field.Double field_1_10_2, double position) {
        if (field_1_10_2.isAvailable()) {
            field_1_10_2.setDouble(this.getRaw(), position);
        } else {
            field_1_8_8.setInteger(this.getRaw(), ProtocolMath.serializePosition_1_8_8(position));
        }
    }

    protected final float getProtocolRotation(Template.Field.Byte field) {
        return ProtocolMath.deserializeRotation((int)field.getByte(this.getRaw()));
    }

    protected final void setProtocolRotation(Template.Field.Byte field, float rotation) {
        field.setByte(this.getRaw(), (byte)ProtocolMath.serializeRotation(rotation));
    }

    public static final class PacketClass
    extends Template.Class<PacketHandle> {
        public final Template.StaticMethod<Iterable<Object>> tryUnwrapBundlePacket = new Template.StaticMethod();
    }
}

