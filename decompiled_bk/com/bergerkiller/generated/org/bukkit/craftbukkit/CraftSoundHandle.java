/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Sound
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Sound;

@Template.InstanceType(value="org.bukkit.craftbukkit.CraftSound")
public abstract class CraftSoundHandle
extends Template.Handle {
    public static final CraftSoundClass T = Template.Class.create(CraftSoundClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftSoundHandle createHandle(Object handleInstance) {
        return (CraftSoundHandle)T.createHandle(handleInstance);
    }

    public static ResourceKey<SoundEffect> getSoundEffect(Sound sound) {
        return CraftSoundHandle.T.getSoundEffect.invoke(sound);
    }

    public static final class CraftSoundClass
    extends Template.Class<CraftSoundHandle> {
        public final Template.StaticMethod.Converted<ResourceKey<SoundEffect>> getSoundEffect = new Template.StaticMethod.Converted();
    }
}

