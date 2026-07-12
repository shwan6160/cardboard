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

import com.bergerkiller.bukkit.common.dep.cloud.type.range.LongRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="LongRange", generator="Immutables")
@Immutable
final class LongRangeImpl
implements LongRange {
    private final long minLong;
    private final long maxLong;

    private LongRangeImpl(long minLong, long maxLong) {
        this.minLong = minLong;
        this.maxLong = maxLong;
    }

    @Override
    public long minLong() {
        return this.minLong;
    }

    @Override
    public long maxLong() {
        return this.maxLong;
    }

    public final LongRangeImpl withMinLong(long value) {
        if (this.minLong == value) {
            return this;
        }
        return new LongRangeImpl(value, this.maxLong);
    }

    public final LongRangeImpl withMaxLong(long value) {
        if (this.maxLong == value) {
            return this;
        }
        return new LongRangeImpl(this.minLong, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof LongRangeImpl && this.equalTo(0, (LongRangeImpl)another);
    }

    private boolean equalTo(int synthetic, LongRangeImpl another) {
        return this.minLong == another.minLong && this.maxLong == another.maxLong;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Long.hashCode(this.minLong);
        h += (h << 5) + Long.hashCode(this.maxLong);
        return h;
    }

    public String toString() {
        return "LongRange{minLong=" + this.minLong + ", maxLong=" + this.maxLong + "}";
    }

    public static LongRangeImpl of(long minLong, long maxLong) {
        return new LongRangeImpl(minLong, maxLong);
    }

    public static LongRangeImpl copyOf(LongRange instance) {
        if (instance instanceof LongRangeImpl) {
            return (LongRangeImpl)instance;
        }
        return LongRangeImpl.of(instance.minLong(), instance.maxLong());
    }
}

