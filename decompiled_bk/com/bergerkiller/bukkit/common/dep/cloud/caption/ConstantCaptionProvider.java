/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import java.util.Map;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public abstract class ConstantCaptionProvider<C>
implements CaptionProvider<C> {
    public abstract @NonNull Map<@NonNull Caption, @NonNull String> captions();

    @Override
    public final @Nullable String provide(@NonNull Caption caption, @NonNull C recipient) {
        return this.captions().get(caption);
    }
}

