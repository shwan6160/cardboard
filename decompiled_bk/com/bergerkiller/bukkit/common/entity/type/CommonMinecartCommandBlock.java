/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.CommandMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlockHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartCommandBlock
extends CommonMinecart<CommandMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"COMMAND_BLOCK_MINECART") : Material.getMaterial((String)"COMMAND_MINECART");
    private static final Material _COMMAND_BLOCK_TYPE = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"COMMAND_BLOCK") : Material.getMaterial((String)"COMMAND");
    public final DataWatcher.EntityItem<String> metaCommand = this.getDataItem(MinecartCommandBlockHandle.DATA_COMMAND);
    public final DataWatcher.EntityItem<ChatText> metaPreviousOutput = this.getDataItem(MinecartCommandBlockHandle.DATA_PREVIOUS_OUTPUT);

    public CommonMinecartCommandBlock(CommandMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(_COMMAND_BLOCK_TYPE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }
}

