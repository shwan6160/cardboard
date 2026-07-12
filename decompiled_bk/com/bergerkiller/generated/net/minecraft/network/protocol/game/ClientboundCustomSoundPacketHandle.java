/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundCustomSoundPacket")
public abstract class ClientboundCustomSoundPacketHandle
extends PacketHandle {
    public static final ClientboundCustomSoundPacketClass T = Template.Class.create(ClientboundCustomSoundPacketClass.class, Common.TEMPLATE_RESOLVER);
    public static final Random SOUND_RANDOM_SEED_SOURCE = new Random();

    public static ClientboundCustomSoundPacketHandle createHandle(Object handleInstance) {
        return (ClientboundCustomSoundPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundCustomSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category, double x, double y, double z, float volume, float pitch, long randomSeed) {
        return ClientboundCustomSoundPacketHandle.T.createNew.invokeVA(soundEffect, category, x, y, z, Float.valueOf(volume), Float.valueOf(pitch), randomSeed);
    }

    public abstract String getCategory();

    public abstract void setCategory(String var1);

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract void setX(double var1);

    public abstract void setY(double var1);

    public abstract void setZ(double var1);

    public abstract void setPitch(float var1);

    public abstract float getPitch();

    public abstract long getRandomSeed();

    public abstract void setRandomSeed(long var1);

    public static ClientboundCustomSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category, Location location, float volume, float pitch) {
        return ClientboundCustomSoundPacketHandle.createNew(soundEffect, category, location.getX(), location.getY(), location.getZ(), volume, pitch);
    }

    public static ClientboundCustomSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category, double x, double y, double z, float volume, float pitch) {
        long randomSeed = SOUND_RANDOM_SEED_SOURCE.nextLong();
        return ClientboundCustomSoundPacketHandle.createNew(soundEffect, category, x, y, z, volume, pitch, randomSeed);
    }

    @Deprecated
    public static ClientboundCustomSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category, World world, double x, double y, double z, float volume, float pitch) {
        return ClientboundCustomSoundPacketHandle.createNew(soundEffect, category, x, y, z, volume, pitch);
    }

    public abstract ResourceKey<SoundEffect> getSound();

    public abstract void setSound(ResourceKey<SoundEffect> var1);

    public abstract float getVolume();

    public abstract void setVolume(float var1);

    public static final class ClientboundCustomSoundPacketClass
    extends Template.Class<ClientboundCustomSoundPacketHandle> {
        public final Template.Field.Converted<ResourceKey<SoundEffect>> sound = new Template.Field.Converted();
        public final Template.Field.Float volume = new Template.Field.Float();
        public final Template.StaticMethod.Converted<ClientboundCustomSoundPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<String> getCategory = new Template.Method();
        public final Template.Method<Void> setCategory = new Template.Method();
        public final Template.Method<Double> getX = new Template.Method();
        public final Template.Method<Double> getY = new Template.Method();
        public final Template.Method<Double> getZ = new Template.Method();
        public final Template.Method<Void> setX = new Template.Method();
        public final Template.Method<Void> setY = new Template.Method();
        public final Template.Method<Void> setZ = new Template.Method();
        public final Template.Method<Void> setPitch = new Template.Method();
        public final Template.Method<Float> getPitch = new Template.Method();
        public final Template.Method<Long> getRandomSeed = new Template.Method();
        public final Template.Method<Void> setRandomSeed = new Template.Method();
    }
}

