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

import com.bergerkiller.bukkit.common.dep.cloud.type.range.DoubleRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="DoubleRange", generator="Immutables")
@Immutable
final class DoubleRangeImpl
implements DoubleRange {
    private final double minDouble;
    private final double maxDouble;

    private DoubleRangeImpl(double minDouble, double maxDouble) {
        this.minDouble = minDouble;
        this.maxDouble = maxDouble;
    }

    @Override
    public double minDouble() {
        return this.minDouble;
    }

    @Override
    public double maxDouble() {
        return this.maxDouble;
    }

    public final DoubleRangeImpl withMinDouble(double value) {
        if (Double.doubleToLongBits(this.minDouble) == Double.doubleToLongBits(value)) {
            return this;
        }
        return new DoubleRangeImpl(value, this.maxDouble);
    }

    public final DoubleRangeImpl withMaxDouble(double value) {
        if (Double.doubleToLongBits(this.maxDouble) == Double.doubleToLongBits(value)) {
            return this;
        }
        return new DoubleRangeImpl(this.minDouble, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof DoubleRangeImpl && this.equalTo(0, (DoubleRangeImpl)another);
    }

    private boolean equalTo(int synthetic, DoubleRangeImpl another) {
        return Double.doubleToLongBits(this.minDouble) == Double.doubleToLongBits(another.minDouble) && Double.doubleToLongBits(this.maxDouble) == Double.doubleToLongBits(another.maxDouble);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Double.hashCode(this.minDouble);
        h += (h << 5) + Double.hashCode(this.maxDouble);
        return h;
    }

    public String toString() {
        return "DoubleRange{minDouble=" + this.minDouble + ", maxDouble=" + this.maxDouble + "}";
    }

    public static DoubleRangeImpl of(double minDouble, double maxDouble) {
        return new DoubleRangeImpl(minDouble, maxDouble);
    }

    public static DoubleRangeImpl copyOf(DoubleRange instance) {
        if (instance instanceof DoubleRangeImpl) {
            return (DoubleRangeImpl)instance;
        }
        return DoubleRangeImpl.of(instance.minDouble(), instance.maxDouble());
    }
}

