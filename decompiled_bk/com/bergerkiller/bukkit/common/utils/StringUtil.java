/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.map.MapFont$CharacterSprite
 *  org.bukkit.map.MinecraftFont
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

public class StringUtil {
    public static final char CHAT_STYLE_CHAR = '\u00a7';
    public static final int SPACE_WIDTH = StringUtil.getWidth(' ');
    public static final String[] EMPTY_ARRAY = new String[0];
    private static final char[] CHAT_CODES;

    public static String setPlayerNameInChatJson(Player player, String text) {
        return text.replaceAll("(?i)\\{PLAYER\\}", player.getName());
    }

    public static String blockToString(Block block) {
        return block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
    }

    public static Block stringToBlock(String str) {
        try {
            String[] s = str.split("_");
            if (s.length < 4) {
                return null;
            }
            int x = Integer.parseInt(s[s.length - 3]);
            int y = Integer.parseInt(s[s.length - 2]);
            int z = Integer.parseInt(s[s.length - 1]);
            StringBuilder worldName = new StringBuilder(12);
            for (int i = 0; i < s.length - 3; ++i) {
                if (i != 0) {
                    worldName.append('_');
                }
                worldName.append(s[i]);
            }
            World world = Bukkit.getServer().getWorld(worldName.toString());
            if (world == null) {
                return null;
            }
            return world.getBlockAt(x, y, z);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static int getTotalWidth(String ... text) {
        int width = 0;
        for (String part : text) {
            width += StringUtil.getWidth(part);
        }
        return width;
    }

    public static int getSignWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) != ' ') continue;
            ++width;
        }
        return width += MinecraftFont.Font.getWidth(text);
    }

    public static int getWidth(CharSequence text) {
        int width = 0;
        for (int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            if (character == '\n') continue;
            if (character == '\u00a7') {
                ++i;
                continue;
            }
            if (character == ' ') {
                width += SPACE_WIDTH;
                continue;
            }
            MapFont.CharacterSprite charsprite = MinecraftFont.Font.getChar(character);
            if (charsprite == null) continue;
            width += charsprite.getWidth();
        }
        return width;
    }

    public static int getWidth(char character) {
        MapFont.CharacterSprite s = MinecraftFont.Font.getChar(character);
        return s == null ? 1 : s.getWidth();
    }

    public static int firstIndexOf(String text, char ... values) {
        for (int i = 0; i < text.length(); ++i) {
            if (!LogicUtil.containsChar(text.charAt(i), values)) continue;
            return i;
        }
        return -1;
    }

    public static int firstIndexOf(String text, String ... values) {
        return StringUtil.firstIndexOf(text, 0, values);
    }

    public static int firstIndexOf(String text, int startindex, String ... values) {
        int i = -1;
        for (String value : values) {
            int index = text.indexOf(value, startindex);
            if (index == -1 || i != -1 && index >= i) continue;
            i = index;
        }
        return i;
    }

    public static String getFilledString(String text, int n) {
        StringBuffer outputBuffer = new StringBuffer(text.length() * n);
        for (int i = 0; i < n; ++i) {
            outputBuffer.append(text);
        }
        return outputBuffer.toString();
    }

    public static String getBefore(String text, String delimiter) {
        int index = text.indexOf(delimiter);
        return index >= 0 ? text.substring(0, index) : "";
    }

    public static String getAfter(String text, String delimiter) {
        int index = text.indexOf(delimiter);
        return index >= 0 ? text.substring(index + delimiter.length()) : "";
    }

    public static String getLastBefore(String text, String delimiter) {
        int index = text.lastIndexOf(delimiter);
        return index >= 0 ? text.substring(0, index) : "";
    }

    public static String getLastAfter(String text, String delimiter) {
        int index = text.lastIndexOf(delimiter);
        return index >= 0 ? text.substring(index + delimiter.length()) : "";
    }

    public static String replace(String text, int startIndex, int endIndex, String replacement) {
        StringBuilder builder = new StringBuilder(text);
        builder.replace(startIndex, endIndex, replacement);
        return builder.toString();
    }

    public static String trimEnd(String text, String ... textToTrim) {
        for (String trim : textToTrim) {
            if (!text.endsWith(trim)) continue;
            return text.substring(0, text.length() - trim.length());
        }
        return text;
    }

    public static String trimStart(String text, String ... textToTrim) {
        for (String trim : textToTrim) {
            if (!text.startsWith(trim)) continue;
            return text.substring(trim.length());
        }
        return text;
    }

    public static String trimStart(String text) {
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == ' ') continue;
            return text.substring(i);
        }
        return "";
    }

    public static String trimEnd(String text) {
        for (int i = text.length() - 1; i >= 0; --i) {
            if (text.charAt(i) == ' ') continue;
            return text.substring(0, i + 1);
        }
        return "";
    }

    public static String[] remove(String[] input, int index) {
        return LogicUtil.removeArrayElement(input, index);
    }

    public static String combineNames(Set items) {
        return StringUtil.combineNames((Collection)items);
    }

    public static String combineNames(Collection items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        if (items.size() == 1) {
            Object item = items.iterator().next();
            return item == null ? "" : item.toString();
        }
        StringBuilder rval = new StringBuilder();
        int i = 0;
        for (Object item : items) {
            if (i == items.size() - 1) {
                rval.append(" and ");
            } else if (i > 0) {
                rval.append(", ");
            }
            if (item != null) {
                rval.append(item);
            }
            ++i;
        }
        return rval.toString();
    }

    public static String combineNames(String ... items) {
        return StringUtil.combineNames(Arrays.asList(items));
    }

    @Deprecated
    public static String combine(String separator, String ... parts) {
        return StringUtil.join(separator, parts);
    }

    @Deprecated
    public static String combine(String separator, Collection<String> parts) {
        return StringUtil.join(separator, parts);
    }

    public static String join(String separator, String ... parts) {
        return StringUtil.join(separator, Arrays.asList(parts));
    }

    public static String join(String separator, Collection<String> parts) {
        StringBuilder builder = new StringBuilder(parts.size() * 16);
        boolean first = true;
        for (String line : parts) {
            if (!first) {
                builder.append(separator);
            }
            if (line != null) {
                builder.append(line);
            }
            first = false;
        }
        return builder.toString();
    }

    public static String[] convertArgs(String[] args) {
        return StringUtil.convertArgsList(Arrays.asList(args)).toArray(new String[0]);
    }

    public static LinkedList<String> convertArgsList(Iterable<String> args) {
        StringArgTokenizer tokenizer = new StringArgTokenizer();
        for (String arg : args) {
            tokenizer.next(arg);
        }
        return tokenizer.complete();
    }

    public static boolean isChatCode(char character) {
        return LogicUtil.containsChar(character, CHAT_CODES);
    }

    public static int getSuccessiveCharCount(String value, char character) {
        return StringUtil.getSuccessiveCharCount(value, character, 0, value.length() - 1);
    }

    public static int getSuccessiveCharCount(String value, char character, int startindex) {
        return StringUtil.getSuccessiveCharCount(value, character, startindex, value.length() - startindex - 1);
    }

    public static int getSuccessiveCharCount(String value, char character, int startindex, int endindex) {
        int count = 0;
        for (int i = startindex; i <= endindex && value.charAt(i) == character; ++i) {
            ++count;
        }
        return count;
    }

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    public static ChatColor getColor(char code, ChatColor def) {
        for (ChatColor color : ChatColor.values()) {
            if (code != color.toString().charAt(1)) continue;
            return color;
        }
        return def;
    }

    public static String ampToColor(String line) {
        return StringUtil.swapColorCodes(line, '&', '\u00a7');
    }

    public static String colorToAmp(String line) {
        return StringUtil.swapColorCodes(line, '\u00a7', '&');
    }

    public static String swapColorCodes(String line, char fromCode, char toCode) {
        StringBuilder builder = new StringBuilder(line);
        for (int i = 0; i < builder.length() - 1; ++i) {
            if (builder.charAt(i) != fromCode || !StringUtil.isChatCode(builder.charAt(i + 1))) continue;
            builder.setCharAt(i, toCode);
            ++i;
        }
        return builder.toString();
    }

    public static String removeSubstring(String text, int beginIndex, int endIndex) {
        return StringUtil.replaceSubstring(text, beginIndex, endIndex, "");
    }

    public static String replaceSubstring(String text, int beginIndex, int endIndex, String replacement) {
        try {
            StringBuilder str = new StringBuilder(text.length() - (endIndex - beginIndex) + replacement.length());
            str.append(text, 0, beginIndex);
            str.append(replacement);
            str.append(text, endIndex, text.length());
            return str.toString();
        }
        catch (RuntimeException ex) {
            if (text == null) {
                throw new IllegalArgumentException("Input text is null");
            }
            if (replacement == null) {
                throw new IllegalArgumentException("Input replacement string is null");
            }
            if (beginIndex < 0) {
                throw new IllegalArgumentException("Begin index is negative");
            }
            if (endIndex > text.length()) {
                throw new IllegalArgumentException("End index is beyond the length of the text");
            }
            if (endIndex < beginIndex) {
                throw new IllegalArgumentException("End index of " + endIndex + " is before begin index " + beginIndex);
            }
            throw ex;
        }
    }

    public static String stripChatStyle(String text) {
        int index = text.indexOf(167);
        if (index == -1) {
            return text;
        }
        int lastIndex = 0;
        int textLength = text.length();
        StringBuilder newStr = new StringBuilder(textLength);
        do {
            newStr.append(text, lastIndex, index);
            lastIndex = index + 2;
            if (lastIndex < textLength) continue;
            return newStr.toString();
        } while ((index = text.indexOf(167, lastIndex)) != -1);
        newStr.append(text, lastIndex, text.length());
        return newStr.toString();
    }

    static {
        ChatColor[] styles = ChatColor.values();
        LinkedHashSet<Character> chars = new LinkedHashSet<Character>(styles.length * 2);
        for (ChatColor style : styles) {
            chars.add(Character.valueOf(Character.toLowerCase(style.getChar())));
            chars.add(Character.valueOf(Character.toUpperCase(style.getChar())));
        }
        chars.add(Character.valueOf('x'));
        chars.add(Character.valueOf('X'));
        CHAT_CODES = new char[chars.size()];
        int i = 0;
        for (Character c : chars) {
            StringUtil.CHAT_CODES[i] = c.charValue();
            ++i;
        }
    }

    private static final class StringArgTokenizer {
        private final LinkedList<String> result = new LinkedList();
        private final StringBuilder buffer = new StringBuilder();
        private boolean isEscaped = false;

        private StringArgTokenizer() {
        }

        public void next(String arg) {
            int numEscapedAtStart;
            if (this.isEscaped) {
                this.buffer.append(' ');
            }
            for (numEscapedAtStart = 0; numEscapedAtStart < arg.length() && arg.charAt(numEscapedAtStart) == '\"'; ++numEscapedAtStart) {
            }
            int remEscapedAtStart = numEscapedAtStart % 3;
            if (numEscapedAtStart == arg.length()) {
                if (this.isEscaped) {
                    if (remEscapedAtStart == 0) {
                        this.appendEscapedQuotes(numEscapedAtStart / 3);
                    } else {
                        this.appendEscapedQuotes(numEscapedAtStart / 3 + remEscapedAtStart - 1);
                        this.commitBuffer();
                    }
                } else {
                    this.appendEscapedQuotes(numEscapedAtStart / 3);
                    if (remEscapedAtStart == 1) {
                        this.isEscaped = true;
                    } else {
                        this.commitBuffer();
                    }
                }
                return;
            }
            if (!this.isEscaped && remEscapedAtStart > 0) {
                this.isEscaped = true;
                --remEscapedAtStart;
            }
            this.appendEscapedQuotes(numEscapedAtStart / 3 + remEscapedAtStart);
            int numTrailingQuotes = 0;
            for (int i = numEscapedAtStart; i < arg.length(); ++i) {
                char c = arg.charAt(i);
                if (c != '\"') {
                    this.appendEscapedQuotes(numTrailingQuotes);
                    numTrailingQuotes = 0;
                    this.buffer.append(c);
                    continue;
                }
                if (++numTrailingQuotes != 3) continue;
                numTrailingQuotes = 0;
                this.buffer.append('\"');
            }
            if (!this.isEscaped) {
                this.appendEscapedQuotes(numTrailingQuotes);
                this.commitBuffer();
            } else if (numTrailingQuotes > 0) {
                this.appendEscapedQuotes(numTrailingQuotes - 1);
                this.commitBuffer();
            }
        }

        public LinkedList<String> complete() {
            if (this.isEscaped) {
                this.commitBuffer();
            }
            return this.result;
        }

        private void appendEscapedQuotes(int num_quotes) {
            for (int n = 0; n < num_quotes; ++n) {
                this.buffer.append('\"');
            }
        }

        private void commitBuffer() {
            this.result.add(this.buffer.toString());
            this.buffer.setLength(0);
            this.isEscaped = false;
        }
    }
}

