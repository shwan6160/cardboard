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

import com.bergerkiller.bukkit.common.dep.cloud.type.range.ByteRange;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ByteRange", generator="Immutables")
@Immutable
final class ByteRangeImpl
implements ByteRange {
    private final byte minByte;
    private final byte maxByte;

    private ByteRangeImpl(byte minByte, byte maxByte) {
        this.minByte = minByte;
        this.maxByte = maxByte;
    }

    @Override
    public byte minByte() {
        return this.minByte;
    }

    @Override
    public byte maxByte() {
        return this.maxByte;
    }

    public final ByteRangeImpl withMinByte(byte value) {
        if (this.minByte == value) {
            return this;
        }
        return new ByteRangeImpl(value, this.maxByte);
    }

    public final ByteRangeImpl withMaxByte(byte value) {
        if (this.maxByte == value) {
            return this;
        }
        return new ByteRangeImpl(this.minByte, value);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ByteRangeImpl && this.equalTo(0, (ByteRangeImpl)another);
    }

    private boolean equalTo(int synthetic, ByteRangeImpl another) {
        return this.minByte == another.minByte && this.maxByte == another.maxByte;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Byte.hashCode(this.minByte);
        h += (h << 5) + Byte.hashCode(this.maxByte);
        return h;
    }

    public String toString() {
        return "ByteRange{minByte=" + this.minByte + ", maxByte=" + this.maxByte + "}";
    }

    public static ByteRangeImpl of(byte minByte, byte maxByte) {
        return new ByteRangeImpl(minByte, maxByte);
    }

    public static ByteRangeImpl copyOf(ByteRange instance) {
        if (instance instanceof ByteRangeImpl) {
            return (ByteRangeImpl)instance;
        }
        return ByteRangeImpl.of(instance.minByte(), instance.maxByte());
    }
}

