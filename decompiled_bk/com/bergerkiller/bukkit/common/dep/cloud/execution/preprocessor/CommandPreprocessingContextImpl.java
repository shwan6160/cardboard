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
package com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
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
@Generated(from="CommandPreprocessingContext", generator="Immutables")
@Immutable
final class CommandPreprocessingContextImpl<C>
implements CommandPreprocessingContext<C> {
    private final @NonNull CommandContext<C> commandContext;
    private final @NonNull CommandInput commandInput;

    private CommandPreprocessingContextImpl(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.commandInput = Objects.requireNonNull(commandInput, "commandInput");
    }

    private CommandPreprocessingContextImpl(CommandPreprocessingContextImpl<C> original, @NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        this.commandContext = commandContext;
        this.commandInput = commandInput;
    }

    @Override
    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    @Override
    public @NonNull CommandInput commandInput() {
        return this.commandInput;
    }

    public final CommandPreprocessingContextImpl<C> withCommandContext(@NonNull CommandContext<C> value) {
        if (this.commandContext == value) {
            return this;
        }
        @NonNull CommandContext<C> newValue = Objects.requireNonNull(value, "commandContext");
        return new CommandPreprocessingContextImpl<C>(this, newValue, this.commandInput);
    }

    public final CommandPreprocessingContextImpl<C> withCommandInput(@NonNull CommandInput value) {
        if (this.commandInput == value) {
            return this;
        }
        @NonNull CommandInput newValue = Objects.requireNonNull(value, "commandInput");
        return new CommandPreprocessingContextImpl<C>(this, this.commandContext, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandPreprocessingContextImpl && this.equalTo(0, (CommandPreprocessingContextImpl)another);
    }

    private boolean equalTo(int synthetic, CommandPreprocessingContextImpl<?> another) {
        return this.commandContext.equals(another.commandContext) && this.commandInput.equals(another.commandInput);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandContext.hashCode();
        h += (h << 5) + this.commandInput.hashCode();
        return h;
    }

    public String toString() {
        return "CommandPreprocessingContext{commandContext=" + this.commandContext + ", commandInput=" + this.commandInput + "}";
    }

    public static <C> CommandPreprocessingContextImpl<C> of(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        return new CommandPreprocessingContextImpl<C>(commandContext, commandInput);
    }

    public static <C> CommandPreprocessingContextImpl<C> copyOf(CommandPreprocessingContext<C> instance) {
        if (instance instanceof CommandPreprocessingContextImpl) {
            return (CommandPreprocessingContextImpl)instance;
        }
        return CommandPreprocessingContextImpl.of(instance.commandContext(), instance.commandInput());
    }
}

