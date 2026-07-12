/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainDisplayedBlocks;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.Material;

public class SignActionBlockChanger
extends TrainCartsSignAction {
    public static final int BLOCK_OFFSET_NONE = Integer.MAX_VALUE;

    public static void setBlocks(Collection<MinecartMember<?>> members, TrainDisplayedBlocks config) {
        if (config.getBlockTypesPattern().isEmpty()) {
            SignActionBlockChanger.setBlocks(members, new ItemParser[0], config.getOffset());
        } else {
            SignActionBlockChanger.setBlocks(members, config.getBlockTypesPattern(), config.getOffset());
        }
    }

    public static void setBlocks(Collection<MinecartMember<?>> members, String blocksText, int blockOffset) {
        SignActionBlockChanger.setBlocks(members, Util.getParsers(blocksText), blockOffset);
    }

    public static void setBlocks(Collection<MinecartMember<?>> members, ItemParser[] blocks, int blockOffset) {
        Iterator<MinecartMember<?>> iter = members.iterator();
        if (blocks != null && blocks.length > 0) {
            block0: while (true) {
                ItemParser[] itemParserArray = blocks;
                int n = itemParserArray.length;
                int n2 = 0;
                while (true) {
                    if (n2 >= n) continue block0;
                    ItemParser block = itemParserArray[n2];
                    int amount = block.hasAmount() ? block.getAmount() : 1;
                    for (int i = 0; i < amount; ++i) {
                        Material type;
                        if (!iter.hasNext()) {
                            return;
                        }
                        CommonMinecart entity = (CommonMinecart)iter.next().getEntity();
                        if (!block.hasType() && !block.hasData()) continue;
                        Material material = type = block.hasType() ? block.getType() : entity.getBlockType();
                        if (block.hasData()) {
                            entity.setBlock(type, block.getData());
                        } else {
                            entity.setBlock(type);
                        }
                        if (blockOffset == Integer.MAX_VALUE) continue;
                        entity.setBlockOffset(blockOffset);
                    }
                    ++n2;
                }
                break;
            }
        }
        if (blockOffset != Integer.MAX_VALUE) {
            for (MinecartMember<?> member : members) {
                ((CommonMinecart)member.getEntity()).setBlockOffset(blockOffset);
            }
        }
    }

    public SignActionBlockChanger() {
        super("blockchanger", "setblock", "changeblock");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isPowered()) {
            return;
        }
        ItemParser[] blocks = Util.getParsers(info.getLine(2), info.getLine(3));
        int blockOffset = ParseUtil.parseInt((String)info.getLine(1), (int)Integer.MAX_VALUE);
        if (info.isTrainSign() && info.hasGroup() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER)) {
            SignActionBlockChanger.setBlocks(info.getGroup(), blocks, blockOffset);
        } else if (info.isCartSign() && info.hasMember() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.MEMBER_ENTER)) {
            ArrayList tmp = new ArrayList(1);
            tmp.add(info.getMember());
            SignActionBlockChanger.setBlocks(tmp, blocks, blockOffset);
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            for (MinecartGroup group : info.getRCTrainGroups()) {
                SignActionBlockChanger.setBlocks(group, blocks, blockOffset);
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_BLOCKCHANGER).setName(event.isCartSign() ? "cart block changer" : "train block changer").setTraincartsWIKIHelp("TrainCarts/Signs/BlockChanger");
        if (event.isTrainSign()) {
            opt.setDescription("change the blocks displayed in a train");
        } else if (event.isCartSign()) {
            opt.setDescription("change the block displayed in a minecart");
        } else if (event.isRCSign()) {
            opt.setDescription("change the blocks displayed in a train remotely");
        }
        return opt.handle(event);
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }
}

