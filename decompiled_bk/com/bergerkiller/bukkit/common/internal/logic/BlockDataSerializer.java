/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer_1_13_to_1_18_2;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer_1_19;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer_1_8_to_1_12_2;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

public abstract class BlockDataSerializer
implements LazyInitializedObject,
LibraryComponent {
    public static final BlockDataSerializer INSTANCE = ((LibraryComponentSelector)LibraryComponentSelector.forModule(BlockDataSerializer.class).runFirst(CommonBootstrap::initServer)).addVersionOption(null, "1.12.2", BlockDataSerializer_1_8_to_1_12_2::new).addVersionOption("1.13", "1.18.2", BlockDataSerializer_1_13_to_1_18_2::new).addVersionOption("1.19", null, BlockDataSerializer_1_19::new).update();

    public abstract String serialize(BlockData var1);

    public abstract BlockData deserialize(String var1);
}

