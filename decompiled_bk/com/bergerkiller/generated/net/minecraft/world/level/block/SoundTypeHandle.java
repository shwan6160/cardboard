/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.SoundType")
public abstract class SoundTypeHandle
extends Template.Handle {
    public static final SoundTypeClass T = Template.Class.create(SoundTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static SoundTypeHandle createHandle(Object handleInstance) {
        return (SoundTypeHandle)T.createHandle(handleInstance);
    }

    public abstract SoundEventHandle getStepSound();

    public abstract SoundEventHandle getPlaceSound();

    public abstract SoundEventHandle getBreakSound();

    public abstract SoundEventHandle getFallSound();

    public static final class SoundTypeClass
    extends Template.Class<SoundTypeHandle> {
        public final Template.Method.Converted<SoundEventHandle> getStepSound = new Template.Method.Converted();
        public final Template.Method.Converted<SoundEventHandle> getPlaceSound = new Template.Method.Converted();
        public final Template.Method.Converted<SoundEventHandle> getBreakSound = new Template.Method.Converted();
        public final Template.Method.Converted<SoundEventHandle> getFallSound = new Template.Method.Converted();
    }
}

