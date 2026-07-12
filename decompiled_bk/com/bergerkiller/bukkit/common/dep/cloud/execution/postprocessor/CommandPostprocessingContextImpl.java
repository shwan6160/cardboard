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
package com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.postprocessor.CommandPostprocessingContext;
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
@Generated(from="CommandPostprocessingContext", generator="Immutables")
@Immutable
final class CommandPostprocessingContextImpl<C>
implements CommandPostprocessingContext<C> {
    private final @NonNull CommandContext<C> commandContext;
    private final @NonNull Command<C> command;

    private CommandPostprocessingContextImpl(@NonNull CommandContext<C> commandContext, @NonNull Command<C> command) {
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.command = Objects.requireNonNull(command, "command");
    }

    private CommandPostprocessingContextImpl(CommandPostprocessingContextImpl<C> original, @NonNull CommandContext<C> commandContext, @NonNull Command<C> command) {
        this.commandContext = commandContext;
        this.command = command;
    }

    @Override
    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    @Override
    public @NonNull Command<C> command() {
        return this.command;
    }

    public final CommandPostprocessingContextImpl<C> withCommandContext(@NonNull CommandContext<C> value) {
        if (this.commandContext == value) {
            return this;
        }
        @NonNull CommandContext<C> newValue = Objects.requireNonNull(value, "commandContext");
        return new CommandPostprocessingContextImpl<C>(this, newValue, this.command);
    }

    public final CommandPostprocessingContextImpl<C> withCommand(@NonNull Command<C> value) {
        if (this.command == value) {
            return this;
        }
        @NonNull Command<C> newValue = Objects.requireNonNull(value, "command");
        return new CommandPostprocessingContextImpl<C>(this, this.commandContext, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandPostprocessingContextImpl && this.equalTo(0, (CommandPostprocessingContextImpl)another);
    }

    private boolean equalTo(int synthetic, CommandPostprocessingContextImpl<?> another) {
        return this.commandContext.equals(another.commandContext) && this.command.equals(another.command);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandContext.hashCode();
        h += (h << 5) + this.command.hashCode();
        return h;
    }

    public String toString() {
        return "CommandPostprocessingContext{commandContext=" + this.commandContext + ", command=" + this.command + "}";
    }

    public static <C> CommandPostprocessingContextImpl<C> of(@NonNull CommandContext<C> commandContext, @NonNull Command<C> command) {
        return new CommandPostprocessingContextImpl<C>(commandContext, command);
    }

    public static <C> CommandPostprocessingContextImpl<C> copyOf(CommandPostprocessingContext<C> instance) {
        if (instance instanceof CommandPostprocessingContextImpl) {
            return (CommandPostprocessingContextImpl)instance;
        }
        return CommandPostprocessingContextImpl.of(instance.commandContext(), instance.command());
    }
}

