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
package com.bergerkiller.bukkit.common.dep.cloud.description;

import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
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
@Generated(from="CommandDescription", generator="Immutables")
@Immutable
final class CommandDescriptionImpl
implements CommandDescription {
    private final @NonNull Description description;
    private final @NonNull Description verboseDescription;

    private CommandDescriptionImpl(@NonNull Description description, @NonNull Description verboseDescription) {
        this.description = Objects.requireNonNull(description, "description");
        this.verboseDescription = Objects.requireNonNull(verboseDescription, "verboseDescription");
    }

    private CommandDescriptionImpl(CommandDescriptionImpl original, @NonNull Description description, @NonNull Description verboseDescription) {
        this.description = description;
        this.verboseDescription = verboseDescription;
    }

    @Override
    public @NonNull Description description() {
        return this.description;
    }

    @Override
    public @NonNull Description verboseDescription() {
        return this.verboseDescription;
    }

    public final CommandDescriptionImpl withDescription(@NonNull Description value) {
        if (this.description == value) {
            return this;
        }
        @NonNull Description newValue = Objects.requireNonNull(value, "description");
        return new CommandDescriptionImpl(this, newValue, this.verboseDescription);
    }

    public final CommandDescriptionImpl withVerboseDescription(@NonNull Description value) {
        if (this.verboseDescription == value) {
            return this;
        }
        @NonNull Description newValue = Objects.requireNonNull(value, "verboseDescription");
        return new CommandDescriptionImpl(this, this.description, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandDescriptionImpl && this.equalTo(0, (CommandDescriptionImpl)another);
    }

    private boolean equalTo(int synthetic, CommandDescriptionImpl another) {
        return this.description.equals(another.description) && this.verboseDescription.equals(another.verboseDescription);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.description.hashCode();
        h += (h << 5) + this.verboseDescription.hashCode();
        return h;
    }

    public String toString() {
        return "CommandDescription{description=" + this.description + ", verboseDescription=" + this.verboseDescription + "}";
    }

    public static CommandDescriptionImpl of(@NonNull Description description, @NonNull Description verboseDescription) {
        return new CommandDescriptionImpl(description, verboseDescription);
    }

    public static CommandDescriptionImpl copyOf(CommandDescription instance) {
        if (instance instanceof CommandDescriptionImpl) {
            return (CommandDescriptionImpl)instance;
        }
        return CommandDescriptionImpl.of(instance.description(), instance.verboseDescription());
    }
}

