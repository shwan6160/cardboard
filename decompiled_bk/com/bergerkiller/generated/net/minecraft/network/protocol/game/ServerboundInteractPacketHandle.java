/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundInteractPacket")
public abstract class ServerboundInteractPacketHandle
extends PacketHandle {
    public static final ServerboundInteractPacketClass T = Template.Class.create(ServerboundInteractPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundInteractPacketHandle createHandle(Object handleInstance) {
        return (ServerboundInteractPacketHandle)T.createHandle(handleInstance);
    }

    public static ServerboundInteractPacketHandle createNew(int usedEntityId, HumanHandRole handRole, boolean isUsingSecondaryAction, Vector atPosition) {
        return ServerboundInteractPacketHandle.T.createNew.invoke(usedEntityId, (Object)handRole, isUsingSecondaryAction, atPosition);
    }

    public static boolean hasSecondaryActionField() {
        return (Boolean)ServerboundInteractPacketHandle.T.hasSecondaryActionField.invoker.invoke(null);
    }

    public abstract int getUsedEntityId();

    public abstract HumanHandRole getHandRole();

    public abstract boolean hasInteractAtPosition();

    public abstract Vector getInteractAtPosition();

    public abstract boolean isUsingSecondaryAction();

    public HumanHand getHand(HumanEntity humanEntity) {
        return this.getHandRole().getHandOf(humanEntity);
    }

    public static ServerboundInteractPacketHandle createNew(int usedEntityId, HumanHandRole handRole, Vector atPosition) {
        return ServerboundInteractPacketHandle.createNew(usedEntityId, handRole, false, atPosition);
    }

    public static ServerboundInteractPacketHandle withUsingSecondaryAction(ServerboundInteractPacketHandle packet, boolean isUsingSecondaryAction) {
        return ServerboundInteractPacketHandle.createNew(packet.getUsedEntityId(), packet.getHandRole(), isUsingSecondaryAction, packet.getInteractAtPosition());
    }

    public static ServerboundInteractPacketHandle withUsedEntityId(ServerboundInteractPacketHandle packet, int usedEntityId) {
        return ServerboundInteractPacketHandle.createNew(usedEntityId, packet.getHandRole(), packet.isUsingSecondaryAction(), packet.getInteractAtPosition());
    }

    public static final class ServerboundInteractPacketClass
    extends Template.Class<ServerboundInteractPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundInteractPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Boolean> hasSecondaryActionField = new Template.StaticMethod();
        public final Template.Method<Integer> getUsedEntityId = new Template.Method();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted();
        public final Template.Method<Boolean> hasInteractAtPosition = new Template.Method();
        public final Template.Method.Converted<Vector> getInteractAtPosition = new Template.Method.Converted();
        public final Template.Method<Boolean> isUsingSecondaryAction = new Template.Method();
    }
}

