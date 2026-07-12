/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.config.transform;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public enum ArmorStandItemTransformType {
    HEAD("head", "HEAD", false),
    SMALL_HEAD("head \u24ae", "HEAD", true),
    LEFT_HAND("left hand", "OFF_HAND", false),
    SMALL_LEFT_HAND("left hand \u24ae", "OFF_HAND", true),
    RIGHT_HAND("right hand", "HAND", false),
    SMALL_RIGHT_HAND("right hand \u24ae", "HAND", true),
    CHEST("chest", "CHEST", false),
    SMALL_CHEST("chest \u24ae", "CHEST", true),
    LEGS("legs", "LEGS", false),
    SMALL_LEGS("legs \u24ae", "LEGS", true),
    FEET("feet", "FEET", false),
    SMALL_FEET("feet \u24ae", "FEET", true);

    private final String name;
    private final EquipmentSlot slot;
    private final boolean small;

    private ArmorStandItemTransformType(String name, String slotName, boolean small) {
        this.name = name;
        this.small = small;
        EquipmentSlot slot = (EquipmentSlot)ParseUtil.parseEnum(EquipmentSlot.class, (String)slotName, null);
        if (slot == null && slotName.equals("OFF_HAND")) {
            slot = (EquipmentSlot)ParseUtil.parseEnum(EquipmentSlot.class, (String)"HAND", null);
        }
        this.slot = slot != null ? slot : EquipmentSlot.HEAD;
    }

    public String toString() {
        return this.name;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public boolean isHead() {
        return this == HEAD || this == SMALL_HEAD;
    }

    public boolean isSmallArmorStand() {
        return this.small;
    }

    public boolean isLeftHand() {
        return this == LEFT_HAND || this == SMALL_LEFT_HAND;
    }

    public boolean isRightHand() {
        return this == RIGHT_HAND || this == SMALL_RIGHT_HAND;
    }

    public boolean isLeg() {
        return this == LEGS || this == SMALL_LEGS || this == FEET || this == SMALL_FEET;
    }

    public double getArmorStandHorizontalOffset() {
        switch (this.ordinal()) {
            case 2: {
                return 0.3125;
            }
            case 3: {
                return 0.12;
            }
            case 4: {
                return -0.3125;
            }
            case 5: {
                return -0.12;
            }
        }
        return 0.0;
    }

    public double getArmorStandVerticalOffset() {
        switch (this.ordinal()) {
            case 2: 
            case 4: {
                return 1.375;
            }
            case 3: 
            case 5: {
                return 0.492;
            }
            case 0: {
                return 1.44;
            }
            case 1: {
                return 0.73;
            }
        }
        return 1.44;
    }

    public ClientboundSetEquipmentPacketHandle createEquipmentPacket(int entityId, ItemStack item) {
        return Util.createNonPlayerEquipmentPacket(entityId, this.getSlot(), item);
    }

    public static ArmorStandItemTransformType get(String name) {
        for (ArmorStandItemTransformType type : ArmorStandItemTransformType.values()) {
            if (!type.toString().equals(name)) continue;
            return type;
        }
        return HEAD;
    }
}

