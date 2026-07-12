/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.MaterialTypeProperty
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  com.bergerkiller.bukkit.common.wrappers.InteractionResult
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.type;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.PoweredCartSoundLoop;
import com.bergerkiller.bukkit.tc.controller.persistence.FuelPersistentCartAttribute;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.events.MemberCoalUsedEvent;
import com.bergerkiller.bukkit.tc.exception.GroupUnloadedException;
import com.bergerkiller.bukkit.tc.exception.MemberMissingException;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MinecartMemberFurnace
extends MinecartMember<CommonMinecartFurnace> {
    private int fuelCheckCounter = 0;
    private boolean isPushingForwards = true;
    private static final MaterialTypeProperty IS_FUEL_ITEM = Common.evaluateMCVersion((String)">=", (String)"1.13") ? new MaterialTypeProperty(new Material[]{MaterialUtil.getMaterial((String)"COAL"), MaterialUtil.getMaterial((String)"CHARCOAL")}) : new MaterialTypeProperty(new Material[]{MaterialUtil.getMaterial((String)"LEGACY_COAL")});

    public MinecartMemberFurnace(TrainCarts plugin) {
        super(plugin);
        this.addPersistentCartAttribute(new FuelPersistentCartAttribute());
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.soundLoop = new PoweredCartSoundLoop(this);
        Vector fwd = this.getOrientationForward();
        Vector push = Math.abs(fwd.getY()) > Math.max(Math.abs(fwd.getX()), Math.abs(fwd.getZ())) ? new Vector(0.0, ((CommonMinecartFurnace)this.entity).getPushX(), 0.0) : new Vector(((CommonMinecartFurnace)this.entity).getPushX(), 0.0, ((CommonMinecartFurnace)this.entity).getPushZ());
        this.isPushingForwards = fwd.dot(push) >= 0.0;
    }

    private void updatePushXZ() {
        Vector fwd = this.getOrientationForward();
        if (!this.isPushingForwards) {
            fwd.multiply(-1.0);
        }
        if (Math.abs(fwd.getY()) > Math.max(Math.abs(fwd.getX()), Math.abs(fwd.getZ()))) {
            ((CommonMinecartFurnace)this.entity).setPushX(fwd.getY() >= 0.0 ? 1.0 : -1.0);
            ((CommonMinecartFurnace)this.entity).setPushZ(0.0);
        } else {
            fwd.setY(0.0);
            if (fwd.lengthSquared() > 1.0E-10) {
                fwd.multiply(MathUtil.getNormalizationFactorLS((double)fwd.lengthSquared()));
                ((CommonMinecartFurnace)this.entity).setPushX(fwd.getX());
                ((CommonMinecartFurnace)this.entity).setPushZ(fwd.getZ());
            }
        }
    }

    public InteractionResult onInteractBy(HumanEntity human, HumanHand hand, Vector location) {
        if (!this.isInteractable()) {
            return InteractionResult.PASS;
        }
        ItemStack itemstack = HumanHand.getHeldItem((HumanEntity)human, (HumanHand)hand);
        if (itemstack != null && ((Boolean)IS_FUEL_ITEM.get(itemstack)).booleanValue()) {
            if (!(human instanceof Player) || ((Player)human).getGameMode() != GameMode.CREATIVE) {
                ItemUtil.subtractAmount((ItemStack)itemstack, (int)1);
                HumanHand.setHeldItem((HumanEntity)human, (HumanHand)hand, (ItemStack)itemstack);
            }
            this.addFuelTicks(3600);
        }
        Location humanEye = human.getEyeLocation();
        Vector eyeFwd = MathUtil.getDirection((float)humanEye.getYaw(), (float)humanEye.getPitch());
        this.isPushingForwards = this.getOrientationForward().dot(eyeFwd) >= 0.0;
        this.updatePushXZ();
        return InteractionResult.CONSUME;
    }

    public void addFuelTicks(int fuelTicks) {
        int newFuelTicks = ((CommonMinecartFurnace)this.entity).getFuelTicks() + fuelTicks;
        if (newFuelTicks <= 0) {
            newFuelTicks = 0;
        }
        ((CommonMinecartFurnace)this.entity).setFuelTicks(newFuelTicks);
    }

    public boolean onCoalUsed() {
        MemberCoalUsedEvent event = MemberCoalUsedEvent.call(this);
        if (event.useCoal()) {
            return this.getCoalFromNeighbours();
        }
        return event.refill();
    }

    public boolean getCoalFromNeighbours() {
        for (MinecartMember<?> mm : this.getNeightbours()) {
            if (!(mm instanceof MinecartMemberChest)) continue;
            Inventory inv = ((CommonMinecartChest)((MinecartMemberChest)mm).getEntity()).getInventory();
            for (int i = 0; i < inv.getSize(); ++i) {
                ItemStack item = inv.getItem(i);
                if (LogicUtil.nullOrEmpty((ItemStack)item) || item.getType() != Material.COAL) continue;
                ItemUtil.subtractAmount((ItemStack)item, (int)1);
                inv.setItem(i, item);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPhysicsPostMove() throws MemberMissingException, GroupUnloadedException {
        super.onPhysicsPostMove();
        if (((CommonMinecartFurnace)this.entity).hasFuel()) {
            ((CommonMinecartFurnace)this.entity).addFuelTicks(-1);
            if (!((CommonMinecartFurnace)this.entity).hasFuel() && this.onCoalUsed()) {
                this.addFuelTicks(3600);
            }
        }
        if (!((CommonMinecartFurnace)this.entity).hasFuel()) {
            if (this.fuelCheckCounter++ % 20 == 0 && TCConfig.useCoalFromStorageCart && this.getCoalFromNeighbours()) {
                this.addFuelTicks(3600);
            }
        } else {
            this.fuelCheckCounter = 0;
        }
        if (!((CommonMinecartFurnace)this.entity).hasFuel()) {
            ((CommonMinecartFurnace)this.entity).setFuelTicks(0);
        }
        ((CommonMinecartFurnace)this.entity).setSmoking(((CommonMinecartFurnace)this.entity).hasFuel());
    }

    @Override
    public void onPhysicsPreMove() {
        super.onPhysicsPreMove();
        if (!this.isDerailed()) {
            if (this.isMovementControlled()) {
                Vector fwd = FaceUtil.faceToVector((BlockFace)this.getDirection());
                double dot = this.getOrientationForward().dot(fwd);
                if (dot < -1.0E-4 || dot > 1.0E-4) {
                    this.isPushingForwards = dot > 0.0;
                }
            } else if (((CommonMinecartFurnace)this.entity).hasFuel()) {
                Vector dir = this.getOrientationForward();
                if (!this.isPushingForwards) {
                    dir.multiply(-1.0);
                }
                dir.multiply(0.04 + TCConfig.poweredCartBoost);
                ((CommonMinecartFurnace)this.entity).vel.multiply(0.8);
                ((CommonMinecartFurnace)this.entity).vel.add(dir);
            } else if (this.getGroup().getProperties().isSlowingDown(SlowdownMode.FRICTION)) {
                ((CommonMinecartFurnace)this.entity).vel.multiply(0.98);
            }
            this.updatePushXZ();
        }
    }

    public void onItemSet(int index, ItemStack item) {
        super.onItemSet(index, item);
        this.onPropertiesChanged();
    }
}

