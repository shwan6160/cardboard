/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.Material;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAddPlayerPacket")
public abstract class ClientboundAddPlayerPacketHandle
extends PacketHandle {
    public static final ClientboundAddPlayerPacketClass T = Template.Class.create(ClientboundAddPlayerPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAddPlayerPacketHandle createHandle(Object handleInstance) {
        return (ClientboundAddPlayerPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundAddPlayerPacketHandle createNew() {
        return ClientboundAddPlayerPacketHandle.T.createNew.invoke();
    }

    public boolean hasDataWatcherSupport() {
        return ClientboundAddPlayerPacketHandle.T.opt_dataWatcher.isAvailable();
    }

    @Deprecated
    public DataWatcher getDataWatcher() {
        if (ClientboundAddPlayerPacketHandle.T.opt_dataWatcher.isAvailable()) {
            return ClientboundAddPlayerPacketHandle.T.opt_dataWatcher.get(this.getRaw());
        }
        return null;
    }

    @Deprecated
    public void setDataWatcher(DataWatcher dataWatcher) {
        if (ClientboundAddPlayerPacketHandle.T.opt_dataWatcher.isAvailable()) {
            ClientboundAddPlayerPacketHandle.T.opt_dataWatcher.set(this.getRaw(), dataWatcher);
        }
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_SPAWN_NAMED;
    }

    public double getPosX() {
        return this.getProtocolPosition(ClientboundAddPlayerPacketHandle.T.posX_1_8_8, ClientboundAddPlayerPacketHandle.T.posX_1_10_2);
    }

    public double getPosY() {
        return this.getProtocolPosition(ClientboundAddPlayerPacketHandle.T.posY_1_8_8, ClientboundAddPlayerPacketHandle.T.posY_1_10_2);
    }

    public double getPosZ() {
        return this.getProtocolPosition(ClientboundAddPlayerPacketHandle.T.posZ_1_8_8, ClientboundAddPlayerPacketHandle.T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        this.setProtocolPosition(ClientboundAddPlayerPacketHandle.T.posX_1_8_8, ClientboundAddPlayerPacketHandle.T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        this.setProtocolPosition(ClientboundAddPlayerPacketHandle.T.posY_1_8_8, ClientboundAddPlayerPacketHandle.T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        this.setProtocolPosition(ClientboundAddPlayerPacketHandle.T.posZ_1_8_8, ClientboundAddPlayerPacketHandle.T.posZ_1_10_2, posZ);
    }

    public float getYaw() {
        return this.getProtocolRotation(ClientboundAddPlayerPacketHandle.T.yaw_raw);
    }

    public float getPitch() {
        return this.getProtocolRotation(ClientboundAddPlayerPacketHandle.T.pitch_raw);
    }

    public void setYaw(float yaw) {
        this.setProtocolRotation(ClientboundAddPlayerPacketHandle.T.yaw_raw, yaw);
    }

    public void setPitch(float pitch) {
        this.setProtocolRotation(ClientboundAddPlayerPacketHandle.T.pitch_raw, pitch);
    }

    public void setHeldItem(Material type) {
        if (ClientboundAddPlayerPacketHandle.T.heldItem.isAvailable()) {
            ClientboundAddPlayerPacketHandle.T.heldItem.set(this.getRaw(), type);
        }
    }

    public Material getHeldItem() {
        if (ClientboundAddPlayerPacketHandle.T.heldItem.isAvailable()) {
            return ClientboundAddPlayerPacketHandle.T.heldItem.get(this.getRaw());
        }
        return Material.AIR;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract UUID getEntityUUID();

    public abstract void setEntityUUID(UUID var1);

    public static final class ClientboundAddPlayerPacketClass
    extends Template.Class<ClientboundAddPlayerPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field<UUID> entityUUID = new Template.Field();
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
        @Template.Optional
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Converted<Material> heldItem = new Template.Field.Converted();
        @Template.Optional
        public final Template.Field.Converted<DataWatcher> opt_dataWatcher = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundAddPlayerPacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

