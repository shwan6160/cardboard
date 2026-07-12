/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.index.qual.NonNegative
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

final class CommandInputImpl
implements CommandInput {
    private final String input;
    private int cursor;

    CommandInputImpl(@NonNull String input) {
        this(input, 0);
    }

    CommandInputImpl(@NonNull String input, @NonNegative int cursor) {
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public @NonNull String input() {
        return this.input;
    }

    @Override
    public @NonNull CommandInput appendString(@NonNull String string) {
        if (this.hasRemainingInput() && !this.remainingInput().endsWith(" ")) {
            return new CommandInputImpl(String.format("%s %s", this.input, string), this.cursor);
        }
        return new CommandInputImpl(this.input + string, this.cursor);
    }

    @Override
    public @NonNegative int cursor() {
        return this.cursor;
    }

    @Override
    public void moveCursor(int chars) {
        if (this.cursor() + chars > this.length()) {
            throw new CommandInput.CursorOutOfBoundsException(this.cursor() + chars, this.length());
        }
        this.cursor += chars;
    }

    @Override
    public @This @NonNull CommandInput cursor(int cursor) {
        if (cursor < 0 || cursor > this.length()) {
            throw new CommandInput.CursorOutOfBoundsException(cursor, this.length());
        }
        this.cursor = cursor;
        return this;
    }

    @Override
    public @NonNull CommandInput copy() {
        return new CommandInputImpl(this.input, this.cursor);
    }
}

