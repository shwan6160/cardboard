/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ArgumentDescriptor", generator="Immutables")
public final class ImmutableArgumentDescriptor
implements ArgumentDescriptor {
    private final @NonNull Parameter parameter;
    private final @NonNull String name;
    private final @Nullable String parserName;
    private final @Nullable String suggestions;
    private final @Nullable DefaultValue<?, ?> defaultValue;
    private final @Nullable Description description;

    private ImmutableArgumentDescriptor(@NonNull Parameter parameter, @NonNull String name, @Nullable String parserName, @Nullable String suggestions, @Nullable DefaultValue<?, ?> defaultValue, @Nullable Description description) {
        this.parameter = Objects.requireNonNull(parameter, "parameter");
        this.name = Objects.requireNonNull(name, "name");
        this.parserName = parserName;
        this.suggestions = suggestions;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    private ImmutableArgumentDescriptor(ImmutableArgumentDescriptor original, @NonNull Parameter parameter, @NonNull String name, @Nullable String parserName, @Nullable String suggestions, @Nullable DefaultValue<?, ?> defaultValue, @Nullable Description description) {
        this.parameter = parameter;
        this.name = name;
        this.parserName = parserName;
        this.suggestions = suggestions;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @Override
    public @NonNull Parameter parameter() {
        return this.parameter;
    }

    @Override
    public @NonNull String name() {
        return this.name;
    }

    @Override
    public @Nullable String parserName() {
        return this.parserName;
    }

    @Override
    public @Nullable String suggestions() {
        return this.suggestions;
    }

    @Override
    public @Nullable DefaultValue<?, ?> defaultValue() {
        return this.defaultValue;
    }

    @Override
    public @Nullable Description description() {
        return this.description;
    }

    public final ImmutableArgumentDescriptor withParameter(@NonNull Parameter value) {
        if (this.parameter == value) {
            return this;
        }
        @NonNull Parameter newValue = Objects.requireNonNull(value, "parameter");
        return new ImmutableArgumentDescriptor(this, newValue, this.name, this.parserName, this.suggestions, this.defaultValue, this.description);
    }

    public final ImmutableArgumentDescriptor withName(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "name");
        if (this.name.equals(newValue)) {
            return this;
        }
        return new ImmutableArgumentDescriptor(this, this.parameter, newValue, this.parserName, this.suggestions, this.defaultValue, this.description);
    }

    public final ImmutableArgumentDescriptor withParserName(@Nullable String value) {
        if (Objects.equals(this.parserName, value)) {
            return this;
        }
        return new ImmutableArgumentDescriptor(this, this.parameter, this.name, value, this.suggestions, this.defaultValue, this.description);
    }

    public final ImmutableArgumentDescriptor withSuggestions(@Nullable String value) {
        if (Objects.equals(this.suggestions, value)) {
            return this;
        }
        return new ImmutableArgumentDescriptor(this, this.parameter, this.name, this.parserName, value, this.defaultValue, this.description);
    }

    public final ImmutableArgumentDescriptor withDefaultValue(@Nullable DefaultValue<?, ?> value) {
        if (this.defaultValue == value) {
            return this;
        }
        return new ImmutableArgumentDescriptor(this, this.parameter, this.name, this.parserName, this.suggestions, value, this.description);
    }

    public final ImmutableArgumentDescriptor withDescription(@Nullable Description value) {
        if (this.description == value) {
            return this;
        }
        return new ImmutableArgumentDescriptor(this, this.parameter, this.name, this.parserName, this.suggestions, this.defaultValue, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableArgumentDescriptor && this.equalTo(0, (ImmutableArgumentDescriptor)another);
    }

    private boolean equalTo(int synthetic, ImmutableArgumentDescriptor another) {
        return this.parameter.equals(another.parameter) && this.name.equals(another.name) && Objects.equals(this.parserName, another.parserName) && Objects.equals(this.suggestions, another.suggestions) && Objects.equals(this.defaultValue, another.defaultValue) && Objects.equals(this.description, another.description);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.parameter.hashCode();
        h += (h << 5) + this.name.hashCode();
        h += (h << 5) + Objects.hashCode(this.parserName);
        h += (h << 5) + Objects.hashCode(this.suggestions);
        h += (h << 5) + Objects.hashCode(this.defaultValue);
        h += (h << 5) + Objects.hashCode(this.description);
        return h;
    }

    public String toString() {
        return "ArgumentDescriptor{parameter=" + this.parameter + ", name=" + this.name + ", parserName=" + this.parserName + ", suggestions=" + this.suggestions + ", defaultValue=" + this.defaultValue + ", description=" + this.description + "}";
    }

    public static ImmutableArgumentDescriptor of(@NonNull Parameter parameter, @NonNull String name, @Nullable String parserName, @Nullable String suggestions, @Nullable DefaultValue<?, ?> defaultValue, @Nullable Description description) {
        return new ImmutableArgumentDescriptor(parameter, name, parserName, suggestions, defaultValue, description);
    }

    public static ImmutableArgumentDescriptor copyOf(ArgumentDescriptor instance) {
        if (instance instanceof ImmutableArgumentDescriptor) {
            return (ImmutableArgumentDescriptor)instance;
        }
        return ImmutableArgumentDescriptor.builder().from(instance).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Generated(from="ArgumentDescriptor", generator="Immutables")
    public static final class Builder {
        private static final long INIT_BIT_PARAMETER = 1L;
        private static final long INIT_BIT_NAME = 2L;
        private long initBits = 3L;
        private @NonNull Parameter parameter;
        private @NonNull String name;
        private @Nullable String parserName;
        private @Nullable String suggestions;
        private @Nullable DefaultValue<?, ?> defaultValue;
        private @Nullable Description description;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public final Builder from(ArgumentDescriptor instance) {
            Objects.requireNonNull(instance, "instance");
            this.from((short)0, instance);
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder from(Descriptor instance) {
            Objects.requireNonNull(instance, "instance");
            this.from((short)0, instance);
            return this;
        }

        private void from(short _unused, Object object) {
            Descriptor instance;
            long bits = 0L;
            if (object instanceof ArgumentDescriptor) {
                DefaultValue<?, ?> defaultValueValue;
                String parserNameValue;
                String suggestionsValue;
                Description descriptionValue;
                instance = (ArgumentDescriptor)object;
                if ((bits & 1L) == 0L) {
                    this.name(instance.name());
                    bits |= 1L;
                }
                if ((descriptionValue = instance.description()) != null) {
                    this.description(descriptionValue);
                }
                if ((suggestionsValue = instance.suggestions()) != null) {
                    this.suggestions(suggestionsValue);
                }
                if ((parserNameValue = instance.parserName()) != null) {
                    this.parserName(parserNameValue);
                }
                if ((defaultValueValue = instance.defaultValue()) != null) {
                    this.defaultValue(defaultValueValue);
                }
                this.parameter(instance.parameter());
            }
            if (object instanceof Descriptor) {
                instance = (Descriptor)object;
                if ((bits & 1L) == 0L) {
                    this.name(instance.name());
                    bits |= 1L;
                }
            }
        }

        @CanIgnoreReturnValue
        public final Builder parameter(@NonNull Parameter parameter) {
            this.parameter = Objects.requireNonNull(parameter, "parameter");
            this.initBits &= 0xFFFFFFFFFFFFFFFEL;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder name(@NonNull String name) {
            this.name = Objects.requireNonNull(name, "name");
            this.initBits &= 0xFFFFFFFFFFFFFFFDL;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder parserName(@Nullable String parserName) {
            this.parserName = parserName;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder suggestions(@Nullable String suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder defaultValue(@Nullable DefaultValue<?, ?> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder description(@Nullable Description description) {
            this.description = description;
            return this;
        }

        public ImmutableArgumentDescriptor build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
            return new ImmutableArgumentDescriptor(null, this.parameter, this.name, this.parserName, this.suggestions, this.defaultValue, this.description);
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("parameter");
            }
            if ((this.initBits & 2L) != 0L) {
                attributes.add("name");
            }
            return "Cannot build ArgumentDescriptor, some of required attributes are not set " + attributes;
        }
    }
}

