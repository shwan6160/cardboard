/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.sounds.SoundSource")
public abstract class SoundSourceHandle
extends Template.Handle {
    public static final SoundSourceClass T = Template.Class.create(SoundSourceClass.class, Common.TEMPLATE_RESOLVER);

    public static SoundSourceHandle createHandle(Object handleInstance) {
        return (SoundSourceHandle)T.createHandle(handleInstance);
    }

    public static SoundSourceHandle byName(String name) {
        return SoundSourceHandle.T.byName.invoke(name);
    }

    public abstract String getName();

    public static final class SoundSourceClass
    extends Template.Class<SoundSourceHandle> {
        public final Template.StaticMethod.Converted<SoundSourceHandle> byName = new Template.StaticMethod.Converted();
        public final Template.Method<String> getName = new Template.Method();
    }
}

