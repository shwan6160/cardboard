/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

class ItemVariantListHandler_1_19_3
extends ItemVariantListHandler {
    private final HandlerLogic handler = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
    private final Function<Object, List<?>> defaultHandler = this.handler::getVariants;
    private final Map<Class<?>, Function<Object, List<?>>> specialHandlers = new LinkedHashMap();
    private IdentityHashMap<Object, Function<Object, List<?>>> handlersByItem = new IdentityHashMap();

    ItemVariantListHandler_1_19_3() {
    }

    @Override
    public void enable() throws Throwable {
        this.handler.forceInitialization();
        this.registerHandler("InstrumentItem", this.handler::getInstrumentItemVariants);
        this.registerHandler("PotionItem", this.handler::getPotionVariants);
        this.registerHandler("TippedArrowItem", this.handler::getTippedArrowVariants);
        this.handlersByItem.put(null, nmsItem -> new ArrayList(0));
        for (Object nmsItem2 : ItemHandle.getRegistry()) {
            this.handlersByItem.put(nmsItem2, this.findHandler(nmsItem2));
        }
    }

    @Override
    public List<ItemStack> getVariants(Object nmsItem) {
        Function handlerFunc = LogicUtil.synchronizeCopyOnWrite(this, () -> this.handlersByItem, nmsItem, IdentityHashMap::get, (map, key) -> {
            Function<Object, List<?>> handler = this.findHandler(key);
            IdentityHashMap newMap = new IdentityHashMap((Map<Object, Function<Object, List<?>>>)map);
            newMap.put(key, handler);
            this.handlersByItem = newMap;
            return handler;
        });
        return new ConvertingList<ItemStack>((List)handlerFunc.apply(nmsItem), DuplexConversion.itemStack);
    }

    private void registerHandler(String itemTypeName, Function<Object, List<?>> handler) {
        Class<?> type = CommonUtil.getClass("net.minecraft.world.item." + itemTypeName);
        if (type == null) {
            throw new IllegalStateException("Required item type not found: " + itemTypeName);
        }
        this.specialHandlers.put(type, handler);
    }

    private Function<Object, List<?>> findHandler(Object nmsItem) {
        Class<?> type = nmsItem.getClass();
        for (Map.Entry<Class<?>, Function<Object, List<?>>> entry : this.specialHandlers.entrySet()) {
            if (!entry.getKey().isAssignableFrom(type)) continue;
            return entry.getValue();
        }
        return this.defaultHandler;
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.item.ItemStack"), @Template.Import(value="net.minecraft.world.item.InstrumentItem"), @Template.Import(value="net.minecraft.world.item.EnchantedBookItem"), @Template.Import(value="net.minecraft.world.item.PotionItem"), @Template.Import(value="net.minecraft.world.item.TippedArrowItem"), @Template.Import(value="net.minecraft.world.item.Items"), @Template.Import(value="net.minecraft.world.item.enchantment.Enchantment"), @Template.Import(value="net.minecraft.world.item.enchantment.EnchantmentInstance"), @Template.Import(value="net.minecraft.world.item.alchemy.Potions"), @Template.Import(value="net.minecraft.world.item.alchemy.Potion"), @Template.Import(value="net.minecraft.world.item.alchemy.PotionUtil"), @Template.Import(value="net.minecraft.world.item.alchemy.PotionContents"), @Template.Import(value="net.minecraft.core.Holder"), @Template.Import(value="net.minecraft.tags.TagKey"), @Template.Import(value="net.minecraft.tags.InstrumentTags"), @Template.Import(value="net.minecraft.core.Registry"), @Template.Import(value="net.minecraft.core.registries.BuiltInRegistries"), @Template.Import(value="org.bukkit.craftbukkit.CraftRegistry")})
    @Template.InstanceType(value="net.minecraft.world.item.Item")
    public static abstract class HandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static List<ItemStack> getVariants(Item item) {\n    List result = new ArrayList(1);\n    result.add(new ItemStack(item));\n    return result;\n}")
        public abstract List<?> getVariants(Object var1);

        @Template.Generated(value="public static List<ItemStack> getVariants(InstrumentItem item) {\n    List result = new ArrayList();\n\n#if version >= 1.21.5\n    TagKey instruments = InstrumentTags.GOAT_HORNS;\n#else\n               #require InstrumentItem private final TagKey<Instrument> instruments;\n    TagKey instruments = item#instruments;\n#endif\n\n           #if version >= 1.21.2\n    Registry registry = CraftRegistry.getMinecraftRegistry(net.minecraft.core.registries.Registries.INSTRUMENT);\n#else\n               Registry registry = BuiltInRegistries.INSTRUMENT;\n#endif\n               for (java.util.Iterator iter = registry.getTagOrEmpty(instruments).iterator(); iter.hasNext();) {\n        Holder holder = (Holder) iter.next();\n        result.add(InstrumentItem.create(Items.GOAT_HORN, holder));\n    }\n               return result;\n}")
        public abstract List<?> getInstrumentItemVariants(Object var1);

        @Template.Generated(value="public static List<ItemStack> getVariants(PotionItem item) {\n    List result = new ArrayList();\n#if version >= 1.20.5\n    java.util.Iterator iterator = BuiltInRegistries.POTION.asHolderIdMap().iterator();\n    while (iterator.hasNext()) {\n        net.minecraft.core.Holder potionregistryHolder = (net.minecraft.core.Holder) iterator.next();\n        Potion potionregistry = (Potion) potionregistryHolder.value();\n        if (!potionregistry.getEffects().isEmpty()) {\n            result.add(PotionContents.createItemStack(item, potionregistryHolder));\n        }\n               }\n#else\n               java.util.Iterator iterator = BuiltInRegistries.POTION.iterator();\n    while (iterator.hasNext()) {\n        Potion potionregistry = (Potion) iterator.next();\n        if (potionregistry != Potions.EMPTY) {\n            result.add(PotionUtil.setPotion(new ItemStack(item), potionregistry));\n        }\n               }\n#endif\n               return result;\n}")
        public abstract List<?> getPotionVariants(Object var1);

        @Template.Generated(value="public static List<ItemStack> getVariants(TippedArrowItem item) {\n    List result = new ArrayList();\n#if version >= 1.20.5\n    java.util.Iterator iterator = BuiltInRegistries.POTION.asHolderIdMap().iterator();\n    while (iterator.hasNext()) {\n        net.minecraft.core.Holder potionregistryHolder = (net.minecraft.core.Holder) iterator.next();\n        Potion potionregistry = (Potion) potionregistryHolder.value();\n        if (!potionregistry.getEffects().isEmpty()) {\n            result.add(PotionContents.createItemStack(item, potionregistryHolder));\n        }\n               }\n#else\n               java.util.Iterator iterator = BuiltInRegistries.POTION.iterator();\n    while (iterator.hasNext()) {\n        Potion potionregistry = (Potion) iterator.next();\n        if (!potionregistry.getEffects().isEmpty()) {\n            result.add(PotionUtil.setPotion(new ItemStack(item), potionregistry));\n        }\n               }\n#endif\n               return result;\n}")
        public abstract List<?> getTippedArrowVariants(Object var1);
    }
}

