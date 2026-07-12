/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundSwingPacket")
public abstract class ServerboundSwingPacketHandle
extends PacketHandle {
    public static final ServerboundSwingPacketClass T = Template.Class.create(ServerboundSwingPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundSwingPacketHandle createHandle(Object handleInstance) {
        return (ServerboundSwingPacketHandle)T.createHandle(handleInstance);
    }

    public abstract HumanHandRole getHandRole();

    public abstract void setHandRole(HumanHandRole var1);

    public HumanHand getHand(HumanEntity humanEntity) {
        return this.getHandRole().getHandOf(humanEntity);
    }

    public void setHand(HumanEntity humanEntity, HumanHand hand) {
        this.setHandRole(hand.getRoleOf(humanEntity));
    }

    public static final class ServerboundSwingPacketClass
    extends Template.Class<ServerboundSwingPacketHandle> {
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted();
    }
}

