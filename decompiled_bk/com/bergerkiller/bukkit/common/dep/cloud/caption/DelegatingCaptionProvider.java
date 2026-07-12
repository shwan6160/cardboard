/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class DelegatingCaptionProvider<C>
implements CaptionProvider<C> {
    public abstract @NonNull CaptionProvider<C> delegate();

    @Override
    public final @Nullable String provide(@NonNull Caption caption, @NonNull C recipient) {
        return this.delegate().provide(caption, recipient);
    }
}

