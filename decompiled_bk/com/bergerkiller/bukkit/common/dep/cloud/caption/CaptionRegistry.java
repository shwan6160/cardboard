/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionRegistryImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public interface CaptionRegistry<C> {
    public @NonNull String caption(@NonNull Caption var1, @NonNull C var2);

    public @This @NonNull CaptionRegistry<C> registerProvider(@NonNull CaptionProvider<C> var1);

    public static <C> CaptionRegistry<C> captionRegistry() {
        return new CaptionRegistryImpl();
    }
}

