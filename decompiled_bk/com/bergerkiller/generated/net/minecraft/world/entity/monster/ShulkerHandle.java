/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.generated.net.minecraft.world.entity.monster;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.monster.Shulker")
public abstract class ShulkerHandle
extends MobHandle {
    public static final ShulkerClass T = Template.Class.create(ShulkerClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<BlockFace> DATA_FACE_DIRECTION = DataWatcher.Key.Type.DIRECTION.createKey(ShulkerHandle.T.DATA_FACE_DIRECTION, -1);
    public static final DataWatcher.Key<Byte> DATA_PEEK = DataWatcher.Key.Type.BYTE.createKey(ShulkerHandle.T.DATA_PEEK, -1);
    public static final DataWatcher.Key<Byte> DATA_COLOR = DataWatcher.Key.Type.BYTE.createKey(ShulkerHandle.T.DATA_COLOR, -1);

    public static ShulkerHandle createHandle(Object handleInstance) {
        return (ShulkerHandle)T.createHandle(handleInstance);
    }

    public static final class ShulkerClass
    extends Template.Class<ShulkerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<BlockFace>> DATA_FACE_DIRECTION = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_PEEK = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_COLOR = new Template.StaticField.Converted();
    }
}

