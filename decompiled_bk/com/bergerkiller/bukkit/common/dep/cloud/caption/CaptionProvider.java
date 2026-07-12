/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.ImmutableConstantCaptionProvider;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public interface CaptionProvider<C> {
    public static <C> @NonNull ImmutableConstantCaptionProvider.Builder<C> constantProvider() {
        return ImmutableConstantCaptionProvider.builder();
    }

    public static <C> @NonNull CaptionProvider<C> constantProvider(@NonNull Caption caption, @NonNull String value) {
        return CaptionProvider.constantProvider().putCaption(caption, value).build();
    }

    public static <C> @NonNull CaptionProvider<C> forCaption(@NonNull Caption caption, @NonNull Function<@NonNull C, @Nullable String> provider) {
        return (key, recipient) -> {
            if (key.equals(caption)) {
                return (String)provider.apply(recipient);
            }
            return null;
        };
    }

    public @Nullable String provide(@NonNull Caption var1, @NonNull C var2);
}

