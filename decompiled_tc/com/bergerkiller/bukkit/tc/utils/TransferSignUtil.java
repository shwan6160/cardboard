/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector2
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.inventory.InventoryBase
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.RecipeUtil
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.Dispenser
 *  org.bukkit.block.Dropper
 *  org.bukkit.block.Furnace
 *  org.bukkit.inventory.DoubleChestInventory
 *  org.bukkit.inventory.FurnaceInventory
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.RecipeUtil;
import com.bergerkiller.bukkit.tc.InteractType;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.itemanimation.ItemAnimatedInventory;
import com.bergerkiller.bukkit.tc.utils.AveragedItemParser;
import com.bergerkiller.bukkit.tc.utils.GroundItemsInventory;
import com.bergerkiller.bukkit.tc.utils.GroundItemsState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TransferSignUtil {
    private static final HashSet<InventoryHolder> chestsBuffer = new HashSet();

    public static Inventory getInventory(SignActionEvent info) {
        if (info.isCartSign()) {
            if (info.getMember() instanceof MinecartMemberChest) {
                return ((CommonMinecartChest)((MinecartMemberChest)info.getMember()).getEntity()).getInventory();
            }
            return null;
        }
        return info.getGroup().getInventory();
    }

    public static Collection<InventoryHolder> getInventories(SignActionEvent info) {
        if (info.isCartSign()) {
            if (info.getMember() instanceof MinecartMemberChest) {
                return Collections.singletonList((InventoryHolder)((CommonMinecart)info.getMember().getEntity()).getEntity());
            }
            return Collections.emptyList();
        }
        ArrayList<InventoryHolder> trainInvs = new ArrayList<InventoryHolder>(info.getGroup().size());
        for (MinecartMember<?> member : info.getGroup()) {
            if (!(member instanceof MinecartMemberChest)) continue;
            trainInvs.add((InventoryHolder)((CommonMinecart)member.getEntity()).getEntity());
        }
        return trainInvs;
    }

    public static int depositInFurnace(TrainCarts traincarts, Inventory from, Furnace toFurnace, ItemParser parser, boolean isFuelPreferred) {
        int startAmount;
        FurnaceInventory to = toFurnace.getInventory();
        ArrayList<ItemParser> heatables = new ArrayList<ItemParser>();
        ArrayList<ItemParser> fuels = new ArrayList<ItemParser>();
        if (!parser.hasType()) {
            for (ItemParser p : traincarts.getParsers("heatable", 1)) {
                if (p == null || !p.hasType()) {
                    heatables.clear();
                    break;
                }
                heatables.add(p);
            }
            for (ItemParser p : traincarts.getParsers("fuel", 1)) {
                if (p == null || !p.hasType()) {
                    fuels.clear();
                    break;
                }
                fuels.add(p);
            }
            if (heatables.isEmpty() && fuels.isEmpty()) {
                return 0;
            }
        } else {
            ItemStack parseritem = parser.getItemStack(1);
            boolean heatable = RecipeUtil.isHeatableItem((ItemStack)parseritem);
            boolean fuel = RecipeUtil.isFuelItem((ItemStack)parseritem);
            if (heatable && fuel) {
                if (isFuelPreferred) {
                    fuels.add(parser);
                } else {
                    heatables.add(parser);
                }
            } else if (heatable) {
                heatables.add(parser);
            } else if (fuel) {
                fuels.add(parser);
            } else {
                return 0;
            }
        }
        int amountToTransfer = startAmount = parser.hasAmount() ? parser.getAmount() : Integer.MAX_VALUE;
        for (ItemParser p : heatables) {
            CommonItemStack item = CommonItemStack.of((ItemStack)to.getItem(0));
            int numTransferred = CommonItemStack.transfer((Inventory)from, (CommonItemStack)item, (Predicate)p, (int)amountToTransfer);
            if (numTransferred <= 0) continue;
            amountToTransfer -= numTransferred;
            to.setItem(0, item.toBukkit());
        }
        for (ItemParser p : fuels) {
            if (p == null) continue;
            if (amountToTransfer == 0) break;
            int transferCount = amountToTransfer;
            CommonItemStack fuel = CommonItemStack.of((ItemStack)to.getItem(1));
            if (!p.hasAmount()) {
                int fuelPerItem;
                int fuelNeeded;
                ItemStack cookeditem = to.getItem(0);
                if (cookeditem == null || cookeditem.getType() == Material.AIR || (fuelNeeded = cookeditem.getAmount() * 200) == 0 || (fuelNeeded -= toFurnace.getCookTime()) <= 0 || (fuelPerItem = fuel.getType() == Material.AIR ? RecipeUtil.getFuelTime((ItemStack)p.getItemStack(1)) : RecipeUtil.getFuelTime((ItemStack)fuel.toBukkit())) == 0 || (fuelNeeded -= fuelPerItem * fuel.getAmount()) <= 0) continue;
                transferCount = Math.min(amountToTransfer, (int)Math.ceil((double)fuelNeeded / (double)fuelPerItem));
            }
            amountToTransfer -= CommonItemStack.transfer((Inventory)from, (CommonItemStack)fuel, (Predicate)p, (int)transferCount);
            to.setItem(1, fuel.toBukkit());
        }
        return startAmount - amountToTransfer;
    }

    public static IntVector2 readRadius(String text) {
        int radWidth = TCConfig.defaultTransferRadius;
        int radHeight = TCConfig.defaultTransferRadius;
        int radStartIndex = text.lastIndexOf(32);
        if (radStartIndex != -1) {
            String radText = text.substring(radStartIndex + 1);
            String[] parts = radText.split(":");
            if (parts.length == 1) {
                radWidth = radHeight = ParseUtil.parseInt((String)radText, (int)TCConfig.defaultTransferRadius);
            } else if (parts.length == 2) {
                radWidth = ParseUtil.parseInt((String)parts[0], (int)TCConfig.defaultTransferRadius);
                radHeight = ParseUtil.parseInt((String)parts[1], (int)TCConfig.defaultTransferRadius);
            }
        }
        radWidth = MathUtil.clamp((int)radWidth, (int)TCConfig.maxTransferRadius);
        radHeight = MathUtil.clamp((int)radHeight, (int)TCConfig.maxTransferRadius);
        return new IntVector2(radWidth, radHeight);
    }

    public static Collection<BlockState> getBlockStates(SignActionEvent info, IntVector2 radius) {
        return TransferSignUtil.getBlockStates(info, radius.x, radius.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Collection<BlockState> getBlockStates(SignActionEvent info, int radWidth, int radHeight) {
        final Block centerBlock = info.getRails();
        int radX = Math.abs(radWidth);
        int radY = Math.abs(radHeight);
        int radZ = Math.abs(radWidth);
        BlockFace dir = info.getCartEnterFace();
        if (FaceUtil.isVertical((BlockFace)dir)) {
            radY = 0;
        } else if (FaceUtil.isAlongX((BlockFace)dir)) {
            radX = 0;
        } else if (FaceUtil.isAlongZ((BlockFace)dir)) {
            radZ = 0;
        }
        ArrayList<BlockState> states = new ArrayList<BlockState>(BlockUtil.getBlockStates((Block)centerBlock, (int)radX, (int)radY, (int)radZ));
        try {
            Iterator iter = states.iterator();
            while (iter.hasNext()) {
                DoubleChestInventory inventory;
                BlockState next = (BlockState)iter.next();
                if (!(next instanceof Chest) || (inventory = (DoubleChestInventory)CommonUtil.tryCast((Object)((Chest)next).getInventory(), DoubleChestInventory.class)) == null || chestsBuffer.add(inventory.getLeftSide().getHolder()) && chestsBuffer.add(inventory.getRightSide().getHolder())) continue;
                iter.remove();
            }
        }
        finally {
            chestsBuffer.clear();
        }
        final boolean widthInv = radWidth < 0;
        final boolean heightInv = radHeight < 0;
        Collections.sort(states, new Comparator<BlockState>(){

            public int getIndex(BlockState state) {
                int dx = MathUtil.invert((int)Math.abs(centerBlock.getX() - state.getX()), (boolean)widthInv);
                int dy = MathUtil.invert((int)Math.abs(centerBlock.getY() - state.getY()), (boolean)heightInv);
                int dz = MathUtil.invert((int)Math.abs(centerBlock.getZ() - state.getZ()), (boolean)widthInv);
                return dx + 16 * dz + 256 * dy;
            }

            @Override
            public int compare(BlockState o1, BlockState o2) {
                return this.getIndex(o1) - this.getIndex(o2);
            }
        });
        return states;
    }

    public static Collection<InventoryHolder> findBlocks(SignActionEvent info, String mode) {
        Collection<InteractType> typesToCheck = InteractType.parse(mode, info.getLine(1));
        if (typesToCheck.isEmpty()) {
            return Collections.emptyList();
        }
        IntVector2 radius = TransferSignUtil.readRadius(info.getLine(1));
        Collection<BlockState> found = TransferSignUtil.getBlockStates(info, radius);
        if (found.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<InventoryHolder> rval = new ArrayList<InventoryHolder>(found.size());
        block7: for (InteractType type : typesToCheck) {
            switch (type) {
                case CHEST: {
                    for (BlockState state : found) {
                        if (!(state instanceof Chest)) continue;
                        rval.add((InventoryHolder)((Chest)state));
                    }
                    continue block7;
                }
                case FURNACE: {
                    for (BlockState state : found) {
                        if (!(state instanceof Furnace)) continue;
                        rval.add((InventoryHolder)((Furnace)state));
                    }
                    continue block7;
                }
                case DISPENSER: {
                    for (BlockState state : found) {
                        if (!(state instanceof Dispenser)) continue;
                        rval.add((InventoryHolder)((Dispenser)state));
                    }
                    continue block7;
                }
                case DROPPER: {
                    for (BlockState state : found) {
                        if (!(state instanceof Dropper)) continue;
                        rval.add((InventoryHolder)((Dropper)state));
                    }
                    continue block7;
                }
                case GROUNDITEM: {
                    rval.add(new GroundItemsState(info.getRails(), Math.abs(radius.x)));
                }
            }
        }
        return rval;
    }

    public static int transferAllItems(TrainCarts traincarts, Collection<InventoryHolder> fromHolders, Collection<InventoryHolder> toHolders, ItemParser itemParser, boolean isFuelPreferred) {
        int transferred = 0;
        for (InventoryHolder fromHolder : fromHolders) {
            int amount;
            Inventory from = fromHolder.getInventory();
            if (itemParser instanceof AveragedItemParser) {
                boolean continueTransferring;
                int totalAmount = itemParser.hasAmount() ? itemParser.getAmount() : Integer.MAX_VALUE;
                ItemParser single = itemParser.setAmount(1);
                int transferredAmount = 0;
                block1: do {
                    continueTransferring = false;
                    for (InventoryHolder toHolder : toHolders) {
                        Inventory to = toHolder.getInventory();
                        amount = TransferSignUtil.transferItems(traincarts, from, to, single, isFuelPreferred);
                        if (amount <= 0) continue;
                        transferred += amount;
                        continueTransferring = (transferredAmount += amount) < totalAmount;
                        if (continueTransferring) continue;
                        continue block1;
                    }
                } while (continueTransferring);
                continue;
            }
            for (InventoryHolder toHolder : toHolders) {
                Inventory to = toHolder.getInventory();
                amount = TransferSignUtil.transferItems(traincarts, from, to, itemParser, isFuelPreferred);
                transferred += amount;
                if (amount <= 0 || !itemParser.hasAmount()) continue;
                itemParser = itemParser.setAmount(itemParser.getAmount() - amount);
            }
        }
        return transferred;
    }

    public static int transferItems(TrainCarts traincarts, Inventory from, Inventory to, ItemParser itemParser, boolean isFuelPreferred) {
        InventoryHolder toHolder = to.getHolder();
        InventoryHolder fromHolder = from.getHolder();
        if (from instanceof FurnaceInventory) {
            final FurnaceInventory finv = (FurnaceInventory)from;
            from = new InventoryBase(){

                public int getSize() {
                    return 1;
                }

                public ItemStack getItem(int index) {
                    return finv.getResult();
                }

                public void setItem(int index, ItemStack item) {
                    finv.setResult(item);
                }
            };
        }
        if (TCConfig.showTransferAnimations && !(from instanceof GroundItemsInventory)) {
            from = ItemAnimatedInventory.convert(from, fromHolder, toHolder);
        }
        if (toHolder instanceof Furnace) {
            return TransferSignUtil.depositInFurnace(traincarts, from, (Furnace)toHolder, itemParser, isFuelPreferred);
        }
        return ItemUtil.transfer((Inventory)from, (Inventory)to, (ItemParser)itemParser, (int)itemParser.getAmount());
    }
}

