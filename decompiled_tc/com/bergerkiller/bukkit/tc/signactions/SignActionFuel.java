/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.Material
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.itemanimation.ItemAnimation;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TransferSignUtil;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignActionFuel
extends TrainCartsSignAction {
    public SignActionFuel() {
        super("fuel");
    }

    @Override
    public void execute(SignActionEvent info) {
        ArrayList carts;
        boolean dotrain;
        if (!info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER)) {
            return;
        }
        boolean docart = info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isCartSign() && info.hasMember();
        boolean bl = dotrain = !docart && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && info.isTrainSign() && info.hasGroup();
        if (!docart && !dotrain) {
            return;
        }
        if (!info.isPowered()) {
            return;
        }
        int radius = ParseUtil.parseInt((String)info.getLine(1), (int)TCConfig.defaultTransferRadius);
        ArrayList<Chest> chests = new ArrayList<Chest>();
        for (BlockState state : TransferSignUtil.getBlockStates(info, radius, radius)) {
            if (!(state instanceof Chest)) continue;
            chests.add((Chest)state);
        }
        if (chests.isEmpty()) {
            return;
        }
        if (dotrain) {
            carts = info.getGroup();
        } else {
            carts = new ArrayList(1);
            carts.add(info.getMember());
        }
        block1: for (MinecartMember cart : carts) {
            MinecartMemberFurnace member;
            if (!(cart instanceof MinecartMemberFurnace) || ((CommonMinecartFurnace)(member = (MinecartMemberFurnace)cart).getEntity()).hasFuel()) continue;
            boolean found = false;
            for (Chest chest : chests) {
                Inventory inv = chest.getInventory();
                for (int i = 0; i < inv.getSize(); ++i) {
                    ItemStack item = inv.getItem(i);
                    if (LogicUtil.nullOrEmpty((ItemStack)item) || item.getType() != Material.COAL) continue;
                    ItemUtil.subtractAmount((ItemStack)item, (int)1);
                    inv.setItem(i, item);
                    found = true;
                    member.addFuelTicks(3600);
                    if (!TCConfig.showTransferAnimations) break;
                    ItemAnimation.start((Object)chest, (Object)member, new ItemStack(Material.COAL, 1));
                    break;
                }
                if (!found) continue;
                continue block1;
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_COLLECTOR).setName("powered minecart coal collector").setDescription("fuel the powered minecart using coal from a chest");
        return opt.handle(event);
    }
}

