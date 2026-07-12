/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.component;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
@FunctionalInterface
public interface DefaultValue<C, T> {
    public static <C, T> @NonNull DefaultValue<C, T> constant(@NonNull T value) {
        return new ConstantDefaultValue(Objects.requireNonNull(value, "value"));
    }

    public static <C, T> @NonNull DefaultValue<C, T> dynamic(@NonNull DefaultValueProvider<C, T> expression) {
        Objects.requireNonNull(expression, "expression");
        return DefaultValue.failableDynamic(ctx -> ArgumentParseResult.success(expression.evaluateDefault(ctx)));
    }

    public static <C, T> @NonNull DefaultValue<C, T> failableDynamic(@NonNull DefaultValue<C, T> expression) {
        return new DynamicDefaultValue(Objects.requireNonNull(expression, "expression"));
    }

    public static <C, T> @NonNull DefaultValue<C, T> parsed(@NonNull String value) {
        return new ParsedDefaultValue(value);
    }

    public @NonNull ArgumentParseResult<T> evaluateDefault(@NonNull CommandContext<C> var1);

    public static final class ConstantDefaultValue<C, T>
    implements DefaultValue<C, T> {
        private final ArgumentParseResult<T> value;

        private ConstantDefaultValue(@NonNull T value) {
            this.value = ArgumentParseResult.success(value);
        }

        @Override
        public @NonNull ArgumentParseResult<T> evaluateDefault(@NonNull CommandContext<C> context) {
            return this.value;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ConstantDefaultValue that = (ConstantDefaultValue)object;
            return Objects.equals(this.value.parsedValue().get(), that.value.parsedValue().get());
        }

        public int hashCode() {
            return Objects.hash(this.value);
        }
    }

    @API(status=API.Status.STABLE)
    @FunctionalInterface
    public static interface DefaultValueProvider<C, T> {
        public @NonNull T evaluateDefault(@NonNull CommandContext<C> var1);
    }

    public static final class DynamicDefaultValue<C, T>
    implements DefaultValue<C, T> {
        private final DefaultValue<C, T> defaultValue;

        private DynamicDefaultValue(@NonNull DefaultValue<C, T> defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public @NonNull ArgumentParseResult<T> evaluateDefault(@NonNull CommandContext<C> context) {
            return this.defaultValue.evaluateDefault(context);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            DynamicDefaultValue that = (DynamicDefaultValue)object;
            return Objects.equals(this.defaultValue, that.defaultValue);
        }

        public int hashCode() {
            return Objects.hash(this.defaultValue);
        }
    }

    public static final class ParsedDefaultValue<C, T>
    implements DefaultValue<C, T> {
        private final String value;

        private ParsedDefaultValue(@NonNull String string) {
            this.value = string;
        }

        @Override
        public @NonNull ArgumentParseResult<T> evaluateDefault(@NonNull CommandContext<C> context) {
            throw new UnsupportedOperationException();
        }

        public @NonNull String value() {
            return this.value;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ParsedDefaultValue that = (ParsedDefaultValue)object;
            return Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return Objects.hash(this.value);
        }
    }
}

