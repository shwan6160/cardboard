/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.InteractType;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TransferSignUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SignActionTransfer
extends TrainCartsSignAction {
    public static final String DEPOSIT = "deposit";
    public static final String COLLECT = "collect";
    public static final String KEY_TYPE_TARGET = "target";

    private static void setTargetConstant(TrainCarts traincarts, Collection<InventoryHolder> inventories) {
        HashSet<String> types = new HashSet<String>();
        StringBuilder nameBuilder = new StringBuilder();
        for (InventoryHolder holder : inventories) {
            for (ItemStack item : holder.getInventory()) {
                if (LogicUtil.nullOrEmpty((ItemStack)item)) continue;
                nameBuilder.setLength(0);
                nameBuilder.append(item.getType().toString().toLowerCase(Locale.ENGLISH));
                if (((Boolean)MaterialUtil.HASDATA.get(item)).booleanValue()) {
                    nameBuilder.append(':');
                    nameBuilder.append(item.getDurability());
                }
                types.add(nameBuilder.toString());
            }
        }
        ItemParser[] parsers = new ItemParser[types.size()];
        Iterator iter = types.iterator();
        for (int i = 0; i < parsers.length; ++i) {
            parsers[i] = ItemParser.parse((String)((String)iter.next()));
        }
        traincarts.putParsers(KEY_TYPE_TARGET, parsers);
    }

    public SignActionTransfer() {
        super(InteractType.getAllUniqueTypeIdentifiers());
    }

    @Override
    public void execute(SignActionEvent info) {
        Collection<InventoryHolder> trainInvs;
        boolean dotrain;
        if (!info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER)) {
            return;
        }
        if (!info.hasRails() || !info.isPowered()) {
            return;
        }
        boolean docart = info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && info.isCartSign() && info.hasMember();
        boolean bl = dotrain = !docart && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && info.isTrainSign() && info.hasGroup();
        if (!docart && !dotrain) {
            return;
        }
        boolean collect = true;
        Collection<InventoryHolder> otherInvs = TransferSignUtil.findBlocks(info, COLLECT);
        if (otherInvs.isEmpty()) {
            collect = false;
            otherInvs = TransferSignUtil.findBlocks(info, DEPOSIT);
            if (otherInvs.isEmpty()) {
                return;
            }
        }
        if ((trainInvs = TransferSignUtil.getInventories(info)).isEmpty()) {
            return;
        }
        if (collect) {
            SignActionTransfer.setTargetConstant(info.getTrainCarts(), trainInvs);
        } else {
            SignActionTransfer.setTargetConstant(info.getTrainCarts(), otherInvs);
        }
        ItemParser[] parsers = Util.getParsers(info.getLine(2), info.getLine(3));
        info.getTrainCarts().putParsers(KEY_TYPE_TARGET, null);
        if (collect) {
            for (ItemParser parser : parsers) {
                TransferSignUtil.transferAllItems(info.getTrainCarts(), otherInvs, trainInvs, parser, false);
            }
        } else {
            int fuelHalfIndex = info.getLine(2).isEmpty() ? 0 : (info.getLine(3).isEmpty() ? Integer.MAX_VALUE : Util.getParsers(info.getLine(2)).length);
            for (int i = 0; i < parsers.length; ++i) {
                TransferSignUtil.transferAllItems(info.getTrainCarts(), trainInvs, otherInvs, parsers[i], i >= fuelHalfIndex);
            }
        }
        for (InventoryHolder holder : otherInvs) {
            if (!(holder instanceof BlockState)) continue;
            BlockUtil.applyPhysics((Block)((BlockState)holder).getBlock(), (Material)Material.AIR);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        Collection<InteractType> typesToCheck = InteractType.parse(COLLECT, event.getLine(1));
        boolean collect = true;
        if (typesToCheck.isEmpty()) {
            collect = false;
            typesToCheck = InteractType.parse(DEPOSIT, event.getLine(1));
        }
        String[] types = new String[typesToCheck.size()];
        int i = 0;
        for (InteractType mat : typesToCheck) {
            types[i] = mat.toString().toLowerCase() + "s";
            ++i;
        }
        if (collect) {
            return SignBuildOptions.create().setPermission(Permission.BUILD_COLLECTOR).setName("storage minecart item collector").setDescription("take items from " + StringUtil.combineNames((String[])types)).setTraincartsWIKIHelp("TrainCarts/Signs/Transfer").handle(event);
        }
        return SignBuildOptions.create().setPermission(Permission.BUILD_DEPOSITOR).setName("storage minecart item depositor").setDescription("make trains put items into " + StringUtil.combineNames((String[])types)).setTraincartsWIKIHelp("TrainCarts/Signs/Transfer").handle(event);
    }
}

