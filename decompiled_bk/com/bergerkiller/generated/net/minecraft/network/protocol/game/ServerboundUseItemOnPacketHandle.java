/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.HumanEntity
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundUseItemOnPacket")
public abstract class ServerboundUseItemOnPacketHandle
extends PacketHandle {
    public static final ServerboundUseItemOnPacketClass T = Template.Class.create(ServerboundUseItemOnPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundUseItemOnPacketHandle createHandle(Object handleInstance) {
        return (ServerboundUseItemOnPacketHandle)T.createHandle(handleInstance);
    }

    public abstract HumanHandRole getHandRole();

    public abstract void setHandRole(HumanHandRole var1);

    public abstract BlockFace getDirection();

    public abstract void setDirection(BlockFace var1);

    public abstract boolean isBlockPlacePacket();

    public abstract void setBlockPlacePacket();

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract float getDeltaX();

    public abstract float getDeltaY();

    public abstract float getDeltaZ();

    public abstract void setDeltaX(float var1);

    public abstract void setDeltaY(float var1);

    public abstract void setDeltaZ(float var1);

    public HumanHand getHand(HumanEntity humanEntity) {
        return this.getHandRole().getHandOf(humanEntity);
    }

    public void setHand(HumanEntity humanEntity, HumanHand hand) {
        this.setHandRole(hand.getRoleOf(humanEntity));
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.IN_USE_ITEM_ON;
    }

    public void setTimestamp(long timestamp) {
        if (ServerboundUseItemOnPacketHandle.T.timestamp.isAvailable()) {
            ServerboundUseItemOnPacketHandle.T.timestamp.setLong(this.getRaw(), timestamp);
        }
    }

    public static final class ServerboundUseItemOnPacketClass
    extends Template.Class<ServerboundUseItemOnPacketHandle> {
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted();
        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setDirection = new Template.Method.Converted();
        public final Template.Method<Boolean> isBlockPlacePacket = new Template.Method();
        public final Template.Method<Void> setBlockPlacePacket = new Template.Method();
        public final Template.Method.Converted<IntVector3> getPosition = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setPosition = new Template.Method.Converted();
        public final Template.Method<Float> getDeltaX = new Template.Method();
        public final Template.Method<Float> getDeltaY = new Template.Method();
        public final Template.Method<Float> getDeltaZ = new Template.Method();
        public final Template.Method<Void> setDeltaX = new Template.Method();
        public final Template.Method<Void> setDeltaY = new Template.Method();
        public final Template.Method<Void> setDeltaZ = new Template.Method();
    }
}

