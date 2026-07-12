/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.ConstantCaptionProvider;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ConstantCaptionProvider", generator="Immutables")
@Immutable
public final class ImmutableConstantCaptionProvider<C>
extends ConstantCaptionProvider<C> {
    private final @NonNull Map<Caption, String> captions;

    private ImmutableConstantCaptionProvider(Map<? extends Caption, ? extends String> captions) {
        this.captions = ImmutableConstantCaptionProvider.createUnmodifiableMap(true, false, captions);
    }

    private ImmutableConstantCaptionProvider(ImmutableConstantCaptionProvider<C> original, @NonNull Map<Caption, String> captions) {
        this.captions = captions;
    }

    @Override
    public @NonNull Map<Caption, String> captions() {
        return this.captions;
    }

    public final ImmutableConstantCaptionProvider<C> withCaptions(Map<? extends Caption, ? extends String> entries) {
        if (this.captions == entries) {
            return this;
        }
        @NonNull Map<Caption, String> newValue = ImmutableConstantCaptionProvider.createUnmodifiableMap(true, false, entries);
        return new ImmutableConstantCaptionProvider<C>(this, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableConstantCaptionProvider && this.equalTo(0, (ImmutableConstantCaptionProvider)another);
    }

    private boolean equalTo(int synthetic, ImmutableConstantCaptionProvider<?> another) {
        return this.captions.equals(another.captions);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.captions.hashCode();
        return h;
    }

    public String toString() {
        return "ConstantCaptionProvider{captions=" + this.captions + "}";
    }

    public static <C> ImmutableConstantCaptionProvider<C> of(Map<? extends Caption, ? extends String> captions) {
        return new ImmutableConstantCaptionProvider<C>(captions);
    }

    public static <C> ImmutableConstantCaptionProvider<C> copyOf(ConstantCaptionProvider<C> instance) {
        if (instance instanceof ImmutableConstantCaptionProvider) {
            return (ImmutableConstantCaptionProvider)instance;
        }
        return ImmutableConstantCaptionProvider.builder().from(instance).build();
    }

    public static <C> Builder<C> builder() {
        return new Builder();
    }

    private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
        switch (map.size()) {
            case 0: {
                return Collections.emptyMap();
            }
            case 1: {
                Map.Entry<K, V> e = map.entrySet().iterator().next();
                K k = e.getKey();
                V v = e.getValue();
                if (checkNulls) {
                    Objects.requireNonNull(k, "key");
                    Objects.requireNonNull(v, v == null ? "value for key: " + k : null);
                }
                if (skipNulls && (k == null || v == null)) {
                    return Collections.emptyMap();
                }
                return Collections.singletonMap(k, v);
            }
        }
        LinkedHashMap<K, V> linkedMap = new LinkedHashMap<K, V>(map.size() * 4 / 3 + 1);
        if (skipNulls || checkNulls) {
            for (Map.Entry<K, V> e : map.entrySet()) {
                K k = e.getKey();
                V v = e.getValue();
                if (skipNulls) {
                    if (k == null || v == null) {
                        continue;
                    }
                } else if (checkNulls) {
                    Objects.requireNonNull(k, "key");
                    Objects.requireNonNull(v, v == null ? "value for key: " + k : null);
                }
                linkedMap.put(k, v);
            }
        } else {
            linkedMap.putAll(map);
        }
        return Collections.unmodifiableMap(linkedMap);
    }

    @Generated(from="ConstantCaptionProvider", generator="Immutables")
    @NotThreadSafe
    public static final class Builder<C> {
        private Map<Caption, String> captions = null;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public final Builder<C> from(ConstantCaptionProvider<C> instance) {
            Objects.requireNonNull(instance, "instance");
            this.putAllCaptions(instance.captions());
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder<C> putCaption(Caption key, String value) {
            if (this.captions == null) {
                this.captions = new LinkedHashMap<Caption, String>();
            }
            this.captions.put(Objects.requireNonNull(key, "captions key"), Objects.requireNonNull(value, value == null ? "captions value for key: " + key : null));
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder<C> putCaption(Map.Entry<? extends Caption, ? extends String> entry) {
            String v;
            if (this.captions == null) {
                this.captions = new LinkedHashMap<Caption, String>();
            }
            Caption k = entry.getKey();
            this.captions.put(Objects.requireNonNull(k, "captions key"), Objects.requireNonNull(v, (v = entry.getValue()) == null ? "captions value for key: " + k : null));
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder<C> captions(Map<? extends Caption, ? extends String> entries) {
            this.captions = new LinkedHashMap<Caption, String>();
            return this.putAllCaptions(entries);
        }

        @CanIgnoreReturnValue
        public final Builder<C> putAllCaptions(Map<? extends Caption, ? extends String> entries) {
            if (this.captions == null) {
                this.captions = new LinkedHashMap<Caption, String>();
            }
            for (Map.Entry<? extends Caption, ? extends String> e : entries.entrySet()) {
                String v;
                Caption k = e.getKey();
                this.captions.put(Objects.requireNonNull(k, "captions key"), Objects.requireNonNull(v, (v = e.getValue()) == null ? "captions value for key: " + k : null));
            }
            return this;
        }

        public ImmutableConstantCaptionProvider<C> build() {
            return new ImmutableConstantCaptionProvider(null, this.captions == null ? Collections.emptyMap() : ImmutableConstantCaptionProvider.createUnmodifiableMap(false, false, this.captions));
        }
    }
}

