/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.flag;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.TypedCommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class CommandFlag<T> {
    private final @NonNull String name;
    private final @NonNull String @NonNull [] aliases;
    private final @NonNull Description description;
    private final @NonNull Permission permission;
    private final @NonNull FlagMode mode;
    private final @Nullable TypedCommandComponent<?, T> commandComponent;

    private CommandFlag(@NonNull String name, @NonNull String @NonNull [] aliases, @NonNull Description description, @NonNull Permission permission, @Nullable TypedCommandComponent<?, T> commandComponent, @NonNull FlagMode mode) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.aliases = Objects.requireNonNull(aliases, "aliases cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
        this.permission = Objects.requireNonNull(permission, "permission cannot be null");
        this.commandComponent = commandComponent;
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull Builder<C, Void> builder(@NonNull String name) {
        return new Builder(name);
    }

    public @NonNull String name() {
        return this.name;
    }

    public @NonNull Collection<@NonNull String> aliases() {
        return Arrays.asList(this.aliases);
    }

    @API(status=API.Status.STABLE)
    public @NonNull FlagMode mode() {
        return this.mode;
    }

    @API(status=API.Status.STABLE)
    public @NonNull Description description() {
        return this.description;
    }

    @API(status=API.Status.STABLE)
    public @Nullable CommandComponent<?> commandComponent() {
        return this.commandComponent;
    }

    @API(status=API.Status.STABLE)
    public Permission permission() {
        return this.permission;
    }

    public String toString() {
        return String.format("--%s", this.name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CommandFlag that = (CommandFlag)o;
        return this.name().equals(that.name());
    }

    public int hashCode() {
        return Objects.hash(this.name());
    }

    @API(status=API.Status.STABLE)
    public static enum FlagMode {
        SINGLE,
        REPEATABLE;

    }

    @API(status=API.Status.STABLE)
    public static final class Builder<C, T> {
        private final String name;
        private final String[] aliases;
        private final Description description;
        private final Permission permission;
        private final TypedCommandComponent<C, T> commandComponent;
        private final FlagMode mode;

        private Builder(@NonNull String name, @NonNull String[] aliases, @NonNull Description description, @NonNull Permission permission, @Nullable TypedCommandComponent<C, T> commandComponent, @NonNull FlagMode mode) {
            this.name = name;
            this.aliases = aliases;
            this.description = description;
            this.permission = permission;
            this.commandComponent = commandComponent;
            this.mode = mode;
        }

        private Builder(@NonNull String name) {
            this(name, new String[0], Description.empty(), Permission.empty(), null, FlagMode.SINGLE);
        }

        public @NonNull Builder<C, T> withAliases(String ... aliases) {
            return this.withAliases(Arrays.asList(aliases));
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C, T> withAliases(@NonNull Collection<@NonNull String> aliases) {
            HashSet<String> filteredAliases = new HashSet<String>();
            for (String alias : aliases) {
                if (alias.isEmpty()) continue;
                if (alias.length() > 1) {
                    throw new IllegalArgumentException(String.format("Alias '%s' has name longer than one character. This is not allowed", alias));
                }
                filteredAliases.add(alias);
            }
            return new Builder<C, T>(this.name, filteredAliases.toArray(new String[0]), this.description, this.permission, this.commandComponent, this.mode);
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C, T> withDescription(@NonNull Description description) {
            return new Builder<C, T>(this.name, this.aliases, description, this.permission, this.commandComponent, this.mode);
        }

        public <N> @NonNull Builder<C, N> withComponent(@NonNull TypedCommandComponent<C, N> component) {
            return new Builder<C, N>(this.name, this.aliases, this.description, this.permission, component, this.mode);
        }

        public <N> @NonNull Builder<C, N> withComponent(@NonNull ParserDescriptor<? super C, N> parserDescriptor) {
            return this.withComponent(CommandComponent.builder(this.name, parserDescriptor));
        }

        public <N> @NonNull Builder<C, N> withComponent(@NonNull CommandComponent.Builder<C, N> builder) {
            return this.withComponent(builder.build());
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C, T> withPermission(@NonNull Permission permission) {
            return new Builder<C, T>(this.name, this.aliases, this.description, permission, this.commandComponent, this.mode);
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C, T> withPermission(@NonNull String permissionString) {
            return this.withPermission(Permission.of(permissionString));
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C, T> asRepeatable() {
            return new Builder<C, T>(this.name, this.aliases, this.description, this.permission, this.commandComponent, FlagMode.REPEATABLE);
        }

        public @NonNull CommandFlag<T> build() {
            return new CommandFlag(this.name, this.aliases, this.description, this.permission, this.commandComponent, this.mode);
        }
    }
}

