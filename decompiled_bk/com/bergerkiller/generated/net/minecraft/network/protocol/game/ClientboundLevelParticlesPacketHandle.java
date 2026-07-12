/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket")
public abstract class ClientboundLevelParticlesPacketHandle
extends PacketHandle {
    public static final ClientboundLevelParticlesPacketClass T = Template.Class.create(ClientboundLevelParticlesPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundLevelParticlesPacketHandle createHandle(Object handleInstance) {
        return (ClientboundLevelParticlesPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundLevelParticlesPacketHandle createNew() {
        return ClientboundLevelParticlesPacketHandle.T.createNew.invoke();
    }

    public abstract ParticleType<?> getParticleType();

    public abstract double getPosX();

    public abstract double getPosY();

    public abstract double getPosZ();

    public abstract void setPosX(double var1);

    public abstract void setPosY(double var1);

    public abstract void setPosZ(double var1);

    public void setParticle(ParticleType<Void> particleType) {
        this.setParticle(particleType, null);
    }

    public <T> void setParticle(ParticleType<T> particleType, T value) {
        ClientboundLevelParticlesPacketHandle.T.setParticle.invoker.invoke(this.getRaw(), particleType.getRawHandle(), value);
    }

    public void setPos(double x, double y, double z) {
        this.setPosX(x);
        this.setPosY(y);
        this.setPosZ(z);
    }

    public void setPos(Vector pos) {
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setPos(Location loc) {
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
    }

    public void setRandom(double rx, double ry, double rz) {
        this.setRandom((float)rx, (float)ry, (float)rz);
    }

    public void setRandom(float rx, float ry, float rz) {
        this.setRandomX(rx);
        this.setRandomY(ry);
        this.setRandomZ(rz);
    }

    public void setRandom(Vector random) {
        this.setRandom(random.getX(), random.getY(), random.getZ());
    }

    public abstract float getRandomX();

    public abstract void setRandomX(float var1);

    public abstract float getRandomY();

    public abstract void setRandomY(float var1);

    public abstract float getRandomZ();

    public abstract void setRandomZ(float var1);

    public abstract float getSpeed();

    public abstract void setSpeed(float var1);

    public abstract int getCount();

    public abstract void setCount(int var1);

    public abstract boolean isOverrideLimiter();

    public abstract void setOverrideLimiter(boolean var1);

    public static final class ClientboundLevelParticlesPacketClass
    extends Template.Class<ClientboundLevelParticlesPacketHandle> {
        public final Template.Field.Float randomX = new Template.Field.Float();
        public final Template.Field.Float randomY = new Template.Field.Float();
        public final Template.Field.Float randomZ = new Template.Field.Float();
        public final Template.Field.Float speed = new Template.Field.Float();
        public final Template.Field.Integer count = new Template.Field.Integer();
        public final Template.Field.Boolean overrideLimiter = new Template.Field.Boolean();
        public final Template.StaticMethod.Converted<ClientboundLevelParticlesPacketHandle> createNew = new Template.StaticMethod.Converted();
        @Template.Optional
        public final Template.Method<Void> setParticle = new Template.Method();
        public final Template.Method.Converted<ParticleType<?>> getParticleType = new Template.Method.Converted();
        public final Template.Method<Double> getPosX = new Template.Method();
        public final Template.Method<Double> getPosY = new Template.Method();
        public final Template.Method<Double> getPosZ = new Template.Method();
        public final Template.Method<Void> setPosX = new Template.Method();
        public final Template.Method<Void> setPosY = new Template.Method();
        public final Template.Method<Void> setPosZ = new Template.Method();
    }
}

