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
package com.bergerkiller.bukkit.common.dep.cloud.help.result;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
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
@Generated(from="CommandEntry", generator="Immutables")
@Immutable
final class CommandEntryImpl<C>
implements CommandEntry<C> {
    private final @NonNull Command<C> command;
    private final @NonNull String syntax;

    private CommandEntryImpl(@NonNull Command<C> command, @NonNull String syntax) {
        this.command = Objects.requireNonNull(command, "command");
        this.syntax = Objects.requireNonNull(syntax, "syntax");
    }

    private CommandEntryImpl(CommandEntryImpl<C> original, @NonNull Command<C> command, @NonNull String syntax) {
        this.command = command;
        this.syntax = syntax;
    }

    @Override
    public @NonNull Command<C> command() {
        return this.command;
    }

    @Override
    public @NonNull String syntax() {
        return this.syntax;
    }

    public final CommandEntryImpl<C> withCommand(@NonNull Command<C> value) {
        if (this.command == value) {
            return this;
        }
        @NonNull Command<C> newValue = Objects.requireNonNull(value, "command");
        return new CommandEntryImpl<C>(this, newValue, this.syntax);
    }

    public final CommandEntryImpl<C> withSyntax(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "syntax");
        if (this.syntax.equals(newValue)) {
            return this;
        }
        return new CommandEntryImpl<C>(this, this.command, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CommandEntryImpl && this.equalTo(0, (CommandEntryImpl)another);
    }

    private boolean equalTo(int synthetic, CommandEntryImpl<?> another) {
        return this.command.equals(another.command) && this.syntax.equals(another.syntax);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.command.hashCode();
        h += (h << 5) + this.syntax.hashCode();
        return h;
    }

    public String toString() {
        return "CommandEntry{command=" + this.command + ", syntax=" + this.syntax + "}";
    }

    public static <C> CommandEntryImpl<C> of(@NonNull Command<C> command, @NonNull String syntax) {
        return new CommandEntryImpl<C>(command, syntax);
    }

    public static <C> CommandEntryImpl<C> copyOf(CommandEntry<C> instance) {
        if (instance instanceof CommandEntryImpl) {
            return (CommandEntryImpl)instance;
        }
        return CommandEntryImpl.of(instance.command(), instance.syntax());
    }
}

