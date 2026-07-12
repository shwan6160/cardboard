/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;

@Template.InstanceType(value="org.bukkit.craftbukkit.util.CraftMagicNumbers")
public abstract class CraftMagicNumbersHandle
extends Template.Handle {
    public static final CraftMagicNumbersClass T = Template.Class.create(CraftMagicNumbersClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftMagicNumbersHandle createHandle(Object handleInstance) {
        return (CraftMagicNumbersHandle)T.createHandle(handleInstance);
    }

    public static Material getMaterialFromBlock(Object nmsBlock) {
        return CraftMagicNumbersHandle.T.getMaterialFromBlock.invoke(nmsBlock);
    }

    public static Material getMaterialFromItem(Object nmsItem) {
        return CraftMagicNumbersHandle.T.getMaterialFromItem.invoke(nmsItem);
    }

    public static Object getItemFromMaterial(Material material) {
        return CraftMagicNumbersHandle.T.getItemFromMaterial.invoker.invoke(null, material);
    }

    public static Object getBlockFromMaterial(Material material) {
        return CraftMagicNumbersHandle.T.getBlockFromMaterial.invoker.invoke(null, material);
    }

    public static int getDataVersion() {
        return (Integer)CraftMagicNumbersHandle.T.getDataVersion.invoker.invoke(null);
    }

    public static boolean isItemMaterial(Material material) {
        return (Boolean)CraftMagicNumbersHandle.T.isItemMaterial.invoker.invoke(null, material);
    }

    public static Object advancementHandleToBukkit(Object advancement_or_holder) {
        return CraftMagicNumbersHandle.T.advancementHandleToBukkit.invoker.invoke(null, advancement_or_holder);
    }

    public static BlockStateHandle getBlockDataFromMaterial(Material material) {
        return BlockHandle.T.getBlockData.invoke(CraftMagicNumbersHandle.getBlockFromMaterial(material));
    }

    public static final class CraftMagicNumbersClass
    extends Template.Class<CraftMagicNumbersHandle> {
        public final Template.StaticMethod.Converted<Material> getMaterialFromBlock = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Material> getMaterialFromItem = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Object> getItemFromMaterial = new Template.StaticMethod();
        public final Template.StaticMethod<Object> getBlockFromMaterial = new Template.StaticMethod();
        public final Template.StaticMethod<Integer> getDataVersion = new Template.StaticMethod();
        public final Template.StaticMethod<Boolean> isItemMaterial = new Template.StaticMethod();
        public final Template.StaticMethod<Object> advancementHandleToBukkit = new Template.StaticMethod();
    }
}

