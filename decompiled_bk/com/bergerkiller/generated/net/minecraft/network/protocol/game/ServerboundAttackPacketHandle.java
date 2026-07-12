/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundAttackPacket")
public abstract class ServerboundAttackPacketHandle
extends PacketHandle {
    public static final ServerboundAttackPacketClass T = Template.Class.create(ServerboundAttackPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundAttackPacketHandle createHandle(Object handleInstance) {
        return (ServerboundAttackPacketHandle)T.createHandle(handleInstance);
    }

    public static ServerboundAttackPacketHandle createNew(int attackedEntityId) {
        return ServerboundAttackPacketHandle.T.createNew.invoke(attackedEntityId);
    }

    public static boolean isAttackInteractionPacket(Object interactPacket) {
        return ServerboundAttackPacketHandle.T.isAttackInteractionPacket.invoke(interactPacket);
    }

    public abstract int getEntityId();

    public static final class ServerboundAttackPacketClass
    extends Template.Class<ServerboundAttackPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundAttackPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Boolean> isAttackInteractionPacket = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getEntityId = new Template.Method();
    }
}

