/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.particles.ParticleTypeHandle;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ParticleType<T>
extends BasicWrapper<ParticleTypeHandle> {
    public static final ParticleType<?> UNKNOWN = new ParticleTypeUnknown();
    private static Map<Object, ParticleType<?>> byNMSHandle;
    public static final ParticleType<Void> ANGRY_VILLAGER;
    public static final ParticleType<Void> HAPPY_VILLAGER;
    public static final ParticleType<Void> FLAME;
    public static final ParticleType<Void> HEART;
    public static final ParticleType<Void> WITCH;
    public static final ParticleType<Void> NOTE;
    public static final ParticleType<Void> SMOKE;
    public static final ParticleType<Void> SPLASH;
    public static final ParticleType<Void> LAVA;
    public static final ParticleType<Void> BUBBLE;
    public static final ParticleType<Void> CLOUD;
    public static final ParticleType<Void> CRIT;
    public static final ParticleType<Void> DRIPPING_LAVA;
    public static final ParticleType<Void> DRIPPING_WATER;
    public static final ParticleType<Void> FIREWORK;
    public static final ParticleType<Void> LARGE_SMOKE;
    public static final ParticleType<Void> PORTAL;
    public static final ParticleType<Void> ENCHANT;
    public static final ParticleType<Void> ITEM_SLIME;
    public static final ParticleType<Void> ITEM_SNOWBALL;
    public static final ParticleType<ColorOptions> ENTITY_EFFECT;
    public static final ParticleType<ColorOptions> AMBIENT_ENTITY_EFFECT;
    public static final ParticleType<DustOptions> DUST;
    public static final ParticleType<Void> POOF;
    public static final ParticleType<Void> EXPLOSION;
    public static final ParticleType<Void> EXPLOSION_EMITTER;
    public static final ParticleType<Void> INSTANT_EFFECT;
    public static final ParticleType<Void> FISHING;
    public static final ParticleType<ItemStack> ITEM;
    public static final ParticleType<BlockData> BLOCK;
    public static final ParticleType<Void> EFFECT;
    public static final ParticleType<Void> ENCHANTED_HIT;
    public static final ParticleType<Void> RAIN;
    public static final ParticleType<Void> DRAGON_BREATH;
    public static final ParticleType<Void> END_ROD;
    public static final ParticleType<Void> DAMAGE_INDICATOR;
    public static final ParticleType<Void> SWEEP_ATTACK;
    public static final ParticleType<BlockData> FALLING_DUST;
    public static final ParticleType<Void> SPIT;
    public static final ParticleType<Void> TOTEM_OF_UNDYING;
    public static final ParticleType<Void> FOOTSTEP;
    public static final ParticleType<Void> MOB_APPEARANCE;
    public static final ParticleType<Void> DEPTH_SUSPEND;
    public static final ParticleType<Void> SUSPENDED;
    public static final ParticleType<Void> ITEM_TAKE;
    public static final ParticleType<Void> TOWN_AURA;
    public static final ParticleType<Void> SNOW_SHOVEL;
    public static final ParticleType<Void> UNDERWATER;
    public static final ParticleType<Void> MYCELIUM;
    public static final ParticleType<Void> SQUID_INK;
    public static final ParticleType<Void> ELDER_GUARDIAN;
    public static final ParticleType<Void> BUBBLE_POP;
    public static final ParticleType<Void> CURRENT_DOWN;
    public static final ParticleType<Void> BUBBLE_COLUMN_UP;
    public static final ParticleType<Void> NAUTILUS;
    public static final ParticleType<Void> DOLPHIN;
    public static final ParticleType<Void> FALLING_LAVA;
    public static final ParticleType<Void> LANDING_LAVA;
    public static final ParticleType<Void> FALLING_WATER;
    public static final ParticleType<Void> FLASH;
    public static final ParticleType<Void> COMPOSTER;
    public static final ParticleType<Void> SNEEZE;
    public static final ParticleType<Void> CAMPFIRE_COSY_SMOKE;
    public static final ParticleType<Void> CAMPFIRE_SIGNAL_SMOKE;
    public static final ParticleType<Void> DRIPPING_HONEY;
    public static final ParticleType<Void> FALLING_HONEY;
    public static final ParticleType<Void> LANDING_HONEY;
    public static final ParticleType<Void> FALLING_NECTAR;
    public static final ParticleType<Void> SOUL_FIRE_FLAME;
    public static final ParticleType<Void> SOUL;
    public static final ParticleType<Void> ASH;
    public static final ParticleType<Void> CRIMSON_SPORE;
    public static final ParticleType<Void> WARPED_SPORE;
    public static final ParticleType<Void> DRIPPING_OBSIDIAN_TEAR;
    public static final ParticleType<Void> FALLING_OBSIDIAN_TEAR;
    public static final ParticleType<Void> LANDING_OBSIDIAN_TEAR;
    public static final ParticleType<Void> REVERSE_PORTAL;
    public static final ParticleType<Void> WHITE_ASH;
    public static final ParticleType<VibrationOptions> VIBRATION;
    public static final ParticleType<DustColorTransitionOptions> DUST_COLOR_TRANSITION;
    public static final ParticleType<Void> FALLING_SPORE_BLOSSOM;
    public static final ParticleType<Void> SPORE_BLOSSOM_AIR;
    public static final ParticleType<Void> SMALL_FLAME;
    public static final ParticleType<Void> SNOWFLAKE;
    public static final ParticleType<Void> DRIPPING_DRIPSTONE_LAVA;
    public static final ParticleType<Void> FALLING_DRIPSTONE_LAVA;
    public static final ParticleType<Void> DRIPPING_DRIPSTONE_WATER;
    public static final ParticleType<Void> FALLING_DRIPSTONE_WATER;
    public static final ParticleType<Void> GLOW_SQUID_INK;
    public static final ParticleType<Void> GLOW;
    public static final ParticleType<Void> SCRAPE;
    public static final ParticleType<Void> WAX_ON;
    public static final ParticleType<Void> WAX_OFF;
    public static final ParticleType<Void> ELECTRIC_SPARK;
    public static final ParticleType<Void> LIGHT;
    public static final ParticleType<Void> BARRIER;
    public static final ParticleType<BlockData> BLOCK_MARKER;
    public static final ParticleType<Void> SONIC_BOOM;
    public static final ParticleType<Void> SCULK_SOUL;
    public static final ParticleType<SculkChargeOptions> SCULK_CHARGE;
    public static final ParticleType<Void> SCULK_CHARGE_POP;
    public static final ParticleType<ShriekOptions> SHRIEK;

    protected ParticleType() {
        this.setHandle((ParticleTypeHandle)ParticleTypeHandle.T.createHandle(null, true));
    }

    private ParticleType(Object nmsHandle) {
        this.setHandle(ParticleTypeHandle.createHandle(nmsHandle));
    }

    public String getName() {
        return ((ParticleTypeHandle)this.handle).getName();
    }

    public boolean hasOptions() {
        return ((ParticleTypeHandle)this.handle).hasOptions();
    }

    public boolean exists() {
        return true;
    }

    @Override
    public String toString() {
        return "ParticleType{name=" + this.getName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    public static ParticleType<?> byNMSParticleHandle(Object nmsParticleHandle) {
        if (nmsParticleHandle == null) {
            return UNKNOWN;
        }
        return LogicUtil.synchronizeCopyOnWrite(ParticleType.class, () -> byNMSHandle, nmsParticleHandle, Map::get, (map, key) -> {
            ParticleType value = new ParticleType(key);
            IdentityHashMap copy = new IdentityHashMap((Map<Object, ParticleType<?>>)map);
            copy.put(key, value);
            byNMSHandle = copy;
            return value;
        });
    }

    public static <T> ParticleType<T> byName(String name) {
        return ParticleType.byNMSParticleHandle(ParticleTypeHandle.byName(name));
    }

    private static <T> ParticleType<T> byName_1_13(String name_1_12, String name_1_13) {
        return ParticleType.byName(CommonCapabilities.PARTICLE_OPTIONS ? name_1_13 : name_1_12);
    }

    public static Collection<ParticleType<?>> values() {
        return byNMSHandle.values();
    }

    static {
        IdentityHashMap byNMSHandleInit = new IdentityHashMap();
        for (Object nmsHandle : ParticleTypeHandle.values()) {
            byNMSHandleInit.put(nmsHandle, new ParticleType(nmsHandle));
        }
        byNMSHandle = byNMSHandleInit;
        ANGRY_VILLAGER = ParticleType.byName_1_13("angryVillager", "angry_villager");
        HAPPY_VILLAGER = ParticleType.byName_1_13("happyVillager", "happy_villager");
        FLAME = ParticleType.byName("flame");
        HEART = ParticleType.byName("heart");
        WITCH = ParticleType.byName_1_13("witchMagic", "witch");
        NOTE = ParticleType.byName("note");
        SMOKE = ParticleType.byName("smoke");
        SPLASH = ParticleType.byName("splash");
        LAVA = ParticleType.byName("lava");
        BUBBLE = ParticleType.byName("bubble");
        CLOUD = ParticleType.byName("cloud");
        CRIT = ParticleType.byName("crit");
        DRIPPING_LAVA = ParticleType.byName_1_13("dripLava", "dripping_lava");
        DRIPPING_WATER = ParticleType.byName_1_13("dripWater", "dripping_water");
        FIREWORK = ParticleType.byName_1_13("fireworksSpark", "firework");
        LARGE_SMOKE = ParticleType.byName_1_13("largesmoke", "large_smoke");
        PORTAL = ParticleType.byName("portal");
        ENCHANT = ParticleType.byName_1_13("enchantmenttable", "enchant");
        ITEM_SLIME = ParticleType.byName_1_13("slime", "item_slime");
        ITEM_SNOWBALL = ParticleType.byName_1_13("snowballpoof", "item_snowball");
        ENTITY_EFFECT = ParticleType.byName_1_13("mobSpell", "entity_effect");
        AMBIENT_ENTITY_EFFECT = CommonBootstrap.evaluateMCVersion(">=", "1.20.5") ? ENTITY_EFFECT : ParticleType.byName_1_13("mobSpellAmbient", "ambient_entity_effect");
        DUST = ParticleType.byName_1_13("reddust", "dust");
        POOF = ParticleType.byName_1_13("explode", "poof");
        EXPLOSION = ParticleType.byName_1_13("largeexplode", "explosion");
        EXPLOSION_EMITTER = ParticleType.byName_1_13("hugeexplosion", "explosion_emitter");
        INSTANT_EFFECT = ParticleType.byName_1_13("instantSpell", "instant_effect");
        FISHING = ParticleType.byName_1_13("wake", "fishing");
        ITEM = ParticleType.byName_1_13("iconcrack", "item");
        BLOCK = ParticleType.byName_1_13("blockcrack", "block");
        EFFECT = ParticleType.byName_1_13("spell", "effect");
        ENCHANTED_HIT = ParticleType.byName_1_13("magicCrit", "enchanted_hit");
        RAIN = ParticleType.byName_1_13("droplet", "rain");
        DRAGON_BREATH = ParticleType.byName_1_13("dragonbreath", "dragon_breath");
        END_ROD = ParticleType.byName_1_13("endRod", "end_rod");
        DAMAGE_INDICATOR = ParticleType.byName_1_13("damageIndicator", "damage_indicator");
        SWEEP_ATTACK = ParticleType.byName_1_13("sweepAttack", "sweep_attack");
        FALLING_DUST = ParticleType.byName_1_13("fallingdust", "falling_dust");
        SPIT = ParticleType.byName("spit");
        TOTEM_OF_UNDYING = ParticleType.byName_1_13("totem", "totem_of_undying");
        FOOTSTEP = ParticleType.byName("footstep");
        MOB_APPEARANCE = ParticleType.byName("mobappearance");
        DEPTH_SUSPEND = ParticleType.byName("depthsuspend");
        SUSPENDED = ParticleType.byName("suspended");
        ITEM_TAKE = ParticleType.byName("take");
        TOWN_AURA = ParticleType.byName("townaura");
        SNOW_SHOVEL = ParticleType.byName("snowshovel");
        UNDERWATER = ParticleType.byName("underwater");
        MYCELIUM = ParticleType.byName("mycelium");
        SQUID_INK = ParticleType.byName("squid_ink");
        ELDER_GUARDIAN = ParticleType.byName("elder_guardian");
        BUBBLE_POP = ParticleType.byName("bubble_pop");
        CURRENT_DOWN = ParticleType.byName("current_down");
        BUBBLE_COLUMN_UP = ParticleType.byName("bubble_column_up");
        NAUTILUS = ParticleType.byName("nautilus");
        DOLPHIN = ParticleType.byName("dolphin");
        FALLING_LAVA = ParticleType.byName("falling_lava");
        LANDING_LAVA = ParticleType.byName("landing_lava");
        FALLING_WATER = ParticleType.byName("falling_water");
        FLASH = ParticleType.byName("flash");
        COMPOSTER = ParticleType.byName("composter");
        SNEEZE = ParticleType.byName("sneeze");
        CAMPFIRE_COSY_SMOKE = ParticleType.byName("campfire_cosy_smoke");
        CAMPFIRE_SIGNAL_SMOKE = ParticleType.byName("campfire_signal_smoke");
        DRIPPING_HONEY = ParticleType.byName("dripping_honey");
        FALLING_HONEY = ParticleType.byName("falling_honey");
        LANDING_HONEY = ParticleType.byName("landing_honey");
        FALLING_NECTAR = ParticleType.byName("falling_nectar");
        SOUL_FIRE_FLAME = ParticleType.byName("soul_fire_flame");
        SOUL = ParticleType.byName("soul");
        ASH = ParticleType.byName("ash");
        CRIMSON_SPORE = ParticleType.byName("crimson_spore");
        WARPED_SPORE = ParticleType.byName("warped_spore");
        DRIPPING_OBSIDIAN_TEAR = ParticleType.byName("dripping_obsidian_tear");
        FALLING_OBSIDIAN_TEAR = ParticleType.byName("falling_obsidian_tear");
        LANDING_OBSIDIAN_TEAR = ParticleType.byName("landing_obsidian_tear");
        REVERSE_PORTAL = ParticleType.byName("reverse_portal");
        WHITE_ASH = ParticleType.byName("white_ash");
        VIBRATION = ParticleType.byName("vibration");
        DUST_COLOR_TRANSITION = ParticleType.byName("dust_color_transition");
        FALLING_SPORE_BLOSSOM = ParticleType.byName("falling_spore_blossom");
        SPORE_BLOSSOM_AIR = ParticleType.byName("spore_blossom_air");
        SMALL_FLAME = ParticleType.byName("small_flame");
        SNOWFLAKE = ParticleType.byName("snowflake");
        DRIPPING_DRIPSTONE_LAVA = ParticleType.byName("dripping_dripstone_lava");
        FALLING_DRIPSTONE_LAVA = ParticleType.byName("falling_dripstone_lava");
        DRIPPING_DRIPSTONE_WATER = ParticleType.byName("dripping_dripstone_water");
        FALLING_DRIPSTONE_WATER = ParticleType.byName("falling_dripstone_water");
        GLOW_SQUID_INK = ParticleType.byName("glow_squid_ink");
        GLOW = ParticleType.byName("glow");
        SCRAPE = ParticleType.byName("scrape");
        WAX_ON = ParticleType.byName("wax_on");
        WAX_OFF = ParticleType.byName("wax_off");
        ELECTRIC_SPARK = ParticleType.byName("electric_spark");
        LIGHT = ParticleType.byName("light");
        BARRIER = ParticleType.byName("barrier");
        BLOCK_MARKER = ParticleType.byName("block_marker");
        SONIC_BOOM = ParticleType.byName("sonic_boom");
        SCULK_SOUL = ParticleType.byName("sculk_soul");
        SCULK_CHARGE = ParticleType.byName("sculk_charge");
        SCULK_CHARGE_POP = ParticleType.byName("sculk_charge_pop");
        SHRIEK = ParticleType.byName("shriek");
    }

    private static final class ParticleTypeUnknown
    extends ParticleType<Object> {
        private ParticleTypeUnknown() {
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public boolean hasOptions() {
            return false;
        }

        @Override
        public String toString() {
            return "ParticleType{UNKNOWN}";
        }
    }

    public static final class ShriekOptions {
        public int delay = 0;

        public static ShriekOptions create(int delay) {
            ShriekOptions opt = new ShriekOptions();
            opt.delay = delay;
            return opt;
        }
    }

    public static final class VibrationOptions {
        public BlockPositionOption origin;
        public PositionOption destination;
        public int arrivalInTicks;

        public static VibrationOptions create(BlockPositionOption origin, PositionOption destination, int arrivalInTicks) {
            VibrationOptions opt = new VibrationOptions();
            opt.origin = origin;
            opt.destination = destination;
            opt.arrivalInTicks = arrivalInTicks;
            return opt;
        }
    }

    public static class EntityByUUIDPositionOption
    implements PositionOption {
        public UUID entityUUID;
        public float yOffset = 0.0f;

        public static EntityByUUIDPositionOption create(UUID entityUUID, float yOffset) {
            EntityByUUIDPositionOption opt = new EntityByUUIDPositionOption();
            opt.entityUUID = entityUUID;
            opt.yOffset = yOffset;
            return opt;
        }
    }

    public static class EntityByIdPositionOption
    implements PositionOption {
        public int entityId;
        public float yOffset = 0.0f;

        public static EntityByIdPositionOption create(int entityId, float yOffset) {
            EntityByIdPositionOption opt = new EntityByIdPositionOption();
            opt.entityId = entityId;
            opt.yOffset = yOffset;
            return opt;
        }
    }

    public static class BlockPositionOption
    implements PositionOption {
        public int x = 0;
        public int y = 0;
        public int z = 0;

        public static BlockPositionOption create(Block block) {
            return BlockPositionOption.create(block.getX(), block.getY(), block.getZ());
        }

        public static BlockPositionOption create(IntVector3 blockCoordinates) {
            return BlockPositionOption.create(blockCoordinates.x, blockCoordinates.y, blockCoordinates.z);
        }

        public static BlockPositionOption create(int x, int y, int z) {
            BlockPositionOption opt = new BlockPositionOption();
            opt.x = x;
            opt.y = y;
            opt.z = z;
            return opt;
        }
    }

    public static interface PositionOption {
    }

    public static final class ColorOptions {
        public Color color = Color.WHITE;

        public static ColorOptions create(int r, int g, int b) {
            return ColorOptions.create(Color.fromRGB((int)r, (int)g, (int)b));
        }

        public static ColorOptions create(Color color) {
            ColorOptions opt = new ColorOptions();
            opt.color = color;
            return opt;
        }
    }

    public static final class SculkChargeOptions {
        public float roll = 0.0f;

        public static SculkChargeOptions create(float roll) {
            SculkChargeOptions opt = new SculkChargeOptions();
            opt.roll = roll;
            return opt;
        }
    }

    public static final class DustColorTransitionOptions
    extends DustOptions {
        public Color endColor = Color.WHITE;

        public static DustColorTransitionOptions create(Color startColor, Color endColor, float scale) {
            DustColorTransitionOptions opt = new DustColorTransitionOptions();
            opt.color = startColor;
            opt.endColor = endColor;
            opt.scale = scale;
            return opt;
        }
    }

    public static class DustOptions {
        public Color color = Color.WHITE;
        public float scale = 1.0f;

        public static DustOptions create(int red, int green, int blue, float scale) {
            return DustOptions.create(Color.fromRGB((int)red, (int)green, (int)blue), scale);
        }

        public static DustOptions create(Color color, float scale) {
            DustOptions opt = new DustOptions();
            opt.color = color;
            opt.scale = scale;
            return opt;
        }
    }
}

