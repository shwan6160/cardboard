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
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ParserDescriptor", generator="Immutables")
@Immutable
final class ParserDescriptorImpl<C, T>
implements ParserDescriptor<C, T> {
    private final @NonNull ArgumentParser<C, T> parser;
    private final @NonNull TypeToken<T> valueType;

    private ParserDescriptorImpl(@NonNull ArgumentParser<C, T> parser, @NonNull TypeToken<T> valueType) {
        this.parser = Objects.requireNonNull(parser, "parser");
        this.valueType = Objects.requireNonNull(valueType, "valueType");
    }

    private ParserDescriptorImpl(ParserDescriptorImpl<C, T> original, @NonNull ArgumentParser<C, T> parser, @NonNull TypeToken<T> valueType) {
        this.parser = parser;
        this.valueType = valueType;
    }

    @Override
    public @NonNull ArgumentParser<C, T> parser() {
        return this.parser;
    }

    @Override
    public @NonNull TypeToken<T> valueType() {
        return this.valueType;
    }

    public final ParserDescriptorImpl<C, T> withParser(@NonNull ArgumentParser<C, T> value) {
        if (this.parser == value) {
            return this;
        }
        @NonNull ArgumentParser<C, T> newValue = Objects.requireNonNull(value, "parser");
        return new ParserDescriptorImpl<C, T>(this, newValue, this.valueType);
    }

    public final ParserDescriptorImpl<C, T> withValueType(@NonNull TypeToken<T> value) {
        if (this.valueType == value) {
            return this;
        }
        @NonNull TypeToken<T> newValue = Objects.requireNonNull(value, "valueType");
        return new ParserDescriptorImpl<C, T>(this, this.parser, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ParserDescriptorImpl && this.equalTo(0, (ParserDescriptorImpl)another);
    }

    private boolean equalTo(int synthetic, ParserDescriptorImpl<?, ?> another) {
        return this.parser.equals(another.parser) && this.valueType.equals(another.valueType);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.parser.hashCode();
        h += (h << 5) + this.valueType.hashCode();
        return h;
    }

    public String toString() {
        return "ParserDescriptor{parser=" + this.parser + ", valueType=" + this.valueType + "}";
    }

    public static <C, T> ParserDescriptorImpl<C, T> of(@NonNull ArgumentParser<C, T> parser, @NonNull TypeToken<T> valueType) {
        return new ParserDescriptorImpl<C, T>(parser, valueType);
    }

    public static <C, T> ParserDescriptorImpl<C, T> copyOf(ParserDescriptor<C, T> instance) {
        if (instance instanceof ParserDescriptorImpl) {
            return (ParserDescriptorImpl)instance;
        }
        return ParserDescriptorImpl.of(instance.parser(), instance.valueType());
    }
}

