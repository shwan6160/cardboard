/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler_1_12_1;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler_1_19_3;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler_1_8;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public abstract class ItemVariantListHandler
implements LibraryComponent {
    public static final ItemVariantListHandler INSTANCE = ((LibraryComponentSelector)LibraryComponentSelector.forModule(ItemVariantListHandler.class).runFirst(CommonBootstrap::initServer)).addVersionOption(null, "1.12", ItemVariantListHandler_1_8::new).addVersionOption("1.12.1", "1.19.2", ItemVariantListHandler_1_12_1::new).addVersionOption("1.19.3", null, ItemVariantListHandler_1_19_3::new).update();

    @Override
    public void disable() {
    }

    public abstract List<ItemStack> getVariants(Object var1);
}

