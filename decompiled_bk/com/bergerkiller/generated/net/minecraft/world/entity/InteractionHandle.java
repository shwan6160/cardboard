/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.Interaction")
public abstract class InteractionHandle
extends EntityHandle {
    public static final InteractionClass T = Template.Class.create(InteractionClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Float> DATA_WIDTH = DataWatcher.Key.Type.FLOAT.createKey(InteractionHandle.T.DATA_WIDTH_ID, -1);
    public static final DataWatcher.Key<Float> DATA_HEIGHT = DataWatcher.Key.Type.FLOAT.createKey(InteractionHandle.T.DATA_HEIGHT_ID, -1);
    public static final DataWatcher.Key<Boolean> DATA_RESPONSE = DataWatcher.Key.Type.BOOLEAN.createKey(InteractionHandle.T.DATA_RESPONSE_ID, -1);

    public static InteractionHandle createHandle(Object handleInstance) {
        return (InteractionHandle)T.createHandle(handleInstance);
    }

    public static final class InteractionClass
    extends Template.Class<InteractionHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_WIDTH_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_HEIGHT_ID = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_RESPONSE_ID = new Template.StaticField.Converted();
    }
}

