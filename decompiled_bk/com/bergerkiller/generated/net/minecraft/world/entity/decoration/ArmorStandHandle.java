/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.entity.decoration.ArmorStand")
public abstract class ArmorStandHandle
extends LivingEntityHandle {
    public static final ArmorStandClass T = Template.Class.create(ArmorStandClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_ARMORSTAND_FLAGS = DataWatcher.Key.Type.BYTE.createKey(ArmorStandHandle.T.DATA_ARMORSTAND_FLAGS, 10);
    public static final DataWatcher.Key<Vector> DATA_POSE_HEAD = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_HEAD, 11);
    public static final DataWatcher.Key<Vector> DATA_POSE_BODY = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_BODY, 12);
    public static final DataWatcher.Key<Vector> DATA_POSE_ARM_LEFT = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_ARM_LEFT, 13);
    public static final DataWatcher.Key<Vector> DATA_POSE_ARM_RIGHT = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_ARM_RIGHT, 14);
    public static final DataWatcher.Key<Vector> DATA_POSE_LEG_LEFT = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_LEG_LEFT, 15);
    public static final DataWatcher.Key<Vector> DATA_POSE_LEG_RIGHT = DataWatcher.Key.Type.ROTATION_VECTOR.createKey(ArmorStandHandle.T.DATA_POSE_LEG_RIGHT, 16);
    public static final int DATA_FLAG_IS_SMALL = 1;
    public static final int DATA_FLAG_HAS_ARMS = 4;
    public static final int DATA_FLAG_NO_BASEPLATE = 8;
    public static final int DATA_FLAG_SET_MARKER = 16;

    public static ArmorStandHandle createHandle(Object handleInstance) {
        return (ArmorStandHandle)T.createHandle(handleInstance);
    }

    public static final class ArmorStandClass
    extends Template.Class<ArmorStandHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_ARMORSTAND_FLAGS = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_HEAD = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_BODY = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_ARM_LEFT = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_ARM_RIGHT = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_LEG_LEFT = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Vector>> DATA_POSE_LEG_RIGHT = new Template.StaticField.Converted();
    }
}

