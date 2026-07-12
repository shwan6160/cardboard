/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.VehicleEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.AbstractMinecart")
public abstract class AbstractMinecartHandle
extends EntityHandle {
    public static final AbstractMinecartClass T = Template.Class.create(AbstractMinecartClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Integer> DATA_SHAKING_FACTOR;
    public static final DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION;
    public static final DataWatcher.Key<Float> DATA_SHAKING_DAMAGE;
    public static final DataWatcher.Key<BlockData> DATA_CUSTOM_DISPLAY_BLOCK;
    public static final DataWatcher.Key<Integer> DATA_BLOCK_TYPE;
    public static final DataWatcher.Key<Integer> DATA_BLOCK_OFFSET;
    public static final DataWatcher.Key<Boolean> DATA_BLOCK_VISIBLE;

    public static AbstractMinecartHandle createHandle(Object handleInstance) {
        return (AbstractMinecartHandle)T.createHandle(handleInstance);
    }

    public abstract float getDamage();

    public abstract void setDamage(float var1);

    public abstract void activateMinecart(World var1, int var2, int var3, int var4, boolean var5);

    public abstract int getHurtTime();

    static {
        DATA_BLOCK_OFFSET = DataWatcher.Key.Type.INTEGER.createKey(AbstractMinecartHandle.T.DATA_BLOCK_OFFSET, 21);
        if (VehicleEntityHandle.T.isAvailable()) {
            DATA_SHAKING_FACTOR = DataWatcher.Key.Type.INTEGER.createKey(VehicleEntityHandle.T.DATA_SHAKING_FACTOR, -1);
            DATA_SHAKING_DIRECTION = DataWatcher.Key.Type.INTEGER.createKey(VehicleEntityHandle.T.DATA_SHAKING_DIRECTION, -1);
            DATA_SHAKING_DAMAGE = DataWatcher.Key.Type.FLOAT.createKey(VehicleEntityHandle.T.DATA_SHAKING_DAMAGE, -1);
        } else {
            DATA_SHAKING_FACTOR = DataWatcher.Key.Type.INTEGER.createKey(AbstractMinecartHandle.T.DATA_SHAKING_FACTOR, 17);
            DATA_SHAKING_DIRECTION = DataWatcher.Key.Type.INTEGER.createKey(AbstractMinecartHandle.T.DATA_SHAKING_DIRECTION, 18);
            DATA_SHAKING_DAMAGE = DataWatcher.Key.Type.FLOAT.createKey(AbstractMinecartHandle.T.DATA_SHAKING_DAMAGE, 19);
        }
        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            DATA_CUSTOM_DISPLAY_BLOCK = DataWatcher.Key.Type.BLOCK_DATA.createKey(AbstractMinecartHandle.T.DATA_ID_CUSTOM_DISPLAY_BLOCK, -1);
            DATA_BLOCK_TYPE = new DataWatcher.Key.Disabled<Integer>(DataWatcher.Key.Type.INTEGER);
            DATA_BLOCK_VISIBLE = new DataWatcher.Key.Disabled<Boolean>(DataWatcher.Key.Type.BOOLEAN);
        } else {
            DATA_CUSTOM_DISPLAY_BLOCK = new DataWatcher.Key.Disabled<BlockData>(DataWatcher.Key.Type.BLOCK_DATA);
            DATA_BLOCK_TYPE = DataWatcher.Key.Type.INTEGER.createKey(AbstractMinecartHandle.T.DATA_BLOCK_TYPE, 20);
            DATA_BLOCK_VISIBLE = DataWatcher.Key.Type.BOOLEAN.createKey(AbstractMinecartHandle.T.DATA_BLOCK_VISIBLE, 22);
        }
    }

    public static final class AbstractMinecartClass
    extends Template.Class<AbstractMinecartHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_SHAKING_FACTOR = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_SHAKING_DIRECTION = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_SHAKING_DAMAGE = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<BlockData>> DATA_ID_CUSTOM_DISPLAY_BLOCK = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_BLOCK_TYPE = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_BLOCK_OFFSET = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_BLOCK_VISIBLE = new Template.StaticField.Converted();
        public final Template.Method<Float> getDamage = new Template.Method();
        public final Template.Method<Void> setDamage = new Template.Method();
        public final Template.Method.Converted<Void> activateMinecart = new Template.Method.Converted();
        public final Template.Method<Integer> getHurtTime = new Template.Method();
    }
}

