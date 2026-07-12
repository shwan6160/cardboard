/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob.AbstractCubeMobHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.monster.cubemob.Slime")
public abstract class SlimeHandle
extends AbstractCubeMobHandle {
    public static final SlimeClass T = Template.Class.create(SlimeClass.class, Common.TEMPLATE_RESOLVER);

    public static SlimeHandle createHandle(Object handleInstance) {
        return (SlimeHandle)T.createHandle(handleInstance);
    }

    public static final class SlimeClass
    extends Template.Class<SlimeHandle> {
    }
}

