/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler_1_14;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler_1_14_1;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler_1_8;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler_1_9;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class PortalHandler
implements LazyInitializedObject {
    public static final PortalHandler INSTANCE = CommonBootstrap.evaluateMCVersion(">=", "1.14.1") ? new PortalHandler_1_14_1() : (CommonBootstrap.evaluateMCVersion(">=", "1.14") ? new PortalHandler_1_14() : (CommonBootstrap.evaluateMCVersion(">=", "1.9") ? new PortalHandler_1_9() : new PortalHandler_1_8()));

    public abstract void enable(CommonPlugin var1);

    public abstract void disable(CommonPlugin var1);

    public abstract void showEndCredits(Player var1);

    public abstract boolean isMainEndWorld(World var1);

    public abstract Block findEndPlatform(World var1);

    public abstract Block createEndPlatform(World var1, Entity var2);

    public abstract void markNetherPortal(Block var1);

    public abstract Block findNetherPortal(Block var1, int var2);

    public abstract Block createNetherPortal(Block var1, BlockFace var2, Entity var3);
}

