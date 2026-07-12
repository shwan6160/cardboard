/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.core.Direction")
public abstract class DirectionHandle
extends Template.Handle {
    public static final DirectionClass T = Template.Class.create(DirectionClass.class, Common.TEMPLATE_RESOLVER);
    public static final DirectionHandle DOWN = DirectionHandle.T.DOWN.getSafe();
    public static final DirectionHandle UP = DirectionHandle.T.UP.getSafe();
    public static final DirectionHandle NORTH = DirectionHandle.T.NORTH.getSafe();
    public static final DirectionHandle SOUTH = DirectionHandle.T.SOUTH.getSafe();
    public static final DirectionHandle WEST = DirectionHandle.T.WEST.getSafe();
    public static final DirectionHandle EAST = DirectionHandle.T.EAST.getSafe();

    public static DirectionHandle createHandle(Object handleInstance) {
        return (DirectionHandle)T.createHandle(handleInstance);
    }

    public static final class DirectionClass
    extends Template.Class<DirectionHandle> {
        public final Template.EnumConstant.Converted<DirectionHandle> DOWN = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<DirectionHandle> UP = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<DirectionHandle> NORTH = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<DirectionHandle> SOUTH = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<DirectionHandle> WEST = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<DirectionHandle> EAST = new Template.EnumConstant.Converted();
    }

    @Template.InstanceType(value="net.minecraft.core.Direction.Axis")
    public static abstract class AxisHandle
    extends Template.Handle {
        public static final AxisClass T = Template.Class.create(AxisClass.class, Common.TEMPLATE_RESOLVER);
        public static final AxisHandle X = AxisHandle.T.X.getSafe();
        public static final AxisHandle Y = AxisHandle.T.Y.getSafe();
        public static final AxisHandle Z = AxisHandle.T.Z.getSafe();

        public static AxisHandle createHandle(Object handleInstance) {
            return (AxisHandle)T.createHandle(handleInstance);
        }

        public int ordinal() {
            return ((Enum)this.getRaw()).ordinal();
        }

        public static final class AxisClass
        extends Template.Class<AxisHandle> {
            public final Template.EnumConstant.Converted<AxisHandle> X = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<AxisHandle> Y = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<AxisHandle> Z = new Template.EnumConstant.Converted();
        }
    }
}

