/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.EquipmentSlot
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.generated.net.minecraft.world.entity.EquipmentSlotHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.inventory.EquipmentSlot;

public class ItemSlotConversion {
    private static final Map<Object, EquipmentSlot> slotMap_a;
    private static final Map<EquipmentSlot, Object> slotMap_b;

    @ConverterMethod(output="net.minecraft.world.entity.EquipmentSlot")
    public static Object getEnumItemSlot(EquipmentSlot slot) {
        return slotMap_b.get(slot);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EquipmentSlot")
    public static EquipmentSlot getEquipmentSlot(Object enumItemSlot) {
        return slotMap_a.get(enumItemSlot);
    }

    @ConverterMethod
    public static int equipmentSlotToFilterFlag(EquipmentSlot equipmentSlot) {
        return WrapperConversion.enumItemSlotToFilterFlag(ItemSlotConversion.getEnumItemSlot(equipmentSlot));
    }

    static {
        String name;
        slotMap_a = new IdentityHashMap<Object, EquipmentSlot>();
        slotMap_b = new IdentityHashMap<EquipmentSlot, Object>();
        ?[] enumItemSlotValues = EquipmentSlotHandle.T.getType().getEnumConstants();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            name = slot.name().toLowerCase(Locale.ENGLISH).replace("_", "");
            if (name.equals("hand")) {
                name = "mainhand";
            }
            Object foundItemSlot = null;
            for (Object enumItemSlot : enumItemSlotValues) {
                if (!EquipmentSlotHandle.T.getName.invoke(enumItemSlot).equals(name)) continue;
                foundItemSlot = enumItemSlot;
                break;
            }
            if (foundItemSlot != null) {
                slotMap_a.put(foundItemSlot, slot);
                slotMap_b.put(slot, foundItemSlot);
                continue;
            }
            Logging.LOGGER_REFLECTION.warning("Failed to find matching EnumItemSlot for EquipmentSlot " + name);
        }
        for (Object enumItemSlot : enumItemSlotValues) {
            if (slotMap_a.containsKey(enumItemSlot)) continue;
            name = EquipmentSlotHandle.T.getName.invoke(enumItemSlot);
            Logging.LOGGER_REFLECTION.warning("Failed to find matching EquipmentSlot for EnumItemSlot " + name);
        }
    }
}

