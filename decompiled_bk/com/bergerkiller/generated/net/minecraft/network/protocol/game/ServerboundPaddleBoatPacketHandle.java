/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket")
public abstract class ServerboundPaddleBoatPacketHandle
extends Template.Handle {
    public static final ServerboundPaddleBoatPacketClass T = Template.Class.create(ServerboundPaddleBoatPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundPaddleBoatPacketHandle createHandle(Object handleInstance) {
        return (ServerboundPaddleBoatPacketHandle)T.createHandle(handleInstance);
    }

    public abstract boolean isLeftPaddle();

    public abstract void setLeftPaddle(boolean var1);

    public abstract boolean isRightPaddle();

    public abstract void setRightPaddle(boolean var1);

    public static final class ServerboundPaddleBoatPacketClass
    extends Template.Class<ServerboundPaddleBoatPacketHandle> {
        public final Template.Field.Boolean leftPaddle = new Template.Field.Boolean();
        public final Template.Field.Boolean rightPaddle = new Template.Field.Boolean();
    }
}

