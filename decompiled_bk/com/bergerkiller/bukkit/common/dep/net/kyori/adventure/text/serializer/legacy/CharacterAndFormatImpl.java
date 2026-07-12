/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextFormat;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.CharacterAndFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CharacterAndFormatImpl
implements CharacterAndFormat {
    private final char character;
    private final TextFormat format;
    private final boolean caseInsensitive;

    CharacterAndFormatImpl(char character, @NotNull TextFormat format, boolean caseInsensitive) {
        this.character = character;
        this.format = Objects.requireNonNull(format, "format");
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public char character() {
        return this.character;
    }

    @Override
    @NotNull
    public TextFormat format() {
        return this.format;
    }

    @Override
    public boolean caseInsensitive() {
        return this.caseInsensitive;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CharacterAndFormatImpl)) {
            return false;
        }
        CharacterAndFormatImpl that = (CharacterAndFormatImpl)other;
        return this.character == that.character && this.format.equals(that.format) && this.caseInsensitive == that.caseInsensitive;
    }

    public int hashCode() {
        int result = this.character;
        result = 31 * result + this.format.hashCode();
        result = 31 * result + Boolean.hashCode(this.caseInsensitive);
        return result;
    }

    @NotNull
    public String toString() {
        return Internals.toString(this);
    }

    static final class Defaults {
        static final List<CharacterAndFormat> DEFAULTS = Defaults.createDefaults();

        private Defaults() {
        }

        static List<CharacterAndFormat> createDefaults() {
            ArrayList<CharacterAndFormat> formats = new ArrayList<CharacterAndFormat>(22);
            formats.add(CharacterAndFormat.BLACK);
            formats.add(CharacterAndFormat.DARK_BLUE);
            formats.add(CharacterAndFormat.DARK_GREEN);
            formats.add(CharacterAndFormat.DARK_AQUA);
            formats.add(CharacterAndFormat.DARK_RED);
            formats.add(CharacterAndFormat.DARK_PURPLE);
            formats.add(CharacterAndFormat.GOLD);
            formats.add(CharacterAndFormat.GRAY);
            formats.add(CharacterAndFormat.DARK_GRAY);
            formats.add(CharacterAndFormat.BLUE);
            formats.add(CharacterAndFormat.GREEN);
            formats.add(CharacterAndFormat.AQUA);
            formats.add(CharacterAndFormat.RED);
            formats.add(CharacterAndFormat.LIGHT_PURPLE);
            formats.add(CharacterAndFormat.YELLOW);
            formats.add(CharacterAndFormat.WHITE);
            formats.add(CharacterAndFormat.OBFUSCATED);
            formats.add(CharacterAndFormat.BOLD);
            formats.add(CharacterAndFormat.STRIKETHROUGH);
            formats.add(CharacterAndFormat.UNDERLINED);
            formats.add(CharacterAndFormat.ITALIC);
            formats.add(CharacterAndFormat.RESET);
            return Collections.unmodifiableList(formats);
        }
    }
}

