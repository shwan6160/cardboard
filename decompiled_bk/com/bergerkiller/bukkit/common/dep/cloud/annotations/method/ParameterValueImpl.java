/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.method;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import java.lang.reflect.Parameter;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ParameterValue", generator="Immutables")
final class ParameterValueImpl
implements ParameterValue {
    private final @NonNull Parameter parameter;
    private final @Nullable Object value;
    private final @Nullable Descriptor descriptor;

    private ParameterValueImpl(@NonNull Parameter parameter, @Nullable Object value, @Nullable Descriptor descriptor) {
        this.parameter = Objects.requireNonNull(parameter, "parameter");
        this.value = value;
        this.descriptor = descriptor;
    }

    private ParameterValueImpl(ParameterValueImpl original, @NonNull Parameter parameter, @Nullable Object value, @Nullable Descriptor descriptor) {
        this.parameter = parameter;
        this.value = value;
        this.descriptor = descriptor;
    }

    @Override
    public @NonNull Parameter parameter() {
        return this.parameter;
    }

    @Override
    public @Nullable Object value() {
        return this.value;
    }

    @Override
    public @Nullable Descriptor descriptor() {
        return this.descriptor;
    }

    public final ParameterValueImpl withParameter(@NonNull Parameter value) {
        if (this.parameter == value) {
            return this;
        }
        @NonNull Parameter newValue = Objects.requireNonNull(value, "parameter");
        return new ParameterValueImpl(this, newValue, this.value, this.descriptor);
    }

    public final ParameterValueImpl withValue(@Nullable Object value) {
        if (this.value == value) {
            return this;
        }
        return new ParameterValueImpl(this, this.parameter, value, this.descriptor);
    }

    public final ParameterValueImpl withDescriptor(@Nullable Descriptor value) {
        if (this.descriptor == value) {
            return this;
        }
        return new ParameterValueImpl(this, this.parameter, this.value, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ParameterValueImpl && this.equalTo(0, (ParameterValueImpl)another);
    }

    private boolean equalTo(int synthetic, ParameterValueImpl another) {
        return this.parameter.equals(another.parameter) && Objects.equals(this.value, another.value) && Objects.equals(this.descriptor, another.descriptor);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.parameter.hashCode();
        h += (h << 5) + Objects.hashCode(this.value);
        h += (h << 5) + Objects.hashCode(this.descriptor);
        return h;
    }

    public String toString() {
        return "ParameterValue{parameter=" + this.parameter + ", value=" + this.value + ", descriptor=" + this.descriptor + "}";
    }

    public static ParameterValueImpl of(@NonNull Parameter parameter, @Nullable Object value, @Nullable Descriptor descriptor) {
        return new ParameterValueImpl(parameter, value, descriptor);
    }

    public static ParameterValueImpl copyOf(ParameterValue instance) {
        if (instance instanceof ParameterValueImpl) {
            return (ParameterValueImpl)instance;
        }
        return ParameterValueImpl.of(instance.parameter(), instance.value(), instance.descriptor());
    }
}

