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

import com.bergerkiller.bukkit.common.dep.cloud.type.range.IntRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="IntRange", generator="Immutables")
@Immutable
final class IntRangeImpl
implements IntRange {
    private final int minInt;
    private final int maxInt;

    private IntRangeImpl(int minInt, int maxInt) {
        this.minInt = minInt;
        this.maxInt = maxInt;
    }

    @Override
    public int minInt() {
        return this.minInt;
    }

    @Override
    public int maxInt() {
        return this.maxInt;
    }

    public final IntRangeImpl withMinInt(int value) {
        if (this.minInt == value) {
            return this;
        }
        return new IntRangeImpl(value, this.maxInt);
    }

    public final IntRangeImpl withMaxInt(int value) {
        if (this.maxInt == value) {
            return this;
        }
        return new IntRangeImpl(this.minInt, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof IntRangeImpl && this.equalTo(0, (IntRangeImpl)another);
    }

    private boolean equalTo(int synthetic, IntRangeImpl another) {
        return this.minInt == another.minInt && this.maxInt == another.maxInt;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.minInt;
        h += (h << 5) + this.maxInt;
        return h;
    }

    public String toString() {
        return "IntRange{minInt=" + this.minInt + ", maxInt=" + this.maxInt + "}";
    }

    public static IntRangeImpl of(int minInt, int maxInt) {
        return new IntRangeImpl(minInt, maxInt);
    }

    public static IntRangeImpl copyOf(IntRange instance) {
        if (instance instanceof IntRangeImpl) {
            return (IntRangeImpl)instance;
        }
        return IntRangeImpl.of(instance.minInt(), instance.maxInt());
    }
}

