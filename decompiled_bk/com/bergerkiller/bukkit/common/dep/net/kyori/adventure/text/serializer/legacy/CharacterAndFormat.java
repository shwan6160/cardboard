/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Unmodifiable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.NamedTextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextFormat;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.CharacterAndFormatImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.Reset;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.NonExtendable
public interface CharacterAndFormat
extends Examinable {
    public static final CharacterAndFormat BLACK = CharacterAndFormat.characterAndFormat('0', NamedTextColor.BLACK, true);
    public static final CharacterAndFormat DARK_BLUE = CharacterAndFormat.characterAndFormat('1', NamedTextColor.DARK_BLUE, true);
    public static final CharacterAndFormat DARK_GREEN = CharacterAndFormat.characterAndFormat('2', NamedTextColor.DARK_GREEN, true);
    public static final CharacterAndFormat DARK_AQUA = CharacterAndFormat.characterAndFormat('3', NamedTextColor.DARK_AQUA, true);
    public static final CharacterAndFormat DARK_RED = CharacterAndFormat.characterAndFormat('4', NamedTextColor.DARK_RED, true);
    public static final CharacterAndFormat DARK_PURPLE = CharacterAndFormat.characterAndFormat('5', NamedTextColor.DARK_PURPLE, true);
    public static final CharacterAndFormat GOLD = CharacterAndFormat.characterAndFormat('6', NamedTextColor.GOLD, true);
    public static final CharacterAndFormat GRAY = CharacterAndFormat.characterAndFormat('7', NamedTextColor.GRAY, true);
    public static final CharacterAndFormat DARK_GRAY = CharacterAndFormat.characterAndFormat('8', NamedTextColor.DARK_GRAY, true);
    public static final CharacterAndFormat BLUE = CharacterAndFormat.characterAndFormat('9', NamedTextColor.BLUE, true);
    public static final CharacterAndFormat GREEN = CharacterAndFormat.characterAndFormat('a', NamedTextColor.GREEN, true);
    public static final CharacterAndFormat AQUA = CharacterAndFormat.characterAndFormat('b', NamedTextColor.AQUA, true);
    public static final CharacterAndFormat RED = CharacterAndFormat.characterAndFormat('c', NamedTextColor.RED, true);
    public static final CharacterAndFormat LIGHT_PURPLE = CharacterAndFormat.characterAndFormat('d', NamedTextColor.LIGHT_PURPLE, true);
    public static final CharacterAndFormat YELLOW = CharacterAndFormat.characterAndFormat('e', NamedTextColor.YELLOW, true);
    public static final CharacterAndFormat WHITE = CharacterAndFormat.characterAndFormat('f', NamedTextColor.WHITE, true);
    public static final CharacterAndFormat OBFUSCATED = CharacterAndFormat.characterAndFormat('k', TextDecoration.OBFUSCATED, true);
    public static final CharacterAndFormat BOLD = CharacterAndFormat.characterAndFormat('l', TextDecoration.BOLD, true);
    public static final CharacterAndFormat STRIKETHROUGH = CharacterAndFormat.characterAndFormat('m', TextDecoration.STRIKETHROUGH, true);
    public static final CharacterAndFormat UNDERLINED = CharacterAndFormat.characterAndFormat('n', TextDecoration.UNDERLINED, true);
    public static final CharacterAndFormat ITALIC = CharacterAndFormat.characterAndFormat('o', TextDecoration.ITALIC, true);
    public static final CharacterAndFormat RESET = CharacterAndFormat.characterAndFormat('r', Reset.INSTANCE, true);

    @NotNull
    public static CharacterAndFormat characterAndFormat(char character, @NotNull TextFormat format) {
        return CharacterAndFormat.characterAndFormat(character, format, false);
    }

    @NotNull
    public static CharacterAndFormat characterAndFormat(char character, @NotNull TextFormat format, boolean caseInsensitive) {
        return new CharacterAndFormatImpl(character, format, caseInsensitive);
    }

    public static @Unmodifiable @NotNull List<CharacterAndFormat> defaults() {
        return CharacterAndFormatImpl.Defaults.DEFAULTS;
    }

    public char character();

    @NotNull
    public TextFormat format();

    public boolean caseInsensitive();

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("character", this.character()), ExaminableProperty.of("format", this.format()), ExaminableProperty.of("caseInsensitive", this.caseInsensitive()));
    }
}

