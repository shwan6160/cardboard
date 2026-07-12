/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.PacketPlayOutUpdateSign")
public abstract class PacketPlayOutUpdateSignHandle
extends PacketHandle {
    public static final PacketPlayOutUpdateSignClass T = Template.Class.create(PacketPlayOutUpdateSignClass.class, Common.TEMPLATE_RESOLVER);

    public static PacketPlayOutUpdateSignHandle createHandle(Object handleInstance) {
        return (PacketPlayOutUpdateSignHandle)T.createHandle(handleInstance);
    }

    public abstract World getWorld();

    public abstract void setWorld(World var1);

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract ChatText[] getLines();

    public abstract void setLines(ChatText[] var1);

    public static final class PacketPlayOutUpdateSignClass
    extends Template.Class<PacketPlayOutUpdateSignHandle> {
        public final Template.Field.Converted<World> world = new Template.Field.Converted();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Converted<ChatText[]> lines = new Template.Field.Converted();
    }
}

