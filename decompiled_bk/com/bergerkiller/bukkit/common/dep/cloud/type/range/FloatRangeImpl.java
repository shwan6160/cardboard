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

import com.bergerkiller.bukkit.common.dep.cloud.type.range.FloatRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="FloatRange", generator="Immutables")
@Immutable
final class FloatRangeImpl
implements FloatRange {
    private final float minFloat;
    private final float maxFloat;

    private FloatRangeImpl(float minFloat, float maxFloat) {
        this.minFloat = minFloat;
        this.maxFloat = maxFloat;
    }

    @Override
    public float minFloat() {
        return this.minFloat;
    }

    @Override
    public float maxFloat() {
        return this.maxFloat;
    }

    public final FloatRangeImpl withMinFloat(float value) {
        if (Float.floatToIntBits(this.minFloat) == Float.floatToIntBits(value)) {
            return this;
        }
        return new FloatRangeImpl(value, this.maxFloat);
    }

    public final FloatRangeImpl withMaxFloat(float value) {
        if (Float.floatToIntBits(this.maxFloat) == Float.floatToIntBits(value)) {
            return this;
        }
        return new FloatRangeImpl(this.minFloat, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof FloatRangeImpl && this.equalTo(0, (FloatRangeImpl)another);
    }

    private boolean equalTo(int synthetic, FloatRangeImpl another) {
        return Float.floatToIntBits(this.minFloat) == Float.floatToIntBits(another.minFloat) && Float.floatToIntBits(this.maxFloat) == Float.floatToIntBits(another.maxFloat);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Float.hashCode(this.minFloat);
        h += (h << 5) + Float.hashCode(this.maxFloat);
        return h;
    }

    public String toString() {
        return "FloatRange{minFloat=" + this.minFloat + ", maxFloat=" + this.maxFloat + "}";
    }

    public static FloatRangeImpl of(float minFloat, float maxFloat) {
        return new FloatRangeImpl(minFloat, maxFloat);
    }

    public static FloatRangeImpl copyOf(FloatRange instance) {
        if (instance instanceof FloatRangeImpl) {
            return (FloatRangeImpl)instance;
        }
        return FloatRangeImpl.of(instance.minFloat(), instance.maxFloat());
    }
}

