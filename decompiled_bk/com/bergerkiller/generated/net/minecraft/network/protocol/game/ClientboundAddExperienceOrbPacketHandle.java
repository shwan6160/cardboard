/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket")
public abstract class ClientboundAddExperienceOrbPacketHandle
extends PacketHandle {
    public static final ClientboundAddExperienceOrbPacketClass T = Template.Class.create(ClientboundAddExperienceOrbPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAddExperienceOrbPacketHandle createHandle(Object handleInstance) {
        return (ClientboundAddExperienceOrbPacketHandle)T.createHandle(handleInstance);
    }

    public double getPosX() {
        return this.getProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posX_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posX_1_10_2);
    }

    public double getPosY() {
        return this.getProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posY_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posY_1_10_2);
    }

    public double getPosZ() {
        return this.getProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posZ_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        this.setProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posX_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        this.setProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posY_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        this.setProtocolPosition(ClientboundAddExperienceOrbPacketHandle.T.posZ_1_8_8, ClientboundAddExperienceOrbPacketHandle.T.posZ_1_10_2, posZ);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int getExperience();

    public abstract void setExperience(int var1);

    public static final class ClientboundAddExperienceOrbPacketClass
    extends Template.Class<ClientboundAddExperienceOrbPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posX_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posY_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posZ_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Double posX_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posY_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posZ_1_10_2 = new Template.Field.Double();
        public final Template.Field.Integer experience = new Template.Field.Integer();
    }
}

