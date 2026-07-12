/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public enum HumanHand {
    LEFT,
    RIGHT;

    private final Object bukkitMainHand;
    private final Object nmsHumanoidArm;
    private static final FastMethod<Object> getBukkitMainHandMethod;

    private HumanHand() {
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            try {
                Class<?> mainHandType = CommonUtil.getClass("org.bukkit.inventory.MainHand");
                this.bukkitMainHand = mainHandType.getField(this.name()).get(null);
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize bukkit main hand for HumanHand enum constant " + this.name(), t);
            }
            try {
                Class<?> humanoidArmType = CommonUtil.getClass("net.minecraft.world.entity.HumanoidArm");
                this.nmsHumanoidArm = Resolver.resolveAndGetDeclaredField(humanoidArmType, this.name()).get(null);
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize nms humanoid arm for HumanHand enum constant " + this.name(), t);
            }
        }
        this.bukkitMainHand = null;
        this.nmsHumanoidArm = null;
    }

    public final HumanHand opposite() {
        return this == LEFT ? RIGHT : LEFT;
    }

    public Object toNMSInteractionHand(HumanEntity humanEntity) {
        return HumanHand.toNMSInteractionHand(humanEntity, this);
    }

    @ConverterMethod(input="org.bukkit.inventory.MainHand")
    public static HumanHand fromBukkitMainHand(Object mainHand) {
        if (!CommonCapabilities.PLAYER_OFF_HAND) {
            return RIGHT;
        }
        if (mainHand != null) {
            if (HumanHand.RIGHT.bukkitMainHand == mainHand) {
                return RIGHT;
            }
            if (HumanHand.LEFT.bukkitMainHand == mainHand) {
                return LEFT;
            }
        }
        return null;
    }

    @ConverterMethod(output="org.bukkit.inventory.MainHand")
    public static Object toBukkitMainHand(HumanHand hand) {
        return hand == null ? null : hand.bukkitMainHand;
    }

    @ConverterMethod(input="net.minecraft.world.entity.HumanoidArm")
    public static HumanHand fromNMSHumanoidArm(Object nmsHumanoidArm) {
        if (!CommonCapabilities.PLAYER_OFF_HAND) {
            return RIGHT;
        }
        if (nmsHumanoidArm != null) {
            if (HumanHand.RIGHT.nmsHumanoidArm == nmsHumanoidArm) {
                return RIGHT;
            }
            if (HumanHand.LEFT.nmsHumanoidArm == nmsHumanoidArm) {
                return LEFT;
            }
        }
        return null;
    }

    @ConverterMethod(output="net.minecraft.world.entity.HumanoidArm")
    public static Object toNMSHumanoidArm(HumanHand hand) {
        return hand == null ? null : hand.nmsHumanoidArm;
    }

    public HumanHandRole getRoleOf(HumanEntity humanEntity) {
        if (this == HumanHand.getMainHand(humanEntity)) {
            return HumanHandRole.MAIN;
        }
        return HumanHandRole.OFF;
    }

    public static HumanHand getMainHand(HumanEntity humanEntity) {
        if (humanEntity != null && getBukkitMainHandMethod != null) {
            return HumanHand.fromBukkitMainHand(getBukkitMainHandMethod.invoke(humanEntity));
        }
        return RIGHT;
    }

    public static HumanHand getOffHand(HumanEntity humanEntity) {
        return HumanHand.getMainHand(humanEntity).opposite();
    }

    public static HumanHand fromNMSInteractionHand(HumanEntity humanEntity, Object nmsInteractionHand) {
        HumanHandRole role = HumanHandRole.fromNMSInteractionHand(nmsInteractionHand);
        return role == null ? null : role.getHandOf(humanEntity);
    }

    public static Object toNMSInteractionHand(HumanEntity humanEntity, HumanHand humanHand) {
        return HumanHandRole.toNMSInteractionHand(humanHand.getRoleOf(humanEntity));
    }

    public static ItemStack getHeldItem(HumanEntity humanEntity, HumanHand humanHand) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        return humanHand.getRoleOf(humanEntity).getHeldItem(humanEntity);
    }

    public static void setHeldItem(HumanEntity humanEntity, HumanHand humanHand, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        humanHand.getRoleOf(humanEntity).setHeldItem(humanEntity, item);
    }

    public static ItemStack getItemInMainHand(HumanEntity humanEntity) {
        return HumanHandRole.MAIN.getHeldItem(humanEntity);
    }

    public static ItemStack getItemInOffHand(HumanEntity humanEntity) {
        return HumanHandRole.OFF.getHeldItem(humanEntity);
    }

    public static void setItemInMainHand(HumanEntity humanEntity, ItemStack item) {
        HumanHandRole.MAIN.setHeldItem(humanEntity, item);
    }

    public static void setItemInOffHand(HumanEntity humanEntity, ItemStack item) {
        HumanHandRole.OFF.setHeldItem(humanEntity, item);
    }

    static {
        getBukkitMainHandMethod = LogicUtil.tryCreate(() -> {
            if (CommonCapabilities.PLAYER_OFF_HAND) {
                FastMethod method = new FastMethod(HumanEntity.class.getMethod("getMainHand", new Class[0]));
                method.forceInitialization();
                return method;
            }
            return null;
        }, t -> {
            throw new UnsupportedOperationException("Failed to initialize the HumanEntity getMainHand method", (Throwable)t);
        });
    }
}

