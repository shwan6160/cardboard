/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.LightLayer")
public abstract class LightLayerHandle
extends Template.Handle {
    public static final LightLayerClass T = Template.Class.create(LightLayerClass.class, Common.TEMPLATE_RESOLVER);
    public static final LightLayerHandle SKY = LightLayerHandle.T.SKY.getSafe();
    public static final LightLayerHandle BLOCK = LightLayerHandle.T.BLOCK.getSafe();

    public static LightLayerHandle createHandle(Object handleInstance) {
        return (LightLayerHandle)T.createHandle(handleInstance);
    }

    public int ordinal() {
        return ((Enum)this.getRaw()).ordinal();
    }

    public static final class LightLayerClass
    extends Template.Class<LightLayerHandle> {
        public final Template.EnumConstant.Converted<LightLayerHandle> SKY = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<LightLayerHandle> BLOCK = new Template.EnumConstant.Converted();
    }
}

