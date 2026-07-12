/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.List;
import org.bukkit.inventory.ItemStack;

class ItemVariantListHandler_1_12_1
extends ItemVariantListHandler {
    private final HandlerLogic handler = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
    private Converter<Object, List<ItemStack>> converter;

    ItemVariantListHandler_1_12_1() {
    }

    @Override
    public void enable() throws Throwable {
        this.converter = (Converter)LogicUtil.unsafeCast(Conversion.find(TypeDeclaration.parse("net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack>"), TypeDeclaration.createGeneric(List.class, ItemStack.class)));
        if (this.converter == null) {
            throw new IllegalStateException("Converter from NonNullList<ItemStack> to List<bukkit.ItemStack> not found!");
        }
        this.handler.forceInitialization();
    }

    @Override
    public List<ItemStack> getVariants(Object nmsItem) {
        return this.converter.convertInput(this.handler.getVariants(nmsItem));
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.item.ItemStack"), @Template.Import(value="net.minecraft.world.item.CreativeModeTab"), @Template.Import(value="net.minecraft.core.NonNullList")})
    @Template.InstanceType(value="net.minecraft.world.item.Item")
    public static abstract class HandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static NonNullList<ItemStack> getVariants(Item item) {\n#if version >= 1.18\n    NonNullList result = NonNullList.create();\n#else\n               NonNullList result = NonNullList.a();\n#endif\n\n           #if version >= 1.18\n    item.fillItemCategory(CreativeModeTab.TAB_SEARCH, result);\n#elseif version >= 1.17\n    item.a(CreativeModeTab.TAB_SEARCH, result);\n#else\n               item.a(CreativeModeTab.g, result);\n#endif\n\n               return result;\n}")
        public abstract Object getVariants(Object var1);
    }
}

