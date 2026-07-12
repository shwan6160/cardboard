package org.bukkit.craftbukkit.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class CraftArrow extends CraftAbstractArrow implements Arrow {

    public CraftArrow(CraftServer server, net.minecraft.world.entity.projectile.arrow.Arrow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.projectile.arrow.Arrow getHandle() {
        return (net.minecraft.world.entity.projectile.arrow.Arrow) this.entity;
    }

    @Override
    public String toString() {
        return "CraftTippedArrow";
    }

    @Override
    public boolean addCustomEffect(PotionEffect effect, boolean override) {
    	// TODO
        return true;
    }

    @Override
    public void clearCustomEffects() {
        // TODO
    }

    @Override
    public List<PotionEffect> getCustomEffects() {
        ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
        // TODO
        return builder.build();
    }

    @Override
    public boolean hasCustomEffect(PotionEffectType type) {
        // TODO
        return false;
    }

    @Override
    public boolean hasCustomEffects() {
        return false; // TODO
    }

    @Override
    public boolean removeCustomEffect(PotionEffectType effect) {
        if (!this.hasCustomEffect(effect)) {
            return false;
        }
        // TODO
        return true;
    }

    @Override
    public void setBasePotionData(PotionData data) {
        this.setBasePotionType(data.getType());
    }

    @Override
    public PotionData getBasePotionData() {
        PotionType type = this.getBasePotionType();
        return (type != null) ? new PotionData(type) : null;
    }

    @Override
    public void setBasePotionType(PotionType potionType) {
        this.getHandle().setPotionContents(this.getHandle().getPotionContents().withPotion(org.bukkit.craftbukkit.potion.CraftPotionType.bukkitToMinecraftHolder(potionType)));
    }

    @Override
    public PotionType getBasePotionType() {
        return this.getHandle().getPotionContents().potion().map(org.bukkit.craftbukkit.potion.CraftPotionType::minecraftHolderToBukkit).orElse(null);
    }

    @Override
    public void setColor(Color color) {
    	// TODO
    }

    @Override
    public Color getColor() {
        int color = this.getHandle().getColor(); // Paper
        // if (color == net.minecraft.entity.projectile.ArrowEntity.NO_POTION_COLOR) { // Paper
        //     return null;
        // }
        return Color.fromARGB(color); // Paper
    }
}
