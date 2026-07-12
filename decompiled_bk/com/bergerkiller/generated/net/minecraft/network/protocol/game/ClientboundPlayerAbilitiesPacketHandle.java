/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket")
public abstract class ClientboundPlayerAbilitiesPacketHandle
extends PacketHandle {
    public static final ClientboundPlayerAbilitiesPacketClass T = Template.Class.create(ClientboundPlayerAbilitiesPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundPlayerAbilitiesPacketHandle createHandle(Object handleInstance) {
        return (ClientboundPlayerAbilitiesPacketHandle)T.createHandle(handleInstance);
    }

    public static final ClientboundPlayerAbilitiesPacketHandle createNew(PlayerAbilities abilities) {
        return ClientboundPlayerAbilitiesPacketHandle.T.constr_abilities.newInstance(abilities);
    }

    public abstract boolean isInvulnerable();

    public abstract void setInvulnerable(boolean var1);

    public abstract boolean isFlying();

    public abstract void setIsFlying(boolean var1);

    public abstract boolean isCanFly();

    public abstract void setCanFly(boolean var1);

    public abstract boolean isInstabuild();

    public abstract void setInstabuild(boolean var1);

    public abstract float getFlyingSpeed();

    public abstract void setFlyingSpeed(float var1);

    public abstract float getWalkingSpeed();

    public abstract void setWalkingSpeed(float var1);

    public static final class ClientboundPlayerAbilitiesPacketClass
    extends Template.Class<ClientboundPlayerAbilitiesPacketHandle> {
        public final Template.Constructor.Converted<ClientboundPlayerAbilitiesPacketHandle> constr_abilities = new Template.Constructor.Converted();
        public final Template.Field.Boolean invulnerable = new Template.Field.Boolean();
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();
        public final Template.Field.Boolean canFly = new Template.Field.Boolean();
        public final Template.Field.Boolean instabuild = new Template.Field.Boolean();
        public final Template.Field.Float flyingSpeed = new Template.Field.Float();
        public final Template.Field.Float walkingSpeed = new Template.Field.Float();
    }
}

