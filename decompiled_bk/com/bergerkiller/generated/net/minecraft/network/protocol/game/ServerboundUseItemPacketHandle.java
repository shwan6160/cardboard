/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundUseItemPacket")
public abstract class ServerboundUseItemPacketHandle
extends PacketHandle {
    public static final ServerboundUseItemPacketClass T = Template.Class.create(ServerboundUseItemPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundUseItemPacketHandle createHandle(Object handleInstance) {
        return (ServerboundUseItemPacketHandle)T.createHandle(handleInstance);
    }

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract void setYaw(float var1);

    public abstract void setPitch(float var1);

    public abstract HumanHandRole getHandRole();

    public abstract void setHandRole(HumanHandRole var1);

    @Override
    public PacketType getPacketType() {
        return PacketType.IN_USE_ITEM;
    }

    public void setTimestamp(long timestamp) {
        if (ServerboundUseItemPacketHandle.T.timestamp.isAvailable()) {
            ServerboundUseItemPacketHandle.T.timestamp.setLong(this.getRaw(), timestamp);
        }
    }

    public HumanHand getHand(HumanEntity humanEntity) {
        return this.getHandRole().getHandOf(humanEntity);
    }

    public void setHand(HumanEntity humanEntity, HumanHand hand) {
        this.setHandRole(hand.getRoleOf(humanEntity));
    }

    public static final class ServerboundUseItemPacketClass
    extends Template.Class<ServerboundUseItemPacketHandle> {
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();
        public final Template.Method<Float> getYaw = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Void> setYaw = new Template.Method();
        public final Template.Method<Void> setPitch = new Template.Method();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted();
    }
}

