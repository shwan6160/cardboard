/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextFormat;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.CharacterAndFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CharacterAndFormatSet {
    static final CharacterAndFormatSet DEFAULT = CharacterAndFormatSet.of(CharacterAndFormat.defaults());
    final List<TextFormat> formats;
    final List<TextColor> colors;
    final String characters;

    static CharacterAndFormatSet of(List<CharacterAndFormat> pairs) {
        int size = pairs.size();
        ArrayList<TextColor> colors = new ArrayList<TextColor>();
        ArrayList<TextFormat> formats = new ArrayList<TextFormat>(size);
        StringBuilder characters = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            CharacterAndFormat pair = pairs.get(i);
            char character = pair.character();
            TextFormat format = pair.format();
            boolean formatIsTextColor = format instanceof TextColor;
            characters.append(character);
            formats.add(format);
            if (formatIsTextColor) {
                colors.add((TextColor)format);
            }
            if (!pair.caseInsensitive()) continue;
            boolean added = false;
            if (Character.isUpperCase(character)) {
                characters.append(Character.toLowerCase(character));
                added = true;
            } else if (Character.isLowerCase(character)) {
                characters.append(Character.toUpperCase(character));
                added = true;
            }
            if (!added) continue;
            formats.add(format);
            if (!formatIsTextColor) continue;
            colors.add((TextColor)format);
        }
        if (formats.size() != characters.length()) {
            throw new IllegalStateException("formats length differs from characters length");
        }
        return new CharacterAndFormatSet(Collections.unmodifiableList(formats), Collections.unmodifiableList(colors), characters.toString());
    }

    CharacterAndFormatSet(List<TextFormat> formats, List<TextColor> colors, String characters) {
        this.formats = formats;
        this.colors = colors;
        this.characters = characters;
    }
}

