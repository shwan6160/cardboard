package org.cardboardpowered.mixin.world.level.storage.loot.functions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.cardboardpowered.bridge.world.level.storage.loot.parameters.LootContextParamsBridge;

@Mixin(EnchantedCountIncreaseFunction.class)
public class EnchantedCountIncreaseFunctionMixin {

    @Shadow
    public int limit;

    @Shadow
    private net.minecraft.world.level.storage.loot.providers.number.NumberProvider count;

    @Shadow
    private net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment;

    @Overwrite
    public ItemStack run(ItemStack itemstack, LootContext loottableinfo) {
        Entity entity = loottableinfo.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if (entity instanceof LivingEntity) {
            int i = EnchantmentHelper.getEnchantmentLevel(this.enchantment, (LivingEntity) entity);
            if (loottableinfo.hasParameter(LootContextParamsBridge.LOOTING_MOD)) {
                i = loottableinfo.getOptionalParameter(LootContextParamsBridge.LOOTING_MOD);
            }
            if (i <= 0) return itemstack;
            float f = (float) i * this.count.getFloat(loottableinfo);
            itemstack.grow(Math.round(f));
            if ((this.limit > 0) && itemstack.getCount() > this.limit) itemstack.setCount(this.limit);
        }
        return itemstack;
    }

}