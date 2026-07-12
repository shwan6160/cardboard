/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPlayerInputPacket")
public abstract class ServerboundPlayerInputPacketHandle
extends PacketHandle {
    public static final ServerboundPlayerInputPacketClass T = Template.Class.create(ServerboundPlayerInputPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundPlayerInputPacketHandle createHandle(Object handleInstance) {
        return (ServerboundPlayerInputPacketHandle)T.createHandle(handleInstance);
    }

    public static ServerboundPlayerInputPacketHandle createNew(boolean isLeft, boolean isRight, boolean isForward, boolean isBackward, boolean isJump, boolean isUnmount, boolean isSprint) {
        return ServerboundPlayerInputPacketHandle.T.createNew.invokeVA(isLeft, isRight, isForward, isBackward, isJump, isUnmount, isSprint);
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract boolean isForward();

    public abstract boolean isBackward();

    public abstract float getSideways();

    public abstract float getForwards();

    public abstract boolean isJump();

    public abstract boolean isUnmount();

    public abstract boolean isSprint();

    public static final class ServerboundPlayerInputPacketClass
    extends Template.Class<ServerboundPlayerInputPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundPlayerInputPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> isLeft = new Template.Method();
        public final Template.Method<Boolean> isRight = new Template.Method();
        public final Template.Method<Boolean> isForward = new Template.Method();
        public final Template.Method<Boolean> isBackward = new Template.Method();
        public final Template.Method<Float> getSideways = new Template.Method();
        public final Template.Method<Float> getForwards = new Template.Method();
        public final Template.Method<Boolean> isJump = new Template.Method();
        public final Template.Method<Boolean> isUnmount = new Template.Method();
        public final Template.Method<Boolean> isSprint = new Template.Method();
    }
}

