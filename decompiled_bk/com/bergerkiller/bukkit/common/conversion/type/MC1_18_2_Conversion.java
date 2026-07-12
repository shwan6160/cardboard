/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.WorldInitEvent
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class MC1_18_2_Conversion {
    private static WeakHashMap<Object, Object> holdersByDimensionType = new WeakHashMap();
    private static HolderLogic handler;

    public static void init() {
        handler = Template.Class.create(HolderLogic.class);
        handler.forceInitialization();
    }

    public static LibraryComponent initComponent(final CommonPlugin plugin) {
        return new LibraryComponent(){

            @Override
            public void enable() throws Throwable {
                for (World world : Bukkit.getWorlds()) {
                    MC1_18_2_Conversion.track(world);
                }
                plugin.register(new Listener(){

                    @EventHandler(priority=EventPriority.LOWEST)
                    public void onWorldInit(WorldInitEvent event) {
                        MC1_18_2_Conversion.track(event.getWorld());
                    }
                });
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void disable() throws Throwable {
                WeakHashMap weakHashMap = holdersByDimensionType;
                synchronized (weakHashMap) {
                    holdersByDimensionType.clear();
                }
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void track(World world) {
        WeakHashMap<Object, Object> weakHashMap = holdersByDimensionType;
        synchronized (weakHashMap) {
            holdersByDimensionType.put(handler.getDimensionTypeOfWorld(world), handler.getHolderOfWorld(world));
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType>", output="net.minecraft.world.level.dimension.DimensionType")
    public static Object fromHolderToDimensionType(Object holder) {
        return handler.getValue(holder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @ConverterMethod(input="net.minecraft.world.level.dimension.DimensionType", output="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType>")
    public static Object fromDimensionTypeToHolder(Object dimensionManager) {
        WeakHashMap<Object, Object> weakHashMap = holdersByDimensionType;
        synchronized (weakHashMap) {
            Object holder = holdersByDimensionType.get(dimensionManager);
            if (holder == null) {
                throw new IllegalArgumentException("Unknown or unregistered dimension type");
            }
            return holder;
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent>")
    public static ResourceKey<SoundEffect> soundEffectHolderToResourceKey(Object nmsHolderHandle) {
        return ResourceKey.fromResourceKeyHandle(handler.getResourceKey(nmsHolderHandle));
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent>")
    public static Object soundEffectHolderFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        return SoundEventHandle.T.rawSoundEffectResourceKeyToHolder.invoke(soundKey.getRawHandle());
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>")
    public static Holder<MobEffectHandle> wrapMobEffectHolder(Object nmsHolder) {
        return Holder.fromHandle(nmsHolder, MobEffectHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>")
    public static Object unwrapMobEffectHolder(Holder<MobEffectHandle> holder) {
        return holder.toRawHolder();
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>")
    public static Holder<AttributeHandle> wrapAttributeHolder(Object nmsHolder) {
        return Holder.fromHandle(nmsHolder, AttributeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>")
    public static Object unwrapAttributeHolder(Holder<AttributeHandle> holder) {
        return holder.toRawHolder();
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="org.bukkit.craftbukkit.CraftWorld"), @Template.Import(value="net.minecraft.world.level.dimension.DimensionType")})
    @Template.InstanceType(value="net.minecraft.core.Holder")
    public static abstract class HolderLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Object getValue(Holder holder) {\n    return holder.value();\n}")
        public abstract Object getValue(Object var1);

        @Template.Generated(value="public static Object getResourceKey(Holder holder) {\n    return holder.unwrapKey().orElse(null);\n}")
        public abstract Object getResourceKey(Object var1);

        @Template.Generated(value="public static Object getDimensionType(CraftWorld world) {\n    return world.getHandle().dimensionType();\n}")
        public abstract Object getDimensionTypeOfWorld(World var1);

        @Template.Generated(value="public static Object getHolder(CraftWorld world) {\n    return ((net.minecraft.world.level.Level) world.getHandle()).dimensionTypeRegistration();\n}")
        public abstract Object getHolderOfWorld(World var1);
    }
}

