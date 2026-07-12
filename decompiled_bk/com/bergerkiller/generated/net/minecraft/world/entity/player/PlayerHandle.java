/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.world.entity.player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.world.entity.player.Player")
public abstract class PlayerHandle
extends LivingEntityHandle {
    public static final PlayerClass T = Template.Class.create(PlayerClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_PLAYER_MODE_CUSTOMISATION = DataWatcher.Key.Type.BYTE.createKey(PlayerHandle.T.DATA_PLAYER_MODE_CUSTOMISATION, -1);
    public static final int DATA_CUSTOMISATION_FLAG_CAPE = 1;
    public static final int DATA_CUSTOMISATION_FLAG_JACKET = 2;
    public static final int DATA_CUSTOMISATION_FLAG_LEFT_SLEEVE = 4;
    public static final int DATA_CUSTOMISATION_FLAG_RIGHT_SLEEVE = 8;
    public static final int DATA_CUSTOMISATION_FLAG_LEFT_PANTS_LEG = 16;
    public static final int DATA_CUSTOMISATION_FLAG_RIGHT_PANTS_LEG = 32;
    public static final int DATA_CUSTOMISATION_FLAG_HAT = 64;
    public static final byte DATA_CUSTOMISATION_FLAG_ALL = 127;

    public static PlayerHandle createHandle(Object handleInstance) {
        return (PlayerHandle)T.createHandle(handleInstance);
    }

    public abstract void attack(Entity var1);

    public abstract Object getInventoryRaw();

    public abstract void setInventoryRaw(Object var1);

    public abstract Object getEnderChestRaw();

    public abstract void setEnderChestRaw(Object var1);

    public abstract AbstractContainerMenuHandle getActiveContainer();

    public abstract void setActiveContainer(AbstractContainerMenuHandle var1);

    public abstract Object getFoodDataRaw();

    public abstract void setFoodDataRaw(Object var1);

    public abstract int getSleepTicks();

    public abstract void setSleepTicks(int var1);

    public abstract PlayerAbilities getAbilities();

    public abstract void setAbilities(PlayerAbilities var1);

    public abstract int getExpLevel();

    public abstract void setExpLevel(int var1);

    public abstract int getExpTotal();

    public abstract void setExpTotal(int var1);

    public abstract float getExp();

    public abstract void setExp(float var1);

    public abstract GameProfileHandle getGameProfile();

    public abstract void setGameProfile(GameProfileHandle var1);

    public static final class PlayerClass
    extends Template.Class<PlayerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_PLAYER_MODE_CUSTOMISATION = new Template.StaticField.Converted();
        public final Template.Field.Converted<Object> inventoryRaw = new Template.Field.Converted();
        public final Template.Field.Converted<Object> enderChestRaw = new Template.Field.Converted();
        public final Template.Field.Converted<AbstractContainerMenuHandle> activeContainer = new Template.Field.Converted();
        public final Template.Field.Converted<Object> foodDataRaw = new Template.Field.Converted();
        public final Template.Field.Integer sleepTicks = new Template.Field.Integer();
        public final Template.Field.Converted<PlayerAbilities> abilities = new Template.Field.Converted();
        public final Template.Field.Integer expLevel = new Template.Field.Integer();
        public final Template.Field.Integer expTotal = new Template.Field.Integer();
        public final Template.Field.Float exp = new Template.Field.Float();
        public final Template.Field.Converted<GameProfileHandle> gameProfile = new Template.Field.Converted();
        public final Template.Method.Converted<Void> attack = new Template.Method.Converted();
    }
}

