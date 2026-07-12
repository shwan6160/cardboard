/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import java.util.function.BiFunction;
import org.bukkit.World;

public class AttachmentWorldFeatures {
    public final boolean MINECART_IMPROVEMENTS;
    private static final BiFunction<World, String, Boolean> hasFeatureMethod = Common.hasCapability((String)"Common:WorldUtil:HasFeatureFlag") ? WorldUtil::hasFeatureFlag : (w, n) -> false;

    public static AttachmentWorldFeatures of(World world) {
        return new AttachmentWorldFeatures(world);
    }

    private AttachmentWorldFeatures(World world) {
        this.MINECART_IMPROVEMENTS = hasFeatureMethod.apply(world, "minecraft:minecart_improvements");
    }

    public static final class Tracker {
        private World world = null;
        private AttachmentWorldFeatures last = null;

        public AttachmentWorldFeatures get(World world) {
            if (this.world == world) {
                return this.last;
            }
            this.world = world;
            this.last = AttachmentWorldFeatures.of(world);
            return this.last;
        }
    }
}

