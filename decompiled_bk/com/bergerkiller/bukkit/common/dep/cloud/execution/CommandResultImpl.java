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
package com.bergerkiller.bukkit.common.dep.cloud.execution;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandResult;
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
@Generated(from="CommandResult", generator="Immutables")
@Immutable
final class CommandResultImpl<C>
implements CommandResult<C> {
    private final @NonNull CommandContext<C> commandContext;

    private CommandResultImpl(@NonNull CommandContext<C> commandContext) {
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
    }

    private CommandResultImpl(CommandResultImpl<C> original, @NonNull CommandContext<C> commandContext) {
        this.commandContext = commandContext;
    }

    @Override
    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    public final CommandResultImpl<C> withCommandContext(@NonNull CommandContext<C> value) {
        if (this.commandContext == value) {
            return this;
        }
        @NonNull CommandContext<C> newValue = Objects.requireNonNull(value, "commandContext");
        return new CommandResultImpl<C>(this, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandResultImpl && this.equalTo(0, (CommandResultImpl)another);
    }

    private boolean equalTo(int synthetic, CommandResultImpl<?> another) {
        return this.commandContext.equals(another.commandContext);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandContext.hashCode();
        return h;
    }

    public String toString() {
        return "CommandResult{commandContext=" + this.commandContext + "}";
    }

    public static <C> CommandResultImpl<C> of(@NonNull CommandContext<C> commandContext) {
        return new CommandResultImpl<C>(commandContext);
    }

    public static <C> CommandResultImpl<C> copyOf(CommandResult<C> instance) {
        if (instance instanceof CommandResultImpl) {
            return (CommandResultImpl)instance;
        }
        return CommandResultImpl.of(instance.commandContext());
    }
}

