/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.org.bukkit.inventory.PlayerInventoryHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public enum HumanHandRole {
    MAIN("MAIN_HAND"),
    OFF("OFF_HAND");

    private final Object nmsInteractionHand;

    private HumanHandRole(String enumName) {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            try {
                Class<?> interactionHandType = CommonUtil.getClass("net.minecraft.world.InteractionHand");
                this.nmsInteractionHand = Resolver.resolveAndGetDeclaredField(interactionHandType, enumName).get(null);
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize HumanHandRole enum constants for " + this.name(), t);
            }
        } else {
            this.nmsInteractionHand = null;
        }
    }

    public final HumanHandRole opposite() {
        return this == MAIN ? OFF : MAIN;
    }

    @ConverterMethod(input="net.minecraft.world.InteractionHand")
    public static HumanHandRole fromNMSInteractionHand(Object nmsInteractionHand) {
        if (nmsInteractionHand != null) {
            if (nmsInteractionHand == HumanHandRole.MAIN.nmsInteractionHand) {
                return MAIN;
            }
            if (nmsInteractionHand == HumanHandRole.OFF.nmsInteractionHand) {
                return OFF;
            }
            return null;
        }
        return null;
    }

    @ConverterMethod(output="net.minecraft.world.InteractionHand")
    public static Object toNMSInteractionHand(HumanHandRole handRole) {
        if (handRole != null) {
            return handRole.nmsInteractionHand;
        }
        return null;
    }

    public HumanHand getHandOf(HumanEntity humanEntity) {
        HumanHand hand = HumanHand.getMainHand(humanEntity);
        return this == MAIN ? hand : hand.opposite();
    }

    public static HumanHandRole fromHandOf(HumanEntity humanEntity, HumanHand hand) {
        if (hand == null) {
            return null;
        }
        HumanHand mainHand = HumanHand.getMainHand(humanEntity);
        return hand == mainHand ? MAIN : OFF;
    }

    public ItemStack getHeldItem(HumanEntity humanEntity) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity cannot be null");
        }
        if (this == MAIN) {
            return PlayerInventoryHandle.T.getItemInMainHand.invoke(humanEntity.getInventory());
        }
        return PlayerInventoryHandle.T.getItemInOffHand.invoke(humanEntity.getInventory());
    }

    public void setHeldItem(HumanEntity humanEntity, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        if (this == MAIN) {
            PlayerInventoryHandle.T.setItemInMainHand.invoke(humanEntity.getInventory(), item);
        } else {
            PlayerInventoryHandle.T.setItemInOffHand.invoke(humanEntity.getInventory(), item);
        }
    }
}

