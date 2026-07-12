package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.Axis;
import org.bukkit.Color;
import org.bukkit.Effect;
import static org.bukkit.Effect.*;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemType;

public class CraftEffect {
    public static <T> int getDataValue(Effect effect, T data) {
        int datavalue = 0;
        if (effect == Effect.PARTICLES_SCULK_CHARGE ||
            effect == Effect.TRIAL_SPAWNER_DETECT_PLAYER ||
            effect == Effect.BEE_GROWTH ||
            effect == Effect.TURTLE_EGG_PLACEMENT ||
            effect == Effect.SMASH_ATTACK ||
            effect == Effect.TRIAL_SPAWNER_DETECT_PLAYER_OMINOUS ||
            effect == Effect.VILLAGER_PLANT_GROW) {
            datavalue = (Integer) data;
        } else if (effect == Effect.POTION_BREAK || effect == Effect.INSTANT_POTION_BREAK) {
            datavalue = ((Color) data).asRGB();
        } else if (effect == Effect.RECORD_PLAY) {
            Preconditions.checkArgument(data == Material.AIR || ((Material) data).isRecord(), "Invalid record type for Material %s!", data);
            datavalue = Item.getId(CraftItemType.bukkitToMinecraft((Material) data));
        } else if (effect == Effect.SHOOT_WHITE_SMOKE) {
            final BlockFace face = (BlockFace) data;
            Preconditions.checkArgument(face.isCartesian(), face + " isn't cartesian");
            datavalue = org.bukkit.craftbukkit.block.CraftBlock.blockFaceToNotch(face).get3DDataValue();
        } else if (effect == Effect.SMOKE) {
            switch ((BlockFace) data) {
                case DOWN:
                    // SPIGOT-6318: Fallback value for the old directions
                case NORTH_EAST:
                case NORTH_WEST:
                case SOUTH_EAST:
                case SOUTH_WEST:
                case SELF:
                    datavalue = 0;
                    break;
                case UP:
                    datavalue = 1;
                    break;
                case NORTH:
                    datavalue = 2;
                    break;
                case SOUTH:
                    datavalue = 3;
                    break;
                case WEST:
                    datavalue = 4;
                    break;
                case EAST:
                    datavalue = 5;
                    break;
                default:
                    throw new IllegalArgumentException("Bad smoke direction!");
            }
        } else if (effect == Effect.STEP_SOUND || effect == Effect.PARTICLES_AND_SOUND_BRUSH_BLOCK_COMPLETE) {
            if (effect == Effect.STEP_SOUND && data instanceof Material) {
                Preconditions.checkArgument(((Material) data).isBlock(), "Material %s is not a block!", data);
                datavalue = Block.getId(CraftBlockType.bukkitToMinecraft((Material) data).defaultBlockState());
            } else {
                datavalue = Block.getId(((org.bukkit.craftbukkit.block.data.CraftBlockData) data).getState());
            }
        } else if (effect == Effect.COMPOSTER_FILL_ATTEMPT ||
                   effect == Effect.TRIAL_SPAWNER_SPAWN ||
                   effect == Effect.TRIAL_SPAWNER_SPAWN_MOB_AT ||
                   effect == Effect.VAULT_ACTIVATE ||
                   effect == Effect.VAULT_DEACTIVATE ||
                   effect == Effect.TRIAL_SPAWNER_BECOME_OMINOUS ||
                   effect == Effect.TRIAL_SPAWNER_SPAWN_ITEM) {
            datavalue = ((Boolean) data) ? 1 : 0;
        } else if (effect == Effect.BONE_MEAL_USE) {
            datavalue = (Integer) data;
        } else if (effect == Effect.ELECTRIC_SPARK) {
            if (data == null) {
                datavalue = -1;
            } else {
                switch ((Axis) data) {
                    case X:
                        datavalue = 0;
                        break;
                    case Y:
                        datavalue = 1;
                        break;
                    case Z:
                        datavalue = 2;
                        break;
                }
            }
        } else {
            datavalue = 0;
        }
        return datavalue;
    }
}
