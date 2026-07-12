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
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionRegistry;
import java.util.LinkedList;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.INTERNAL)
public final class CaptionRegistryImpl<C>
implements CaptionRegistry<C> {
    private final LinkedList<@NonNull CaptionProvider<C>> providers = new LinkedList();

    CaptionRegistryImpl() {
    }

    @Override
    public @NonNull String caption(@NonNull Caption caption, @NonNull C sender) {
        for (CaptionProvider captionProvider : this.providers) {
            String result = captionProvider.provide(caption, sender);
            if (result == null) continue;
            return result;
        }
        throw new IllegalArgumentException(String.format("There is no caption stored with key '%s'", caption));
    }

    @Override
    public @This @NonNull CaptionRegistry<C> registerProvider(@NonNull CaptionProvider<C> provider) {
        this.providers.addFirst(provider);
        return this;
    }
}

