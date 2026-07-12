/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.mojang.brigadier.StringReader;
import org.checkerframework.checker.nullness.qual.NonNull;

final class CloudStringReader
extends StringReader {
    private final CommandInput commandInput;

    static @NonNull CloudStringReader of(@NonNull CommandInput commandInput) {
        return new CloudStringReader(commandInput);
    }

    private CloudStringReader(@NonNull CommandInput commandInput) {
        super(commandInput.input());
        this.commandInput = commandInput;
        super.setCursor(commandInput.cursor());
    }

    @Override
    public void setCursor(int cursor) {
        super.setCursor(cursor);
        this.commandInput.cursor(cursor);
    }

    @Override
    public char read() {
        super.read();
        return this.commandInput.read();
    }

    @Override
    public void skip() {
        super.skip();
        this.commandInput.moveCursor(1);
    }
}

