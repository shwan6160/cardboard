/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock;
import com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject;
import com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class MC1_8_8_Conversion {
    @ConverterMethod
    public static int dataWatcherObjectToId(DataWatcherObject<?> dataWatcherObject) {
        return dataWatcherObject.getId();
    }

    @ConverterMethod
    public static DataPaletteBlock createProxyDataPaletteBlock(char[] data) {
        return new DataPaletteBlock(data);
    }

    @ConverterMethod
    public static Holder<MobEffectHandle> createMobEffectListHolderFromId(Integer id) {
        return Holder.directWrap(((Template.StaticMethod)MobEffectHandle.T.fromId.raw).invoker.invoke(null, id), MobEffectHandle::createHandle);
    }

    @ConverterMethod
    public static Integer getMobEffectListIdFromHolder(Holder<MobEffectHandle> holder) {
        return MC1_8_8_Conversion.getMobEffectListId(holder.rawValue());
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object createMobEffectListFromId(Integer id) {
        return ((Template.StaticMethod)MobEffectHandle.T.fromId.raw).invoker.invoke(null, id);
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static Integer getMobEffectListId(Object mobEffectListHandle) {
        return (Integer)((Template.StaticMethod)MobEffectHandle.T.getId.raw).invoker.invoke(null, mobEffectListHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.DataValue")
    public static DataWatcher.Item<?> watchableObjectToItem(Object watchableObject) {
        SynchedEntityDataHandle.DataItemHandle handle = SynchedEntityDataHandle.DataItemHandle.createHandle(watchableObject);
        return new DataWatcher.Item(handle);
    }

    @ConverterMethod
    public static SoundEffect_1_8_8 soundEffectFromName(String name) {
        return new SoundEffect_1_8_8(IdentifierHandle.createNew(name));
    }
}

