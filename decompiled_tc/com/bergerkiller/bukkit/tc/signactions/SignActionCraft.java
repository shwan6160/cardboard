/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.RecipeUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.inventory.Inventory
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.RecipeUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.itemanimation.ItemAnimatedInventory;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TransferSignUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;

public class SignActionCraft
extends TrainCartsSignAction {
    private static final Material WORKBENCH_TYPE = MaterialUtil.getFirst((String[])new String[]{"CRAFTING_TABLE", "LEGACY_WORKBENCH"});

    public SignActionCraft() {
        super("craft");
    }

    @Override
    public void execute(SignActionEvent info) {
        int radZ;
        boolean dotrain;
        boolean docart = info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isCartSign() && info.hasMember();
        boolean bl = dotrain = !docart && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && info.isTrainSign() && info.hasGroup();
        if (!docart && !dotrain || !info.hasRailedMember() || !info.isPowered()) {
            return;
        }
        int radY = radZ = ParseUtil.parseInt((String)info.getLine(1), (int)TCConfig.defaultTransferRadius);
        int radX = radZ;
        BlockFace dir = info.getCartEnterFace();
        if (FaceUtil.isAlongX((BlockFace)dir)) {
            radX = 0;
        } else if (FaceUtil.isAlongZ((BlockFace)dir)) {
            radZ = 0;
        }
        World world = info.getWorld();
        Block m = info.getRails();
        Block w = null;
        for (int x = -radX; x <= radX && w == null; ++x) {
            for (int y = -radY; y <= radY && w == null; ++y) {
                for (int z = -radZ; z <= radZ && w == null; ++z) {
                    BlockData data = WorldUtil.getBlockData((World)world, (int)(m.getX() + x), (int)(m.getY() + y), (int)(m.getZ() + z));
                    if (!data.isType(WORKBENCH_TYPE)) continue;
                    w = m.getRelative(x, y, z);
                }
            }
        }
        if (w != null || !TCConfig.craftingRequireWorkbench) {
            Inventory inventory = TransferSignUtil.getInventory(info);
            if (inventory == null) {
                return;
            }
            if (w != null && TCConfig.showTransferAnimations) {
                inventory = ItemAnimatedInventory.convert(inventory, info.getMember(), w);
            }
            for (ItemParser item : Util.getParsers(info.getLine(2), info.getLine(3))) {
                RecipeUtil.craftItems((ItemParser)item, (Inventory)inventory);
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_CRAFTER).setName("workbench item crafter").setDescription("craft items inside storage minecarts").setTraincartsWIKIHelp("TrainCarts/Signs/Crafter").handle(event);
    }
}

