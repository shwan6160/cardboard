/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityHuman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Deprecated
public class NMSEntityPlayer
extends NMSEntityHuman {
    public static final ClassTemplate<?> T = ClassTemplate.create(ServerPlayerHandle.T.getType());
    public static final FieldAccessor<Object> playerConnection = ((Template.Field)ServerPlayerHandle.T.playerConnection.raw).toFieldAccessor();

    public static Object getNetworkManager(Player player) {
        ServerGamePacketListenerImplHandle conn = ServerPlayerHandle.T.playerConnection.get(HandleConversion.toEntityHandle((Entity)player));
        return conn == null ? null : conn.getNetworkManager();
    }
}

