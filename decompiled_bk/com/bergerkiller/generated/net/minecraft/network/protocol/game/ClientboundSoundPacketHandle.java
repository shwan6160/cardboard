/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSoundPacket")
public abstract class ClientboundSoundPacketHandle
extends PacketHandle {
    public static final ClientboundSoundPacketClass T = Template.Class.create(ClientboundSoundPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSoundPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSoundPacketHandle)T.createHandle(handleInstance);
    }

    public float getPitch() {
        if (ClientboundSoundPacketHandle.T.pitch_1_10_2.isAvailable()) {
            return ClientboundSoundPacketHandle.T.pitch_1_10_2.getFloat(this.getRaw());
        }
        return (float)ClientboundSoundPacketHandle.T.pitch_1_8_8.getInteger(this.getRaw()) / 63.0f;
    }

    public void setPitch(float pitch) {
        if (ClientboundSoundPacketHandle.T.pitch_1_10_2.isAvailable()) {
            ClientboundSoundPacketHandle.T.pitch_1_10_2.setFloat(this.getRaw(), pitch);
        } else {
            ClientboundSoundPacketHandle.T.pitch_1_8_8.setInteger(this.getRaw(), (int)(pitch * 63.0f));
        }
    }

    public String getCategory() {
        if (ClientboundSoundPacketHandle.T.category_1_10_2.isAvailable()) {
            return ClientboundSoundPacketHandle.T.category_1_10_2.get(this.getRaw());
        }
        return "master";
    }

    public void setCategory(String categoryName) {
        if (ClientboundSoundPacketHandle.T.category_1_10_2.isAvailable()) {
            ClientboundSoundPacketHandle.T.category_1_10_2.set(this.getRaw(), categoryName);
        }
    }

    public abstract ResourceKey<SoundEffect> getSound();

    public abstract void setSound(ResourceKey<SoundEffect> var1);

    public abstract int getX();

    public abstract void setX(int var1);

    public abstract int getY();

    public abstract void setY(int var1);

    public abstract int getZ();

    public abstract void setZ(int var1);

    public abstract float getVolume();

    public abstract void setVolume(float var1);

    public static final class ClientboundSoundPacketClass
    extends Template.Class<ClientboundSoundPacketHandle> {
        public final Template.Field.Converted<ResourceKey<SoundEffect>> sound = new Template.Field.Converted();
        @Template.Optional
        public final Template.Field.Converted<String> category_1_10_2 = new Template.Field.Converted();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Float volume = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Integer pitch_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Float pitch_1_10_2 = new Template.Field.Float();
    }
}

