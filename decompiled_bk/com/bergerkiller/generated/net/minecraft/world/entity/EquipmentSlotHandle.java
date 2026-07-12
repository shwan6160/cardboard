/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.EquipmentSlot")
public abstract class EquipmentSlotHandle
extends Template.Handle {
    public static final EquipmentSlotClass T = Template.Class.create(EquipmentSlotClass.class, Common.TEMPLATE_RESOLVER);

    public static EquipmentSlotHandle createHandle(Object handleInstance) {
        return (EquipmentSlotHandle)T.createHandle(handleInstance);
    }

    public abstract int getFilterFlag();

    public abstract String getName();

    public static Object fromFilterFlagRaw(int index) {
        for (Object value : T.getType().getEnumConstants()) {
            if (EquipmentSlotHandle.T.getFilterFlag.invoke(value) != index) continue;
            return value;
        }
        return null;
    }

    public static final class EquipmentSlotClass
    extends Template.Class<EquipmentSlotHandle> {
        public final Template.Method<Integer> getFilterFlag = new Template.Method();
        public final Template.Method<String> getName = new Template.Method();
    }
}

