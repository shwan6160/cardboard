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
package com.bergerkiller.bukkit.common.dep.cloud.bean;

import com.bergerkiller.bukkit.common.dep.cloud.bean.CommandProperties;
import java.util.Collection;
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
@Generated(from="CommandProperties", generator="Immutables")
@Immutable
final class CommandPropertiesImpl
implements CommandProperties {
    private final @NonNull String name;
    private final @NonNull Collection<String> aliases;

    private CommandPropertiesImpl(@NonNull String name, @NonNull Collection<String> aliases) {
        this.name = Objects.requireNonNull(name, "name");
        this.aliases = Objects.requireNonNull(aliases, "aliases");
    }

    private CommandPropertiesImpl(CommandPropertiesImpl original, @NonNull String name, @NonNull Collection<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    @Override
    public @NonNull String name() {
        return this.name;
    }

    @Override
    public @NonNull Collection<String> aliases() {
        return this.aliases;
    }

    public final CommandPropertiesImpl withName(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "name");
        if (this.name.equals(newValue)) {
            return this;
        }
        return new CommandPropertiesImpl(this, newValue, this.aliases);
    }

    public final CommandPropertiesImpl withAliases(@NonNull Collection<String> value) {
        if (this.aliases == value) {
            return this;
        }
        @NonNull Collection<String> newValue = Objects.requireNonNull(value, "aliases");
        return new CommandPropertiesImpl(this, this.name, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandPropertiesImpl && this.equalTo(0, (CommandPropertiesImpl)another);
    }

    private boolean equalTo(int synthetic, CommandPropertiesImpl another) {
        return this.name.equals(another.name) && this.aliases.equals(another.aliases);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.name.hashCode();
        h += (h << 5) + this.aliases.hashCode();
        return h;
    }

    public String toString() {
        return "CommandProperties{name=" + this.name + ", aliases=" + this.aliases + "}";
    }

    public static CommandPropertiesImpl of(@NonNull String name, @NonNull Collection<String> aliases) {
        return new CommandPropertiesImpl(name, aliases);
    }

    public static CommandPropertiesImpl copyOf(CommandProperties instance) {
        if (instance instanceof CommandPropertiesImpl) {
            return (CommandPropertiesImpl)instance;
        }
        return CommandPropertiesImpl.of(instance.name(), instance.aliases());
    }
}

