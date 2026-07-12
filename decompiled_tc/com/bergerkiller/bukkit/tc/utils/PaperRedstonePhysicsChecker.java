/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.generated.net.minecraft.world.level.LevelHandle
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.World;

public class PaperRedstonePhysicsChecker {
    private static final Set<UUID> _checked = new HashSet<UUID>();

    public static void check(World world) {
        if (!Common.IS_PAPERSPIGOT_SERVER || !_checked.add(world.getUID())) {
            return;
        }
        try {
            Object worldHandle = LevelHandle.fromBukkit((World)world).getRaw();
            Field worldConfigField = worldHandle.getClass().getField("paperConfig");
            Object worldConfig = worldConfigField.get(worldHandle);
            Field propertyField = worldConfig.getClass().getField("firePhysicsEventForRedstone");
            boolean property = propertyField.getBoolean(worldConfig);
            if (!property) {
                TrainCarts.plugin.log(Level.WARNING, "Traincarts is used on a world that has 'fire-physics-event-for-redstone' set to 'false' in paper.yml");
                TrainCarts.plugin.log(Level.WARNING, "This may cause some Traincarts signs to malfunction on world: '" + world.getName() + "'");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

