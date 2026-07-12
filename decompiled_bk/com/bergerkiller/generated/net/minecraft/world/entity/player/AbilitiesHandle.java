/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.player.Abilities")
public abstract class AbilitiesHandle
extends Template.Handle {
    public static final AbilitiesClass T = Template.Class.create(AbilitiesClass.class, Common.TEMPLATE_RESOLVER);

    public static AbilitiesHandle createHandle(Object handleInstance) {
        return (AbilitiesHandle)T.createHandle(handleInstance);
    }

    public static final AbilitiesHandle createNew() {
        return AbilitiesHandle.T.constr.newInstance();
    }

    public void setFlySpeed(double flySpeed) {
        if (AbilitiesHandle.T.flySpeed_double.isAvailable()) {
            AbilitiesHandle.T.flySpeed_double.setDouble(this.getRaw(), flySpeed);
        } else {
            AbilitiesHandle.T.flySpeed_float.setFloat(this.getRaw(), (float)flySpeed);
        }
    }

    public double getFlySpeed() {
        if (AbilitiesHandle.T.flySpeed_double.isAvailable()) {
            return AbilitiesHandle.T.flySpeed_double.getDouble(this.getRaw());
        }
        return AbilitiesHandle.T.flySpeed_float.getFloat(this.getRaw());
    }

    public abstract boolean isInvulnerable();

    public abstract void setIsInvulnerable(boolean var1);

    public abstract boolean isFlying();

    public abstract void setIsFlying(boolean var1);

    public abstract boolean isCanFly();

    public abstract void setCanFly(boolean var1);

    public abstract boolean isCanInstantlyBuild();

    public abstract void setCanInstantlyBuild(boolean var1);

    public abstract boolean isMayBuild();

    public abstract void setMayBuild(boolean var1);

    public abstract float getWalkSpeed();

    public abstract void setWalkSpeed(float var1);

    public static final class AbilitiesClass
    extends Template.Class<AbilitiesHandle> {
        public final Template.Constructor.Converted<AbilitiesHandle> constr = new Template.Constructor.Converted();
        public final Template.Field.Boolean isInvulnerable = new Template.Field.Boolean();
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();
        public final Template.Field.Boolean canFly = new Template.Field.Boolean();
        public final Template.Field.Boolean canInstantlyBuild = new Template.Field.Boolean();
        public final Template.Field.Boolean mayBuild = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field.Float flySpeed_float = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Double flySpeed_double = new Template.Field.Double();
        public final Template.Field.Float walkSpeed = new Template.Field.Float();
    }
}

