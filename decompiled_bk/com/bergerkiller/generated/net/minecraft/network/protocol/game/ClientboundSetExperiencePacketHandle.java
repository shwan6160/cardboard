/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetExperiencePacket")
public abstract class ClientboundSetExperiencePacketHandle
extends PacketHandle {
    public static final ClientboundSetExperiencePacketClass T = Template.Class.create(ClientboundSetExperiencePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetExperiencePacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetExperiencePacketHandle)T.createHandle(handleInstance);
    }

    public abstract float getExperienceProgress();

    public abstract void setExperienceProgress(float var1);

    public abstract int getTotalExperience();

    public abstract void setTotalExperience(int var1);

    public abstract int getExperienceLevel();

    public abstract void setExperienceLevel(int var1);

    public static final class ClientboundSetExperiencePacketClass
    extends Template.Class<ClientboundSetExperiencePacketHandle> {
        public final Template.Field.Float experienceProgress = new Template.Field.Float();
        public final Template.Field.Integer totalExperience = new Template.Field.Integer();
        public final Template.Field.Integer experienceLevel = new Template.Field.Integer();
    }
}

