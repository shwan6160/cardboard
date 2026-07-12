/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.monster.cubemob;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.monster.cubemob.AbstractCubeMob")
public abstract class AbstractCubeMobHandle
extends MobHandle {
    public static final AbstractCubeMobClass T = Template.Class.create(AbstractCubeMobClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Integer> DATA_SIZE = DataWatcher.Key.Type.SLIME_SIZE_TYPE.createKey(AbstractCubeMobHandle.T.DATA_SIZE, 16);

    public static AbstractCubeMobHandle createHandle(Object handleInstance) {
        return (AbstractCubeMobHandle)T.createHandle(handleInstance);
    }

    public static final class AbstractCubeMobClass
    extends Template.Class<AbstractCubeMobHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_SIZE = new Template.StaticField.Converted();
    }
}

