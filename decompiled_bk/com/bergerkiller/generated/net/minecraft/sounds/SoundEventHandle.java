/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.sounds;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

@Template.InstanceType(value="net.minecraft.sounds.SoundEvent")
public abstract class SoundEventHandle
extends Template.Handle {
    public static final SoundEventClass T = Template.Class.create(SoundEventClass.class, Common.TEMPLATE_RESOLVER);

    public static SoundEventHandle createHandle(Object handleInstance) {
        return (SoundEventHandle)T.createHandle(handleInstance);
    }

    public static SoundEventHandle createVariableRangeEvent(IdentifierHandle minecraftkey) {
        return SoundEventHandle.T.createVariableRangeEvent.invoke(minecraftkey);
    }

    public static SoundEventHandle byName(String name) {
        return SoundEventHandle.T.byName.invoke(name);
    }

    public static SoundEventHandle byKey(IdentifierHandle key) {
        return SoundEventHandle.T.byKey.invoke(key);
    }

    public static Collection<IdentifierHandle> getSoundNames() {
        return SoundEventHandle.T.getSoundNames.invoke();
    }

    @Deprecated
    public static SoundEventHandle createNew(IdentifierHandle name) {
        return SoundEventHandle.createVariableRangeEvent(name);
    }

    public abstract IdentifierHandle getName();

    public abstract void setName(IdentifierHandle var1);

    public static final class SoundEventClass
    extends Template.Class<SoundEventHandle> {
        public final Template.Field.Converted<IdentifierHandle> name = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<SoundEventHandle> createVariableRangeEvent = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<SoundEventHandle> byName = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<SoundEventHandle> byKey = new Template.StaticMethod.Converted();
        @Template.Optional
        public final Template.StaticMethod<Object> rawSoundEffectResourceKeyToHolder = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<Collection<IdentifierHandle>> getSoundNames = new Template.StaticMethod.Converted();
    }
}

