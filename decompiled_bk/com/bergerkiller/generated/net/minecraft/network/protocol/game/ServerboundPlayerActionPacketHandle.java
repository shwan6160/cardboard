/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPlayerActionPacket")
public abstract class ServerboundPlayerActionPacketHandle
extends PacketHandle {
    public static final ServerboundPlayerActionPacketClass T = Template.Class.create(ServerboundPlayerActionPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundPlayerActionPacketHandle createHandle(Object handleInstance) {
        return (ServerboundPlayerActionPacketHandle)T.createHandle(handleInstance);
    }

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract BlockFace getDirection();

    public abstract void setDirection(BlockFace var1);

    public abstract ActionHandle getDigType();

    public abstract void setDigType(ActionHandle var1);

    public static final class ServerboundPlayerActionPacketClass
    extends Template.Class<ServerboundPlayerActionPacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Converted<BlockFace> direction = new Template.Field.Converted();
        public final Template.Field.Converted<ActionHandle> digType = new Template.Field.Converted();
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action")
    public static abstract class ActionHandle
    extends Template.Handle {
        public static final ActionClass T = Template.Class.create(ActionClass.class, Common.TEMPLATE_RESOLVER);
        public static final ActionHandle START_DESTROY_BLOCK = ActionHandle.T.START_DESTROY_BLOCK.getSafe();
        public static final ActionHandle ABORT_DESTROY_BLOCK = ActionHandle.T.ABORT_DESTROY_BLOCK.getSafe();
        public static final ActionHandle STOP_DESTROY_BLOCK = ActionHandle.T.STOP_DESTROY_BLOCK.getSafe();
        public static final ActionHandle DROP_ALL_ITEMS = ActionHandle.T.DROP_ALL_ITEMS.getSafe();
        public static final ActionHandle DROP_ITEM = ActionHandle.T.DROP_ITEM.getSafe();
        public static final ActionHandle RELEASE_USE_ITEM = ActionHandle.T.RELEASE_USE_ITEM.getSafe();

        public static ActionHandle createHandle(Object handleInstance) {
            return (ActionHandle)T.createHandle(handleInstance);
        }

        public static final class ActionClass
        extends Template.Class<ActionHandle> {
            public final Template.EnumConstant.Converted<ActionHandle> START_DESTROY_BLOCK = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> ABORT_DESTROY_BLOCK = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> STOP_DESTROY_BLOCK = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> DROP_ALL_ITEMS = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> DROP_ITEM = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> RELEASE_USE_ITEM = new Template.EnumConstant.Converted();
        }
    }
}

