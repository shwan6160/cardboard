/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.World
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;

@Deprecated
public class NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.create(LevelHandle.T.getType());
    private static final MethodAccessor<Server> getServer = ((Template.Method)LevelHandle.T.getServer.raw).toMethodAccessor();
    public static final FieldAccessor<World> bukkitWorld = LevelHandle.T.bukkitWorld.toFieldAccessor();
    public static final MethodAccessor<Boolean> getBlockCollisions = ((Template.Method)LevelHandle.T.getBlockCollisions.raw).toMethodAccessor();
    public static final MethodAccessor<List<?>> getEntities = ((Template.Method)LevelHandle.T.getNearbyEntities.raw).toMethodAccessor();
    public static final int UPDATE_PHYSICS = 1;
    public static final int UPDATE_NOTIFY = 2;
    public static final int UPDATE_DEFAULT = 3;

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle, new Object[0]);
    }

    public static boolean updateBlock(Object worldHandle, int x, int y, int z, BlockData data, int updateFlags) {
        Object blockPosition = ((Template.Constructor)BlockPosHandle.T.constr_x_y_z.raw).newInstance(x, y, z);
        return (Boolean)((Template.Method)LevelHandle.T.setBlockData.raw).invoke(worldHandle, blockPosition, data.getData(), updateFlags);
    }
}

