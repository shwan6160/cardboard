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

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="FlagDescriptor", generator="Immutables")
public final class ImmutableFlagDescriptor
implements FlagDescriptor {
    private final @NonNull Parameter parameter;
    private final @NonNull String name;
    private final @NonNull Collection<String> aliases;
    private final @Nullable String parserName;
    private final @Nullable String suggestions;
    private final @Nullable Permission permission;
    private final @Nullable Description description;
    private final boolean repeatable;

    private ImmutableFlagDescriptor(@NonNull Parameter parameter, @NonNull String name, @NonNull Collection<String> aliases, @Nullable String parserName, @Nullable String suggestions, @Nullable Permission permission, @Nullable Description description, boolean repeatable) {
        this.parameter = Objects.requireNonNull(parameter, "parameter");
        this.name = Objects.requireNonNull(name, "name");
        this.aliases = Objects.requireNonNull(aliases, "aliases");
        this.parserName = parserName;
        this.suggestions = suggestions;
        this.permission = permission;
        this.description = description;
        this.repeatable = repeatable;
    }

    private ImmutableFlagDescriptor(ImmutableFlagDescriptor original, @NonNull Parameter parameter, @NonNull String name, @NonNull Collection<String> aliases, @Nullable String parserName, @Nullable String suggestions, @Nullable Permission permission, @Nullable Description description, boolean repeatable) {
        this.parameter = parameter;
        this.name = name;
        this.aliases = aliases;
        this.parserName = parserName;
        this.suggestions = suggestions;
        this.permission = permission;
        this.description = description;
        this.repeatable = repeatable;
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
    public @NonNull Collection<String> aliases() {
        return this.aliases;
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
    public @Nullable Permission permission() {
        return this.permission;
    }

    @Override
    public @Nullable Description description() {
        return this.description;
    }

    @Override
    public boolean repeatable() {
        return this.repeatable;
    }

    public final ImmutableFlagDescriptor withParameter(@NonNull Parameter value) {
        if (this.parameter == value) {
            return this;
        }
        @NonNull Parameter newValue = Objects.requireNonNull(value, "parameter");
        return new ImmutableFlagDescriptor(this, newValue, this.name, this.aliases, this.parserName, this.suggestions, this.permission, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withName(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "name");
        if (this.name.equals(newValue)) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, newValue, this.aliases, this.parserName, this.suggestions, this.permission, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withAliases(@NonNull Collection<String> value) {
        if (this.aliases == value) {
            return this;
        }
        @NonNull Collection<String> newValue = Objects.requireNonNull(value, "aliases");
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, newValue, this.parserName, this.suggestions, this.permission, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withParserName(@Nullable String value) {
        if (Objects.equals(this.parserName, value)) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, this.aliases, value, this.suggestions, this.permission, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withSuggestions(@Nullable String value) {
        if (Objects.equals(this.suggestions, value)) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, this.aliases, this.parserName, value, this.permission, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withPermission(@Nullable Permission value) {
        if (this.permission == value) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, this.aliases, this.parserName, this.suggestions, value, this.description, this.repeatable);
    }

    public final ImmutableFlagDescriptor withDescription(@Nullable Description value) {
        if (this.description == value) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, this.aliases, this.parserName, this.suggestions, this.permission, value, this.repeatable);
    }

    public final ImmutableFlagDescriptor withRepeatable(boolean value) {
        if (this.repeatable == value) {
            return this;
        }
        return new ImmutableFlagDescriptor(this, this.parameter, this.name, this.aliases, this.parserName, this.suggestions, this.permission, this.description, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableFlagDescriptor && this.equalTo(0, (ImmutableFlagDescriptor)another);
    }

    private boolean equalTo(int synthetic, ImmutableFlagDescriptor another) {
        return this.parameter.equals(another.parameter) && this.name.equals(another.name) && this.aliases.equals(another.aliases) && Objects.equals(this.parserName, another.parserName) && Objects.equals(this.suggestions, another.suggestions) && Objects.equals(this.permission, another.permission) && Objects.equals(this.description, another.description) && this.repeatable == another.repeatable;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.parameter.hashCode();
        h += (h << 5) + this.name.hashCode();
        h += (h << 5) + this.aliases.hashCode();
        h += (h << 5) + Objects.hashCode(this.parserName);
        h += (h << 5) + Objects.hashCode(this.suggestions);
        h += (h << 5) + Objects.hashCode(this.permission);
        h += (h << 5) + Objects.hashCode(this.description);
        h += (h << 5) + Boolean.hashCode(this.repeatable);
        return h;
    }

    public String toString() {
        return "FlagDescriptor{parameter=" + this.parameter + ", name=" + this.name + ", aliases=" + this.aliases + ", parserName=" + this.parserName + ", suggestions=" + this.suggestions + ", permission=" + this.permission + ", description=" + this.description + ", repeatable=" + this.repeatable + "}";
    }

    public static ImmutableFlagDescriptor of(@NonNull Parameter parameter, @NonNull String name, @NonNull Collection<String> aliases, @Nullable String parserName, @Nullable String suggestions, @Nullable Permission permission, @Nullable Description description, boolean repeatable) {
        return new ImmutableFlagDescriptor(parameter, name, aliases, parserName, suggestions, permission, description, repeatable);
    }

    public static ImmutableFlagDescriptor copyOf(FlagDescriptor instance) {
        if (instance instanceof ImmutableFlagDescriptor) {
            return (ImmutableFlagDescriptor)instance;
        }
        return ImmutableFlagDescriptor.builder().from(instance).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Generated(from="FlagDescriptor", generator="Immutables")
    public static final class Builder {
        private static final long INIT_BIT_PARAMETER = 1L;
        private static final long INIT_BIT_NAME = 2L;
        private static final long INIT_BIT_ALIASES = 4L;
        private static final long INIT_BIT_REPEATABLE = 8L;
        private long initBits = 15L;
        private @NonNull Parameter parameter;
        private @NonNull String name;
        private @NonNull Collection<String> aliases;
        private @Nullable String parserName;
        private @Nullable String suggestions;
        private @Nullable Permission permission;
        private @Nullable Description description;
        private boolean repeatable;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public final Builder from(FlagDescriptor instance) {
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
            if (object instanceof FlagDescriptor) {
                String parserNameValue;
                String suggestionsValue;
                Permission permissionValue;
                Description descriptionValue;
                instance = (FlagDescriptor)object;
                this.aliases(instance.aliases());
                this.repeatable(instance.repeatable());
                this.parameter(instance.parameter());
                if ((bits & 1L) == 0L) {
                    this.name(instance.name());
                    bits |= 1L;
                }
                if ((descriptionValue = instance.description()) != null) {
                    this.description(descriptionValue);
                }
                if ((permissionValue = instance.permission()) != null) {
                    this.permission(permissionValue);
                }
                if ((suggestionsValue = instance.suggestions()) != null) {
                    this.suggestions(suggestionsValue);
                }
                if ((parserNameValue = instance.parserName()) != null) {
                    this.parserName(parserNameValue);
                }
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
        public final Builder aliases(@NonNull Collection<String> aliases) {
            this.aliases = Objects.requireNonNull(aliases, "aliases");
            this.initBits &= 0xFFFFFFFFFFFFFFFBL;
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
        public final Builder permission(@Nullable Permission permission) {
            this.permission = permission;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder description(@Nullable Description description) {
            this.description = description;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder repeatable(boolean repeatable) {
            this.repeatable = repeatable;
            this.initBits &= 0xFFFFFFFFFFFFFFF7L;
            return this;
        }

        public ImmutableFlagDescriptor build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
            return new ImmutableFlagDescriptor(null, this.parameter, this.name, this.aliases, this.parserName, this.suggestions, this.permission, this.description, this.repeatable);
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("parameter");
            }
            if ((this.initBits & 2L) != 0L) {
                attributes.add("name");
            }
            if ((this.initBits & 4L) != 0L) {
                attributes.add("aliases");
            }
            if ((this.initBits & 8L) != 0L) {
                attributes.add("repeatable");
            }
            return "Cannot build FlagDescriptor, some of required attributes are not set " + attributes;
        }
    }
}

