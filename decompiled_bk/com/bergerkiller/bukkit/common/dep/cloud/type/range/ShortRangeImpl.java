/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.type.range;

import com.bergerkiller.bukkit.common.dep.cloud.type.range.ShortRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ShortRange", generator="Immutables")
@Immutable
final class ShortRangeImpl
implements ShortRange {
    private final short minShort;
    private final short maxShort;

    private ShortRangeImpl(short minShort, short maxShort) {
        this.minShort = minShort;
        this.maxShort = maxShort;
    }

    @Override
    public short minShort() {
        return this.minShort;
    }

    @Override
    public short maxShort() {
        return this.maxShort;
    }

    public final ShortRangeImpl withMinShort(short value) {
        if (this.minShort == value) {
            return this;
        }
        return new ShortRangeImpl(value, this.maxShort);
    }

    public final ShortRangeImpl withMaxShort(short value) {
        if (this.maxShort == value) {
            return this;
        }
        return new ShortRangeImpl(this.minShort, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ShortRangeImpl && this.equalTo(0, (ShortRangeImpl)another);
    }

    private boolean equalTo(int synthetic, ShortRangeImpl another) {
        return this.minShort == another.minShort && this.maxShort == another.maxShort;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Short.hashCode(this.minShort);
        h += (h << 5) + Short.hashCode(this.maxShort);
        return h;
    }

    public String toString() {
        return "ShortRange{minShort=" + this.minShort + ", maxShort=" + this.maxShort + "}";
    }

    public static ShortRangeImpl of(short minShort, short maxShort) {
        return new ShortRangeImpl(minShort, maxShort);
    }

    public static ShortRangeImpl copyOf(ShortRange instance) {
        if (instance instanceof ShortRangeImpl) {
            return (ShortRangeImpl)instance;
        }
        return ShortRangeImpl.of(instance.minShort(), instance.maxShort());
    }
}

