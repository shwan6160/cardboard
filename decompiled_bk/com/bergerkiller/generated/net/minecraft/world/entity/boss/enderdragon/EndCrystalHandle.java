/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.boss.enderdragon;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.boss.enderdragon.EndCrystal")
public abstract class EndCrystalHandle
extends EntityHandle {
    public static final EndCrystalClass T = Template.Class.create(EndCrystalClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<IntVector3> DATA_BEAM_TARGET = DataWatcher.Key.Type.BLOCK_POSITION.createKey(EndCrystalHandle.T.DATA_BEAM_TARGET, -1);

    public static EndCrystalHandle createHandle(Object handleInstance) {
        return (EndCrystalHandle)T.createHandle(handleInstance);
    }

    public static final class EndCrystalClass
    extends Template.Class<EndCrystalHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<IntVector3>> DATA_BEAM_TARGET = new Template.StaticField.Converted();
    }
}

