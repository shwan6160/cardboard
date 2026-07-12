/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityWeather")
public abstract class PacketPlayOutSpawnEntityWeatherHandle
extends PacketHandle {
    public static final PacketPlayOutSpawnEntityWeatherClass T = Template.Class.create(PacketPlayOutSpawnEntityWeatherClass.class, Common.TEMPLATE_RESOLVER);

    public static PacketPlayOutSpawnEntityWeatherHandle createHandle(Object handleInstance) {
        return (PacketPlayOutSpawnEntityWeatherHandle)T.createHandle(handleInstance);
    }

    public double getPosX() {
        return this.getProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posX_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posX_1_10_2);
    }

    public double getPosY() {
        return this.getProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posY_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posY_1_10_2);
    }

    public double getPosZ() {
        return this.getProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posZ_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        this.setProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posX_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        this.setProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posY_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        this.setProtocolPosition(PacketPlayOutSpawnEntityWeatherHandle.T.posZ_1_8_8, PacketPlayOutSpawnEntityWeatherHandle.T.posZ_1_10_2, posZ);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int getType();

    public abstract void setType(int var1);

    public static final class PacketPlayOutSpawnEntityWeatherClass
    extends Template.Class<PacketPlayOutSpawnEntityWeatherHandle> {
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
        public final Template.Field.Integer type = new Template.Field.Integer();
    }
}

