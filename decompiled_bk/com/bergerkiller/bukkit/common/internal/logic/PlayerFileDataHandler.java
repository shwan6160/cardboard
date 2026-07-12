/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler_1_16_to_1_21_5;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler_1_21_6;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler_1_21_9;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler_1_8_to_1_15_2;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import org.bukkit.entity.HumanEntity;

public abstract class PlayerFileDataHandler
implements LazyInitializedObject,
LibraryComponent {
    public static final PlayerFileDataHandler INSTANCE = LibraryComponentSelector.forModule(PlayerFileDataHandler.class).addVersionOption(null, "1.15.2", PlayerFileDataHandler_1_8_to_1_15_2::new).addVersionOption("1.16", "1.21.5", PlayerFileDataHandler_1_16_to_1_21_5::new).addVersionOption("1.21.6", "1.21.8", PlayerFileDataHandler_1_21_6::new).addVersionOption("1.21.9", null, PlayerFileDataHandler_1_21_9::new).update();

    public abstract PlayerDataController get();

    public abstract Hook hook(PlayerDataController var1);

    public abstract Hook mock(PlayerDataController var1);

    public abstract void unhook(Hook var1, PlayerDataController var2);

    public abstract CommonTagCompound migratePlayerData(CommonTagCompound var1);

    public static interface Hook {
        public CommonTagCompound base_load(HumanEntity var1);

        public CommonTagCompound base_load_offline(String var1, String var2);

        public void base_save(HumanEntity var1);
    }
}

