/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinateType;
import java.util.Locale;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class LocationCoordinate {
    private final LocationCoordinateType type;
    private final double coordinate;

    private LocationCoordinate(@NonNull LocationCoordinateType type, double coordinate) {
        this.type = type;
        this.coordinate = coordinate;
    }

    public static @NonNull LocationCoordinate of(@NonNull LocationCoordinateType type, double coordinate) {
        return new LocationCoordinate(type, coordinate);
    }

    public @NonNull LocationCoordinateType type() {
        return this.type;
    }

    public double coordinate() {
        return this.coordinate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LocationCoordinate that = (LocationCoordinate)o;
        return Double.compare(that.coordinate, this.coordinate) == 0 && this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.coordinate});
    }

    public String toString() {
        return String.format("LocationCoordinate{type=%s, coordinate=%f}", this.type.name().toLowerCase(Locale.ROOT), this.coordinate);
    }
}

